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

import java.io.IOException;
import java.util.ArrayList;
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

    public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_REGION_ATTRIB_FROM_ID = "SELECT * FROM seq_region_attrib WHERE seq_region_id = ? and attrib_type_id = 3";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_ASSEMBLY_SIZE_SLICE = "SELECT count(a.asm_seq_region_id) FROM assembly a, seq_region s where a.asm_seq_region_id = ? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? and a.asm_start >= ? and a.asm_start <= ?";

    public static final String GET_Assembly = "SELECT a.asm_seq_region_id,a.cmp_seq_region_id,a.asm_start,a.asm_end FROM assembly a, seq_region s where a.asm_seq_region_id =? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? ORDER BY asm_start ASC";

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
                    int no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end}, Integer.class);
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
                        assemblyTracks.addAll(getAssemblyGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
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
                        assemblyTracks.addAll(recursiveAssemblyGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
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
            int no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
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
            int no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), from, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);
                    trackList.add(eachTrack);
                    from = to;
                }
            } else {
                trackList.addAll(recursiveAssemblyGraph(0, id, trackId, start, end));
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
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end}, Integer.class) > 0) {
                        hit_size += template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", ""), track_start, track_end}, Integer.class);
                    } else {
                        hit_size += countRecursiveAssembly(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
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
            int hit_size = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end}, Integer.class);

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
    public JSONArray getAssembly(int id, String trackId, int delta) throws Exception {
        try {
            JSONArray trackList = new JSONArray();
            List<Integer> ends = new ArrayList<Integer>();
            List<Map<String, Object>> maps = template.queryForList(GET_Assembly, new Object[]{id, trackId.replace("cs", "")});
            if (maps.size() > 0) {
                ends.add(0, 0);
                trackList = getAssemblyLevel(maps, ends, delta);
            } else {
                trackList = recursiveAssembly(0, id, trackId, delta);
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
    public JSONArray recursiveAssembly(int start, int id, String trackId, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_Assembly, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId.replace("cs", "")});
                    JSONObject eachTrack_temp = new JSONObject();
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(getAssemblyLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), maps_two, delta));
                    } else {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveAssembly(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, delta));
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
                eachTrack_temp.put("id", map_temp.get("cmp_seq_region_id"));
                eachTrack_temp.put("start", start + Integer.parseInt(map_temp.get("asm_start").toString()) - 1);
                eachTrack_temp.put("end", start + Integer.parseInt(map_temp.get("asm_end").toString()) - 1);
                eachTrack_temp.put("flag", false);
                for (int i = 0; i < ends.size(); i++) {
                    if ((Integer.parseInt(map_temp.get("asm_start").toString()) - ends.get(i)) > delta) {
                        ends.remove(i);
                        ends.add(i, Integer.parseInt(map_temp.get("asm_end").toString()));
                        eachTrack_temp.put("layer", i + 1);
                        break;
                    } else if ((Integer.parseInt(map_temp.get("asm_start").toString()) - ends.get(i) <= delta && (i + 1) == ends.size()) || Integer.parseInt(map_temp.get("asm_start").toString()) == ends.get(i)) {
                        if (i == 0) {
                            eachTrack_temp.put("layer", ends.size());
                            ends.add(i, Integer.parseInt(map_temp.get("asm_end").toString()));
                        } else {
                            ends.add(ends.size(), Integer.parseInt(map_temp.get("asm_end").toString()));
                            eachTrack_temp.put("layer", ends.size());
                        }
                        break;
                    }
                }
                eachTrack_temp.put("desc", template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{map_temp.get("cmp_seq_region_id")}, String.class));
                attribs = template.queryForList(GET_SEQ_REGION_ATTRIB_FROM_ID, new Object[]{map_temp.get("cmp_seq_region_id")});
                for (Map attrib : attribs) {
                    eachTrack_temp.put("colour", attrib.get("value"));
                }
                assemblyTracks.add(eachTrack_temp);
            }
            return assemblyTracks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Assembly level " + e.getMessage());
        }
    }

    /**
     * Get Assembly Level tracks
     *
     * @param maps
     * @param ends
     * @param delta
     * @return JSONArray with Assembly information
     * @throws Exception
     */
    public JSONArray getAssemblyLevel(List<Map<String, Object>> maps, List<Integer> ends, int delta) throws Exception {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> attribs;

            for (Map map : maps) {
                JSONObject eachTrack = new JSONObject();
                eachTrack.put("id", map.get("cmp_seq_region_id"));
                eachTrack.put("start", map.get("asm_start"));
                eachTrack.put("end", map.get("asm_end"));
                eachTrack.put("flag", false);
                for (int i = 0; i < ends.size(); i++) {
                    if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i)) > delta) {
                        ends.remove(i);
                        ends.add(i, Integer.parseInt(map.get("asm_end").toString()));
                        eachTrack.put("layer", i + 1);
                        break;

                    } else if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i) <= delta) && (i + 1) == ends.size()) {

                        if (i == 0) {
                            eachTrack.put("layer", ends.size());
                            ends.add(i, Integer.parseInt(map.get("asm_end").toString()));
                        } else {
                            ends.add(ends.size(), Integer.parseInt(map.get("asm_end").toString()));
                            eachTrack.put("layer", ends.size());
                        }
                        break;
                    }
                }
                eachTrack.put("desc", template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{map.get("cmp_seq_region_id")}, String.class));
                attribs = template.queryForList(GET_SEQ_REGION_ATTRIB_FROM_ID, new Object[]{map.get("cmp_seq_region_id")});
                for (Map attrib : attribs) {
                    eachTrack.put("colour", attrib.get("value"));
                }
                assemblyTracks.add(eachTrack);
            }
            return assemblyTracks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Assembly level " + e.getMessage());
        }
    }
}
