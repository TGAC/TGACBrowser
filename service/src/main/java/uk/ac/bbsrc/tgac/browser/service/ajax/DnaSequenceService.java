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
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.*;
import net.sf.samtools.seekablestream.SeekableStream;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.browser.core.store.SequenceStore;
import uk.ac.bbsrc.tgac.browser.service.ajax.FileService;
import uk.ac.bbsrc.tgac.browser.service.ajax.SamBamService;
import uk.ac.bbsrc.tgac.browser.service.ajax.BigWigService;



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

    public void setSequenceStore(SequenceStore sequenceStore) {
        this.sequenceStore = sequenceStore;
    }

    /**
     * Returns a JSONObject that can be read as single reference or a list of results
     * <p/>
     * This calls the methods in sequenceStore class
     * and search through database for the keyword
     * first look for seq_region table
     * then gene, transcript and gene_attrib and transcript_attrib
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with one result or list of result
     */

    public JSONObject searchSequence(HttpSession session, JSONObject json) {
        String seqName = json.getString("query");
        JSONObject response = new JSONObject();
        try {

            Integer queryid = sequenceStore.getSeqRegionearchsize(seqName);

//      if more than one results
            if (queryid > 1) {
                response.put("html", "seqregion");
                response.put("chromosome", sequenceStore.checkChromosome());
                response.put("seqregion", sequenceStore.getSeqRegionSearch(seqName));
            }
//      if no result from seq_region
            else if (queryid == 0) {
                response.put("html", "gene");
                response.put("gene", sequenceStore.getGenesSearch(seqName));
                response.put("transcript", sequenceStore.getTranscriptSearch(seqName));
                response.put("GO", sequenceStore.getGOSearch(seqName));
                response.put("chromosome", sequenceStore.checkChromosome());
            }
//      if only one result from seq_region
            else {
                Integer query = sequenceStore.getSeqRegion(seqName);
                String seqRegName = sequenceStore.getSeqRegionName(query);
                String seqlength = sequenceStore.getSeqLengthbyId(query);
                response.put("seqlength", seqlength);
                response.put("html", "");
                response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
                response.put("seqregname", seqRegName);
                response.put("tracklists", sequenceStore.getAnnotationId(query));
                response.put("coord_sys", sequenceStore.getCoordSys(seqName));
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }

    }

    /**
     * Returns a JSONObject that can be read as single reference
     * <p/>
     * This calls the methods in sequenceStore class
     * and search through database for the keyword for seq_region table for only one result
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with one result
     */

    public JSONObject seqregionSearchSequence(HttpSession session, JSONObject json) throws IOException {
        try {
            JSONObject response = new JSONObject();
            String seqName = json.getString("query");
            Integer query = sequenceStore.getSeqRegionforone(seqName);
            String seqRegName = sequenceStore.getSeqRegionName(query);
            String seqlength = sequenceStore.getSeqLengthbyId(query);

            response.put("seqlength", seqlength);
            response.put("html", "");
            response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
            response.put("seqregname", seqRegName);
            response.put("tracklists", sequenceStore.getAnnotationId(query));
            response.put("coord_sys", sequenceStore.getCoordSys(seqRegName));

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }

    }

    public JSONObject searchSeqRegionforMap(HttpSession session, JSONObject json) {
        String seqName = "";
        JSONObject response = new JSONObject();
        try {
            response.put("seqregion", sequenceStore.getSeqRegionSearchMap(seqName));
            return response;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * Returns JSONObject as first key track name and second key with tracks detail
     * <p>
     * It checks for the trackId
     * if it contains keyword like .sam or .bam call method getSamBam
     * if it contains keyword like .wig call method getWig
     * if it contains keyword like cs then call method getAssembly
     * if it contains keyword like repeat then call method getRepeat
     * if it contains keyword like gene then call method getGene
     * or last it call method getHit
     * </p>
     * <p>
     * for genes if result is more than 1000 it will return result in form of graphs
     * for repeats and hits if result is more than 5000 it will return result in form of graphs
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject as first key track name and second key with tracks detail
     */

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
        int count;
        try {
            Integer queryid = sequenceStore.getSeqRegion(seqName);
            if (trackId.contains(".bw") ) {
                response.put(trackName, BigWigService.getBigWig(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".sam") || trackId.contains(".bam")) {
                response.put(trackName, SamBamService.getSamBam(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".wig")) {
                response.put(trackName, SamBamService.getWig(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".bed")) {
                response.put(trackName, SamBamService.getBed(start, end, delta, trackId, seqName));
            } else if (trackId.indexOf("cs") >= 0) {
                count = sequenceStore.countAssembly(queryid, trackId, start, end);
                if (count < 5000) {
                    response.put(trackName, sequenceStore.getAssembly(queryid, trackId, delta));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, sequenceStore.getAssemblyGraph(queryid, trackId, start, end));
                }
            } else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*repeat.*")) {
                count = sequenceStore.countRepeat(queryid, trackId, start, end);
                if (count < 5000) {
                    response.put(trackName, sequenceStore.processRepeat(sequenceStore.getRepeat(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, sequenceStore.getRepeatGraph(queryid, trackId, start, end));
                }
            } else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*gene.*")) {
                count = sequenceStore.countGene(queryid, trackId, start, end);
                if (count < 1000) {
                    response.put(trackName, sequenceStore.processGenes(sequenceStore.getGenes(queryid, trackId), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, sequenceStore.getGeneGraph(queryid, trackId, start, end));
                }
            } else {
                count = sequenceStore.countHit(queryid, trackId, start, end);
                if (count < 5000) {
                    response.put(trackName, sequenceStore.processHit(sequenceStore.getHit(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, sequenceStore.getHitGraph(queryid, trackId, start, end));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return response;
    }

    /**
     * Return result as JSONObject
     * call method grtdbinfo to retrieve meta info of the database
     * and call method checkChromosome to check chromosome exist or not for genome map
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with metainfo and chr (boolean)
     */
    public JSONObject metaInfo(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        try {
            response.put("metainfo", sequenceStore.getdbinfo());
            response.put("chr", sequenceStore.checkChromosome());
            return response;
        } catch (Exception e) {
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
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * Return JSONObject
     * <p>
     * check for the name of transcript in description
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return
     */
    public JSONObject loadTranscriptName(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        int query = json.getInt("id");
        try {
            response.put("name", sequenceStore.getTranscriptNamefromId(query));
            return response;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * Returns JSONObject
     * <p>
     * check if the track related to assembly track / repeats / genes or hits
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with name
     */

    public JSONObject loadTrackName(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String trackName = json.getString("track");
        int query = json.getInt("id");
        try {
            String trackid = sequenceStore.getTrackIDfromName(trackName);
            if (trackid.indexOf("cs") >= 0) {
                response.put("name", sequenceStore.getSeqRegionName(query));
            } else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackid)).matches("(?i).*repeat.*")) {
//              as we dont have decided to save repeat name in a table
                response.put("name", "");
            } else if (sequenceStore.getLogicNameByAnalysisId(Integer.parseInt(trackid)).matches("(?i).*gene.*")) {
                response.put("name", sequenceStore.getGeneNamefromId(query));
            } else {
                response.put("name", sequenceStore.getHitNamefromId(query));
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * Return result as JSONObject
     * <p>
     * first call method getSeqRegion to get seq_region_id
     * then get sequence from method getSeq
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with sequence string
     */
    public JSONObject loadSequence(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String query = json.getString("query");
        int from = json.getInt("from");
        int to = json.getInt("to");
        try {
            String queryid = sequenceStore.getSeqRegion(query).toString();
            if (from <= to) {
                response.put("seq", sequenceStore.getSeq(queryid, from, to));
            } else {
                response.put("seq", sequenceStore.getSeq(queryid, to, from));
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }


    /**
     * Return result as JSONObject
     * <p>
     * this method call getMarker method to get markers for all the markers for the database
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with marker information
     */
    public JSONObject loadMarker(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        try {
            response.put("marker", sequenceStore.getMarker());

            return response;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }
}