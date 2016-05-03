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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;
import uk.ac.bbsrc.tgac.browser.blastmanager.store.BLASTManagerStore;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 4/17/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */

@Ajaxified
public class BlastServiceSLURM {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BLASTManagerStore blastManagerStore;

    public void setBlastManagerStore(BLASTManagerStore blastManagerStore) {
        this.blastManagerStore = blastManagerStore;
    }


    private BlastServiceSystem blastServiceSystem = new BlastServiceSystem();


    /**
     * Return JSONObject
     * <p>
     * Reads the xml file get ID from ajax call
     * Format data into table format and sends back
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with BLAST result formatted into table
     * @throws IOException
     */
    public JSONObject blastSearchSequence(HttpSession session, JSONObject json) throws IOException {
        try {
            JSONArray blasts = new JSONArray();
            JSONObject html = new JSONObject();

            String blastAccession = json.getString("accession");
            String location = json.getString("location");
            String old_blastAccession = json.getString("old_taskid");
            String slurm_id = json.getString("slurm_id");


            JSONObject error = blastServiceSystem.checkError(slurm_id);
            if (error.getBoolean("found") == true) {
                html.put("id", blastAccession);
                html.put("html", "error");
                html.put("error", error.getString("error"));
            } else if (blastManagerStore.checkResultDatabase(old_blastAccession)) {
                log.info("already in db");
                blasts = blastManagerStore.getFromDatabase(old_blastAccession, location);
                html.put("id", blastAccession);
                html.put("html", blasts);
            } else {
                blasts = blastManagerStore.getFromDatabase(blastAccession, location);
                html.put("id", blastAccession);
                html.put("html", blasts);
            }

            return html;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }

    }


    /**
     * Return JSONObject
     * <p>
     * Reads file ID from ajax call
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with data formatted into tracks style
     * @throws IOException
     */
    public JSONObject blastSearchTrack(HttpSession session, JSONObject json) throws IOException {
        String blastAccession = json.getString("accession");
        JSONArray blasts = new JSONArray();

        JSONObject blast_response = new JSONObject();

        int query_start = json.getInt("start");
        int query_end = json.getInt("end");
        int noofhits = json.getInt("hit");
        String location = json.getString("location");
        String old_blastAccession = json.getString("old_taskid");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;

        try {

            if (blastManagerStore.checkResultDatabase(old_blastAccession)) {
                blasts = blastManagerStore.getTrackFromDatabase(old_blastAccession, query_start);
                blast_response.put("blast", blasts);

            } else {

                blasts = blastManagerStore.getTrackFromDatabase(blastAccession, query_start);
                blast_response.put("blast", blasts);
            }
            return blast_response; //JSONUtils.JSONObjectResponse("blast", result);

        } catch (SAXParseException sax) {
            sax.printStackTrace();

            throw new RuntimeException(sax);
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException(err);
        }
    }

    /**
     * Return JSONObject
     * <p>
     * Reads file with ID from ajax call
     * Parse file based on space and format to track style
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with blasttrack result
     * @throws IOException
     */
    public JSONObject blastEntry(HttpSession session, JSONObject json) throws IOException {
        try {
            String seqRegion = json.getString("seqregion");
            String blastAccession = json.getString("accession");
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            if (blastManagerStore.checkResultDatabase(blastAccession)) {
                jsonArray = blastManagerStore.getBLASTEntryFromDatabase(blastAccession, seqRegion);

            } else {

                FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
                BufferedReader in = new BufferedReader(new InputStreamReader(fstream));

                String str;
                while (null != (str = in.readLine())) {

                    Pattern p = Pattern.compile("#");
                    Matcher matcher_comment = p.matcher(str);
                    if (matcher_comment.find()) {
                    } else {
                        Pattern p1 = Pattern.compile("<.*>");
                        Matcher matcher_score = p1.matcher(str);
                        if (matcher_score.find()) {
                        } else {
                            String[] list = str.split("\\s+");
                            if (list[1].equals(seqRegion)) {
                                JSONObject jb = new JSONObject();
                                if (Long.parseLong(list[8]) < Long.parseLong(list[9])) {
                                    jb.put("start", list[8]);
                                    jb.put("end", list[9]);
                                } else {
                                    jb.put("start", list[9]);
                                    jb.put("end", list[8]);
                                }

                                jb.put("score", " ");
                                jb.put("desc", " ");
                                jb.put("indels", "");
                                jsonArray.add(jb);
                            }
                        }
                    }
                }
                in.close();
            }
            jsonObject.put("entries", jsonArray);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
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
    protected String getTextValue(Element ele, String tagName) {
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
     * Reads BLAST parameter from ajax call
     * Creates a FASTA file for query
     * call method sendMessage with parameters
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return
     * @throws IOException
     */
    public JSONObject submitBlastTask(HttpSession session, JSONObject json) throws IOException {
        try {
            String id = null;
            String fasta = json.getString("querystring");
            String accession = json.getString("BlastAccession");
            String type = json.getString("type");
            String location = json.getString("location");
            String blastdb = json.getString("blastdb");
            String params = json.getString("params");
            String format = json.getString("format");
            if (blastManagerStore.checkDatabase(fasta, blastdb, location, type, params, format)) {

                id = blastManagerStore.getIDFromDatabase(fasta, blastdb, location, type, params, format).toString();
                JSONObject r = new JSONObject();
                r.put("id", id);
                return r;
            } else {
                blastManagerStore.insertintoDatabase(accession, fasta, blastdb, location, type, params, format);

                JSONObject parameters = new JSONObject();
                parameters.put("accession", accession);

                JSONObject r = new JSONObject();

                id = blastServiceSystem.submitJob(parameters);
                r.put("slurm_id", id);
                r.put("response", "Task submitted: browser_blast");
                return r;

            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }


    /**
     * Return JSONObject
     * <p>
     * call method taskSQL with taskId received from ajax call
     * IF task is completed it returns true for stopUpdater
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with stopUpdater
     * @throws IOException
     */
    public JSONObject checkTask(HttpSession session, JSONObject json) throws IOException {
        JSONObject q1 = new JSONObject();
        String slurmId = json.getString("slurm_id");
        String old_taskId = json.getString("old_taskid");

        try {

            String state = blastServiceSystem.checkJob(slurmId);
            q1.put("result", state);
            q1.put("old_id", old_taskId);

            return q1;
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }
}



