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

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.earlham.browser.blastmanager.store.BLASTManagerStore;

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
public class BlastServiceLocalSystem {

    @Autowired
    private BLASTManagerStore blastManagerStore;

    public void setBlastManagerStore(BLASTManagerStore blastManagerStore) {
        this.blastManagerStore = blastManagerStore;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    static String dir = "/usr/local/bin/scripts/";
    static String script = "blast.sh";
    static String job = "job";
    static String output = "%t";
    static String noheader = "-h";
    static String output_format = "-o";
    static String show = "show";

    public String submitJob(JSONObject parameters) throws IOException {
        try {
            log.info("\n\n\n\t accesstion "+ parameters.get("accession").toString());
            log.info("\n\n\n\t url "+ parameters.get("url").toString());
            log.info("\n\n\n\t db "+ parameters.get("db").toString());
            log.info("\n\n\n\t usrname "+ parameters.get("usrname").toString());
            log.info("\n\n\n\t pwd "+ parameters.get("pwd").toString());
            String cmd = dir + script + " " + parameters.get("accession").toString()+ " " + parameters.get("url").toString()+ " "+ parameters.get("db").toString()+ " " + parameters.get("usrname").toString()+ " " + parameters.get("pwd").toString();
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String error = stdError.readLine();
            while (error != null) {
                System.out.println("\t\t\t" + error);
            }

            String line = reader.readLine();
            while (line != null) {
                log.info("\n\n line "+line);
                line = reader.readLine();
            }
            return parameters.get("accession").toString();
        } catch (IOException e1) {
            System.out.println("Pblm found1. " + e1.getMessage());
        } catch (InterruptedException e2) {
            System.out.println("Pblm found2.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String consoleValueByKey(String input, String key, String regex) {
        final Pattern pattern = Pattern.compile(key + regex);
        Matcher matcher;
        String rtn = null;
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            rtn = matcher.group(0).substring(key.length());
        }
        return rtn;
    }

}
