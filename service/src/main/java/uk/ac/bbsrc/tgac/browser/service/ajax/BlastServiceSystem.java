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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 4/20/16
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class BlastServiceSystem {

    private Logger log = LoggerFactory.getLogger(getClass());

    static String sbatch = "sbatch";
    static String dir = "/usr/users/ga002/tgacbrowser/";
    static String script = "blast.sh";
    static String squeue = "squeue";
    static String job = "job";
    static String output = "%t";
    static String noheader = "-h";
    static String output_format = "-o";
    static String scontrol = "scontrol";
    static String show = "show";

    public String submitJob(JSONObject parameters) throws IOException {
        try {
            String jobid = null;
            String cmd = sbatch + " " + dir + script + " " + parameters.get("accession").toString();
            log.info(" \n\n\n\t cmd " + cmd);
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String line = reader.readLine();
            while (line != null) {
                if (line != null && line.indexOf("Submitted") >= 0) {
                    String[] output = line.split(" ");
                    jobid = output[3];
                } else {
                    continue;
                }
                line = reader.readLine();

            }

            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }

            return jobid;
        } catch (IOException e1) {
            System.out.println("Pblm found1. " + e1.getMessage());
        } catch (InterruptedException e2) {
            System.out.println("Pblm found2.");
        }

        System.out.println("finished.");
        return null;
    }

    public static String checkJob(String id) throws IOException {
        String state = null;
        try {
            String cmd = scontrol + " " + show + " " + job + " " + id;
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String line = reader.readLine();



            while (line != null || state != null) {

                state = consoleValueByKey(line, "JobState=", "[A-Z]+");

                line = reader.readLine();
            }
            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }

        } catch (IOException e1) {
            return "Pblm found1.";
        } catch (InterruptedException e2) {
            return "Pblm found2.";
        }

        return state;
    }


    public static String consoleValueByKey(String input, String key, String regex) {
        final Pattern pattern = Pattern.compile(key + regex);
        Matcher matcher;
        String rtn = null;
            matcher = pattern.matcher(input);
            if (matcher.find()) {
                // strip the value from the value / key pair.
                rtn = matcher.group(0).substring(key.length());
            }
        return rtn;
    }

}
