/*
#
# Copyright (c) 2013. The Genome Analysis Centre, Norwich, UK
# TGAC Browser project contacts: Anil Thanki, Xingdong Bian, Robert Davey, Mario Caccamo @ TGAC
# **********************************************************************
#
# This file is part of TGAC Browser.
#
# TGAC Browser is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# TGAC Browser is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with TGAC Browser.  If not, see <http://www.gnu.org/licenses/>.
#
# ***********************************************************************
#
 */

package uk.ac.bbsrc.tgac.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.lang.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;
import uk.ac.bbsrc.tgac.browser.blastmanager.store.BLASTManagerStore;

import static org.apache.commons.lang.StringUtils.split;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Ajaxified

public class BlastServiceLocal {
    private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BLASTManagerStore blastManagerStore;

    public void setBlastManagerStore(BLASTManagerStore blastManagerStore) {
        this.blastManagerStore = blastManagerStore;
    }

    /**
     * Return JSON Object
     * <p>
     * Reads json object from ajax call
     * generate FASTA file from query sequence
     * run BLAST on local system and wait to finish
     * read resulting file
     * Format data into table format and sends back
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with BLAST result converted to tabular format
     * @throws IOException
     */
    public JSONObject blastSearchSequence(HttpSession session, JSONObject json) throws IOException {
        try {
            JSONArray blasts = new JSONArray();
            JSONObject html = new JSONObject();

            String blastdb = json.getString("blastdb");
            String fasta = json.getString("query");
            String location = json.getString("location");
            String blastAccession = json.getString("BlastAccession");
            String type = json.getString("type");
            String blastBinary = json.getString("blastBinary");
            String format = json.getString("format");
            String params = json.getString("params");


            if (blastManagerStore.checkDatabase(fasta, blastdb, location, type, params, format)) {
                blasts = blastManagerStore.getFromDatabase(blastAccession, location);
            } else {
                blastManagerStore.insertintoDatabase(blastAccession, fasta, blastdb, location, type, params, format);

                File fastaTmp = File.createTempFile("blast", ".fa");

                PrintWriter out = new PrintWriter(fastaTmp);
                out.println(fasta);
                out.flush();
                out.close();

                String execBlast = blastBinary + "/" + type + " -db " + blastdb + " -query " + fastaTmp + "  -outfmt 6 -task blastn";
                Process proc = Runtime.getRuntime().exec(execBlast);
                proc.waitFor();
                int i = 0;

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));

                // read the output from the command
                System.out.println("Here is the standard output of the command:\n");
                String str = null;
                while ((str = stdInput.readLine()) != null) {
                    JSONObject eachBlast = new JSONObject();

                    Pattern p = Pattern.compile("#");
                    Matcher matcher_comment = p.matcher(str);
                    if (matcher_comment.find()) {
                    } else {
                        Pattern p1 = Pattern.compile("<.*>");
                        Matcher matcher_score = p1.matcher(str);
                        if (matcher_score.find()) {
                        } else {
                            String str1 = str.replaceAll("\\s+", "<td>");
                            String[] id;
                            id = str1.split("<td>");
                            String seqregionName = id[1];
                            String hsp_from = id[8];
                            String hsp_to = id[9];
                            String str2 = "";
                            eachBlast.put("q_id", id[0]);
                            if (location.length() > 0) {
                                eachBlast.put("s_id", "<a target='_blank' href='../" + location + "/index.jsp?query=" + seqregionName + "&from=" + hsp_from + "&to=" + hsp_to + "&blasttrack=" + blastAccession + "'>"
                                        + seqregionName + "</a>");
                            } else {
                                eachBlast.put("s_id", id[1]);

                            }
                            eachBlast.put("identity", id[2]);
                            eachBlast.put("aln_length", id[3]);
                            eachBlast.put("mismatch", id[4]);
                            eachBlast.put("gap_open", id[5]);
                            eachBlast.put("q_start", id[6]);
                            eachBlast.put("q_end", id[7]);
                            eachBlast.put("s_start", id[8]);
                            eachBlast.put("s_end", id[9]);
                            eachBlast.put("e_value", id[10]);
                            eachBlast.put("bit_score", id[11]);
                            eachBlast.put("s_db", blastdb.substring(blastdb.lastIndexOf("/") + 1));


                            i++;
                        }
                    }
                    blasts.add(eachBlast);
                }

                // read any errors from the attempted command
                System.out.println("Here is the standard error of the command (if any):\n");
                while ((str = stdError.readLine()) != null) {
                    System.out.println(str);
                }

                blastManagerStore.setResultToDatabase(blastAccession, blasts);
                String result = null;
                if (i > 0) {
                } else {
                    blasts.add("No hits found.");
                }
            }

