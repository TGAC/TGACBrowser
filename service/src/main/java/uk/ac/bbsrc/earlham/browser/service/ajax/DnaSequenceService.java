/*
#
# Copyright (c) 2013. The Genome Analysis Centre, Norwich, UK
# TGAC Browser project contacts: Anil Thanki, Xingdong Bian, Robert Davey @ Earlham Institute
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

package uk.ac.bbsrc.earlham.browser.service.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.earlham.browser.core.store.*;
import uk.ac.bbsrc.earlham.browser.service.ajax.javagenomicsio.BigWigService;
import uk.ac.bbsrc.earlham.browser.service.ajax.javagenomicsio.GFFService;
import uk.ac.bbsrc.earlham.browser.service.ajax.javagenomicsio.SamBamService;
import uk.ac.bbsrc.earlham.browser.service.ajax.javagenomicsio.VCFService;

import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 10:52:16
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class DnaSequenceService {
    protected final Logger log = LoggerFactory.getLogger(DnaSequenceService.class);
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

    @Autowired
    private MarkerStore markerStore;

    public void setMarkerStore(MarkerStore markerStore) {
        this.markerStore = markerStore;
    }

    public void setSequenceStore(SequenceStore sequenceStore) {
        this.sequenceStore = sequenceStore;
    }

    public void setSearchStore(SearchStore searchStore) {
        this.searchStore = searchStore;
    }

    private SamBamService samBamService = new SamBamService();
    private GFFService gffService = new GFFService();
    private VCFService vcfService = new VCFService();


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
        log.info("\n\n\n\n\n\n\nsearchsequence");

        String seqName = json.getString("query");
        JSONObject response = new JSONObject();
        try {

            Integer queryid = sequenceStore.getSeqRegionearchsize(seqName);
            if (queryid > 1) {
                response.put("html", "seqregion");
                response.put("chromosome", searchStore.checkChromosome());
                response.put("seqregion", searchStore.getSeqRegionSearch(seqName));
            } else if (queryid == 0) {
                response.put("gene", searchStore.getGenesSearch(seqName));
                response.put("chromosome", searchStore.checkChromosome());
                response.put("transcript", searchStore.getTranscriptSearch(seqName));
                response.put("GO", searchStore.getGOSearch(seqName));
                response.put("marker", searchStore.getMarkerSearch(seqName));
                response.put("chromosome", searchStore.checkChromosome());
                if (response.get("gene").toString().equals("[]") && response.get("transcript").toString().equals("[]") && response.get("GO").toString().equals("[]") && response.get("marker").toString().equals("[]")) {
                    response.put("html", "none");
                } else {
                    response.put("html", "gene");
                }
            } else {
                Integer query = sequenceStore.getSeqRegion(seqName);
                String seqRegName = sequenceStore.getSeqRegionName(query);
                String seqlength = sequenceStore.getSeqLengthbyId(query);
                response.put("seqlength", seqlength);
                response.put("chromosome", searchStore.checkChromosome(query));
                response.put("html", "one");
                response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
                response.put("seqregname", seqRegName);
                response.put("tracklists", analysisStore.getAnnotationId(query));
                response.put("coord_sys", sequenceStore.getSeqRegionCoordId(seqName));
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

            Integer queryid = sequenceStore.getSeqRegionearchsizeformatch(seqName);
            if (queryid > 1) {
                response.put("html", "seqregion");
                response.put("chromosome", searchStore.checkChromosome());
                response.put("seqregion", searchStore.getSeqRegionSearchformatch(seqName));
            } else if (queryid == 0) {
                queryid = sequenceStore.getSeqRegionearchsize(seqName);
                if (queryid > 1) {
                    response.put("html", "seqregion");
                    response.put("chromosome", searchStore.checkChromosome());
                    response.put("seqregion", searchStore.getSeqRegionSearch(seqName));
                } else if (queryid == 0) {
                    response.put("gene", searchStore.getGenesSearch(seqName));
                    response.put("transcript", searchStore.getTranscriptSearch(seqName));
                    response.put("GO", searchStore.getGOSearch(seqName));
                    response.put("marker", searchStore.getMarkerSearch(seqName));
                    response.put("chromosome", searchStore.checkChromosome());
                    if (response.get("gene").toString().equals("[]") && response.get("transcript").toString().equals("[]") && response.get("GO").toString().equals("[]") && response.get("marker").toString().equals("[]")) {
                        response.put("html", "none");
                    } else {
                        response.put("html", "gene");
                    }
                }
            } else {
                Integer query = sequenceStore.getSeqRegion(seqName);
                String seqRegName = sequenceStore.getSeqRegionName(query);
                String seqlength = sequenceStore.getSeqLengthbyId(query);
                response.put("seqlength", seqlength);
                response.put("chromosome", searchStore.checkChromosome(query));
                response.put("html", "one");
                response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
                response.put("seqregname", seqRegName);
                response.put("tracklists", analysisStore.getAnnotationId(query));
                response.put("coord_sys", sequenceStore.getSeqRegionCoordId(seqName));
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

    public JSONObject seqregionSearchSequenceWithCoord(HttpSession session, JSONObject json) throws IOException {
        try {
            JSONObject response = new JSONObject();
            String seqName = json.getString("query");
            String coord = json.getString("coord");

            Integer queryid = sequenceStore.getSeqRegionearchsizeformatch(seqName);

            Integer query = sequenceStore.getSeqRegionWithCoord(seqName, coord);
            String seqRegName = sequenceStore.getSeqRegionName(query, coord);
            String seqlength = sequenceStore.getSeqLengthbyId(query, coord);
            response.put("seqlength", seqlength);
            response.put("html", "one");
            response.put("chromosome", searchStore.checkChromosome(query));
            response.put("seqname", "<p> <b>Seq Region ID:</b> " + query + ",<b> Name: </b> " + seqRegName);//+", <b>cds:</b> "+cds+"</p>");
            response.put("seqregname", seqRegName);
            response.put("tracklists", analysisStore.getAnnotationId(query));
            response.put("coord_sys", coord);
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
        String coord = json.getString("coord");

        long start = json.getInt("start");
        long end = json.getInt("end");
        int delta = json.getInt("delta");
        response.put("name", trackName);
        int count;
        try {
            Integer queryid = sequenceStore.getSeqRegionWithCoord(seqName, coord);
            if (trackId.toLowerCase().contains(".bw") || trackId.toLowerCase().contains(".bigwig") || trackId.toLowerCase().contains(".wig")) {
                response.put(trackName, BigWigService.getWig(start, end, delta, trackId, seqName));
            } else if (trackId.contains(".sam") || trackId.contains(".bam")) {
                count = SamBamService.countBAM(start, end, delta, trackId, seqName);

                log.info("\n\n\nBAM count " + count);


                if (count == 0) {
                    response.put(trackName, "getHit no result found");

                } else if (count < 5000) {
                    response.put(trackName, samBamService.getBAMReads(start, end, delta, trackId, seqName));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "wig");
                    response.put(trackName, SamBamService.getBAMGraphs(start, end, delta, trackId, seqName));
                }
            } else if (trackId.contains(".gff") || trackId.contains(".GFF")) {
                count = GFFService.countGFF(start, end, delta, trackId, seqName);
                log.info("\n\n\nGFF count " + count);

                if (count == 0) {
                    response.put(trackName, "getHit no result found");

                } else if (count < 5000) {
                    response.put(trackName, gffService.getGFFReads(start, end, delta, trackId, seqName));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, GFFService.getGFFGraphs(start, end, delta, trackId, seqName));
                }
            } else if (trackId.contains(".vcf") || trackId.contains(".VCF")) {
                count = VCFService.countVCF(start, end, delta, trackId, seqName);

                log.info("\n\n\nVCF count " + count);


//                if (count ==0) {
//                    response.put(trackName, "getHit no result found");
//
//                } else
                if (count < 5000) {
                    response.put(trackName, vcfService.getVCFReads(start, end, delta, trackId, seqName));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, VCFService.getVCFGraphs(start, end, delta, trackId, seqName));
                }
            } else if (trackId.contains(".bed")) {
                response.put(trackName, SamBamService.getBed(start, end, delta, trackId, seqName));
            } else if (trackId.indexOf("cs") >= 0) {
                count = assemblyStore.countAssembly(queryid, trackId, start, end);
                log.info("\n\n\nassembly count " + count);
                if (count == 0) {
                    response.put(trackName, "getHit no result found");

                } else if (count < 5000) {
                    response.put(trackName, assemblyStore.getAssembly(queryid, trackId, delta, start, end));
                } else if (count < 50000) {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, assemblyStore.getAssemblyGraph(queryid, trackId, start, end));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "heat");
                    response.put(trackName, assemblyStore.getAssemblyOverviewGraph(queryid, trackId, start, end));
                }
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*repeat.*")) {
                count = repeatStore.countRepeat(queryid, trackId, start, end);
                log.info("\n\n\nrepeat count " + count);

                if (count == 0) {
                    response.put(trackName, "getHit no result found");

                } else if (count < 5000) {
                    response.put(trackName, repeatStore.processRepeat(repeatStore.getRepeat(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, repeatStore.getRepeatGraph(queryid, trackId, start, end));
                }
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*marker.*")) {
                count = markerStore.countMarker(queryid, trackId, start, end);
                log.info("\n\n\nrepeat count " + count);

                if (count == 0) {
                    response.put(trackName, "getHit no result found");

                } else if (count < 5000) {
                    response.put(trackName, markerStore.processMarker(markerStore.getMarker(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, markerStore.getMarkerGraph(queryid, trackId, start, end));
                }
            } else if (analysisStore.getLogicNameByAnalysisId(Integer.parseInt(trackId)).matches("(?i).*gene.*")) {
                count = geneStore.countGene(queryid, trackId, start, end);


                log.info("\n\n\ngene count " + count);
                if (count == 0) {
                    response.put(trackName, new JSONArray());
                } else if (count < 1000) {
                    response.put(trackName, geneStore.processGenes(geneStore.getGenes(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, geneStore.getGeneGraph(queryid, trackId, start, end));
                }
            } else {
                count = dafStore.countHit(queryid, trackId, start, end);

                log.info("\n\n\nhit count " + count);

                if (count == 0) {
                    response.put(trackName, "getHit no result found");
                } else if (count < 5000) {
                    response.put(trackName, dafStore.processHit(dafStore.getHit(queryid, trackId, start, end), start, end, delta, queryid, trackId));
                } else {
                    response.put("type", "graph");
                    response.put("graphtype", "bar");
                    response.put(trackName, dafStore.getHitGraph(queryid, trackId, start, end));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            e.getCause();
            return JSONUtils.SimpleJSONError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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
            response.put("metainfo", sequenceStore.getdbinfo());
            response.put("chr", searchStore.checkChromosome());
            if (sequenceStore.getScale().length() > 0) {
                response.put("scale", sequenceStore.getScale());
            }
            if (sequenceStore.getUnit().length() > 0) {
                response.put("unit", sequenceStore.getUnit());
            }
            if (sequenceStore.getLink().length() > 0) {
                response.put("link", sequenceStore.getLink());
            }

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
        String coord = json.getString("coord");


        try {
            String queryid = sequenceStore.getSeqRegionWithCoord(query, coord).toString();
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
        String coord = json.getString("coord");
        String query = json.getString("query");

        try {
            response.put("marker", sequenceStore.getMarker(query, coord));

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with marker information
     */
    public JSONObject loadMarkerForRegion(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String coord = json.getString("coord");
        String query = json.getString("query");
        long start = json.getLong("start");
        long end = json.getLong("end");
        try {
            response.put("marker", sequenceStore.getMarkerforRegion(query, coord, start, end));

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }


    /**
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with marker information
     */
    public JSONObject getSNPs(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String name = json.getString("id");
        long start = json.getLong("start");
        long end = json.getLong("end");
        String coord = json.getString("coord");


        try {
            Integer query = sequenceStore.getSeqRegionWithCoord(name, coord);
            response.put("SNP", dafStore.getallSNPsonGene(query, coord, start, end));

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    /**
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with marker information
     */
    public JSONObject getOtherSNPs(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String name = json.getString("id");
        long start = json.getLong("start");
        String coord = json.getString("coord");


        try {
            Integer query = sequenceStore.getSeqRegionWithCoord(name, coord);
            response.put("SNP", dafStore.getallSNPsonSNP(query, coord, start));

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }


    public JSONObject listSNPs(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();

        try {
            response.put("list", analysisStore.listSNPs());

            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }

    public JSONObject getGroupedSNPs(HttpSession session, JSONObject json) {
        JSONObject response = new JSONObject();
        String groupA = json.getString("group_a");
        String groupB = json.getString("group_b");

        String[] groupA_array = groupA.split(",");
        String[] groupB_array = groupB.split(",");
        String[] groupC_array = (String[]) ArrayUtils.addAll(groupA_array, groupB_array);

        try {
            response.put("group_A", dafStore.getSNPs(groupA_array));
            response.put("group_B", dafStore.getSNPs(groupB_array));
//            response.put("unique", dafStore.getSNPs(groupC_array));
            return response;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }
    }
}