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

import edu.unc.genomics.SAMEntry;
import edu.unc.genomics.io.BAMFileReader;
import edu.unc.genomics.io.BedFileWriter;
import edu.unc.genomics.io.IntervalFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.*;
import org.broad.tribble.*;

import edu.unc.genomics.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.browser.core.store.UtilsStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 2/1/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SamBamService {

    protected static final Logger log = LoggerFactory.getLogger(SamBamService.class);

    @Autowired
    private static UtilsStore util;

    public void setUtilsStore(UtilsStore util) {
        this.util = util;
    }

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
        boolean found = false;
        try {
            File inputfile = new File(trackId);

            BufferedReader br = null;
            String sCurrentLine;

            br = new BufferedReader(new FileReader(inputfile));
//       Pattern p = Pattern.compile(".*" + reference + "$");

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
            IntervalFileReader<? extends Interval> readers = IntervalFileReader.autodetect(path);
            int count = readers.load(reference, (int) start, (int) end).size();
            return count;
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
    public static JSONArray getBAMReads(long start, long end, int delta, String trackId, String reference) throws Exception {
        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        Path path = Paths.get(trackId);


        try {

            BAMFileReader reader = new BAMFileReader(path);

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
                if (record.getProperPairFlag()) {
                    if (record.getFirstOfPairFlag()) {
                        read.put("colour", "steelblue");
                    } else if (record.getSecondOfPairFlag()) {
                        read.put("colour", "brown");
                    }
                } else {
                    read.put("colour", "orange");
                }
                /**
                 * flags that csn be used in future
                 */
//                read.put("flag0", record.getFlags());
//                read.put("flag1", record.getDuplicateReadFlag());
//                read.put("flag4", record.getReadPairedFlag());
//                read.put("flag3", record.getProperPairFlag());
//                read.put("flag8", record.getSecondOfPairFlag());
//                read.put("flag7", record.getDuplicateReadFlag());
//                read.put("flag6", record.getMateNegativeStrandFlag());
//                read.put("flag9", record.getReadFailsVendorQualityCheckFlag());
//                read.put("flag10", record.getReadUnmappedFlag());
//                read.put("flag2", record.getMateUnmappedFlag());
//                read.put("flag11", record.getNotPrimaryAlignmentFlag());
//                read.put("flag12", record.getNotPrimaryAlignmentFlag());

                read.put("cigars", record.getCigarString());
                read.put("layer", util.stackLayerInt(ends, start_pos, delta, end_pos));
                ends = util.stackLayerList(ends, start_pos, delta, end_pos);
                wig.add(read);
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
        JSONArray bam = new JSONArray();
        JSONObject response = new JSONObject();

        Path path = Paths.get(trackId);

        try {
            JSONObject read = new JSONObject();
            IntervalFileReader<? extends Interval> readers = IntervalFileReader.autodetect(path);

            long diff = (end - start) / 400;
            long temp_start, temp_end;

            for (int i = 0; i < 400; i++) {
                temp_start = start + (i * diff);
                temp_end = temp_start + diff;
                int count = readers.load(reference, (int) temp_start, (int) temp_end).size();
                read.put("start", temp_start);
                read.put("end", temp_end);
                read.put("graph", count);
                bam.add(read);
            }
            return bam;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            bam.add(response);
            return bam;
        }

    }


}