            html.put("id", json.getString("BlastAccession"));
            html.put("html", blasts);
            return html;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }

    }


    /**
     * Return JSONObject
     * <p>
     * Reads json object from ajax call
     * generate FASTA file from query sequence
     * run BLAST on local system and wait to finish
     * read resulting file
     * Parse resulting file and convert into track format
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with BLAST result formatted into track style
     * @throws IOException
     */
    public JSONObject blastSearchTrack(HttpSession session, JSONObject json) throws IOException {
        String fasta = json.getString("query");
        String blastBinary = json.getString("blastBinary");
        String blastdb = json.getString("blastdb");
        int query_start = json.getInt("start");
        int query_end = json.getInt("end");
        int noofhits = json.getInt("hit");
        String location = json.getString("location");
        String type = json.getString("type");
        String format = json.getString("format");


        JSONObject blast_response = new JSONObject();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;
        String blastAccession = json.getString("BlastAccession");
        String params = json.getString("params");

        try {
            JSONArray blasts = new JSONArray();

            if (blastManagerStore.checkResultDatabase(blastAccession)) {
                blasts = blastManagerStore.getFromDatabase(blastAccession, location);
            } else {
                blastManagerStore.insertintoDatabase(blastAccession, fasta, blastdb, location, type, params, format);

                String file = "../webapps/" + location + "/temp/" + json.getString("BlastAccession") + ".xml";

                File fastaTmp = File.createTempFile("blast", ".fa");

                PrintWriter out = new PrintWriter(fastaTmp);
                out.println(fasta.replaceAll("[0-9 \t\n\r]", ""));
                out.flush();
                out.close();

                String execBlast = blastBinary + "/" + type + " -db " + blastdb + " -query " + fastaTmp + " -outfmt 5 -max_target_seqs 10 ";


                log.info("command " + execBlast);
                Process proc = Runtime.getRuntime().exec(execBlast);

                proc.waitFor();


                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));

                DataInputStream input = new DataInputStream(proc.getInputStream());
                int in = 0;
                int findHits = 1;
                DocumentBuilder db = dbf.newDocumentBuilder();

                dom = db.parse(input);
                Element docEle = dom.getDocumentElement();
                NodeList nl = docEle.getElementsByTagName("Hit");

                if (nl != null && nl.getLength() > 0) {
                    HIT:
                    for (int a = 0; a < nl.getLength(); a++) {

                        Element el = (Element) nl.item(a);
                        String hit_id = getTextValue(el, "Hit_def");

                        NodeList hsps = el.getElementsByTagName("Hsp");
                        if (hsps != null && hsps.getLength() > 0) {
                            for (int b = 0; b < hsps.getLength(); b++) {


                                Element ell = (Element) hsps.item(b);

                                String hsp_from = getTextValue(ell, "Hsp_query-from");

                                String hsp_score = getTextValue(ell, "Hsp_score");

                                String hsp_to = getTextValue(ell, "Hsp_query-to");

                                JSONObject eachBlast = new JSONObject();
                                JSONArray indels = new JSONArray();
                                JSONObject eachIndel = new JSONObject();

                                eachBlast.put("start", query_start + Integer.parseInt(hsp_from));
                                eachBlast.put("end", query_start + Integer.parseInt(hsp_to));
                                eachBlast.put("desc", "<a href=\"javascript:void(0);\" onclick=\"seqregionSearchPopup(\'" + hit_id + "\')\">"
                                        + hit_id + "</a>");
                                eachBlast.put("score", hsp_score);
                                eachBlast.put("flag", false);
                                eachBlast.put("reverse", "");

                                String hsp_midline = getTextValue(ell, "Hsp_midline");
                                if (hsp_midline.split(" ").length > 1) {
                                    String hsp_query_seq = getTextValue(ell, "Hsp_qseq");
                                    String hsp_hit_seq = getTextValue(ell, "Hsp_hseq");
                                    String[] newtemp = hsp_midline.split(" ");
                                    int ins = 0;
                                    for (int x = 0; x < newtemp.length - 1; x++) {
                                        ins = ins + ((newtemp[x].length() + 1));
                                        eachIndel.put("position", ins + in);
                                        eachIndel.put("query", hsp_query_seq.substring((ins - 3) > -1 ? (ins - 3) : 0, (ins + 2) <= hsp_query_seq.length() ? (ins + 2) : hsp_query_seq.length()));
                                        eachIndel.put("hit", hsp_hit_seq.substring((ins - 3) > -1 ? (ins - 3) : 0, (ins + 2) <= hsp_hit_seq.length() ? (ins + 2) : hsp_hit_seq.length()));
                                        indels.add(eachIndel);
                                    }
                                }

                                eachBlast.put("indels", indels);
                                blasts.add(eachBlast);

                                findHits++;
                                if (findHits > noofhits) {
                                    break HIT;

                                }

                            }
                        }
                    }
                } else {
                    blasts.add("No hits found.");
                }
                blastManagerStore.setResultToDatabase(blastAccession, blasts);
            }
            blast_response.put("id", json.getString("BlastAccession"));
            blast_response.put("blast", blasts);
            return blast_response;

        } catch (SAXParseException sax) {
            throw new RuntimeException(sax);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    /**
     * Return String
     * <p>
     * Read XMLDomElement and read the value from key name
     * </p>
     *
     * @param ele     XMLDOMElement
     * @param tagName XMLDOMElement tag name
     * @return Strnig value related to tag name
     */

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }


    /**
     * Return JSONObject
     * <p>
     * check if the file exist
     * if file exist then call method parseFileXML
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with blastid and BLAST track result
     * @throws IOException
     */

    public JSONObject checkBlast(HttpSession session, JSONObject json) throws IOException {
        try {
            log.info("checkBlast");

            JSONObject blast_response = new JSONObject();
            String location = json.getString("location");
            String link = json.getString("link");

            Boolean isExist;
            String file = "../webapps/" + location + "/temp/" + json.getString("BlastAccession") + ".xml";
            isExist = false;
            int query_start = json.getInt("start");
            int query_end = json.getInt("end");
            int noofhits = json.getInt("hit");
            JSONArray blasts = new JSONArray();
            while (isExist == false) {
                log.info("loop " + isExist);
                isExist = fileExist(file);
                if (isExist == true) {
                    log.info("loop if" + isExist);
                    blasts = parseFileXML(file, query_start, query_end, noofhits, link, location);
                    break;
                }

            }
            blast_response.put("id", json.getString("BlastAccession"));
            blast_response.put("blast", blasts);
            return blast_response;
        } catch (Exception e) {
            throw new IOException(e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    /**
     * Return Boolean
     * <p>
     * check if File Exist
     * </p>
     *
     * @param file String file name
     * @return boolean file exist or not
     * @throws IOException
     */
    private boolean fileExist(String file) throws IOException {
        try {
            boolean isExist = new File(file).exists();
            return isExist;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Return JSONArray
     * <p>
     * read resulting file
     * Parse resulting file and convert into track format
     * </p>
     *
     * @param file        String file name
     * @param query_start int start position of query
     * @param query_end   int end position of query
     * @param noofhits    int noohhits
     * @param link        int link for the hits
     * @param location    int location of the file
     * @return JSONArray with BLAST hits
     */
    private JSONArray parseFileXML(String file, int query_start, int query_end, int noofhits, String link, String location) {
        try {

            FileInputStream fstream = new FileInputStream(file);
            DataInputStream input = new DataInputStream(fstream);
            int in = 0;
            int findHits = 1;
            JSONArray blasts = new JSONArray();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document dom;
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse(input);
            Element docEle = dom.getDocumentElement();
            NodeList nl = docEle.getElementsByTagName("Hit");

            if (nl != null && nl.getLength() > 0) {
                HIT:
                for (int a = 0; a < nl.getLength(); a++) {

                    Element el = (Element) nl.item(a);
                    String hit_id = getTextValue(el, "Hit_def");

                    NodeList hsps = el.getElementsByTagName("Hsp");
                    if (hsps != null && hsps.getLength() > 0) {
                        for (int b = 0; b < hsps.getLength(); b++) {


                            Element ell = (Element) hsps.item(b);

                            String hsp_from = getTextValue(ell, "Hsp_query-from");

                            String hsp_score = getTextValue(ell, "Hsp_score");

                            String hsp_to = getTextValue(ell, "Hsp_query-to");

                            JSONObject eachBlast = new JSONObject();
                            JSONArray indels = new JSONArray();
                            JSONObject eachIndel = new JSONObject();

                            eachBlast.put("start", query_start + Integer.parseInt(hsp_from));
                            eachBlast.put("end", query_start + Integer.parseInt(hsp_to));
                            if (link.length() > 0) {
                                eachBlast.put("desc", " <a target='_blank' href='../" + location + "/index.jsp?query=" + hit_id + "&from=" + hsp_from + "&to=" + hsp_to + "'>"
                                        + hit_id + "</a>");
                            } else {
                                eachBlast.put("desc", hit_id);
                            }
                            eachBlast.put("score", hsp_score);
                            eachBlast.put("flag", false);
                            eachBlast.put("reverse", "");

                            String hsp_midline = getTextValue(ell, "Hsp_midline");
                            if (hsp_midline.split(" ").length > 1) {
                                String hsp_query_seq = getTextValue(ell, "Hsp_qseq");
                                String hsp_hit_seq = getTextValue(ell, "Hsp_hseq");
                                String[] newtemp = hsp_midline.split(" ");
                                int ins = 0;
                                for (int x = 0; x < newtemp.length - 1; x++) {
                                    ins = ins + ((newtemp[x].length() + 1));
                                    eachIndel.put("position", ins + in);
                                    eachIndel.put("query", hsp_query_seq.substring((ins - 3) > -1 ? (ins - 3) : 0, (ins + 2) <= hsp_query_seq.length() ? (ins + 2) : hsp_query_seq.length()));
                                    eachIndel.put("hit", hsp_hit_seq.substring((ins - 3) > -1 ? (ins - 3) : 0, (ins + 2) <= hsp_hit_seq.length() ? (ins + 2) : hsp_hit_seq.length()));
                                    indels.add(eachIndel);
                                }
                            }

                            eachBlast.put("indels", indels);
                            blasts.add(eachBlast);

                            findHits++;
                            if (findHits > noofhits) {
                                break HIT;
                            }
                        }
                    } else {
                        blasts.add("No hits found.");
                    }
                }
            }
            return blasts;
        } catch (Exception e) {
            JSONArray er = new JSONArray();
            e.printStackTrace();
            er.add(JSONUtils.SimpleJSONError(e.getMessage()));
            return er;
        }

    }
}