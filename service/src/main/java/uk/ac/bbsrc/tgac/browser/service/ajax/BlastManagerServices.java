package uk.ac.bbsrc.tgac.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.jackson.JsonParser;
import org.jfree.base.log.DefaultLog;
//import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 7/8/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlastManagerServices {


  private Logger log = LoggerFactory.getLogger(getClass());


  protected static boolean checkDatabase(String query, String db, String link, String type, String filter, String format) throws Exception {
    boolean check = false;

    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id_blast from blast_params where blast_db=\"" + db + "\" AND blast_seq = \"" + query + "\" AND blast_filter = \"" + filter + "\" AND blast_type = \"" + type + "\" AND link = \"" + link + "\" AND output_format = \"" + format + "\"");

    while (rs.next()) {
      check = true;
    }
    rs.close();
    stmt.close();
    conn.close();
    return check;
  }

  protected static boolean checkResultDatabase(String query) throws Exception {
    boolean check = false;

    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from blast_result where id_blast=\"" + query + "\" ");

    while (rs.next()) {
      check = true;
    }
    rs.close();
    stmt.close();
    conn.close();
    return check;
  }

  public static String getSeq(String id) throws ClassNotFoundException {
    String fasta = "";
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select blast_seq from blast_params where id_blast=\"" + id + "\"");

      while (rs.next()) {
        fasta += rs.getString("blast_seq");
      }

      rs.close();
      stmt.close();
      conn.close();
    }
    catch (InstantiationException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    catch (SQLException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    return fasta;
  }


  protected static String getIDFromDatabase(String query, String db, String link, String type, String filter, String format) throws Exception {
    String id = "";
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id_blast from blast_params where blast_db=\"" + db + "\" AND blast_seq = \"" + query + "\" AND blast_filter = \"" + filter + "\" AND blast_type = \"" + type + "\" AND link = \"" + link + "\"AND output_format = \"" + format + "\"");
    while (rs.next()) {
      id = rs.getString("id_blast");
    }
    rs.close();
    stmt.close();
    conn.close();
    return id;
  }


  protected static void setResultToDatabase(String blastAccession, JSONArray result) throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    stmt.execute("insert into blast_result values (\"" + blastAccession + "\",'" + result + "')");
    stmt.close();
    conn.close();
  }

  protected static void updateDatabase(String taskId, String status) throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    stmt.executeUpdate("update blast_status set status = \"" + status + "\" where id_blast = \"" + taskId + "\"");

    stmt.close();
    conn.close();
  }

  protected static JSONArray getFromDatabase(String id, String link) throws Exception {
    JSONArray blasts = new JSONArray();
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from blast_result where id_blast=\"" + id + "\"");

    while (rs.next()) {
      JSONObject eachBlast = new JSONObject();
      String newJSONString = rs.getString("result_json").toString();
      JSONObject jsnobject = new JSONObject();
      jsnobject.put("result", newJSONString);

      JSONArray jsonArray = JSONArray.fromObject(newJSONString);

      if (rs.getString("result_json").toString().indexOf("No hits found") < 0) {
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONObject explrObject = jsonArray.getJSONObject(i);
          eachBlast.put("q_id", explrObject.get("q_id"));
          eachBlast.put("s_id", explrObject.get("s_id"));
          eachBlast.put("identity", explrObject.get("identity"));
          eachBlast.put("aln_length", explrObject.get("aln_length"));
          eachBlast.put("mismatch", explrObject.get("mismatch"));
          eachBlast.put("gap_open", explrObject.get("gap_open"));
          eachBlast.put("q_start", explrObject.get("q_start"));
          eachBlast.put("q_end", explrObject.get("q_end"));
          eachBlast.put("s_start", explrObject.get("s_start"));
          eachBlast.put("s_end", explrObject.get("s_end"));
          eachBlast.put("e_value", explrObject.get("e_value"));
          eachBlast.put("bit_score", explrObject.get("bit_score"));
          eachBlast.put("s_db", explrObject.get("s_db"));
          blasts.add(eachBlast);
        }
      }
      else {
        blasts.add(jsonArray);

      }
    }
    rs.close();
    stmt.close();
    conn.close();
    return blasts;
  }

  protected static JSONArray getBLASTEntryFromDatabase(String id) throws Exception {
    JSONArray blasts = new JSONArray();
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from blast_result where id_blast=\"" + id + "\"");

    while (rs.next()) {
      JSONObject eachBlast = new JSONObject();
      String newJSONString = rs.getString("result_json").toString();
      JSONObject jsnobject = new JSONObject();
      jsnobject.put("result", newJSONString);

      JSONArray jsonArray = JSONArray.fromObject(newJSONString);

      if (rs.getString("result_json").toString().indexOf("No hits found") < 0) {
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONObject explrObject = jsonArray.getJSONObject(i);
          eachBlast.put("start", explrObject.get("s_start"));
          eachBlast.put("end", explrObject.get("s_end"));
          eachBlast.put("desc", explrObject.get("s_id"));
          eachBlast.put("flag", 0);
          eachBlast.put("reverse", " ");
          eachBlast.put("indels", " ");
          eachBlast.put("score", " ");
          blasts.add(eachBlast);
        }
      }
      else {
        blasts.add(jsonArray);

      }
    }
    rs.close();
    stmt.close();
    conn.close();
    return blasts;
  }

  protected static JSONArray getTrackFromDatabase(String id) throws Exception {
    JSONArray blasts = new JSONArray();
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from blast_result where id_blast=\"" + id + "\"");

    while (rs.next()) {
      JSONObject eachBlast = new JSONObject();
      String newJSONString = rs.getString("result_json").toString();
      JSONObject jsnobject = new JSONObject();
      jsnobject.put("result", newJSONString);

      JSONArray jsonArray = JSONArray.fromObject(newJSONString);

      if (rs.getString("result_json").toString().indexOf("No hits found") < 0) {
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONObject explrObject = jsonArray.getJSONObject(i);
          eachBlast.put("start", explrObject.get("start"));
          eachBlast.put("end", explrObject.get("end"));
          eachBlast.put("desc", explrObject.get("desc"));
          eachBlast.put("flag", explrObject.get("flag"));
          eachBlast.put("reverse", explrObject.get("reverse"));
          eachBlast.put("indels", explrObject.get("indels"));
          eachBlast.put("score", explrObject.get("score"));
          blasts.add(eachBlast);
        }
      }
      else {
        blasts.add(jsonArray);

      }
    }
    rs.close();
    stmt.close();
    conn.close();
    return blasts;
  }

  protected static void insertintoDatabase(String taskId, String query, String db, String link, String type, String filter, String format) throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mysql://n78048.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
    Statement stmt = conn.createStatement();
    stmt.execute("insert into blast_status values (\"" + taskId + "\",\"RUNNING\")");
    System.out.println("insert into blast_params values (\"" + taskId + "\",\"" + db + "\",query,\"" +  filter + "\",\"" + type + "\",\"" + link + "\",\"" + format + "\"");
    stmt.execute("insert into blast_params values (\"" + taskId + "\",\"" + db + "\",\"" + query + "\",\"" + filter + "\",\"" + type + "\",\"" + link + "\",\"" + format + "\")");
    stmt.close();
    conn.close();
  }

}
