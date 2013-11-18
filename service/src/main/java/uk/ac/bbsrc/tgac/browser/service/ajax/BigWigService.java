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
import edu.unc.genomics.io.WigFileException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.samtools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unc.genomics.IntervalFactory;
import edu.unc.genomics.io.BigWigFileReader;
import edu.unc.genomics.io.WigFileReader;
import edu.unc.genomics.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import net.sf.samtools.seekablestream.SeekableStream.*;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 2/1/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BigWigService {

    protected static final Logger log = LoggerFactory.getLogger(BigWigService.class);

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
    public static JSONArray getBigWig(long start, long end, int delta, String trackId, String reference) throws Exception {
        JSONArray wig = new JSONArray();
        JSONObject response = new JSONObject();
        Path path = Paths.get(trackId);
        try {
            BigWigFileReader bw = new BigWigFileReader(path);
            Contig result = bw.query(reference, (int) start, (int) end);

            float[] values = result.getValues();

            for (int j = 0; j < values.length; j++) {
                JSONArray span = new JSONArray();
                int bp = (int) start + j;
                   if(values[j] > 0 || values[j] < 0 ){
                       span.add(bp);
                       span.add(values[j]);
                       wig.add(span);
                }
            }
            return wig;
        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            response.put("error", e.toString() + " " + e.getMessage());
            wig.add(response);
            return wig;
        }
    }
}
