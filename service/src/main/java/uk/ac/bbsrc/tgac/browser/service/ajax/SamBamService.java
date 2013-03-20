package uk.ac.bbsrc.tgac.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 2/1/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SamBamService {

  protected static final Logger log = LoggerFactory.getLogger(SamBamService.class);

  public static JSONArray getSamBam(long start, long end, int delta, String trackId, String referece) {
    JSONArray sam = new JSONArray();
    JSONObject response = new JSONObject();
    try {
      File inputfile = new File(trackId);
      SAMFileReader inputSam = new SAMFileReader(inputfile);

      if (trackId.indexOf("sam") >= 0) {
        inputSam = new SAMFileReader(inputfile);
      }
      else if (trackId.indexOf("bam") >= 0) {
        log.info("bam");
        final File index = new File(trackId + ".bai");
        inputSam = new SAMFileReader(inputfile, index, false);
      }

      List<Integer> ends = new ArrayList<Integer>();
      ends.add(0, 0);


      for (final SAMRecord samRecord : inputSam) {
        int cigar_pos = 0;
        int cigar_len = 0;
        int start_pos = samRecord.getAlignmentStart();
        int end_pos = samRecord.getAlignmentEnd();
        String ref = samRecord.getReferenceName();
        if (ref.equalsIgnoreCase(referece) && (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end)) {
//        if ((start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end)) {
          JSONObject read = new JSONObject();
          JSONObject cigars = new JSONObject();
//          // Convert read name to upper case.
//          read.put("cigar", samRecord.getCigar().toString());
          read.put("start", samRecord.getAlignmentStart());
          read.put("end", samRecord.getAlignmentEnd());
          read.put("desc", samRecord.getReadName().toString());
          read.put("layer", ends.size());
          List<CigarElement> cigarelemts = samRecord.getCigar().getCigarElements();
          for (CigarElement c : cigarelemts) {
            if (cigars.containsKey(c.getOperator().toString())) {
              cigars.put(c.getOperator(), cigars.get(c.getOperator().toString()).toString() + "," + cigar_pos + ":" + c.getLength());
            }
            else {
              cigars.put(c.getOperator(), cigar_pos + ":" + c.getLength());
            }

            if (c.getOperator().toString().equalsIgnoreCase("I") || c.getOperator().toString().equalsIgnoreCase("M") || c.getOperator().toString().equalsIgnoreCase("=") || c.getOperator().toString().equalsIgnoreCase("X")) {
              cigar_pos += c.getLength();
            }
            else if (c.getOperator().toString().equalsIgnoreCase("D") || c.getOperator().toString().equalsIgnoreCase("N")) {
              //                 nothing doing here
            }
            //              soft clip and hard clip not included
          }
          for (int i = 0; i < ends.size(); i++) {
            if (start_pos - ends.get(i) > delta) {
              ends.remove(i);
              ends.add(i, end_pos);
              read.put("layer", i + 1);
              break;
            }
            else if ((start_pos - ends.get(i) < delta) && (i + 1) == ends.size()) {
              if (i == 0) {
                read.put("layer", ends.size());
                ends.add(i, end_pos);
              }
              else {
                read.put("layer", ends.size());
                ends.add(ends.size(), end_pos);
              }
              break;
            }
            else {
              continue;
            }
          }
          read.put("cigars", cigars);
          sam.add(read);
        }
      }
      log.info("size " + sam.size());
      return sam;
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      response.put("error", e.toString());
      sam.add(response);
      return sam;
    }
  }

  public static JSONArray getWig(long start, long end, int delta, String trackId, String referece) throws Exception {
    JSONArray bed = new JSONArray();
    boolean found = false;
    log.info("wig");
    try {
      File inputfile = new File(trackId);

      BufferedReader br = null;


      String sCurrentLine;

      br = new BufferedReader(new FileReader(inputfile));

      while ((sCurrentLine = br.readLine()) != null) {
        String[] line = sCurrentLine.split("\t");
        JSONObject response = new JSONObject();
        if ((sCurrentLine.contains("chr"))) {
          if (sCurrentLine.contains("MAL11")) {
            log.info(sCurrentLine+": found");
            found = true;
          }
          else {
            log.info(sCurrentLine);
            found = false;
          }
        }
        else if (found == true && line.length == 2 && (Integer.parseInt(line[0].toString()) >= start && Integer.parseInt(line[0].toString()) <= end)) {
          response.put("start", line[0]);
          response.put("value", line[1]);
          bed.add(response);

        }
        else if (found == true){
//          log.info(line.length + ":" + sCurrentLine);
        }
        else {

        }
      }
      Object[] myArray = bed.toArray();

      JSONArray sortedJArray = new JSONArray();
      for (Object obj : myArray) {
        sortedJArray.add(obj);
      }
      return bed;
    }
    catch (Exception e) {
      JSONObject response = new JSONObject();
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      response.put("error", e.toString());
      bed.add(response);
      return bed;
    }
  }

}
