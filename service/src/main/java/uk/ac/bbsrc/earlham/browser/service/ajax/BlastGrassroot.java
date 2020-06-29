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
import java.util.Collections;
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
     * Method to get Params from Grassroot backend
     * At the moment not in use
     * @param session
     * @param json
     * @return
     * @throws IOException
     */
    public JSONObject getParams(HttpSession session, JSONObject json) throws IOException {

        JSONObject query = new JSONObject();

        JSONObject response = new JSONObject();
        JSONObject operation = new JSONObject();
        JSONArray services = new JSONArray();
        JSONObject service = new JSONObject();

        operation.put("operation", "get_named_service");
        service.put("so:alternateName","blast-blastn");
        services.add(service);

        query.put("operations", operation);
        query.put("services", services);
        response.put("query", query);

        String urlParameters = query.toString();

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
        response.put("response", result);

        JSONObject result_obj = response.getJSONObject("response");

        JSONArray groups = result_obj.getJSONArray("services").getJSONObject(0).getJSONObject("operation").getJSONObject("parameter_set").getJSONArray("groups");

        JSONArray parameters = result_obj.getJSONArray("services").getJSONObject(0).getJSONObject("operation").getJSONObject("parameter_set").getJSONArray("parameters");

        response.put("parameters", parameters);
        JSONObject params = new JSONObject();
        for (int i=0; i< parameters.size(); i++){
            JSONObject param = parameters.getJSONObject(i);
            String key = param.getString("group");
            if(params.containsKey(key)){
                params.getJSONArray(key).addAll(Collections.singleton(param));
            }else{
                params.put(key,Collections.singleton(param));
            }
        }

        response.put("groups", groups);
        response.put("params", params);

        return response;
    }

    /**
     * Return JSONObject
     * <p>
     * Generate url for Grassroot BLAST submission and
     * make a HTTPCOnnection to submit a job to Grassroot BLAST
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
            JSONObject in_params = json.getJSONObject("params");
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
            param.put("current_value", in_params.getInt("word_size"));

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "task");
            param.put("current_value", type);

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "reward");
            param.put("current_value", in_params.getInt("reward"));

            parameters.add(param);
            param = new JSONObject();
            param.put("param", "penalty");
            param.put("current_value", in_params.getInt("penalty"));

            parameters.add(param);

            param = new JSONObject();
            param.put("param", "Available Databases provided by EI Grassroots development server -> IWGSC v1.0");

            param.put("current_value", true);

            parameters.add(param);

            parameter_set.put("level", "simple");
            parameter_set.put("parameters", parameters);


            service.put("parameter_set", parameter_set);


            services.add(service);

            JSONObject params = new JSONObject();
            params.put("services", services);

            String urlParameters = params.toString();

            JSONObject result_obj = new JSONObject();

            log.info("\n\n\n\n\t\t\t urlParameters" + urlParameters);
            result_obj.put("urlParameters", urlParameters);


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


            result_obj.put("result_string", result);
            JSONObject web = result_obj.getJSONObject("result_string");
            result_obj.put("result", result);//.getJSONArray("results").getJSONObject(0).getString("job_uuid"));
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
     * call connectGrassRoot to check BLAST is finished or running
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

            JSONObject url_req = new JSONObject();
            JSONObject operation = new JSONObject();
            String[] ids = {id};
            operation.put("operation", 5);
            url_req.put("operations", operation);
            url_req.put("services", ids);

            JSONObject jsonObject = new JSONObject();
            String urlParameters = url_req.toString();

            String str;
            str = connectGrassRoot(urlParameters);

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
     * Connect to Grassroot for BLAST result and decide from RegEx that BLAST finished or running
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
