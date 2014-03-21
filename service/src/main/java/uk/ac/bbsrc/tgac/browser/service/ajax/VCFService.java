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

import edu.unc.genomics.VCFEntry;
import edu.unc.genomics.io.VCFFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.browser.store.ensembl.Util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 2/1/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class VCFService {

    protected static final Logger log = LoggerFactory.getLogger(VCFService.class);


    private Util util = new Util();


    /**
     * Count reads in VCF file
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return int no of reads
     * @throws Exception
     */
    public static int countVCF(long start, long end, int delta, String trackId, String reference) throws Exception {
        log.info("\n\n\n\n\nVCF count");


        Path path = Paths.get(trackId);
        int gene = 0;

        try {
            VCFFileReader reader = new VCFFileReader(path);
            for (VCFEntry entry : reader) { // All entries in the file
                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {
                    gene++;
                }
            }

            return gene;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new Exception("count VCF :" + e.getMessage());
        }
    }

    /**
     * Read VCF file to get read information
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public JSONArray getVCFReads(long start, long end, int delta, String trackId, String reference) throws Exception {
        log.info("\n\n\n\n\nVCF reads");

        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        List<Integer> genes_ends = new ArrayList<Integer>();
        genes_ends.add(0, 0);

        Path path = Paths.get(trackId);


        try {
            VCFFileReader reader = new VCFFileReader(path);

            JSONObject gene = new JSONObject();
            JSONObject transcript = new JSONObject();
            JSONObject exon = new JSONObject();

            JSONArray exonList = new JSONArray();
            JSONArray transcriptList = new JSONArray();
            boolean genes = false;
            boolean transcripts = false;

            for (VCFEntry entry : reader) { // All entries in the file
                // do what you want with the entry
                // maybe store entries from chr1
                int start_pos, end_pos;

                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {

                    start_pos = entry.getStart();
                    end_pos = entry.getStop();


                    gene.put("id", entry.getId());
                    gene.put("start", start_pos);
                    gene.put("end", end_pos);
                    gene.put("info", entry.getInfoString());
                    gene.put("qual", entry.getQual());
                    gene.put("alt", entry.getAlt());
                    gene.put("filter", entry.getFilter());
                    gene.put("genotype", entry.getGenotypes());


                    wig.add(gene);

                }
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
     * Read VCF file for getting overview information in form of graphs
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */

    public static JSONArray getVCFGraphs(long start, long end, int delta, String trackId, String reference) throws Exception {

        log.info("\n\n\n\n\nVCF graphs");

        JSONArray VCF = new JSONArray();
        JSONObject response = new JSONObject();

        Path path = Paths.get(trackId);

        try {
            JSONObject read = new JSONObject();
            List<Integer> gene_start = new ArrayList<>();
            List<Integer> gene_end = new ArrayList<>();

            long diff = (end - start) / 400;
            long temp_start, temp_end;

            VCFFileReader reader = new VCFFileReader(path);
            for (VCFEntry entry : reader) { // All entries in the file
                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {
                    gene_start.add(entry.getStart());
                    gene_end.add(entry.getStop());
                }
            }

            for (int i = 0; i < 400; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;
                int count = 0;

                for (Integer entry : gene_start) { // All entries in the file
                    if (entry < temp_end && entry > temp_start) ;
                    {
                        count++;
                        gene_start.remove(entry);
                    }
                }
                read.put("start", temp_start);
                read.put("end", temp_end);
                read.put("graph", count);
                VCF.add(read);
            }
            return VCF;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            VCF.add(response);
            return VCF;
        }

    }
}
