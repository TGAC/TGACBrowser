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

package uk.ac.bbsrc.earlham.browser.service.ajax.javagenomicsio;

import edu.unc.genomics.*;
import edu.unc.genomics.io.GFFFileReader;
import edu.unc.genomics.io.GeneTrackFileReader;
import edu.unc.genomics.io.IntervalFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import uk.ac.bbsrc.earlham.browser.store.ensembl.Util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 2/1/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class GFFService {
//
    protected static final Logger log = LoggerFactory.getLogger(GFFService.class);
//
//
    private Util util = new Util();


    /**
     * Count reads in GFF file
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return int no of reads
     * @throws Exception
     */
    public static int countGFF(long start, long end, int delta, String trackId, String reference) throws Exception {
        Path path = Paths.get(trackId);
        int gene = 0;

        try {
            GFFFileReader readers = new GFFFileReader(path);
//            GeneTrackFileReader readers = new GeneTrackFileReader(path);
//            IntervalFileReader<? extends Interval> readers = IntervalFileReader.autodetect(path);
            int count = 0;
            long diff = (end - start) / 400;
            long temp_start, temp_end;
            long span[] = new long[2];

            for (int i = 0; i < 400; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;
                count += readers.load(reference, (int) temp_start, (int) temp_end).size();
                log.info("temp_start "+temp_start+" temp_end "+temp_end+ " count "+count);

                temp_start = start + (i * diff);
                temp_end = temp_start + diff;

            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new Exception("count GFF :" + e.getMessage());
        }
    }

    /**
     * Read GFF file to get read information
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public JSONArray getGFFReads(long start, long end, int delta, String trackId, String reference) throws Exception {

        log.info("\n\n\t getGFFreads "+start+" "+end+" "+reference);
        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        List<Integer> genes_ends = new ArrayList<Integer>();
        genes_ends.add(0, 0);

        Path path = Paths.get(trackId);


        try {
            GFFFileReader readers = new GFFFileReader(path);

            JSONObject gene = new JSONObject();
            JSONObject transcript = new JSONObject();
            JSONObject exon = new JSONObject();

            JSONArray exonList = new JSONArray();
            JSONArray transcriptList = new JSONArray();
            boolean genes = false;
            boolean transcripts = false;

            List<GFFEntry> reader = readers.load(reference, (int) start, (int) end);

            for (GFFEntry entry : reader) { // All entries in the file
                // do what you want with the entry
                // maybe store entries from chr1
                int start_pos, end_pos;
                log.info(String.valueOf(entry));
//                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {

                    start_pos = entry.getStart();
                    end_pos = entry.getStop();

                    if (entry.getFeature().toLowerCase().contains("gene")) {
                        if (genes) {
                            gene.put("transcript", transcriptList);
                            wig.add(gene);
                            transcriptList = new JSONArray();
                        }
                        genes = true;
                        gene.put("id", entry.getId());
                        gene.put("start", start_pos);
                        gene.put("end", end_pos);
                        gene.put("source", entry.getSource());

                        transcript.put("domain", util.stackLayerInt(genes_ends, start_pos, delta, end_pos));
                        genes_ends = util.stackLayerList(ends, start_pos, delta, end_pos);
                    } else if (entry.getFeature().toLowerCase().contains("mrna")) {
                        if (transcripts) {
                            transcript.put("Exons", exonList);
                            transcriptList.add(transcript);
                            exonList = new JSONArray();
                        }
                        transcripts = true;
                        transcript.put("id", entry.getId());
                        transcript.put("start", start_pos);
                        transcript.put("end", end_pos);
                        transcript.put("layer", util.stackLayerInt(ends, start_pos, delta, end_pos));
                        transcript.put("domain", entry.getSource());

                        ends = util.stackLayerList(ends, start_pos, delta, end_pos);
                    } else if (entry.getFeature().toLowerCase().contains("exon")) {
                        exon.put("start", start_pos);
                        exon.put("end", end_pos);
                        exonList.add(exon);
                    }
//                }
            }
            if (genes) {
                gene.put("transcript", transcriptList);
                wig.add(gene);
            }
            return wig;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            wig.add(response);
            return wig;
        }
    }

    /**
     * Read GFF file for getting overview information in form of graphs
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */

    public static JSONArray getGFFGraphs(long start, long end, int delta, String trackId, String reference) throws Exception {
        log.info("\n\n\t getGFFGraphs");

        JSONArray gff = new JSONArray();
        JSONObject response = new JSONObject();

        Path path = Paths.get(trackId);

        try {
            JSONObject read = new JSONObject();
//            List<Integer> gene_start = new ArrayList<Integer>();
//            List<Integer> gene_end = new ArrayList<Integer>();
//
//            long diff = (end - start) / 400;
//            long temp_start, temp_end;
//
//            GFFFileReader reader = new GFFFileReader(path);
//            for (GFFEntry entry : reader) { // All entries in the file
//                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {
//                        gene_start.add(entry.getStart());
//                        gene_end.add(entry.getStop());
//                }
//            }
//
//            for (int i = 0; i < 400; i++) {
//                temp_start = start + (i * diff);
//                temp_end = temp_start + diff;
//                int count = 0;
//
//                for (Integer entry : gene_start) { // All entries in the file
//                    if(entry < temp_end && entry > temp_start);
//                    {
//                        count++;
//                        gene_start.remove(entry);
//                    }
//                }
            GFFFileReader readers = new GFFFileReader(path);
            int count = 0;
            long diff = (end - start) / 400;
            long temp_start, temp_end;
            long span[] = new long[2];

            for (int i = 0; i < 400; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;
                count = readers.load(reference, (int) temp_start, (int) temp_end).size();

                temp_start = start + (i * diff);
                temp_end = temp_start + diff;


                read.put("start", temp_start);
                read.put("end", temp_end);
                read.put("graph", count);
                gff.add(read);
            }
            return gff;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            gff.add(response);
            return gff;
        }

    }
}
