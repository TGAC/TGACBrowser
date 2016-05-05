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

package uk.ac.bbsrc.tgac.browser.service.ajax.javagenomicsio;

import edu.unc.genomics.Contig;
import edu.unc.genomics.io.WigFileException;
import edu.unc.genomics.io.WigFileReader;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import edu.unc.genomics.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 22/11/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BigWigService {

//    protected static final Logger log = LoggerFactory.getLogger(BigWigService.class);
//
    /**
     * Read bigWig file
     *
     * @param start     long Start position from where track details to be extracted
     * @param end       long End position to where track details to be extracted
     * @param delta     int delta for tracks layers
     * @param trackId   String trackId its the file path and name
     * @param reference String reference for the tracks
     * @return JSONArray of tracks
     * @throws Exception
     */
    public static JSONArray getWig(long start, long end, int delta, String trackId, String reference) throws Exception, WigFileException {
        JSONArray wig = new JSONArray();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        final int MEGABYTE = (1024 * 1024);

        try {
            Path path = Paths.get(trackId);
            WigFileReader bw = WigFileReader.autodetect(path);
            Contig result;

            long span[] = new long[2];

            int bp;

            if (end - start < 5000) {
                result = bw.query(reference, (int) start, (int) end);
                float[] values = result.getValues();

                for (int j = 0; j < values.length; j++) {
                    bp = (int) start + j;
                    if (values[j] > 0 || values[j] < 0) {
                        span[0] = bp;
                        span[1] = (long) values[j];
                        wig.add(span);
                    }
                }
                return wig;
            } else {

                long diff = (end - start) / 5000;
                Interval it = new Interval(reference, (int) start, (int) end);
                long temp_start;
                long temp_end;
                double value;
                for (int i = 0; i < 5000; i++) {
                    temp_start = start + (i * diff);
                    temp_end = temp_start + diff;

                    it.setStart((int) temp_start);
                    it.setStop((int) temp_end);
                    value = bw.queryStats(it).getSum();

                    bp = (int) temp_start;

                    if (value > 0 || value < 0) {
                        span[0] = bp;
                        span[1] = (long) value;
                        wig.add(span);
                    }
                }
                bw.close();
                return wig;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long maxMemory = heapUsage.getMax() / MEGABYTE;
            long usedMemory = heapUsage.getUsed() / MEGABYTE;
            throw new Exception("Memory Use :" + usedMemory + "M/" + maxMemory + "M");
        } catch (WigFileException e) {
            e.printStackTrace();
            throw new Exception("WigFile Exception error" + e.toString() + " " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("getBigWig error" + e.toString() + " " + e.getMessage());
        }
    }
}
