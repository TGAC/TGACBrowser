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

package uk.ac.bbsrc.tgac.browser.store.ensembl;


import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.core.store.RepeatStore;
import uk.ac.bbsrc.tgac.browser.core.store.UtilsStore;

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

public class SQLRepeatDAO implements RepeatStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLRepeatDAO.class);

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
    public static final String GET_REPEAT = "SELECT repeat_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand, repeat_start as repeatstart,repeat_end as repeatend, score as score FROM repeat_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?)) ORDER BY start,(end-start) asc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
    public static final String GET_REPEAT_SIZE = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_REPEAT_SIZE_SLICE = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";

    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * @param start_pos
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public JSONArray recursiveRepeatGraph(int start_pos, int id, String trackId, long start, long end) throws Exception {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    int asm_start = Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    int asm_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());

                    long track_start = start - asm_start;
                    long track_end = end - asm_start;
                    if (track_start < 0) {
                        track_start = 0;
                    }
                    if (track_end > asm_end) {
                        track_end = asm_end;
                    }
                    int no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    if (no_of_tracks > 0) {
                        track_start = start - asm_start;
                        track_end = end - asm_start;
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > asm_end) {
                            track_end = asm_end;
                        }
                        assemblyTracks.addAll(getRepeatGraphLevel(asm_start, Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    } else {
                        track_start = start - asm_start;
                        track_end = end - asm_start;
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > asm_end) {
                            track_end = asm_end;
                        }
                        start_pos += asm_start;
                        assemblyTracks.addAll(recursiveRepeatGraph(asm_start, Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }

                }
            }
            return assemblyTracks;
        } catch (Exception e) {
            e.getMessage();
            throw new Exception("recursiveRepeatGraph no result found " + e.getMessage());
        }
    }

    /**
     * @param start_pos
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public JSONArray getRepeatGraphLevel(int start_pos, int id, String trackId, long start, long end) throws Exception {
        log.info("\n\n\n getRepeatGraphLevel");
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", start_pos + from);
                    eachTrack.put("end", start_pos + to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);

                    trackList.add(eachTrack);
                    from = to;
                }
            }
            return trackList;
        } catch (Exception e) {
            throw new IOException("getRepeatGraphLevel no result found " + e.getMessage());
        }
    }

    /**
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public JSONArray getRepeatGraph(int id, String trackId, long start, long end) throws Exception {
        log.info("\n\n\n getRepeatGraph");
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, from, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    trackList.add(eachTrack);
                    from = to;
                }
            } else {
                trackList.addAll(recursiveRepeatGraph(0, id, trackId, start, end));
            }
            return trackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("getRepeat graph no result found " + e.getMessage());
        }
    }

    /**
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     */
    public int countRecursiveRepeat(String query, int id, String trackId, long start, long end) {
        int repeat_size = 0;

        String new_query = query + ")";

        String GET_REPEAT_SIZE_SLICE_IN = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id " +
                new_query +
                " and analysis_id = " + trackId;

        int size = template.queryForInt(GET_REPEAT_SIZE_SLICE_IN, new Object[]{});
        if (size > 0) {
            repeat_size += size;//template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
        } else {
            String SQL = "SELECT count(cmp_seq_region_id) from assembly where asm_seq_region_id " + query + ")";
            int count = template.queryForInt(SQL, new Object[]{});
            String cmp_seq_region_id = "select cmp_seq_region_id from assembly where asm_seq_region_id = " + id + " limit 1";

            id = template.queryForInt(cmp_seq_region_id, new Object[]{});
            if (count > 0) {
                query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id " + query + ")";
                repeat_size += countRecursiveRepeat(query, id, trackId, 0, 0);
            }
        }


        return repeat_size;
    }

    /**
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     */
    public int countRepeat(int id, String trackId, long start, long end) {
        log.info("\n\n\n countRepeat");
        int count_size = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
        if (count_size == 0) {
            String query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id = " + id;
            count_size = countRecursiveRepeat(query, id, trackId, start, end);
        }
        return count_size;
    }

    //  @Cacheable(cacheName = "hitCache",
    //             keyGenerator = @KeyGenerator(
    //                     name = "HashCodeCacheKeyGenerator",
    //                     properties = {
    //                             @Property(name = "includeMethod", value = "false"),
    //                             @Property(name = "includeParameterTypes", value = "false")
    //                     }
    //             )
    //  )

    /**
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getRepeat(int id, String trackId, long start, long end) throws Exception {

        try {

                String GET_REPEAT = "SELECT repeat_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand, repeat_start as repeatstart,repeat_end as repeatend, score as score " +
                        "FROM repeat_feature " +
                        "WHERE seq_region_id = " + id + " AND analysis_id = " + trackId + " and ((seq_region_start >= "+start+" AND seq_region_end <= "+end+") OR (seq_region_start <= "+start+" AND seq_region_end >= "+end+") OR (seq_region_end >= "+start+"  AND  seq_region_end <= "+end+") OR (seq_region_start <= "+start+" AND seq_region_start <= "+end+"))"+
                        " order by seq_region_start";
                return template.queryForList(GET_REPEAT, new Object[]{});

        } catch (Exception e){
            e.printStackTrace();
            throw new IOException("GET Repeat "+ e.getMessage());
        }
    }

    /**
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getRepeat(String query, int id, String trackId, long start, long end) throws Exception {
        try {
            query = query + ")";
                String GET_REPEAT = "SELECT repeat_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand, repeat_start as repeatstart,repeat_end as repeatend, score as score " +
                        "FROM repeat_feature " +
                        "WHERE seq_region_id  " + query + " AND analysis_id = " + trackId +
                        " order by seq_region_start";

                return template.queryForList(GET_REPEAT, new Object[]{});

        } catch (Exception e){
            e.printStackTrace();
            throw new IOException("GET Repeat "+ e.getMessage());
        }
    }


    /**
     * @param start_pos
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @param delta
     * @return
     * @throws Exception
     */
    public JSONArray recursiveRepeat(String query, int start_pos, int id, String trackId, long start, long end, int delta) throws Exception {
        try {
            JSONArray assemblyTracks = new JSONArray();

            String new_query = query + ")";


            String GET_REPEAT_SIZE_SLICE_IN = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id " +
                    new_query +
                    " and analysis_id = " + trackId;


            int size = template.queryForInt(GET_REPEAT_SIZE_SLICE_IN, new Object[]{});
            if (size > 0) {

                assemblyTracks.addAll(getRepeatLevel(start_pos, getRepeat(query, 0, trackId, 0, 0), start, end, delta, id));
            } else {
                String SQL = "SELECT count(cmp_seq_region_id) from assembly where asm_seq_region_id " + query + ")";
                log.info("\n\n countquery = " + SQL);
                int count = template.queryForInt(SQL, new Object[]{});

                if (count > 0) {
                    query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id " + query + ")";
                    log.info("\n\n new query = " + query);
                    assemblyTracks.addAll(recursiveRepeat(query, start_pos, id, trackId, 0, 0, delta));
                }
            }

            return assemblyTracks;
        } catch (Exception e) {
            throw new IOException("recursive repeat no result found " + e.getMessage());

        }

    }

    /**
     * @param start_add
     * @param maps
     * @param start
     * @param end
     * @param delta
     * @return
     */
    public JSONArray getRepeatLevel(int start_add, List<Map<String, Object>> maps, long start, long end, int delta, long id) throws Exception {

        List<Integer> ends = new ArrayList<Integer>();
        JSONObject eachTrack_temp = new JSONObject();
        ends.add(0, 0);
        JSONArray trackList = new JSONArray();
        for (Map<String, Object> map : maps) {
            String GET_HIT_addition = "SELECT if(seq_region_id =  "+id+", 0 , get_ref_coord(seq_region_id,  "+id+"))  AS start "+
                    "FROM repeat_feature " +
                    "WHERE repeat_feature_id = "+map.get("id");

            int start_addition =  template.queryForInt(GET_HIT_addition, new Object[]{});

            int start_pos = start_add + Integer.parseInt(map.get("start").toString());
            int end_pos = start_add + Integer.parseInt(map.get("end").toString());
              if (start_addition+start >= start && start_addition+end <= end || start_addition+start <= start && start_addition+end >= end || start_addition+end >= start && start_addition+end <= end || start_addition+start >= start && start_addition+start <= end) {
                eachTrack_temp.put("id", map.get("id"));
                eachTrack_temp.put("start", start_addition+start_pos);
                eachTrack_temp.put("end", start_addition+end_pos);
                eachTrack_temp.put("flag", false);
                eachTrack_temp.put("strand", map.get("strand"));
                eachTrack_temp.put("repeatstart", start_add + Integer.parseInt(map.get("repeatstart").toString()));
                eachTrack_temp.put("repeatend", start_add + Integer.parseInt(map.get("repeatstart").toString()));
                eachTrack_temp.put("score", map.get("score"));
                eachTrack_temp.put("layer", util.stackLayerInt(ends,start_pos, delta, end_pos));
                ends = util.stackLayerList(ends,start_pos, delta, end_pos);

                trackList.add(eachTrack_temp);
            }
        }
        return trackList;
    }


    /**
     * @param maps
     * @param start
     * @param end
     * @param delta
     * @param id
     * @param trackId
     * @return
     * @throws Exception
     */
    public JSONArray processRepeat(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws Exception {
        try {

            JSONArray trackList = new JSONArray();

            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);

            if (template.queryForObject(GET_REPEAT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
                if (maps.size() > 0) {
                    ends.add(0, 0);
                    trackList = getRepeatLevel(0, maps, start, end, delta, id);
                }
            } else {
                String query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id = " + id + " and ((asm_start >= "+start+" AND asm_end <= "+end+") OR (asm_start <= "+start+" AND asm_end >= "+end+") OR (asm_end >= "+start+"  AND  asm_end <= "+end+") OR (asm_start >= "+start+" AND asm_start <= "+end+"))";
                trackList = recursiveRepeat(query, 0, id, trackId, start, end, delta);
            }
            int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
            int countrepeat = countRepeat(id, trackId, 0, length);
            if (countrepeat < 1) {
                trackList.add("getHit no result found");
            }
            return trackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("process repeat no result found " + e.getMessage());
        }
    }
}
