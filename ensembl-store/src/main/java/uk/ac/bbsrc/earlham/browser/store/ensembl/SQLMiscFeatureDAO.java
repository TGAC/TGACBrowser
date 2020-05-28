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

package uk.ac.bbsrc.earlham.browser.store.ensembl;


import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.earlham.browser.core.store.MarkerStore;
import uk.ac.bbsrc.earlham.browser.core.store.MiscFeatureStore;
import uk.ac.bbsrc.earlham.browser.core.store.UtilsStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 18-Nov-2013
 * Time: 21:50:13
 * To change this template use File | Settings | File Templates.
 */

public class SQLMiscFeatureDAO implements MiscFeatureStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLMiscFeatureDAO.class);

    @Autowired
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public JdbcTemplate getJdbcTemplate() {
        return template;
    }

    @Autowired
    private UtilsStore util;

    public void setUtilsStore(UtilsStore util) {
        this.util = util;
    }

    public static final String GET_length_from_seqreg_id = "SELECT length FROM seq_region where seq_region_id =?";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_MARKER = "SELECT marker_feature_id as id,seq_region_start as start, seq_region_end as end " +
            "FROM marker_feature " +
            "where seq_region_id =? and analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?)) " +
            "ORDER BY start,(end-start) asc";
    public static final String GET_MARKER_SIZE = "SELECT COUNT(marker_feature_id) FROM marker_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_MARKER_SIZE_SLICE = "SELECT COUNT(marker_feature_id) FROM marker_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String COUNT_ASSEMBLIES = "SELECT COUNT(asm_seq_region_id) FROM assembly WHERE asm_seq_region_id = ?";
    public static final String GET_ALL_MISC_FEATURE = "select mf.*, ms.misc_set_id, ms.code, a.name  from misc_feature mf left join misc_feature_misc_set mfms on mf.misc_feature_id = mfms.misc_feature_id left join misc_set ms on mfms.misc_set_id = ms.misc_set_id left join misc_attrib ma on mf.misc_feature_id = ma.misc_feature_id left join attrib_type a on a.attrib_type_id = ma.attrib_type_id";
    public static final String GET_ALL_MISC_FEATURE_REF = "select distinct seq_region_id from misc_feature";
    public static final String GET_ALL_ATTRIB_TYPE = "select * from attrib_type where attrib_type_id in (select attrib_type_id from misc_attrib)";
    public static final String GET_MISC_FEATURE_BY_ID = "select mf.*, a.name  from misc_feature mf, misc_attrib ma, attrib_type a where mf.misc_feature_id = ma.misc_feature_id and a.attrib_type_id = ma.attrib_type_id and a.attrib_type_id = ?";
    public static final String GET_ATTRIB_ID_NAME = "select attrib_type_id from attrib_type where name = ?";
    public static final String GET_MISC_FEATURE_SIZE_SLICE = "SELECT COUNT(mf.misc_feature_id) FROM misc_feature mf, misc_attrib ma, attrib_type a where mf.seq_region_id =? and (mf.misc_feature_id = ma.misc_feature_id and a.attrib_type_id = ma.attrib_type_id and a.attrib_type_id = ?) and ((mf.seq_region_start >= ? AND mf.seq_region_end <= ?) OR (mf.seq_region_start <= ? AND mf.seq_region_end >= ?) OR (mf.seq_region_end >= ?  AND  mf.seq_region_end <= ?) OR (mf.seq_region_start >= ? AND mf.seq_region_start <= ?))";
    public static final String GET_MISC_FEATURE_SIZE_SLICE_FOR_ALL = "SELECT COUNT(misc_feature_id) FROM misc_feature where seq_region_id =? and ((seq_region_start >= ? AND seq_region_end <= ?)" +
            " OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ?  AND  seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)" +
            ")";
    public static final String GET_MISC_FEATURE_BY_REF = "SELECT * FROM misc_feature where seq_region_id =? order by seq_region_start";
    public static final String GET_MISC_FEATURES = "SELECT * FROM misc_feature order by seq_region_start";
    public static final String GET_FEATURE_SIZE = "SELECT COUNT(misc_feature_id) FROM misc_feature where seq_region_id =?";


    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public JSONObject getAllMiscFeatures() throws Exception {
        JSONObject eachTrack_temp = new JSONObject();
        List<Map<String, Object>> refs = template.queryForList(GET_ALL_MISC_FEATURE_REF, new Object[]{});

        for (Map<String, Object> ref : refs) {
            int seq_region_id = Integer.parseInt(ref.get("seq_region_id").toString());
            List<Map<String, Object>> features = template.queryForList(GET_MISC_FEATURE_BY_REF, new Object[]{seq_region_id});
            JSONArray trackList = new JSONArray();

            for (Map<String, Object> map : features) {
                JSONObject eachTrack = new JSONObject();
                eachTrack.put("start", map.get("seq_region_start"));
                eachTrack.put("end", map.get("seq_region_end"));
                trackList.add(eachTrack);
            }
            eachTrack_temp.put(seq_region_id, trackList);
        }

        return eachTrack_temp;
    }

    public JSONArray getAllAttrib_type() throws Exception {
        JSONObject eachTrack_temp = new JSONObject();
        JSONArray trackList = new JSONArray();
        List<Map<String, Object>> maps = template.queryForList(GET_ALL_ATTRIB_TYPE, new Object[]{});
        for (Map<String, Object> map : maps) {
            eachTrack_temp.put("id", map.get("attrib_type_id"));
            eachTrack_temp.put("name", map.get("name"));
            trackList.add(eachTrack_temp);

        }
        return trackList;


    }

    public JSONArray getMiscFeaturesbyID(String name) throws Exception {
        JSONObject eachTrack_temp = new JSONObject();
        JSONArray trackList = new JSONArray();
        int id = template.queryForInt(GET_ATTRIB_ID_NAME, new Object[]{name});
        List<Map<String, Object>> maps = template.queryForList(GET_MISC_FEATURE_BY_ID, new Object[]{id});
        for (Map<String, Object> map : maps) {
            eachTrack_temp.put("id", map.get("mis_feature_id"));
            eachTrack_temp.put("parent", map.get("seq_region_id"));
            eachTrack_temp.put("start", map.get("seq_region_start"));
            eachTrack_temp.put("end", map.get("seq_region_end"));
            eachTrack_temp.put("strand", map.get("seq_region_strand"));
            eachTrack_temp.put("line_id", id);
            trackList.add(eachTrack_temp);

        }
        return trackList;


    }


    public int countMiscFeature(int id, String trackId, long start, long end) throws Exception {
        int size = 0;
        try {
            size = template.queryForObject(GET_MISC_FEATURE_SIZE_SLICE_FOR_ALL, new Object[]{id, start, end, start, end, start, end, start, end}, Integer.class);
            return size;
        } catch (IncorrectResultSizeDataAccessException irsde) {
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Count Gene " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getFeatures(int id, String trackId, long start, long end) throws IOException {
        log.info("\n\n\n\t get features");

        try {
            String GET_Feature_by_view = "SELECT mf.misc_feature_id as id, mf.seq_region_start as start, mf.seq_region_end as end, mf.seq_region_strand as strand, a.name as description " +
                    "FROM misc_feature mf, misc_attrib ma, attrib_type a " +
                    "WHERE mf.seq_region_id = " + id + " and ((mf.seq_region_start >= " + start + " AND mf.seq_region_end <= " + end + ") OR (mf.seq_region_start <= " + start + " AND mf.seq_region_end >= " + end + ") OR (mf.seq_region_end >= " + end + "  AND mf.seq_region_start <= " + end + ") OR (mf.seq_region_start <= " + start + " AND mf.seq_region_end >= " + start + ")) " +
                    "and mf.misc_feature_id = ma.misc_feature_id and ma.attrib_type_id = a.attrib_type_id " +
                    " order by seq_region_start";
            log.info("\n\n\n\t get features " + GET_Feature_by_view);

            return template.queryForList(GET_Feature_by_view, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Get gene " + e.getMessage());
        }
    }

    public JSONArray processFeatures(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws Exception {
        try {
            log.info("\n\n\n\t process features");
            JSONArray trackList = new JSONArray();

            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);

            if (template.queryForObject(GET_FEATURE_SIZE, new Object[]{id}, Integer.class) > 0) {
                if (maps.size() > 0) {
                    ends.add(0, 0);
                    trackList = getFeatureLevel(0, maps, start, end, delta, id);
                }
//            } else {
//                String query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id = " + id + " and ((asm_start >= "+start+" AND asm_end <= "+end+") OR (asm_start <= "+start+" AND asm_end >= "+end+") OR (asm_end >= "+start+"  AND  asm_end <= "+end+") OR (asm_start >= "+start+" AND asm_start <= "+end+"))";
//                trackList = recursiveRepeat(query, 0, id, trackId, start, end, delta);
            }
            int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
            int countrepeat = countMiscFeature(id, trackId, 0, length);
            if (countrepeat < 1) {
                trackList.add("getFeature no result found");
            }
            return trackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("process repeat no result found " + e.getMessage());
        }
    }

    public JSONArray getFeatureLevel(int start_add, List<Map<String, Object>> maps, long start, long end, int delta, long id) throws Exception {
        log.info("\n\n\n\t getFeatureLevel");

        List<Integer> ends = new ArrayList<Integer>();
        JSONObject eachTrack_temp = new JSONObject();
        ends.add(0, 0);
        JSONArray trackList = new JSONArray();
        for (Map<String, Object> map : maps) {
//            String GET_HIT_addition = "SELECT if(seq_region_id =  "+id+", 0 , get_ref_coord(seq_region_id,  "+id+"))  AS start "+
//                    "FROM repeat_feature " +
//                    "WHERE repeat_feature_id = "+map.get("id");

            int start_addition = 0;// template.queryForInt(GET_HIT_addition, new Object[]{});

            int start_pos = start_add + Integer.parseInt(map.get("start").toString());
            int end_pos = start_add + Integer.parseInt(map.get("end").toString());
            if (start_addition + start >= start && start_addition + end <= end || start_addition + start <= start && start_addition + end >= end || start_addition + end >= start && start_addition + end <= end || start_addition + start >= start && start_addition + start <= end) {
                eachTrack_temp.put("id", map.get("id"));
                eachTrack_temp.put("start", start_addition + start_pos);
                eachTrack_temp.put("end", start_addition + end_pos);
                eachTrack_temp.put("flag", false);
                eachTrack_temp.put("strand", map.get("strand"));
                eachTrack_temp.put("layer", util.stackLayerInt(ends, start_pos, delta, end_pos));
                eachTrack_temp.put("desc", map.get("description"));
                String line[] = map.get("description").toString().split("\\.");
                line[0] = line[0].replaceAll("[A-Za-z]", "");
                int r = Integer.parseInt(line[0]) * 30;
                int g = Integer.parseInt(line[1]) * 2;
                int b = Integer.parseInt(line[0]) + Integer.parseInt(line[1]) * 2;
                String colour = "rgb(" + r + "," + g + "," + b + ")";
                eachTrack_temp.put("colour", colour);
                ends = util.stackLayerList(ends, start_pos, delta, end_pos);

                trackList.add(eachTrack_temp);
            }
        }
        return trackList;
    }

    public JSONArray getFeatureGraph(int id, String trackId, long start, long end) throws IOException {
        log.info("\n\n\n getGeneGraph " + trackId + " " + id);

        try {

            JSONArray trackList = new JSONArray();
            long from = start;
            long to;
            int no_of_tracks = 0;

            no_of_tracks = template.queryForObject(GET_MISC_FEATURE_SIZE_SLICE_FOR_ALL, new Object[]{id, from, end, from, end, from, end, from, end}, Integer.class);

            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_MISC_FEATURE_SIZE_SLICE_FOR_ALL, new Object[]{id, from, to, from, to, from, to, from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    trackList.add(eachTrack);
                    from = to;
                }
            } else {
//                trackList.addAll(recursiveGeneGraph(0, id, trackId, start, end));
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getGene no result found");
        }
    }

}
