package uk.ac.bbsrc.tgac.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.SAMFileReader;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Random;

import net.sf.samtools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 07/03/12
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class FileService {
  protected static final Logger log = LoggerFactory.getLogger(FileService.class);

  /**
   * Return JSONObject
   * <p>
   *   Create a file from file context received in JSONObject and return a link
   * </p>
   * @param session  an HTTPSession comes from ajax call
   * @param json  json object with key parameters sent from ajax call
   * @return JSONObject with file link
   */
  public JSONObject exportFile(HttpSession session, JSONObject json) {

    Random generator = new Random();
    String fileContent = json.getString("filecontent");
    JSONObject response = new JSONObject();
    String location = json.getString("location");

    try {
      int i = generator.nextInt();
      BufferedWriter out = new BufferedWriter(new FileWriter("../webapps/" + location + "/temp/" + i + ".gff"));
      out.write(fileContent);
      out.close();
      response.put("link", "temp/" + i + ".gff");
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

  }

  /**
   * Return JSONObject
   * <p>
   *   creates a file for the session attributes received from JSONOject in ajax call
   * </p>
   * @param session an HTTPSession comes from ajax call
   * @param json json object with key parameters sent from ajax call
   * @return JSONObject with file content
   */
  public JSONObject saveFile(HttpSession session, JSONObject json) {
    String reference = json.getString("reference");
    String sessionid = json.getString("session");
    String track = json.getString("track");
    String seqlength = json.getString("seqlen");
    String from = json.getString("from");
    String to = json.getString("to");
    String seq = json.getString("seq");
    String tracks = json.getString("tracks");
    String edited_tracks = json.getString("edited_tracks");
    String removed_tracks = json.getString("removed_tracks");

    String filename = json.getString("filename");
    String location = json.getString("location");
    String blast = json.getString("blast");
    log.info(tracks);

    JSONObject response = new JSONObject();
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter("../webapps/" + location + "/temp/" + filename + ".json"));
      response.put("reference", reference);
      response.put("session", sessionid);
      response.put("from", from);
      response.put("to", to);
      response.put("seqlen", seqlength);
      response.put("seq", seq);
      response.put("tracklist", track);
      response.put("tracks", tracks);
      response.put("edited_tracks",edited_tracks);
      response.put("removed_tracks",removed_tracks);
      response.put("blast", blast);
      out.write(response.toString());
      out.close();
      response.put("link", "temp/" + filename + ".json");
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

  }

  /**
   * Return JSONObject
   * <p>
   *    loads file from the id received from ajax call
   * </p>
   * @param session an HTTPSession comes from ajax call
   * @param json  json object with key parameters sent from ajax call
   * @return JSONObject with session key and values
   */
  public String loadSession(HttpSession session, JSONObject json) {
    String query = json.getString("query");
//    JSONObject response = new JSONObject();
    String location = json.getString("location");
    try {
      BufferedReader br = new BufferedReader(new FileReader("../webapps/" + location + "/temp/" + query + ".json"));
      String response = "";
      String line;
      while ((line = br.readLine()) != null) {
        response += line;
      }
      br.close();
      return response;
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return e.toString();
    }

  }

  /**
   * Return JSONObject
   * <p>
   *   creates a FASTA file data received from ajax call
   * </p>
   * @param session an HTTPSession comes from ajax call
   * @param json json object with key parameters sent from ajax call
   * @return JSONObject with link to FASTA file
   */
  public JSONObject saveFasta(HttpSession session, JSONObject json) {
    String reference = json.getString("reference");
    String sequence = json.getString("sequence");
    String start = json.getString("from");
    String end = json.getString("to");
    String location = json.getString("location");
    JSONObject response = new JSONObject();
    Random generator = new Random();
    try {
      int i = generator.nextInt();
      BufferedWriter out = new BufferedWriter(new FileWriter("../webapps/" + location + "/temp/" + reference + "" + i + ".fasta"));
      if (sequence.length() < 70) {
        out.write(">" + reference + ":" + start + "-" + end + "\n" + sequence);
      }
      else {
        out.write(">" + reference + ":" + start + "-" + end + "\n" + fastaFormat(sequence));
      }
      out.close();
      response.put("link", "../" + location + "/temp/" + reference + "" + i + ".fasta");
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      response.put("error", e.toString());
      return response;
    }

  }

  /**
   * Returns string
   * <p>
   *   formats FASTA sequence each row of 70
   * </p>
   * @param sequence String sequence
   * @return String formatted FASTA sequence
   */
  protected String fastaFormat(String sequence) {
    int start = 0;
    int end = 69;
    String oldString = sequence;
    String newString = "";

    while (oldString.length() > 70) {
      newString = newString + '\n' + oldString.substring(start, end);
      oldString = oldString.substring(end, oldString.length());
    }
    return newString + '\n' + oldString;
  }

}

