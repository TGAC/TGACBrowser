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

package uk.ac.bbsrc.tgac.browser.store.blastmanager;


import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.blastmanager.store.BLASTManagerStore;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:10:23
 * To change this template use File | Settings | File Templates.
 */

public class BLASTManagerDAO implements BLASTManagerStore {
    protected static final Logger log = LoggerFactory.getLogger(BLASTManagerDAO.class);

    @Autowired
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public JdbcTemplate getJdbcTemplate() {
        return template;
    }


    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }


    public static final String COUNT_BLAST_ID_FROM_PARAMS = "select count(*) from blast_params where blast_db = ? AND blast_seq = ? AND blast_filter = ? AND blast_type = ? AND link = ? AND output_format = ?";
    public static final String COUNT_BLAST_ID_FROM_RESULTS = "select count(*) from blast_result where id_blast = ?";
    public static final String GET_SEQ_FROM_PARAMS = "select blast_seq from blast_params where id_blast = ?";
    public static final String GET_ID_FROM_PARAMS = "select id_blast from blast_params where blast_db = ? AND blast_seq = ? AND blast_filter = ? AND blast_type = ? AND link = ? AND output_format = ? ";
    public static final String UPDATE_RESULTS = "insert into blast_result values (?,?)";
    public static final String UPDATE_STATUS = "update blast_status set status = ? where id_blast = ?";
    public static final String GET_RESULT_FROM_ID = "select * from blast_result where id_blast= ?";
    public static final String INSERT_PARAMS = "insert into blast_params values (?,?,?,?,?,?,?)";
    public static final String INSERT_STATUS = "insert into blast_status values(?,?)";


    /**
     *
     * @param query
     * @param db
     * @param link
     * @param type
     * @param filter
     * @param format
     * @return
     * @throws Exception
     */
    public boolean checkDatabase(String query, String db, String link, String type, String filter, String format) throws Exception {
        try {
            boolean check = false;
            if (format.indexOf("\"") >= 0) {
                format = format.replaceAll("\"", "");
            }
            if(query.indexOf("\\n") >= 0){
                query = query.replaceAll("\\n", "");
            }
            int str = template.queryForObject(COUNT_BLAST_ID_FROM_PARAMS, new Object[]{"'"+db+"'", "'"+query+"'", "'"+filter+"'", "'"+type+"'", "'"+link+"'", "'"+format+"'"}, Integer.class);

            if (str > 0) {
                check = true;
            }

            return check;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @param query
     * @return
     * @throws Exception
     */
    public boolean checkResultDatabase(String query) throws Exception {
        boolean check = false;

        int str = template.queryForObject(COUNT_BLAST_ID_FROM_RESULTS, new Object[]{query}, Integer.class);

        if (str > 0) {
            check = true;
        }

        return check;
    }

    /**
     *
     * @param id
     * @return
     * @throws ClassNotFoundException
     */
    public String getSeq(String id) throws ClassNotFoundException {
        String fasta = "";
        try {
            fasta = template.queryForObject(GET_SEQ_FROM_PARAMS, new Object[]{id}, String.class);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return fasta;
    }


    /**
     *
     * @param query
     * @param db
     * @param link
     * @param type
     * @param filter
     * @param format
     * @return
     * @throws Exception
     */
    public String getIDFromDatabase(String query, String db, String link, String type, String filter, String format) throws Exception {
        String id = "";

        if (format.indexOf("\"") >= 0) {
            format = format.replaceAll("\"", "");
        }

        id = template.queryForObject(GET_ID_FROM_PARAMS, new Object[]{"'"+db+"'", "'"+query+"'", "'"+filter+"'", "'"+type+"'", "'"+link+"'", "'"+format+"'"}, String.class);

        return id;
    }


    /**
     *
     * @param blastAccession
     * @param result
     * @throws Exception
     */
    public void setResultToDatabase(String blastAccession, JSONArray result) throws Exception {
        template.update(UPDATE_RESULTS, new Object[]{blastAccession, result.toString()});
    }

    /**
     *
     * @param taskId
     * @param status
     * @throws Exception
     */
    public void updateDatabase(String taskId, String status) throws Exception {
        template.update(UPDATE_STATUS, new Object[]{status, taskId});
    }

    /**
     *
     * @param id
     * @param link
     * @return
     * @throws Exception
     */
    public JSONArray getFromDatabase(String id, String link) throws Exception {
        JSONArray blasts = new JSONArray();
        Map<String, Object> rs = template.queryForMap(GET_RESULT_FROM_ID, new Object[]{id});

        JSONObject eachBlast = new JSONObject();
        String newJSONString = rs.get("result_json").toString();
        JSONObject jsnobject = new JSONObject();
        jsnobject.put("result", newJSONString);

        JSONArray jsonArray = JSONArray.fromObject(newJSONString);

        if (rs.get("result_json").toString().indexOf("No hits found") < 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                String seqregionName = explrObject.get("s_id").toString();
                int hsp_from = Integer.parseInt(explrObject.get("s_start").toString());
                int hsp_to = Integer.parseInt(explrObject.get("s_end").toString());
                eachBlast.put("q_id", explrObject.get("q_id"));
                if (link.length() > 0) {
                    eachBlast.put("s_id", "<a target=\\\"_blank\\\" href=../" + link + "/index.jsp?query=" + seqregionName + "&from=" + hsp_from + "&to=" + hsp_to + "&blasttrack=" + id + ">"
                            + seqregionName + "</a>");
                } else {
                    eachBlast.put("s_id", explrObject.get("s_id"));
                }

                eachBlast.put("identity", explrObject.get("identity"));
                eachBlast.put("aln_length", explrObject.get("aln_length"));
                eachBlast.put("mismatch", explrObject.get("mismatch"));
                eachBlast.put("gap_open", explrObject.get("gap_open"));
                eachBlast.put("q_start", explrObject.get("q_start"));
                eachBlast.put("q_end", explrObject.get("q_end"));
                eachBlast.put("s_start", explrObject.get("s_start"));
                eachBlast.put("s_end", explrObject.get("s_end"));
                eachBlast.put("e_value", explrObject.get("e_value"));
                eachBlast.put("bit_score", explrObject.get("bit_score"));
                eachBlast.put("s_db", explrObject.get("s_db"));
                eachBlast.put("sequence", explrObject.get("sequence"));
<<<<<<< HEAD
                eachBlast.put("qsequence", explrObject.get("qsequence"));
=======
>>>>>>> d0c84427f50bd87c9a10b46a1ef948a4820ffa66
                blasts.add(eachBlast);
            }
        } else {
            blasts.add(jsonArray);

        }
        return blasts;
    }

    /**
     *
     * @param id
     * @param seqRegion
     * @return
     * @throws Exception
     */
    public JSONArray getBLASTEntryFromDatabase(String id, String seqRegion) throws Exception {
        JSONArray blasts = new JSONArray();
        Map<String, Object> rs = template.queryForMap(GET_RESULT_FROM_ID, new Object[]{id});
        JSONObject eachBlast = new JSONObject();
        String newJSONString = rs.get("result_json").toString();
        JSONObject jsnobject = new JSONObject();
        jsnobject.put("result", newJSONString);

        JSONArray jsonArray = JSONArray.fromObject(newJSONString);

        if (rs.get("result_json").toString().indexOf("No hits found") < 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                String s_id = explrObject.get("s_id").toString();
                if (s_id.matches(seqRegion)) {
                    eachBlast.put("start", explrObject.get("s_start"));
                    eachBlast.put("end", explrObject.get("s_end"));
                    eachBlast.put("desc", explrObject.get("s_id"));
                    eachBlast.put("flag", false);
                    eachBlast.put("reverse", " ");
                    eachBlast.put("indels", " ");
                    eachBlast.put("score", " ");
                    blasts.add(eachBlast);
                }
            }
        } else {
            blasts.add(jsonArray);

        }
        return blasts;
    }

    /**
     *
     * @param id
     * @param query_start
     * @return
     * @throws Exception
     */
    public JSONArray getTrackFromDatabase(String id, int query_start) throws Exception {
        try {
            JSONArray blasts = new JSONArray();
            Map<String, Object> rs = template.queryForMap(GET_RESULT_FROM_ID, new Object[]{id});

            JSONObject eachBlast = new JSONObject();
            String newJSONString = rs.get("result_json").toString();
            JSONObject jsnobject = new JSONObject();
            jsnobject.put("result", newJSONString);

            JSONArray jsonArray = JSONArray.fromObject(newJSONString);

            if (rs.get("result_json").toString().indexOf("No hits found") < 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    eachBlast.put("start", query_start + Integer.parseInt(explrObject.get("start").toString()));
                    eachBlast.put("end", query_start + Integer.parseInt(explrObject.get("end").toString()));
                    eachBlast.put("desc", explrObject.get("desc"));
                    eachBlast.put("flag", false);
                    eachBlast.put("reverse", explrObject.get("reverse"));
                    eachBlast.put("indels", explrObject.get("indels"));
                    eachBlast.put("score", explrObject.get("score"));
                    blasts.add(eachBlast);
                }
            } else {
                blasts.add(jsonArray);
            }
            return blasts;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     *
     * @param taskId
     * @param query
     * @param db
     * @param link
     * @param type
     * @param filter
     * @param format
     * @throws Exception
     */
    public void insertintoDatabase(String taskId, String query, String db, String link, String type, String filter, String format) throws Exception {
        if (format.indexOf("\"") >= 0) {
            format = format.replaceAll("\"", "");
        }
        template.update(INSERT_STATUS, new Object[]{taskId,"RUNNING"});
        template.update(INSERT_PARAMS, new Object[]{taskId, db, query, filter, type, link, format});
    }
}


