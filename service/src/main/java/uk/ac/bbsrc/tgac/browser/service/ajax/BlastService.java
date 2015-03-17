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

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;
import uk.ac.bbsrc.tgac.browser.blastmanager.store.BLASTManagerStore;


/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 4/17/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */

@Ajaxified
public class BlastService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BLASTManagerStore blastManagerStore;

    public void setBlastManagerStore(BLASTManagerStore blastManagerStore) {
        this.blastManagerStore = blastManagerStore;
    }

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
            String blastdb = json.getString("db");

            String blastAccession = json.getString("accession");
            String location = json.getString("location");
            String old_blastAccession = json.getString("old_taskid");

            if (blastManagerStore.checkResultDatabase(old_blastAccession)) {
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
                DataInputStream in = new DataInputStream(fstream);
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

            String fasta = json.getString("querystring");
            String accession = json.getString("BlastAccession");
            String type = json.getString("type");
            String location = json.getString("location");
            String blastdb = json.getString("blastdb");
            String params = json.getString("params");
            String format = json.getString("format");
            if (blastManagerStore.checkDatabase(fasta, blastdb, location, type, params, format)) {
                String id = blastManagerStore.getIDFromDatabase(fasta, blastdb, location, type, params, format).toString();
                JSONObject r = new JSONObject();
                r.put("id", id);
                return r;
            } else {
                blastManagerStore.insertintoDatabase(accession, fasta, blastdb, location, type, params, format);


                JSONObject task = new JSONObject();

                JSONObject j = new JSONObject();

                if (json.has("priority")) {
                    j.put("priority", json.get("priority"));
                } else {
                    j.put("priority", "HIGH");
                }
                j.put("pipeline", "browser_blast");//json.get("pipeline"));
                json.remove("querystring");
                j.put("params", json);

                task.put("submit", j);
                String response = sendMessage(prepareSocket("v0376.nbi.ac.uk", 7899), task.toString());
                if (!"".equals(response)) {
                    JSONObject r = JSONObject.fromObject(response);
                    return r;
                }
            }
            JSONObject r = new JSONObject();
            r.put("error", "empty-response");
            r.put("id", accession);
            return r;
        } catch (IOException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * Return Socket
     * <p>
     * create new Socket from information
     * </p>
     *
     * @param host String with host name
     * @param port int with port number
     * @return Socket
     * @throws IOException
     */
    public static Socket prepareSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    /**
     * Return String
     * <p>
     * <p/>
     * </p>
     *
     * @param socket Socket information
     * @param query  String with parameters
     * @return String
     * @throws IOException
     */
    public String sendMessage(Socket socket, String query) throws IOException {
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));

            // Send data
            wr.write(query + "\r\n");
            wr.flush();
            log.info("send Message " + query);
            // Get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            wr.close();
            rd.close();

            String dirty = sb.toString();
            StringBuilder response = new StringBuilder();
            int codePoint;
            int i = 0;
            while (i < dirty.length()) {
                codePoint = dirty.codePointAt(i);
                if ((codePoint == 0x9) ||
                        (codePoint == 0xA) ||
                        (codePoint == 0xD) ||
                        ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                        ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                        ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
                    response.append(Character.toChars(codePoint));
                }
                i += Character.charCount(codePoint);
            }

            return response.toString().replace("\\\n", "").replace("\\\t", "");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * @param taskId
     * @return
     * @throws IOException
     */
    public JSONArray getTask(String taskId) throws IOException {
        JSONArray er = new JSONArray();
        try {
            JSONObject q1 = new JSONObject();
            q1.put("query", "getTask");
            JSONObject params = new JSONObject();
            params.put("name", taskId);
            q1.put("params", params);
            String query = q1.toString();
            log.info(">>>>>" + query);
            String response = sendMessage(prepareSocket("v0376.nbi.ac.uk", 7899), query);
            log.info("\n\n\n<<<<" + response);
            if (!"".equals(response)) {
                JSONArray r = JSONArray.fromObject(response);
                return r;
            }
            er.add(JSONUtils.SimpleJSONError("empty response"));
            return er;
        } catch (IOException e) {
            e.printStackTrace();
            er.add(JSONUtils.SimpleJSONError(e.getMessage()));
            return er;
        }
    }

    /**
     * Return String
     * <p>
     * check State of a taskId got from argument and return
     * </p>
     *
     * @param taskId String
     * @return String with state
     * @throws Exception
     */
    public String getTaskSQL(String taskId) throws Exception {
        String result = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/tgac_browser_conan", "tgacbrowser", "tgac_bioinf");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select STATE from CONAN_TASKS where NAME=\"" + taskId + "\"");
        while (rs.next()) {
            result = rs.getString(1);
        }
        rs.close();
        stmt.close();
        conn.close();
        return result;
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
        String taskId = json.getString("taskid");
        String old_taskId = json.getString("old_taskid");

        try {

            if (getTaskSQL(old_taskId) != null) {
                if (getTaskSQL(old_taskId).equals("COMPLETED") || getTaskSQL(old_taskId).equals("FAILED")) {
                    blastManagerStore.updateDatabase(old_taskId, getTaskSQL(old_taskId).toString());
                    q1.put("stopUpdater", "true");
                }
            }
            q1.put("result", getTaskSQL(old_taskId));
            q1.put("old_id", old_taskId);
            return q1;
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }
}



