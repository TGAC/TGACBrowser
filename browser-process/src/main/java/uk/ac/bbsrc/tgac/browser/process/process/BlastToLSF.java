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

package uk.ac.bbsrc.tgac.browser.process.process;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.browser.process.parameter.FlagParameter;
import uk.ac.bbsrc.tgac.browser.process.parameter.PathParameter;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

import java.io.File;
import java.io.PrintWriter;
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


    public BlastToLSF() {
        setQueueName("cho_blast");

        blastAccession = new FlagParameter("BlastAccession");
        // blastQuery = new FlagParameter("querystring");
        blastDB = new FlagParameter("blastdb");
        format = new FlagParameter("format");
        type = new FlagParameter("type");

        parameters = new ArrayList<ConanParameter>();

        parameters.add(blastAccession);
        // parameters.add(blastQuery);
        parameters.add(blastDB);
        parameters.add(format);
        parameters.add(type);
    }

    protected Logger getLog() {
        return log;
    }

    protected String getBLastPath() {
        return "path-to-BLAST+";
    }

    protected String getFilestPath() {
        return "path-to-File";////accessible from server";
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
            String blast_type = "";
            blast_type = parameters.get(type);
            String blastPath = getBLastPath();
            String filePath = getFilestPath();
            String blastBinary = blastPath + blast_type + " ";
            getLog().debug("Executing " + getName() + " with the following parameters: " + parameters.toString());

            StringBuilder sb = new StringBuilder();

            sb.append(blastBinary);
            sb.append(" -db " + parameters.get(blastDB));
            sb.append(" -query "+filePath+" " + parameters.get(blastAccession) + ".fa ");
            sb.append(" -out "+filePath+" " + parameters.get(blastAccession) + ".xml ");
            sb.append(" -outfmt " + parameters.get(format) + " -max_target_seqs 10");
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
}
