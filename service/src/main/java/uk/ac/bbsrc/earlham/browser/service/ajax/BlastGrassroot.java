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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 5/8/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Ajaxified

public class BlastGrassroot {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Return JSONObject
     * <p>
     * Generate url for NCBI BLAST submission and
     * make a HTTPCOnnection to submit a job to NCBI BLAST
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with NCBI submission id
     * @throws IOException
     */
    public JSONObject BlastSearchSequence(HttpSession session, JSONObject json) throws IOException {
        try {
            String blastdb = json.getString("blastdb");
            StringBuilder sb = new StringBuilder();
            String query = json.getString("querystring");
            String type = json.getString("type");
            String BlastAccession = json.getString("BlastAccession");
            int format = json.getInt("format");
            query = query.replaceAll(">+", "#>");

            JSONArray services = new JSONArray();
            JSONObject service = new JSONObject();

            service.put("start_service", true);
            service.put("so:alternateName", "blast-blastn");

            JSONObject parameter_set = new JSONObject();
            JSONArray parameters = new JSONArray();

            JSONObject param = new JSONObject();

            param.put("param", "job_id");
            param.put("current_value", null);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "query");
            param.put("current_value", query);


            parameters.add(param);
            param = new JSONObject();
            param.put("param", "subrange_from");
            param.put("current_value", null);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "subrange_to");
            param.put("current_value", null);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Chinese Spring");
            param.put("current_value", true);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "max_target_seqs");
            param.put("current_value", 5);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "evalue");
            param.put("current_value", 10);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "outfmt");
            param.put("current_value", format);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "word_size");
            param.put("current_value", 11);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "task");
            param.put("current_value", "megablast");

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "reward");
            param.put("current_value", 2);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "penalty");
            param.put("current_value", -3);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Fielder Chinese Spring");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Cadenza");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Kronos");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Paragon");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Robigus");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Claire");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> CS42 cdna");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> CS42 cds");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Aegilops Tauschii");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Chinese Spring CS42");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Chinses Sping CS42 5x");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Chinese Spring CS42 orthologous group sub-assemblies");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> IWGSC v2");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Draft genome of the wheat A-genome progenitor Triticum Urartu");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Synthetic W7984 WGS");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Wild Winter Wheat G3116");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Domesticated Spring Wheat DV92");
            param.put("current_value", false);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> Golden Promise");
            param.put("current_value", false);

            parameters.add(param);

            parameter_set.put("level", "simple");
            parameter_set.put("parameters", parameters);


            service.put("parameter_set", parameter_set);


            services.add(service);

            JSONObject params = new JSONObject();
            params.put("services", services);

            String urlParameters = params.toString();


            log.info("\n\n\n\n\t\t\t" + urlParameters);
//            String urlParameters = "QUERY=" + query +
//                    "&PROGRAM=" + type +
//                    "&DATABASE=" + blastdb +
//                    "&ALIGNMENT_VIEW=" + "Tabular" +
//                    "&ALIGNMENTS=" + "100";

            URL url = new URL("https://grassroots.tools/dev/public_backend");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String result = "";
            String str;

            while (null != (str = input.readLine())) {
                result += str;
            }
            input.close();
            connection.disconnect();

            JSONObject result_obj = new JSONObject();

            result_obj.put("result_string", result);
            JSONObject web = result_obj.getJSONObject("result_string");
            result_obj.put("id", web.getJSONArray("results").getJSONObject(0).getString("job_uuid"));
            result_obj.put("BlastAccession", BlastAccession);

            return result_obj;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }

    }

    /**
     * Return JSONObject
     * <p>
     * call connectNCBI to check BLAST is finished or running
     * if finished call method parseNCBI
     * </p>
     *
     * @param session an HTTPSession comes from ajax call
     * @param json    json object with key parameters sent from ajax call
     * @return JSONObject with BLAST output converted into tabular format
     * @throws IOException
     */
    public JSONObject BlastGetResult(HttpSession session, JSONObject json) throws IOException {
        try {

            String BlastAccession = json.getString("BlastAccession");
            String id = json.getString("id");

//            {"operations": {"operation": "get_service_results"}, "services": ["cc595396-4b0b-416b-adf8-07f4329f96ad"]}"

            JSONObject url_req = new JSONObject();
            JSONObject operation = new JSONObject();
            String[] ids = {id};
            operation.put("operation", 5);
//            operation.put("services", ids);
            url_req.put("operations", operation);
            url_req.put("services", ids);




            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            String urlParameters = url_req.toString();

            StringBuilder sb = new StringBuilder();
            String str;
            int i = 0;

//            sb.append(connectNCBI(urlParameters));//;new DataInputStream(connection.getInputStream());
            str = connectGrassRoot(urlParameters);

//            while (str == "running") {
//                str = connectNCBI(urlParameters);
//                if (str == "finished") {
//                    jsonArray = parseNCBI(urlParameters);
//                    break;
//                }
//            }


            String result = null;
            jsonObject.put("id", id);
            jsonObject.put("response", str);
            jsonObject.put("BlastAccession", BlastAccession);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return JSONUtils.SimpleJSONError(e.getMessage());
        }


    }

    /**
     * Return String
     * <p>
     * Connect to NCBI for BLAST result and decide from RegEx that BLAST finished or running
     * </p>
     *
     * @param urlParameters String urlParameters for NCBI connection
     * @return String running/finished
     */
    private String connectGrassRoot(String urlParameters) {

        try {

            log.info("\n\n\n\n\t connectNCBI "+ urlParameters);
            URL url = new URL("https://grassroots.tools/dev/public_backend");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String str;
            String result = "";

            while (null != (str = input.readLine())) {
                result += str;
            }

            JSONArray status = new JSONArray();

            input.close();
            connection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            String sb = e.toString();
            return sb;
        }
    }

}
