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
import uk.ac.bbsrc.tgac.browser.core.store.AssemblyStore;
import uk.ac.bbsrc.tgac.browser.core.store.UtilsStore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 1-Nov-2013
 * Time: 10:49:21
 * To change this template use File | Settings | File Templates.
 */

public class SQLAssemblyDAO implements AssemblyStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLAssemblyDAO.class);

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

    public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_REGION_ATTRIB_FROM_ID = "SELECT * FROM seq_region_attrib WHERE seq_region_id = ? and attrib_type_id = 3";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";

    public static final String GET_Assembly_for_reference_SLICE = "SELECT * FROM assembly where asm_seq_region_id =? AND ( (asm_start >= ? AND asm_end <= ?) OR (asm_start <= ? AND asm_end >= ?) OR (asm_end >= ? AND asm_end <= ?) OR (asm_start >= ? AND asm_start <= ?));";

    public static final String COUNT_Assembly_for_reference = "SELECT count(asm_seq_region_id) FROM assembly where asm_seq_region_id =?";
    public static final String GET_ASSEMBLY_SIZE_SLICE = "SELECT COUNT(a.asm_seq_region_id) FROM assembly a, seq_region s WHERE a.asm_seq_region_id = ? AND a.cmp_seq_region_id = s.seq_region_id AND s.coord_system_id = ? AND ( (a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";
    public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_ASSEMBLY_SIZE_SLICE_for_ref = "SELECT count(a.asm_seq_region_id) FROM assembly a, seq_region s where a.asm_seq_region_id = ? and a.cmp_seq_region_id = s.seq_region_id and  a.asm_start >= ? and a.asm_start <= ?";
    public static final String GET_Assembly_for_reference_SIZE_SLICE = "SELECT * FROM assembly a, seq_region s WHERE a.asm_seq_region_id = ? AND a.cmp_seq_region_id = s.seq_region_id AND ( (a.asm_start >= ? AND a.asm_end <=?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_start <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?))";

    public static final String GET_Assembly = "SELECT a.asm_seq_region_id,a.cmp_seq_region_id,a.asm_start,a.asm_end, s.name, sa.value as attrib " +
            "FROM assembly a " +
            "LEFT JOIN seq_region s " +
            "ON a.cmp_seq_region_id = s.seq_region_id " +
            "LEFT JOIN seq_region_attrib sa " +
            "ON sa.seq_region_id = s.seq_region_id " +
            "WHERE " +
            "a.asm_seq_region_id = ? AND " +
            "s.coord_system_id = ? AND " +
            "((a.asm_start >= ? AND a.asm_end <=?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_start <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?)) ORDER BY a.asm_start asc, (a.asm_end-a.asm_start) desc";

    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Generates Assembly Graph information
     * Calls Assembly Graph Level when reaches to the bottom level or recursively call method itself
     *
     * @param start_pos to be added because of lower assembly level
     * @param id        Reference Id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray recursiveAssemblyGraph(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference_SLICE, new Object[]{id, start, end, start, end, end, end, start, start});

            if (maps_one.size() > 0) {
                main:
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (track_start < 0) {
                        track_start = 0;
                    }
                    if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                        track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                    }
                    int no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end, track_start, track_end, track_end, track_end, track_start, track_start}, Integer.class);

                    if (no_of_tracks < 1) {
                        start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(recursiveAssemblyGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    } else {
                        assemblyTracks.addAll(getAssemblyGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));

                    }
                }
            }
            return assemblyTracks;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Recursive Assembly Graph no result found ");
        }
    }


    /**
     * Gets Graphical information for assembly level graphs
     *
     * @param start_pos to be added because of lower assembly level
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray getAssemblyGraphLevel(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end, start, end, end, end, start, start}, Integer.class);

            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), from, to, from, to, to, to, from, from}, Integer.class);
                    eachTrack.put("start", start_pos + from);
                    eachTrack.put("end", start_pos + to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);

                    trackList.add(eachTrack);
                    from = to;
                }
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Get Assembly Graph Level no result found " + e.getMessage());
        }
    }

    /**
     * Get graphical information for assembly or call method recursive for lower level
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray getAssemblyGraph(int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();

            long from = start;
            long to = 0;
            int no_of_tracks = 0;

            for (int i = 1; i <= 200; i++) {
                JSONObject eachTrack = new JSONObject();
                to = start + (i * (end - start) / 200);
                no_of_tracks = countAssembly(id, trackId, from, to);
                eachTrack.put("start", from);
                eachTrack.put("end", to);
                eachTrack.put("graph", no_of_tracks);
                eachTrack.put("id", id);
                trackList.add(eachTrack);
                from = to;
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Get Assembly Graph no result found for id " + id + "-" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new IOException("Get Assembly Graph no result found for id " + id + "-" + e.getMessage());
        }
    }

    /**
     * Get graphical information for assembly or call method recursive for lower level
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray getAssemblyOverviewGraph(int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            List<Map<String, Object>> maps_two = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

            int no_of_tracks = 0;
            for (Map map_temp : maps_two) {
                JSONObject eachTrack = new JSONObject();
                no_of_tracks = template.queryForInt(COUNT_Assembly_for_reference, new Object[]{map_temp.get("cmp_seq_region_id")});
                eachTrack.put("start", map_temp.get("asm_start"));
                eachTrack.put("end", map_temp.get("asm_end"));
                eachTrack.put("graph", no_of_tracks);
                eachTrack.put("id", id);
                trackList.add(eachTrack);
                from = to;
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Get Assembly Graph no result found for id " + id + "-" + e.getMessage());
        }
    }


    /**
     * counts assembly or call recursive method
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return number of assembly per region
     * @throws Exception
     */
    public int countRecursiveAssembly(int id, String trackId, long start, long end) throws Exception {
        int hit_size = 0;
        try {
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference_SIZE_SLICE, new Object[]{id, start, end, start, end, end, end, start, start});

            if (maps_one.size() > 0) {
                main:
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());

                    int count = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end, track_start, track_end, track_end, track_end, track_start, track_start}, Integer.class);

                    if (count < 1) {
                        hit_size += countRecursiveAssembly(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId.replace("cs", ""), track_start, track_end);
                    } else if (count > 5000) {
                        hit_size = count;
                        break main;
                    } else {
                        hit_size += count;
                    }
                }
            }
            return hit_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("count recursive assembly " + e.getMessage());
        }
    }

    /**
     * count no of assembly present in the region
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return number of assembly
     * @throws Exception
     */
    public int countAssembly(int id, String trackId, long start, long end) throws Exception {
        try {
            int hit_size = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end, start, end, end, end, start, start}, Integer.class);

            if (hit_size == 0) {
                hit_size = countRecursiveAssembly(id, trackId, start, end);
            }
            return hit_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Count Assembly " + e.getMessage());
        }
    }


    /**
     * Gets Assembly information if not available looks into lower level calls method recursiveAssembly
     *
     * @param id
     * @param trackId
     * @param delta
     * @return JSONArray of Assembly information
     * @throws Exception
     */
    public JSONArray getAssembly(int id, String trackId, int delta, long start, long end) throws Exception {
        try {
            JSONArray trackList = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_Assembly, new Object[]{id, trackId.replace("cs", ""), start, end, start, end, end, end, start, start});
            if (maps.size() > 0) {
                trackList = getAssemblyLevel(0, maps, delta);
            } else {
                trackList = recursiveAssembly(0, id, trackId, delta, start, end);
            }
            if (trackList.size() == 0) {
                trackList.add("getHit no result found");
            }
            return trackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("getAssembly no result found " + e.getMessage());

        }
    }

    /**
     * Gets Assembly information
     * Calls Assembly Level when reaches to the bottom level or recursively call method itself
     *
     * @param start
     * @param id
     * @param trackId
     * @param delta
     * @return JSONArray with Assembly information
     * @throws IOException
     */
    public JSONArray recursiveAssembly(int start_pos, int id, String trackId, int delta, long start, long end) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference_SIZE_SLICE, new Object[]{id, start, end, start, end, end, end, start, start});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    List<Map<String, Object>> maps_two = template.queryForList(GET_Assembly, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end, track_start, track_end, track_end, track_end, track_start, track_start});
                    if (maps_two.size() > 0) {
                        assemblyTracks.addAll(getAssemblyLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), maps_two, delta));
                    } else {
                        track_start = track_start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        track_end = track_start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(recursiveAssembly(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, delta, track_start, track_end));
                    }
                }
            }
            return assemblyTracks;
        } catch (Exception e) {
            throw new IOException("recursiveAssembly no result found" + e.getMessage());
        }

    }

    /**
     * Get Assembly Level tracks
     *
     * @param start
     * @param maps_two
     * @param delta
     * @return JSONArray with Assembly information
     * @throws Exception
     */
    public JSONArray getAssemblyLevel(int start, List<Map<String, Object>> maps_two, int delta) throws Exception {
        try {
            List<Map<String, Object>> attribs;
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            JSONObject eachTrack_temp = new JSONObject();
            JSONArray assemblyTracks = new JSONArray();

            for (Map map_temp : maps_two) {
                int asm_start_pos = Integer.parseInt(map_temp.get("asm_start").toString());
                int asm_end_pos = Integer.parseInt(map_temp.get("asm_end").toString());

                eachTrack_temp.put("id", map_temp.get("cmp_seq_region_id"));
                eachTrack_temp.put("start", start + asm_start_pos - 1);
                eachTrack_temp.put("end", start + asm_end_pos - 1);
                eachTrack_temp.put("flag", false);

                eachTrack_temp.put("layer", util.stackLayerInt(ends, asm_start_pos, delta, asm_end_pos));
                ends = util.stackLayerList(ends, asm_start_pos, delta, asm_end_pos);

                eachTrack_temp.put("desc", map_temp.get("name"));
                eachTrack_temp.put("colour", map_temp.get("attrib"));
                assemblyTracks.add(eachTrack_temp);
            }
            return assemblyTracks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Assembly level " + e.getMessage());
        }
    }
}
