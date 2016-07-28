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

package uk.ac.bbsrc.earlham.browser.process.process;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.earlham.browser.process.parameter.FlagParameter;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 31/05/12
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
@ServiceProvider
public class BlastToLSF extends AbstractTgacLsfProcess {

    private Logger log = LoggerFactory.getLogger(getClass());

    private final Collection<ConanParameter> parameters;
    private final FlagParameter blastAccession;
    // private final FlagParameter blastQuery;
    private final FlagParameter blastDB;
    private final FlagParameter format;
    private final FlagParameter type;
    private final FlagParameter params;


    public BlastToLSF() {
        setQueueName("webservices");



        blastAccession = new FlagParameter("BlastAccession");
        // blastQuery = new FlagParameter("querystring");
        blastDB = new FlagParameter("blastdb");
        format = new FlagParameter("format");
        type = new FlagParameter("type");
        params = new FlagParameter("params");

        parameters = new ArrayList<ConanParameter>();

        parameters.add(blastAccession);
        // parameters.add(blastQuery);
        parameters.add(blastDB);
        parameters.add(format);
        parameters.add(type);
        parameters.add(params);
    }

    protected Logger getLog() {
        return log;
    }

    @Override
    protected String getComponentName() {
        return "blastn";
    }

    @Override
    protected String getLSFOptions(Map<ConanParameter, String> parameters) {
        return "-J " + parameters.get(blastAccession) + "_blast";
    }

    @Override
    protected String getCommand(Map<ConanParameter, String> parameters) {
        try {
            log.info("\n\nget command\n");

//            String blast_type = "";
            String misc_params = "";
//            blast_type = parameters.get(type);
            misc_params = parameters.get(params);
//            String blastBinary = "/data/workarea/bianx/blast+/" + blast_type + " ";
            getLog().debug("Executing " + getName() + " with the following parameters: " + parameters.toString() + " " + misc_params);

            StringBuilder sb = new StringBuilder();
            File file = new File("/tgac/services/browser/working/" + parameters.get(blastAccession).toString() + ".fa");

            FileWriter writer = new FileWriter(file, true);

            PrintWriter output = new PrintWriter(writer);

            output.print(getSeq(parameters.get(blastAccession).toString()));

            output.close();
            sb.append("perl /tgac/services/browser/software/script/BLAST/BLASTCommand.pl " +
                    "'" + parameters.get(type) + "' " +
                    "'" + parameters.get(blastDB) + "' " +
                    "'" + parameters.get(blastAccession) + "' " +
                    "'" + parameters.get(params) + "' " +
                    "'" + parameters.get(format) + "' " +
                    "> /tgac/services/browser/working/" + parameters.get(blastAccession) + ".txt");
            return sb.toString();
        } catch (Exception e) {
            return ("Exception: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "blast_to_lsf";
    }

    @Override
    public Collection<ConanParameter> getParameters() {
        return parameters;
    }

    public String getSeq(String id) throws ClassNotFoundException {
        String fasta = "";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection conn = DriverManager.getConnection("jdbc:mysql://tgac-db1.nbi.ac.uk:3306/thankia_blast_manager", "tgacbrowser", "tgac_bioinf");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select blast_seq from blast_params where id_blast=\"" + id + "\"");

            while (rs.next()) {
                fasta += rs.getString("blast_seq");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return fasta;
    }

}
