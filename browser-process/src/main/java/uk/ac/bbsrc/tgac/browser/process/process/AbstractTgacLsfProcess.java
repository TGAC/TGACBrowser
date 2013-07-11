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

import uk.ac.ebi.fgpt.conan.lsf.AbstractLSFProcess;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 14/10/11
 * @since 0.1.6
 */
public abstract class AbstractTgacLsfProcess extends AbstractLSFProcess {
  private String bsubPath = "/export/lsf/7.0/linux2.6-glibc2.3-x86_64/bin/bsub -o out";// -R 'select[pg<1.0 && ut < 5]'";

  @Override
  protected String getLSFOutputFilePath(Map<ConanParameter, String> parameters) throws IllegalArgumentException {
    final File parentDir = new File(System.getProperty("user.home"));

    // files to write output to
    File outputDir = new File(parentDir, ".conan");

    for (ConanParameter parameter : parameters.keySet()) {
      if (parameter.getName().contains("Accession")) {
        outputDir = new File(new File(parentDir, ".conan"), parameters.get(parameter));
        break;
      }
    }

    // lsf output file
    return new File(outputDir, getName() + ".lsfoutput.txt").getAbsolutePath();
  }
}
