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

import com.google.common.collect.Iterators;
import edu.unc.genomics.SAMEntry;
import edu.unc.genomics.io.BAMFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import edu.unc.genomics.Interval;
import uk.ac.bbsrc.earlham.browser.store.ensembl.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

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
public class SamBamService {

    protected static final Logger log = LoggerFactory.getLogger(SamBamService.class);
    private Util util = new Util();

    /**
     * Return JSONArray
     * <p>
     * Read wig file
     * loop though each line and parse it to tracks format filtering position and reference
     * </p>
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public static JSONArray getBed(long start, long end, int delta, String trackId, String reference) throws Exception {
        JSONArray bed = new JSONArray();
        try {
            File inputfile = new File(trackId);

            BufferedReader br = null;
            String sCurrentLine;

            br = new BufferedReader(new FileReader(inputfile));

            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine.split("\t");
                JSONObject response = new JSONObject();
                if (line[0].equalsIgnoreCase("MAL1")) {//reference) {
                    if (Integer.parseInt(line[1].toString()) >= start && Integer.parseInt(line[2].toString()) <= end) {
                        response.put("start", line[1]);
                        response.put("end", line[2]);
                        response.put("value", line[4]);
                        bed.add(response);
                    }
                }
            }
            Object[] myArray = bed.toArray();

            JSONArray sortedJArray = new JSONArray();
            for (Object obj : myArray) {
                sortedJArray.add(obj);
            }

            if (bed.size() == 0) {
                bed.add("");
            }
            return bed;
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString());
            bed.add(response);
            return bed;
        }
    }

    /**
     * Count reads in BAM file
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return int no of reads
     * @throws Exception
     */
    public static int countBAM(long start, long end, int delta, String trackId, String reference) throws Exception {
        Path path = Paths.get(trackId);

        try {
            BAMFileReader reader = new BAMFileReader(path, false);


            Iterator<? extends Interval> count;
            count = reader.query(reference, (int) start, (int) end);
//            reader.close();
            return Iterators.size(count);
        } catch (OutOfMemoryError e) {
            return 50000;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new Exception("count BAM :" + e.getMessage());
        }
    }

    /**
     * Read BAM file to get read information
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public JSONArray getBAMReads(long start, long end, int delta, String trackId, String reference) throws Exception {
        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        Path path = Paths.get(trackId);


        try {

            BAMFileReader reader = new BAMFileReader(path, false);

            Iterator<SAMEntry> result = reader.query(reference, (int) start, (int) end);
            JSONObject read = new JSONObject();

            int start_pos, end_pos;

            SAMEntry record;


            while (result.hasNext()) {
                record = result.next();
                start_pos = record.getAlignmentStart();
                end_pos = record.getAlignmentEnd();

                read.put("start", start_pos);
                read.put("end", end_pos);
                read.put("desc", record.getReadName());
                if (record.getMateNegativeStrandFlag() == true) {
                    read.put("strand", false);
                } else {
                    read.put("strand", true);

                }
                if (record.getProperPairFlag()) {
                    if (record.getFirstOfPairFlag()) {
                        read.put("colour", "steelblue");
                    } else if (record.getSecondOfPairFlag()) {
                        read.put("colour", "brown");
                    }
                    read.put("mate", record.getReadName());

                } else {
                    read.put("colour", "orange");
                }

                read.put("cigars", record.getCigarString());
                read.put("alignment", record.getSequence());
                wig.add(read);

            }
            reader.close();

            JSONArray sorted = sort(wig, "start");

            for (int i=0; i < sorted.size(); i++){
                start_pos = sorted.getJSONObject(i).getInt("start");
                end_pos = sorted.getJSONObject(i).getInt("end");
                JSONObject layer = util.stackLayer(ends, start_pos, delta, end_pos);
                sorted.getJSONObject(i).put("layer", layer.getInt("position"));
                ends = (List<Integer>) layer.get("ends");
            }

            return sorted;

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            wig.add(response);
            return wig;
        }
    }

    JSONArray sort(JSONArray jsonArr, String sortBy) {

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        final String KEY_NAME = sortBy;
        Collections.sort( jsonValues, new Comparator<JSONObject>() {

            @Override
            public int compare(JSONObject a, JSONObject b) {
                int valA = 0, valB = 0;

                try {
                    valA = (Integer) a.get(KEY_NAME);
                    valB = (Integer) b.get(KEY_NAME);

                }
                catch (JSONException e) {
                    //exception
                }
                return (valA - valB);

            }
        });

        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    /**
     * Read BAM file for getting overview information in form of graphs
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public static JSONArray getBAMGraphs(long start, long end, int delta, String trackId, String reference) throws Exception {
        log.info("\n\ngetBAMGraphs");

        JSONArray bam = new JSONArray();
        JSONObject response = new JSONObject();

        Path path = Paths.get(trackId);

        int chunks  = 400;

        if((end - start)/chunks < 2)
        {
            chunks = 200;
        }

        try {
            BAMFileReader reader = new BAMFileReader(path, false);

            long diff = (end - start) / chunks;
            long temp_start, temp_end;
            long span[] = new long[2];

            for (int i = 0; i < chunks; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;

                Iterator<SAMEntry> count;

                count = reader.query(reference, (int) temp_start, (int) temp_end);

                span[0] = temp_start;
                span[1] = Iterators.size(count);

                bam.add(span);
            }

            reader.close();
            return bam;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            bam.add(response);
            return bam;
        }

    }


}
