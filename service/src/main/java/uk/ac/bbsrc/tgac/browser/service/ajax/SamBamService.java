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

import edu.unc.genomics.Contig;
import edu.unc.genomics.SAMEntry;
import edu.unc.genomics.io.BAMFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.*;
import net.sf.samtools.util.CloseableIterator;
//import org.broad.igv.sam.*;

//import org.broad.igv.sam.reader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.io.*;
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

    /**
     * Return JSONArray
     * <p>
     * Read sam or bam+bai file
     * convert it to SamRecord
     * loop though each record and parse it to tracks format
     * </p>
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     */
    protected static JSONArray getSamBam(long start, long end, int delta, String trackId, String reference) {
        JSONArray sam = new JSONArray();
        JSONObject response = new JSONObject();
        try {
            File inputfile = new File(trackId);
            SAMFileReader inputSam = new SAMFileReader(inputfile);

            if (trackId.indexOf("sam") >= 0) {
                inputSam = new SAMFileReader(inputfile);
            } else if (trackId.indexOf("bam") >= 0) {
                final File index = new File(trackId + ".bai");
//        inputSam = new SAMFileReader(inputfile, index, false);
                BrowseableBAMIndex bbi = inputSam.getBrowseableIndex();
                BinList bl = bbi.getBinsOverlapping(0, (int) start, (int) end);

                SAMFileSpan sfs = inputSam.getFilePointerSpanningReads();

                log.info("binlist " + bl.toString());
                log.info("sfs " + sfs.toString());

//        BAMRecord br = new BAMRecord(inputSam.getFileHeader(), reference, );
            }
            log.info("hererere");
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);


            for (final SAMRecord samRecord : inputSam) {
                int cigar_pos = 0;
                int cigar_len = 0;
                int start_pos = samRecord.getAlignmentStart();
                int end_pos = samRecord.getAlignmentEnd();
                String ref = samRecord.getReferenceName();
//        check for reference and positions match
                if (ref.equalsIgnoreCase(reference) && (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end)) {
                    JSONObject read = new JSONObject();
                    JSONObject cigars = new JSONObject();
//          // Convert read name to upper case.
                    read.put("start", samRecord.getAlignmentStart());
                    read.put("end", samRecord.getAlignmentEnd());
                    read.put("desc", samRecord.getReadName().toString());
                    read.put("layer", ends.size());
                    List<CigarElement> cigarelemts = samRecord.getCigar().getCigarElements();
                    for (CigarElement c : cigarelemts) {
                        if (cigars.containsKey(c.getOperator().toString())) {
                            cigars.put(c.getOperator(), cigars.get(c.getOperator().toString()).toString() + "," + cigar_pos + ":" + c.getLength());
                        } else {
                            cigars.put(c.getOperator(), cigar_pos + ":" + c.getLength());
                        }

                        if (c.getOperator().toString().equalsIgnoreCase("I") || c.getOperator().toString().equalsIgnoreCase("M") || c.getOperator().toString().equalsIgnoreCase("=") || c.getOperator().toString().equalsIgnoreCase("X")) {
                            cigar_pos += c.getLength();
                        } else if (c.getOperator().toString().equalsIgnoreCase("D") || c.getOperator().toString().equalsIgnoreCase("N")) {
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
                        } else if ((start_pos - ends.get(i) < delta) && (i + 1) == ends.size()) {
                            if (i == 0) {
                                read.put("layer", ends.size());
                                ends.add(i, end_pos);
                            } else {
                                read.put("layer", ends.size());
                                ends.add(ends.size(), end_pos);
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                    read.put("cigars", cigars);
                    sam.add(read);
                }
            }
            return sam;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString());
            sam.add(response);
            return sam;
        }
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
    public static JSONArray getWig(long start, long end, int delta, String trackId, String reference) throws Exception {
        JSONArray wig = new JSONArray();
        boolean found = false;
        try {
            File inputfile = new File(trackId);

            BufferedReader br = null;
            String sCurrentLine;

            br = new BufferedReader(new FileReader(inputfile));
            Pattern p = Pattern.compile(".*" + reference + "$");

            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine.split("\t");
                JSONObject response = new JSONObject();
                if ((sCurrentLine.contains("variableStep"))) {
                    Matcher matcher_comment = p.matcher(sCurrentLine);
                    if (matcher_comment.find()) {
                        found = true;
                    } else {
                        found = false;
                    }
                } else if (found == true && line.length == 2 && (Integer.parseInt(line[0].toString()) >= start && Integer.parseInt(line[0].toString()) <= end)) {
                    response.put("start", line[0]);
                    response.put("value", line[1]);
                    wig.add(response);

                } else if (found == true) {
                } else {

                }
            }
            Object[] myArray = wig.toArray();

            JSONArray sortedJArray = new JSONArray();
            for (Object obj : myArray) {
                sortedJArray.add(obj);
            }

            if (wig.size() == 0) {
                wig.add("");
            }
            return wig;
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString());
            wig.add(response);
            return wig;
        }
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
     * Read BAM file
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public static JSONArray getBAM(long start, long end, int delta, String trackId, String reference) throws Exception {
        log.info("\n\nget bam");
        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        log.info("\n\n1");
        Path path = Paths.get(trackId);


        try {
            BAMFileReader reader = new BAMFileReader(path);
            log.info("\n\n2");
            log.info("\n\n3");



            Iterator<SAMEntry> result = reader.query(reference, (int) start, (int) end);
            JSONObject read = new JSONObject();

            log.info("\n\n4 " + result.toString());
            int start_pos, end_pos;
//            int count_break = 0;
//            boolean detailed = true;
//            counter :while (result.hasNext()) {
//                count_break++;
//                if(count_break > 1000){
//                    detailed = false ;
//                    log.info("\n\n if "+detailed);
//                    break counter;
//                }
//            }
//            if (count_break <= 5000) {
                SAMEntry record;
                while (result.hasNext()) {
                    log.info("\nloop");
                    record = result.next();
                    start_pos = record.getAlignmentStart();
                    end_pos = record.getAlignmentEnd();
                    read.put("start", start_pos);
                    read.put("end", end_pos);
                    read.put("desc", record.getReadName());
                    read.put("layer", ends.size());
                    read.put("cigars", record.getCigarString());
                    for (int i = 0; i < ends.size(); i++) {
                        if (start_pos - ends.get(i) >= delta) {
                            ends.remove(i);
                            ends.add(i, end_pos);
                            read.put("layer", i + 1);
                            break;
                        } else if ((start_pos - ends.get(i) < delta) && (i + 1) == ends.size()) {
                            if (i == 0) {
                                read.put("layer", ends.size());
                                ends.add(i, end_pos);
                            } else {
                                read.put("layer", ends.size());
                                ends.add(ends.size(), end_pos);
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                    wig.add(read);
                }
//            } else {
//                log.info("\n\n here else "+detailed);
//
//                long diff = (end - start) / 400;
//                long temp_start, temp_end;
//                for (int i = 0; i < 400; i++) {
//                    log.info("\n\n here loop "+i);
//
//                    int count = 0;
//                    temp_start = start + (i * diff);
//                    temp_end = temp_start + diff;
//                    result = reader.query(reference, (int) temp_start, (int) temp_end);
//
//                    while (result.hasNext()) {
//                        count++;
//                    }
//
//                    read.put("start", temp_start);
//                    read.put("end", temp_end);
//                    read.put("graph", count);
//
//                }
//                return wig;
//            }

            return wig;


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            wig.add(response);
            return wig;
        }
    }


}
