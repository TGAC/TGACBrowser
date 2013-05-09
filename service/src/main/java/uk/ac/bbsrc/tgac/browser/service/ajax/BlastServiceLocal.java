package uk.ac.bbsrc.tgac.browser.service.ajax;

import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
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
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Ajaxified

public class BlastServiceLocal {
  private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

  public JSONObject blastSearchSequence(HttpSession session, JSONObject json) throws IOException {
    try {
      log.info("here" + json.toString());
      String blastdb = json.getString("blastdb");
      StringBuilder sb = new StringBuilder();
      String fasta = json.getString("query");
      String location = json.getString("location");
      String blastAccession = json.getString("BlastAccession");

      String file = "../webapps/" + location + "/temp/" + json.getString("BlastAccession") + ".xml";

//      link     location etc
//      multiple blast ??


      String type = json.getString("type");
      String blastBinary = json.getString("blastBinary");
      String blastDB = "/storage/blastdb/choblastdb/TGAC_CHO_v1.2_COMPLETE.fa";
      File fastaTmp = File.createTempFile("blast", ".fa");
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      Document dom;

      PrintWriter out = new PrintWriter(fastaTmp);
      out.println(fasta);
      out.flush();
      out.close();


      String execBlast = blastBinary + "/" + type + " -db " + blastdb + " -query " + fastaTmp + "  -outfmt 6 -out " + file + " -task blastn";

      log.info(execBlast);

      Process proc = Runtime.getRuntime().exec(execBlast);
      proc.waitFor();
      FileInputStream fstream = new FileInputStream(file);

//// get its output (your input) stream
      String str = "running";
//      while (str == "running") {
//        str = checkFile(file);
//        if (str == "finished") {
////          log.info(parseNCBIXML(urlParameters, query_start, query_end, noofhits, location).toString());
////          blasts = parseNCBIXML(urlParameters, query_start, query_end, noofhits, location);
//          break;
//        }
//      }

      DataInputStream input = new DataInputStream(fstream);

//      DataInputStream input = new DataInputStream(connection.getInputStream());
//      String str;
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
      while (null != (str = input.readLine())) {

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
            String hsp_from = id[8];
            String hsp_to = id[9];
            String str2 = "";
            if (location.length() > 0) {
              str2 = str1.replaceAll(id[1], " <a target='_blank' href='../" + location + "/index.jsp?query=" + seqregionName + "&from=" + hsp_from + "&to=" + hsp_to + "&blasttrack=" + blastAccession + "'>"
                                            + seqregionName + "</a>");
            }
            else {
              str2 = str1;
            }
//            String str2 = str1.replaceAll(id[1], "<a href=\"javascript:void(0);\" onclick=\"seqregionSearch(\'" + seqregionName + "\')\">"
//                                                 + seqregionName + "</a>");
            sb.append("<tr> <td> " + str2 + "</td></tr>");
            i++;
          }
        }
      }
      sb.append("</tbody></table");

      input.close();

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

  ////check file if exist
//  private String checkFile(String file) {
//
//      try {
//
//          String str = "";
//
//        if (new File(file).exists()) {
//          log.info("running");
//          str = "running";
//        }
//        else {
//          log.info("finished");
//          str = "finished";
//        }
//        return str;
//      }
//      catch (Exception e) {
//        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        String sb = e.toString();
//        return sb;
//      }
//    }
//
//
  public JSONObject blastSearchTrack(HttpSession session, JSONObject json) throws IOException {
    log.info(json.toString());

//    String blastdb = json.getString("blastdb");
    String fasta = json.getString("query");
//    String blastBinary = "/opt/blast+/blastn";
    String blastBinary = json.getString("blastBinary");

    String blastDB = "/storage/blastdb/choblastdb/TGAC_CHO_v1.2_COMPLETE.fa";

    String blastdb = json.getString("blastdb");
    int query_start = json.getInt("start");
    int query_end = json.getInt("end");
    int noofhits = json.getInt("hit");
    String location = json.getString("location");
    String type = json.getString("type");
    String file = "../webapps/" + location + "/temp/" + json.getString("BlastAccession") + ".xml";
    StringBuilder sb1 = new StringBuilder();

    JSONObject blast_response = new JSONObject();

//    int query_start = json.getInt("start");
//    int query_end = json.getInt("end");
//    int noofhits = json.getInt("hit");
//    StringBuilder sb1 = new StringBuilder();

    File fastaTmp = File.createTempFile("blast", ".fa");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Document dom;
//    dom = parser.newDocument();

    try {
      PrintWriter out = new PrintWriter(fastaTmp);
      out.println(fasta.replaceAll("[0-9 \t\n\r]", ""));
      out.flush();
      out.close();

      String execBlast = blastBinary + "/" + type + " -db " + blastdb + " -query " + fastaTmp + " -out " + file + " -outfmt 5 -max_target_seqs 10";
      log.info("execBlast " + execBlast);
      Process proc = Runtime.getRuntime().exec(execBlast);

      log.info("\n\n\nrun time" + Runtime.getRuntime());
// get its output (your input) stream
      proc.waitFor();
//      DataInputStream input = new DataInputStream(
//                  proc.getInputStream());

      FileInputStream fstream = new FileInputStream(file);
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
      blast_response.put("id", json.getString("BlastAccession"));
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

  private String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      Element el = (Element) nl.item(0);
      textVal = el.getFirstChild().getNodeValue();
    }

    return textVal;
  }


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

//            log.info(parseNCBIXML(urlParameters, query_start, query_end, noofhits, location).toString());
          blasts = parseFileXML(file, query_start, query_end, noofhits, link, location);
          break;
        }

      }
      blast_response.put("id", json.getString("BlastAccession"));
      blast_response.put("blast", blasts);
      return blast_response;
    }
    catch (Exception e) {
      throw new IOException(e);  //To change body of catch statement use File | Settings | File Templates.
    }

  }


  private boolean fileExist(String file) throws IOException {
    log.info("file exists " + file);
    try {
      File f = new File(file);
      boolean check1 = f.exists();
      log.info("check 1" + check1);

      boolean isExist = new File(file).exists();
      log.info("file exist" + isExist);
      return isExist;
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

  private JSONArray parseFileXML(String file, int query_start, int query_end, int noofhits, String link, String location) {
    try {

      log.info("parsefileXML");
      log.info(file + ":" + query_start);

      FileInputStream fstream = new FileInputStream(file);
      DataInputStream input = new DataInputStream(fstream);
      int in = 0;
      int findHits = 1;
      JSONArray blasts = new JSONArray();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      Document dom;
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
//              eachBlast.put("desc", "<a href=\"javascript:void(0);\" onclick=\"seqregionSearchPopup(\'" + hit_id + "\')\">"
//                                    + hit_id + "</a>");

              if (link.length() > 0) {
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
          else {
            blasts.add("No hits found.");
          }
        }
      }
      return blasts;
    }
    catch (Exception e) {
      JSONArray er = new JSONArray();
      e.printStackTrace();
      er.add(JSONUtils.SimpleJSONError(e.getMessage()));
      return er;
    }

  }
}
