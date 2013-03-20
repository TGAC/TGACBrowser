package uk.ac.bbsrc.tgac.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.*;
import net.sf.samtools.util.SeekableStream;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.browser.core.store.SequenceStore;
import uk.ac.bbsrc.tgac.browser.service.ajax.FileService;
import uk.ac.bbsrc.tgac.browser.service.ajax.SamBamService;


import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 10:52:16
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class DnaSequenceService {
  protected static final Logger log = LoggerFactory.getLogger(DnaSequenceService.class);
  @Autowired
  private SequenceStore sequenceStore;
//  private SamBamService sambamservice;

  /* user the store interface */

  public void setSequenceStore(SequenceStore sequenceStore) {
    this.sequenceStore = sequenceStore;
  }

  public JSONObject searchSequence(HttpSession session, JSONObject json) {
    String seqName = json.getString("query");
    JSONObject response = new JSONObject();
    JSONArray tracks = new JSONArray();
    try {
      Integer queryid = sequenceStore.getSeqRegionearchsize(seqName);
      if (queryid > 1) {
        response.put("html", "seqregion");
        response.put("seqregion", sequenceStore.getSeqRegionearch(seqName));
      }
      else if (queryid == 0) {
        response.put("html", "gene");
        response.put("gene", sequenceStore.getGenesSearch(seqName));
        response.put("transcript", sequenceStore.getTranscriptSearch(seqName));
        response.put("GO", sequenceStore.getGOSearch(seqName));
      }
      else {
        Integer query = sequenceStore.getSeqRegion(seqName);
        String seqRegName = sequenceStore.getSeqRegionName(query);
        String seqlength = sequenceStore.getSeqLengthbyId(query);
        response.put("seqlength", seqlength);
        response.put("html", "");
        response.put("seqname", "<p> <b>Seq Region ID:</b> " + queryid + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
        response.put("seqregname", seqRegName);
        response.put("tracklists", sequenceStore.getAnnotationId(queryid));
      }
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

  }

  public JSONObject seqregionSearchSequence(HttpSession session, JSONObject json) throws IOException {
    try {
      JSONObject response = new JSONObject();
      JSONArray tracks = new JSONArray();
      String seqName = json.getString("query");
      Integer query = sequenceStore.getSeqRegionforone(seqName);
      String seqRegName = sequenceStore.getSeqRegionName(query);
      String seqlength = sequenceStore.getSeqLengthbyId(query);

      Integer queryid_coord_system_id = sequenceStore.getSeqRegionCoordId(seqName);

      response.put("seqlength", seqlength);
      response.put("html", "");
      response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
      response.put("seqregname", seqRegName);
      response.put("tracklists", sequenceStore.getAnnotationId(query));

      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

  }

//  public JSONObject seqregionSearchSequence(HttpSession session, JSONObject json) throws IOException {
//    try {
//
//      JSONObject response = new JSONObject();
//      JSONArray tracks = new JSONArray();
//      String query = json.getString("query");
//
//     String dna  = "";
//      int seqRegion = sequenceStore.getSeqRegion(query);
//      if(seqRegion ==0){
//           response.put("html", "gene");
////           sequenceStore.getGeneSearch
//      } else{
//         dna = sequenceStore.getSeqBySeqRegionId(seqRegion);
//        response.put("html", dna);
//      response.put("seqname", "<p> <b>Seq Region ID:</b> " + sequenceStore.getSeqRegion(query) + ",<b> Name: </b> " + query);//+", <b>cds:</b> "+cds+"</p>");
//      response.put("seqregname", query);
//      response.put("tracklists", sequenceStore.getAnnotationId());
//      }
//
//
//
//
//
//      return response; //JSONUtils.JSONObjectResponse("html", dna);
//    }
//    catch (IOException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      return JSONUtils.SimpleJSONError(e.getMessage());
//    }
//
//  }


  public JSONObject loadTrack(HttpSession session, JSONObject json) {
    String seqName = json.getString("query");
    JSONObject response = new JSONObject();
    String trackName = json.getString("name");
    String trackId = json.getString("trackid");
    long start = json.getInt("start");
    long end = json.getInt("end");
    int delta = json.getInt("delta");
    response.put("name", trackName);
    log.info(trackName);
    log.info(trackId);
    try {
      Integer queryid = sequenceStore.getSeqRegion(seqName);
      if (trackId.contains("sam") || trackId.contains("bam")) {
        response.put(trackName, SamBamService.getSamBam(start, end, delta, trackId, seqName));
      }
      else if (trackId.contains("wig")) {
        response.put(trackName, SamBamService.getWig(start, end, delta, trackId, seqName));
      }
      else if (trackId.indexOf("cs") >= 0) {
//        graph code
        response.put(trackName, sequenceStore.getAssembly(queryid, trackId, delta));
      }
      else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*repeat.*")) {
        log.info("repeat" + trackName);
        if (sequenceStore.countRepeat(queryid, trackId, start, end) < 5000) {
          response.put(trackName, sequenceStore.processRepeat(sequenceStore.getRepeat(queryid, trackId, start, end), start, end, delta, queryid, trackId));
        }
        else {
          response.put("type", "graph");
          response.put(trackName, sequenceStore.getRepeatGraph(queryid, trackId, start, end));
        }
      }
      else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*gene.*")) {
        log.info("gene" + trackName);
        if (sequenceStore.countGene(queryid, trackId, start, end) < 5000) {
          log.info("gene" + sequenceStore.countGene(queryid, trackId, start, end));
          response.put(trackName, sequenceStore.processGenes(sequenceStore.getGenes(queryid, trackId), start, end, delta, queryid, trackId));
        }
        else {
          response.put("type", "graph");
          response.put(trackName, sequenceStore.getGeneGraph(queryid, trackId, start, end));
        }
      }
      else {
        log.info("hit" + trackName);
        if (sequenceStore.countHit(queryid, trackId, start, end) < 5000) {
          response.put(trackName, sequenceStore.processHit(sequenceStore.getHit(queryid, trackId, start, end), start, end, delta, queryid, trackId));
        }
        else {
          response.put("type", "graph");
          response.put(trackName, sequenceStore.getHitGraph(queryid, trackId, start, end));
        }
      }

    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    return response;
  }

  public JSONObject metaInfo(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    try {
      response.put("metainfo", sequenceStore.getdbinfo());
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject loadDomains(HttpSession session, JSONObject json) {
    String geneid = json.getString("geneid");
    JSONObject response = new JSONObject();
    try {
      response.put("domains", sequenceStore.getDomains(geneid));
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject loadTranscriptName(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    int query = json.getInt("id");
    try {
      response.put("name", sequenceStore.getTranscriptNamefromId(query));
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject loadTrackName(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    String trackName = json.getString("track");
    int query = json.getInt("id");
    try {
      String trackid = sequenceStore.getTrackIDfromName(trackName);
      if (trackid.indexOf("cs") >= 0) {
        response.put("name", sequenceStore.getSeqRegionName(query));
      }
      else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackid)).matches("(?i).*gene.*")) {
        response.put("name", sequenceStore.getGeneNamefromId(query));
      }
      else {
        response.put("name", sequenceStore.getHitNamefromId(query));
      }
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject loadSequence(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    String query = json.getString("query");
    int from = json.getInt("from");
    int to = json.getInt("to");
    try {
      String queryid = sequenceStore.getSeqRegion(query).toString();
      if (from <= to) {
        response.put("seq", sequenceStore.getSeq(queryid, from, to));
      }
      else {
        response.put("seq", sequenceStore.getSeq(queryid, to, from));
      }

      return response;
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

//  public JSONArray getSamBam(long start, long end, int delta) {
//      JSONArray sam = new JSONArray();
//      JSONObject response = new JSONObject();
//      try {
//        final File inputfile = new File("../webapps/vietnamese_rice/temp/temp.sam");
////        final File inputfile = new File("../webapps/vietnamese_rice/temp/Pla.bam");
////        final File index = new File("../webapps/vietnamese_rice/temp/Pla.bam.bai");
//        final SAMFileReader inputSam;
//        inputSam = new SAMFileReader(inputfile);//, index, false);
//
//        List<Integer> ends = new ArrayList<Integer>();
//              ends.add(0, 0);
////
//        for (final SAMRecord samRecord : inputSam) {
//          int cigar_pos = 0;
//          int cigar_len = 0;
//          int start_pos = samRecord.getAlignmentStart();
//          int end_pos = samRecord.getAlignmentEnd();
//
//          if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
//            JSONObject read = new JSONObject();
//            JSONObject cigars = new JSONObject();
//            // Convert read name to upper case.
//            read.put("cigar", samRecord.getCigar().toString());
//            read.put("start", samRecord.getAlignmentStart());
//            read.put("end", samRecord.getAlignmentEnd());
//            read.put("desc", samRecord.getReadName().toString());
//            read.put("layer", ends.size());
//            //        read.put("seq", samRecord.getReadString().toString());
//            List<CigarElement> cigarelemts = samRecord.getCigar().getCigarElements();
//            for (CigarElement c : cigarelemts) {
//
//              if (cigars.containsKey(c.getOperator().toString())) {
//                cigars.put(c.getOperator(), cigars.get(c.getOperator().toString()).toString() + "," +cigar_pos+":"+c.getLength());
//              }
//              else {
//                cigars.put(c.getOperator(), cigar_pos+":"+c.getLength());
//              }
//
//              if(c.getOperator().toString().equalsIgnoreCase("I") || c.getOperator().toString().equalsIgnoreCase("M") || c.getOperator().toString().equalsIgnoreCase("=") || c.getOperator().toString().equalsIgnoreCase("X")){
//                cigar_pos +=  c.getLength();
//              } else if(c.getOperator().toString().equalsIgnoreCase("D") || c.getOperator().toString().equalsIgnoreCase("N")){
////                 nothing doing here
//              }
////              soft clip and hard clip not included
//            }
//            for (int i = 0; i < ends.size(); i++) {
//                if (start_pos - ends.get(i) > delta) {
//                  ends.remove(i);
//                  ends.add(i, end_pos);
//                  read.put("layer", i + 1);
//                  break;
//                }
//                else if ((start_pos - ends.get(i) < delta) && (i + 1) == ends.size()) {
//                  if (i == 0) {
//                    read.put("layer", ends.size());
//                    ends.add(i, end_pos);
//                  }
//                  else {
//                    read.put("layer", ends.size());
//                    ends.add(ends.size(), end_pos);
//                  }
//                  break;
//                }
//                else {
//                   continue;
//                }
//              }
//            read.put("cigars", cigars);
//            sam.add(read);
//          }
//        }
//
//        return sam;
//      }
//      catch (Exception e) {
//        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        response.put("error", e.toString());
//        sam.add(response);
//        return sam;
//      }
//    }


}