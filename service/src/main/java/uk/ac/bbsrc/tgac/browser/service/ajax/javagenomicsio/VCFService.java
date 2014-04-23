/*
#
# Copyright (c) 2013. The Genome Analysis Centre, Norwich, UK
# TGAC Browser project contacts: Anil Thanki, Xingdong Bian, Robert Davey, Mario Caccamo @ TGAC
# **********************************************************************
#
# This file is part of TGAC Browser.
#
# TGAC Browser is free software: you can redistribute it and/or modify
# it under the terms of the GNU eachEntryral Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# TGAC Browser is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU eachEntryral Public License for more details.
#
# You should have received a copy of the GNU eachEntryral Public License
# along with TGAC Browser.  If not, see <http://www.gnu.org/licenses/>.
#
# ***********************************************************************
#
 */

package uk.ac.bbsrc.tgac.browser.service.ajax.javagenomicsio;

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
        int eachEntry = 0;

        try {
            VCFFileReader reader = new VCFFileReader(path);
            for (VCFEntry entry : reader) { // All entries in the file
                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {
                    eachEntry++;
                }
            }

            return eachEntry;
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

        JSONArray VCF = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        List<Integer> eachEntrys_ends = new ArrayList<Integer>();
        eachEntrys_ends.add(0, 0);

        Path path = Paths.get(trackId);


        try {
            VCFFileReader reader = new VCFFileReader(path);

            JSONObject eachEntry = new JSONObject();

            for (VCFEntry entry : reader) { // All entries in the file
                // do what you want with the entry
                // maybe store entries from chr1
                int start_pos, end_pos;

                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {

                    start_pos = entry.getStart();
                    end_pos = entry.getStop();


                    eachEntry.put("id", entry.getId());
                    eachEntry.put("start", start_pos);
                    eachEntry.put("end", end_pos);
                    eachEntry.put("info", entry.getInfoString());
                    eachEntry.put("qual", entry.getQual());
                    eachEntry.put("alt", entry.getAlt());
                    eachEntry.put("filter", entry.getFilter());
                    eachEntry.put("genotype", entry.getGenotypes());
                    eachEntry.put("ref",entry.getRef());


                    VCF.add(eachEntry);

                }
            }
            return VCF;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            VCF.add(response);
            return VCF;
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
            List<Integer> eachEntry_start = new ArrayList<>();
            List<Integer> eachEntry_end = new ArrayList<>();

            long diff = (end - start) / 400;
            long temp_start, temp_end;

            VCFFileReader reader = new VCFFileReader(path);
            for (VCFEntry entry : reader) { // All entries in the file
                if (entry.getChr().equals(reference) && entry.getStart() >= start && entry.getStop() <= end) {
                    eachEntry_start.add(entry.getStart());
                    eachEntry_end.add(entry.getStop());
                }
            }

            for (int i = 0; i < 400; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;
                int count = 0;

                for (Integer entry : eachEntry_start) { // All entries in the file
                    if (entry < temp_end && entry > temp_start) ;
                    {
                        count++;
                        eachEntry_start.remove(entry);
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
