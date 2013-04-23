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


  /*
    bsub -q ngs_processing
         -J $run_updateDB
         -w 'ended("$run_statsqc")'
         cd ~tgaclims/quality_control/ | perl parse_fastqc.pl
            --input $run_path_to_basecalls/Unaligned/statsDB_string.txt
            --db_config ~tgaclims/quality_control/db_connection.txt
   */

  public BlastToLSF() {
    setQueueName("cho_blast");

    blastAccession = new FlagParameter("BlastAccession");
   // blastQuery = new FlagParameter("querystring");
    blastDB = new FlagParameter("blastdb");
    format = new FlagParameter("format");
    type =  new FlagParameter("type");

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
      String blast_type = "blastn";
       blast_type = parameters.get(type);
      String blastBinary = "/data/workarea/bianx/blast+/"+blast_type+" ";
      getLog().debug("Executing " + getName() + " with the following parameters: " + parameters.toString());

      StringBuilder sb = new StringBuilder();
      //write query to file first:
  //    sb.append("echo " + parameters.get(blastQuery) + " > /net/tgac-cfs3/ifs/TGAC/browser/jobs/" + parameters.get(blastAccession) + ".fa &&");
      // do the blast
      sb.append(blastBinary);
      sb.append(" -db " +  parameters.get(blastDB));
      sb.append(" -query /net/tgac-cfs3.tgaccluster/ifs/TGAC/browser/jobs/" + parameters.get(blastAccession) + ".fa ");
      sb.append(" -out /net/tgac-cfs3.tgaccluster/ifs/TGAC/browser/jobs/" + parameters.get(blastAccession) + ".xml ");
      sb.append(" -outfmt "+parameters.get(format)+" -task "+blast_type+" -max_target_seqs 10");
//
//            .append(" -f " + parameters.get(makefilePath))
//            .append(" -C " + parameters.get(fastqPath));

      log.info("getCommand : "+sb.toString());
      return sb.toString();
    }
    catch (Exception e) {
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
