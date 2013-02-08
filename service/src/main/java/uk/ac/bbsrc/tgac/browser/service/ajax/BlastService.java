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

  public JSONObject blastSearchSequence(HttpSession session, JSONObject json) throws IOException {
    try {
      String blastDB = json.getString("db");
      String blastAccession = json.getString("accession");
      StringBuilder sb = new StringBuilder();

      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
      log.info(">>>" + fstream);
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
            Pattern pline = Pattern.compile(".*1_.*");
            String seqregionName = id[1];
            String str2 = "";
            if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/TGAC_CHO_v2.0_COMPLETE.fa")) {
              str2 = str1.replaceAll(id[1], " <a target='_blank' href='../vietnamese_rice/index.jsp?query=" + seqregionName + "&blasttrack=" + blastAccession + "'>"
                                            + seqregionName + "</a>");
            }
            else if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/unplaced.scaf.fa")) {
              str2 = str1.replaceAll(id[1], " <a target='_blank' href='../chobgi/index.jsp?query=" + seqregionName + "&blasttrack=" + blastAccession + "'>"
                                            + seqregionName + "</a>");
            }
            else {
              str2 = str1;
            }
            sb.append("<tr> <td> " + str2 + "</td></tr>");
            i++;
          }
        }
      }
      sb.append("</tbody></table");

      in.close();

      String result = null;
      if (i > 0) {
        result = sb.toString();
      }
      else {
        result = "No hits found.";
      }

      return JSONUtils.JSONObjectResponse("html", result);
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

  }

//
//  public JSONObject blastSearchSequence(HttpSession session, JSONObject json) throws IOException {
//    try {
//      String blastDB = json.getString("db");
//      String blastAccession = json.getString("accession");
//      StringBuilder sb = new StringBuilder();
//
//      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");
//      log.info(">>>" + fstream);
//      DataInputStream in = new DataInputStream(fstream);
//      String str;
//      int i = 0;
//      sb.append("<table class='list' id='blasttable'> <thead><tr>  " +
////                "<th class=\"header\"> Query id </th>" +
//                "<th class=\"header\"> Subject id </th> " +
//                "<th class=\"header\"> % identity </th> " +
//                "<th class=\"header\"> alignment length </th> " +
//                "<th class=\"header\"> mismatches </th> " +
//                "<th class=\"header\"> gap openings </th> " +
//                "<th class=\"header\"> q.start </th> " +
//                "<th class=\"header\"> q.end </th> " +
//                "<th class=\"header\"> s.start </th> " +
//                "<th class=\"header\"> s.end </th> " +
//                "<th class=\"header\"> e-value </th> " +
//                "<th class=\"header\"> bit score </th></tr></thead><tbody>");
//      while (null != (str = in.readLine())) {
//
//        Pattern p = Pattern.compile("#");
//        Matcher matcher_comment = p.matcher(str);
//        if (matcher_comment.find()) {
//        }
//        else {
//          Pattern p1 = Pattern.compile("<.*>");
//          Matcher matcher_score = p1.matcher(str);
//          if (matcher_score.find()) {
//          }
//          else {
//            String[] list = str.split("\\s+");
//             sb.append("<tr><td>");
//            if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/TGAC_CHO_v2.0_COMPLETE.fa")) {
//              sb.append(" <a target='_blank' href='../CHO/index.jsp?query=" + list[1] + "&blasttrack="+blastAccession+"'>"
//                                            +  list[1] + "</a>");
//            }
//            else if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/unplaced.scaf.fa")) {
//              sb.append(" <a target='_blank' href='../chobgi/index.jsp?query=" +  list[1] + "&blasttrack="+blastAccession+"'>"
//                                            +  list[1] + "</a>");
//            }
//            else {
//              sb.append(list[1]);
//            }
//            sb.append("</td><td>");
//            sb.append(list[2]);
//            sb.append("</td><td>");
//            sb.append(list[3]);
//            sb.append("</td><td>");
//            sb.append(list[4]);
//            sb.append("</td><td>");
//            sb.append(list[5]);
//            sb.append("</td><td>");
//            sb.append(list[6]);
//            sb.append("</td><td>");
//            sb.append(list[7]);
//            sb.append("</td><td>");
//            sb.append(list[8]);
//            sb.append("</td><td>");
//            sb.append(list[9]);
//            sb.append("</td><td>");
//            sb.append(list[10]);
//            sb.append("</td><td>");
//            sb.append(list[11]);
//            sb.append("</td></tr>");
//            i++;
//          }
//        }
//      }
//      sb.append("</tbody></table");
//
//      in.close();
//
//      String result = null;
//      if (i > 0) {
//        result = sb.toString();
//      }
//      else {
//        result = "No hits found.";
//      }
//
//      return JSONUtils.JSONObjectResponse("html", result);
//    }
//    catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      return JSONUtils.SimpleJSONError(e.getMessage());
//    }
//
//  }

  public JSONObject blastSearchTrack(HttpSession session, JSONObject json) throws IOException {
    String blastAccession = json.getString("accession");

    JSONObject blast_response = new JSONObject();

    String blastDB = json.getString("db");
    int query_start = json.getInt("start");
    int query_end = json.getInt("end");
    int noofhits = json.getInt("hit");
    StringBuilder sb1 = new StringBuilder();

    File fastaTmp = File.createTempFile("blast", ".fa");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Document dom;
//    dom = parser.newDocument();

    try {

      FileInputStream fstream = new FileInputStream("/net/tgac-cfs3/ifs/TGAC/browser/jobs/" + blastAccession + ".xml");

      DataInputStream input = new DataInputStream(fstream);
      int in = 0;
      int findHits = 1;
      JSONArray blasts = new JSONArray();
      DocumentBuilder db = dbf.newDocumentBuilder();

      dom = db.parse(input);
//
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


              if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/TGAC_CHO_v2.0_COMPLETE.fa")) {
                eachBlast.put("desc", " <a target='_blank' href='../CHO/index.jsp?query=" + hit_id + "&blast='>"
                                      + hit_id + "</a>");
              }
              else if (blastDB.equals("/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/unplaced.scaf.fa")) {
                eachBlast.put("desc", " <a target='_blank' href='../chobgi/index.jsp?query=" + hit_id.replaceAll("(gi\\-[0-9]+\\-gb\\-[0-9A-z]+\\.[0-9]\\-).*", "$1") + "&blast='>"
                                      + hit_id + "</a>");
              }
              else {
                eachBlast.put("desc", hit_id);
              }

//              eachBlast.put("desc", "<a href=\"javascript:void(0);\" onclick=\"seqregionSearchPopup(\'" + hit_id + "\')\">"
//                                    + hit_id + "</a>");
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
//                 ins = (newtemp[x].length() + 1);
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

          //get the Employee object

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

  private String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      Element el = (Element) nl.item(0);
      textVal = el.getFirstChild().getNodeValue();
    }

    return textVal;
  }


  public JSONObject submitBlastTask(HttpSession session, JSONObject json) throws IOException {
    try {

      String fasta = json.getString("querystring");
      String accession = json.getString("BlastAccession");

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
//        if (r.has("error")) {
//          String error = r.getString("error");
//        }
        return r;
      }
      return JSONUtils.SimpleJSONError("empty response");
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public static Socket prepareSocket(String host, int port) throws IOException {
    return new Socket(host, port);
  }

  public static String sendMessage(Socket socket, String query) throws IOException {
    try {
      BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));

      // Send data
      wr.write(query + "\r\n");
      wr.flush();

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



