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


import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.core.store.DafStore;
import uk.ac.bbsrc.tgac.browser.core.store.UtilsStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 01-Nov-2013
 * Time: 11:58:55
 * To change this template use File | Settings | File Templates.
 */

public class SQLDafDAO implements DafStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLDafDAO.class);

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
    public static final String GET_HIT_SIZE = "SELECT COUNT(dna_align_feature_id) FROM dna_align_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_HIT_SIZE_SLICE = "SELECT COUNT(dna_align_feature_id) FROM dna_align_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_HIT = "SELECT dna_align_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end,seq_region_strand as strand,hit_start as hitstart, hit_end as hitend, hit_name as 'desc', cigar_line as cigarline FROM dna_align_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start >= ? AND seq_region_end <= ?) OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ? AND seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)) ORDER BY (end-start) desc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_hit_name_from_ID = "SELECT hit_name FROM dna_align_feature where dna_align_feature_id =?";

    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * @param hitID
     * @return
     * @throws IOException
     */
    public String getHitNamefromId(int hitID) throws IOException {
        try {
            String str = template.queryForObject(GET_hit_name_from_ID, new Object[]{hitID}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getHitNamefromId no result found" + e.getMessage());

        }
    }


    /**
     * Generates hit Graph information
     * Calls Hit Graph Level when reaches to the bottom level or recursively call method itself
     *
     * @param start_pos to be added because of lower assembly level
     * @param id        Reference Id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with hit graph information
     * @throws IOException
     */
    public JSONArray recursiveHitGraph(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (track_start < 0) {
                        track_start = 0;
                    }
                    if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                        track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                    }
                    int no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    if (no_of_tracks > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                            track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                        }
                        assemblyTracks.addAll(getHitGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    } else {
                        track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                            track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                        }
                        List<Integer> ends = new ArrayList<Integer>();
                        start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveHitGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }

                }
            }
            return assemblyTracks;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Recursive Hit no result found" + e.getMessage());
        }
    }

    /**
     * Gets Graphical information for hit level graphs
     *
     * @param start_pos to be added because of lower assembly level
     * @param id        Reference Id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with hit graph information
     * @throws IOException
     */
    public JSONArray getHitGraphLevel(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", start_pos + from);
                    eachTrack.put("end", start_pos + to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);

                    trackList.add(eachTrack);
                    from = to;
                }
            } else {


            }

            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("GetHit graph level no result found" + e.getMessage());
        }
    }

    /**
     * Get graphical information for hit or call method recursive for lower level
     *
     * @param id      Reference Id
     * @param trackId analysis id
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray getHitGraph(int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, from, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);
                    trackList.add(eachTrack);
                    from = to;
                }
            } else {
                trackList.addAll(recursiveHitGraph(0, id, trackId, start, end));
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getHitGraph no result found" + e.getMessage());
        }
    }


    /**
     * counts hit or call recursive method
     *
     * @param id      Reference Id
     * @param trackId
     * @param start
     * @param end
     * @return number of hit per region
     */
    public int countRecursiveHit(int id, String trackId, long start, long end) throws Exception {
        try {
            int hit_size = 0;
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class) > 0) {
                        hit_size += template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    } else {
                        hit_size += countRecursiveHit(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                    }
                }
            }

            return hit_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("count recursive hit " + e.getMessage());
        }
    }

    /**
     * count no of hits present in the region
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return number of hits
     * @throws Exception
     */
    public int countHit(int id, String trackId, long start, long end) throws Exception {
        try {
            int hit_size = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);

            if (hit_size == 0) {
                hit_size = countRecursiveHit(id, trackId, start, end);
            }
            return hit_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Count Hit " + e.getMessage());
        }
    }

    //
//    @Cacheable(cacheName = "hitCache",
//             keyGenerator = @KeyGenerator(
//                     name = "HashCodeCacheKeyGenerator",
//                     properties = {
//                             @Property(name = "includeMethod", value = "false"),
//                             @Property(name = "includeParameterTypes", value = "false")
//                     }
//             )
//  )

    /**
     * Gets Hit information
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray of Hit information
     * @throws IOException
     */
    public List<Map<String, Object>> getHit(int id, String trackId, long start, long end) throws IOException {
        return template.queryForList(GET_HIT, new Object[]{id, trackId, start, end, start, end, end, end, start, start});
    }

    /**
     * Gets Hit information
     * Calls Hit Level when reaches to the bottom level or recursively call method itself
     *
     * @param start_pos
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @param delta
     * @return JSONArray with Hit information
     * @throws IOException
     */
    public JSONArray recursiveHit(int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_HIT_SIZE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId});
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(getHitLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), getHit(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end), start, end, delta));
                    } else {
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveHit(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end, delta));
                    }

                }
            }
            return assemblyTracks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("recursivetHit no result found " + e.getMessage());
        }
    }

    /**
     * Get Hit Level tracks
     *
     * @param start_pos
     * @param maps_two
     * @param start
     * @param end
     * @param delta
     * @return JSONArray with Assembly information
     * @throws Exception
     */
    public JSONArray getHitLevel(int start_pos, List<Map<String, Object>> maps_two, long start, long end, int delta) throws Exception {
        try {
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            JSONObject eachTrack_temp = new JSONObject();
            JSONArray hitTracks = new JSONArray();
            for (Map map_temp : maps_two) {
                int track_start = start_pos + Integer.parseInt(map_temp.get("start").toString()) - 1;
                int track_end = start_pos + Integer.parseInt(map_temp.get("end").toString()) - 1;
                if (track_start >= start && track_end <= end || track_start <= start && track_end >= end || track_end >= start && track_end <= end || track_start >= start && track_start <= end) {
                    eachTrack_temp.put("id", map_temp.get("id"));
                    eachTrack_temp.put("start", track_start);
                    eachTrack_temp.put("end", track_end);
                    eachTrack_temp.put("flag", false);
                    if (map_temp.get("cigarline") != null) {
                        eachTrack_temp.put("cigarline", map_temp.get("cigarline").toString());
                    }
                    if (track_end - track_start > 1) {
                        eachTrack_temp.put("layer", util.stackLayerInt(ends,Integer.parseInt(map_temp.get("start").toString()), delta, Integer.parseInt(map_temp.get("end").toString())));
                        ends = util.stackLayerList(ends,Integer.parseInt(map_temp.get("start").toString()), delta, Integer.parseInt(map_temp.get("end").toString()));
                    }
                    eachTrack_temp.put("desc", map_temp.get("desc"));
                    hitTracks.add(eachTrack_temp);
                }
            }
            return hitTracks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("getHitLevel " + e.getMessage());
        }
    }


    /**
     * process hits returns from getHit
     *
     * @param maps    hits returned from getHit
     * @param start
     * @param end
     * @param delta
     * @param id
     * @param trackId
     * @return JSONArray with Hit information
     * @throws Exception
     */
    public JSONArray processHit(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws Exception {
        try {
            JSONArray trackList = new JSONArray();

            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            if (template.queryForObject(GET_HIT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {

                if (maps.size() > 0) {
                    ends.add(0, 0);
                    trackList = getHitLevel(0, maps, start, end, delta);
                } else {
                }
            } else {
                trackList = recursiveHit(0, id, trackId, start, end, delta);
            }
            if (trackList.size() == 0) {
                int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
                int counthit = countHit(id, trackId, 0, length);
                if (counthit < 1) {
                    trackList.add("getHit no result found");
                }
            }

            return trackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("processHit no result found " + e.getMessage());
        }
    }
}