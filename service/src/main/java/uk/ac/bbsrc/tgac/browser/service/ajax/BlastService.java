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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.codec.Base64;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.lang.*;

import org.w3c.dom.*;
import org.xml.sax.SAXParseException;

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
      StringBuilder sb = new StringBuilder();

      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
      DataInputStream in = new DataInputStream(fstream);
      String str;
      int i = 0;
      sb.append("<table class='list' id='blasttable'> <thead><tr>  " +
                "<th class=\"header\"> Query id </th>" +
                "<th class=\"header\"> Subject id </th> " +
                "<th class=\"header\"> % identity </th> " +
                "<th class=\"header\"> alignment length </th> " +
                "<th class=\"header\"> mismatches </th> " +
                "<th class=\"header\"> gap openings </th> " +
                "<th class=\"header\"> q.start </th> " +
                "<th class=\"header\"> q.end </th> " +
                "<th class=\"header\"> s.start </th> " +
                "<th class=\"header\"> s.end </th> " +
                "<th class=\"header\"> e-value </th> " +
                "<th class=\"header\"> bit score </th></tr></thead><tbody>");
      while (null != (str = in.readLine())) {
        JSONObject eachBlast = new JSONObject();


        Pattern p = Pattern.compile("#");
        Matcher matcher_comment = p.matcher(str);
        if (matcher_comment.find()) {
        }
        else {
          Pattern p1 = Pattern.compile("<.*>");
          Matcher matcher_score = p1.matcher(str);
          if (matcher_score.find()) {
          }
          else {
            String str1 = str.replaceAll("\\s+", "<td>");
            String[] id;
            id = str1.split("<td>");
            String seqregionName = id[1];
            String hsp_from = id[8];
            String hsp_to = id[9];

            eachBlast.put("q_id", id[0]);
            if (location.length() > 0) {
              eachBlast.put("s_id", "<a target='_blank' href='../" + location + "/index.jsp?query=" + seqregionName + "&from=" + hsp_from + "&to=" + hsp_to + "&blasttrack=" + blastAccession + "'>"
                                    + seqregionName + "</a>");
            }
            else {
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
      sb.append("</tbody></table");

      in.close();

      String result = null;
      if (i > 0) {
      }
      else {
        blasts.add("No hits found.");
      }

      html.put("html", blasts);
      return html;
    }
    catch (Exception e) {
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

    JSONObject blast_response = new JSONObject();

    int query_start = json.getInt("start");
    int query_end = json.getInt("end");
    int noofhits = json.getInt("hit");
    String location = json.getString("location");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Document dom;

    try {

      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
      DataInputStream input = new DataInputStream(fstream);
      int in = 0;
      int findHits = 1;
      JSONArray blasts = new JSONArray();
      DocumentBuilder db = dbf.newDocumentBuilder();

//      parse file for DOMs
      dom = db.parse(input);
      Element docEle = dom.getDocumentElement();
//      get nodes for Hit
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

              if (location.length() > 0) {
                eachBlast.put("desc", " <a target='_blank' href='../" + location + "/index.jsp?query=" + hit_id + "&from=" + hsp_from + "&to=" + hsp_to + "'>"
                                      + hit_id + "</a>");
              }
              else {
                eachBlast.put("desc", hit_id);
              }

              eachBlast.put("score", hsp_score);
              eachBlast.put("flag", false);
              eachBlast.put("reverse", "");

              String hsp_midline = getTextValue(ell, "Hsp_midline");
//              if indels present
              if (hsp_midline.split(" ").length > 1) {
                String hsp_query_seq = getTextValue(ell, "Hsp_qseq");
                String hsp_hit_seq = getTextValue(ell, "Hsp_hseq");
                String[] newtemp = hsp_midline.split(" ");
                int ins = 0;
                for (int x = 0; x < newtemp.length - 1; x++) {

                  ins = ins + ((newtemp[x].length() + 1));
                  eachIndel.put("position", ins + in);
//                  put 3 bases before and after of indel if starting or ending position then most possible
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
      }

      else {
        blasts.add("No hits found.");
      }
      blast_response.put("blast", blasts);
      return blast_response; //JSONUtils.JSONObjectResponse("blast", result);

    }
    catch (SAXParseException sax) {
      throw new RuntimeException(sax);
    }
    catch (Exception err) {
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
      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
      DataInputStream in = new DataInputStream(fstream);
      String str;
      while (null != (str = in.readLine())) {

        Pattern p = Pattern.compile("#");
        Matcher matcher_comment = p.matcher(str);
        if (matcher_comment.find()) {
        }
        else {
          Pattern p1 = Pattern.compile("<.*>");
          Matcher matcher_score = p1.matcher(str);
          if (matcher_score.find()) {
          }
          else {
            String[] list = str.split("\\s+");
            if (list[1].equals(seqRegion)) {
              JSONObject jb = new JSONObject();
              if (Long.parseLong(list[8]) < Long.parseLong(list[9])) {
                jb.put("start", list[8]);
                jb.put("end", list[9]);
              }
              else {
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
      jsonObject.put("entries", jsonArray);
      return jsonObject;
    }
    catch (Exception e) {
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
      log.info("submitted" + accession);
      File file = new File("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + accession + ".fa");
      FileWriter writer = new FileWriter(file, true);
      PrintWriter output = new PrintWriter(writer);
      output.print(fasta);
      output.close();

      JSONObject task = new JSONObject();

      JSONObject j = new JSONObject();

      if (json.has("priority")) {
        j.put("priority", json.get("priority"));
      }
      else {
        j.put("priority", "MEDIUM");
      }

      j.put("pipeline", "browser_blast");//json.get("pipeline"));
      json.remove("querystring");
      j.put("params", json);


      task.put("submit", j);

      String response = sendMessage(prepareSocket("norwich.nbi.bbsrc.ac.uk", 7899), task.toString());
      if (!"".equals(response)) {
        JSONObject r = JSONObject.fromObject(response);
        return r;
      }
      return JSONUtils.SimpleJSONError("empty response");
    }
    catch (IOException e) {
      e.printStackTrace();
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
    }
    catch (UnknownHostException e) {
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
      String response = sendMessage(prepareSocket("norwich.nbi.bbsrc.ac.uk", 7899), query);
      log.info("\n\n\n<<<<" + response);
      if (!"".equals(response)) {
        JSONArray r = JSONArray.fromObject(response);
        return r;
      }
      er.add(JSONUtils.SimpleJSONError("empty response"));
      return er;
    }
    catch (IOException e) {
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
    Connection conn = DriverManager.getConnection("jdbc:mysql://london.nbi.bbsrc.ac.uk:3306/conan", "conan", "conan");
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
    try {
      q1.put("result", getTaskSQL(taskId));
      if (getTaskSQL(taskId) != null) {
        if (getTaskSQL(taskId).equals("COMPLETED") || getTaskSQL(taskId).equals("FAILED")) {
          q1.put("stopUpdater", "true");
        }
      }
      return q1;
    }
    catch (Exception e) {
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }
}



