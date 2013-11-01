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

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.browser.core.store.*;
import uk.ac.bbsrc.tgac.browser.core.store.DafStore;


import javax.servlet.http.HttpSession;
import java.io.*;


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

    @Autowired
    private SearchStore searchStore;

    @Autowired
    private GeneStore geneStore;

    public void setGeneStore(GeneStore geneStore) {
        this.geneStore = geneStore;
    }

    @Autowired
    private AnalysisStore analysisStore;

    public void setAnalysisStore(AnalysisStore analysisStore) {
        this.analysisStore = analysisStore;
    }

    @Autowired
    private DafStore dafStore;

    public void setDafStore(DafStore dafStore) {
        this.dafStore = dafStore;
    }

    @Autowired
    private AssemblyStore assemblyStore;

    public void setAssemblyStore(AssemblyStore assemblyStore) {
        this.assemblyStore = assemblyStore;
    }

    @Autowired
    private RepeatStore repeatStore;

    public void setRepeatStore(RepeatStore repeatStore) {
        this.repeatStore = repeatStore;
    }


    public void setSequenceStore(SequenceStore sequenceStore) {
        this.sequenceStore = sequenceStore;
    }

    public void setSearchStore(SearchStore searchStore) {
        this.searchStore = searchStore;
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
                response.put("chromosome", searchStore.checkChromosome());
                response.put("seqregion", searchStore.getSeqRegionSearch(seqName));
            }
//      if no result from seq_region
            else if (queryid == 0) {
                response.put("html", "gene");
                response.put("gene", searchStore.getGenesSearch(seqName));
                response.put("transcript", searchStore.getTranscriptSearch(seqName));
                response.put("GO", searchStore.getGOSearch(seqName));
                response.put("chromosome", searchStore.checkChromosome());
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
                response.put("tracklists", analysisStore.getAnnotationId(query));
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
            response.put("tracklists", analysisStore.getAnnotationId(query));
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
            if (trackId.contains(".sam") || trackId.contains(".bam")) {
                response.put(trackName, SamBamService.getSamBam(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".wig")) {
                response.put(trackName, SamBamService.getWig(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".bed")) {
                response.put(trackName, SamBamService.getBed(start, end, delta, trackId, seqName));
            } else if (trackId.indexOf("cs") >= 0) {
                count = assemblyStore.countAssembly(queryid, trackId, start, end);
                if (count < 5000) {
                    response.put(trackName, assemblyStore.getAssembly(queryid, trackId, delta));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, assemblyStore.getAssemblyGraph(queryid, trackId, start, end));
                }
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*repeat.*")) {
                count = repeatStore.countRepeat(queryid, trackId, start, end);
                if (count < 5000) {
                    response.put(trackName, repeatStore.processRepeat(repeatStore.getRepeat(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, repeatStore.getRepeatGraph(queryid, trackId, start, end));
                }
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*gene.*")) {
                count = geneStore.countGene(queryid, trackId, start, end);
                if (count < 1000) {
                    response.put(trackName, geneStore.processGenes(geneStore.getGenes(queryid, trackId), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, geneStore.getGeneGraph(queryid, trackId, start, end));
                }
            } else {
                log.info("\n\nloadtrack else");
                log.info("\n\n"+dafStore.getClass().getName());
                count = dafStore.countHit(queryid, trackId, start, end);
                log.info("\n\n\n\nelse"+count);

                if (count < 5000) {
                    response.put(trackName,dafStore.processHit(dafStore.getHit(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put(trackName, dafStore.getHitGraph(queryid, trackId, start, end));
                }
            }

        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            e.getMessage();
            e.getCause();
            return JSONUtils.SimpleJSONError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            e.getMessage();
            e.getCause();
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
            log.info("\n\nmetainfo\n\n");
            response.put("metainfo", sequenceStore.getdbinfo());
            log.info("\n\nchr\n\n");

            response.put("chr", searchStore.checkChromosome());
            log.info("\n\nafter\n\n");

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
            response.put("name", geneStore.getTranscriptNamefromId(query));
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
            String trackid = analysisStore.getTrackIDfromName(trackName);
            if (trackid.indexOf("cs") >= 0) {
                response.put("name", sequenceStore.getSeqRegionName(query));
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackid)).matches("(?i).*repeat.*")) {
//              as we dont have decided to save repeat name in a table
                response.put("name", "");
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackid)).matches("(?i).*gene.*")) {
                response.put("name", geneStore.getGeneNamefromId(query));
            } else {
                response.put("name", dafStore.getHitNamefromId(query));
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
     * This method get markers for all the markers for the database
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