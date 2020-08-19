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
import uk.ac.bbsrc.earlham.browser.core.store.SimpleFeatureStore;
import uk.ac.bbsrc.earlham.browser.core.store.UtilsStore;

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

public class SQLSimpleFeatureDAO implements SimpleFeatureStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLSimpleFeatureDAO.class);

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
    public static final String COUNT_ASSEMBLIES = "SELECT COUNT(asm_seq_region_id) FROM assembly WHERE asm_seq_region_id = ?";
    public static final String GET_HIT_SIZE = "SELECT COUNT(simple_feature_id) FROM simple_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_HIT_SIZE_SLICE = "SELECT COUNT(simple_feature_id) FROM simple_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_HIT = "SELECT simple_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end,seq_region_strand as strand, display_label, score FROM simple_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start >= ? AND seq_region_end <= ?) OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ? AND seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)) ORDER BY (end-start) desc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_hit_name_from_ID = "SELECT hit_name FROM simple_feature where simple_feature_id =?";

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
                    int no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    if (no_of_tracks > 0) {
                        track_start = start - asm_start;
                        track_end = end - asm_start;
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > asm_end) {
                            track_end = asm_end;
                        }
                        assemblyTracks.addAll(getHitGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    } else {
                        track_start = start - asm_start;
                        track_end = end - asm_start;
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > asm_end) {
                            track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                        }
                        start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
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
    public int countRecursiveHit(String query, int id, String trackId, long start, long end) throws Exception {
        int hit_size = 0;
        try {
            String new_query = query + ")";

            String GET_DAF_SIZE_SLICE_IN = "SELECT COUNT(simple_feature_id) FROM simple_feature where seq_region_id " +
                    new_query +
                    " and analysis_id = " + trackId;

            int size = template.queryForInt(GET_DAF_SIZE_SLICE_IN, new Object[]{});
            int assemblies = template.queryForInt(COUNT_ASSEMBLIES, new Object[]{id});
            if (size > 0) {
                hit_size += size;
            } else if (assemblies > 0){
                String SQL = "SELECT count(cmp_seq_region_id) from assembly where asm_seq_region_id " + query + ")";
                int count = template.queryForInt(SQL, new Object[]{});
                if (count > 0){
                    String cmp_seq_region_id = "select cmp_seq_region_id from assembly where asm_seq_region_id = " + id + " limit 1";

                    if (count > 0) {
                        id = template.queryForInt(cmp_seq_region_id, new Object[]{});
                        query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id " + query + ")";
                        hit_size += countRecursiveHit(query, id, trackId, 0, 0);
                    }
                }

            }

            return hit_size;
        }  catch (IncorrectResultSizeDataAccessException irsde){
        return hit_size;
    }catch (Exception e) {
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

            int assemblies = template.queryForInt(COUNT_ASSEMBLIES, new Object[]{id});
            log.info("\n\n\n\t assembly "+assemblies+"\n\n\n\n");

            if (hit_size == 0 && assemblies > 0) {

                String query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id = " + id;

                hit_size = countRecursiveHit(query, id, trackId, start, end);
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
        try {
            String GET_HIT = "SELECT simple_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end, display_label, score " +
                    "FROM simple_feature " +
                    "WHERE seq_region_id = " + id + " AND analysis_id = " + trackId + " and (seq_region_start >= " + start + " AND seq_region_start <= " + end + ")" +
                    " order by seq_region_start";

            return template.queryForList(GET_HIT, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Get DAF " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getHit(String query, int id, String trackId, long start, long end) throws IOException {
        query = query + ")";

        try {


            String GET_HIT = "SELECT simple_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end, display_label, score  " +
                    "FROM simple_feature " +
                    "WHERE seq_region_id " + query + " AND analysis_id = " + trackId +
                    " order by seq_region_start";


            return template.queryForList(GET_HIT, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Get gene " + e.getMessage());
        }
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
    public JSONArray recursiveHit(String query, int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {

            log.info("\n\n\nrecursiveHit");

            JSONArray assemblyTracks = new JSONArray();
            String new_query = query + ")";

            String GET_DAF_SIZE_SLICE_IN = "SELECT COUNT(simple_feature_id) FROM simple_feature where seq_region_id " +
                    new_query +
                    " and analysis_id = " + trackId;


            int size = template.queryForInt(GET_DAF_SIZE_SLICE_IN, new Object[]{});
            if (size > 0) {

                assemblyTracks.addAll(getHitLevel(start_pos, getHit(query, 0, trackId, 0, 0), start, end, delta, id));
            } else {
                String SQL = "SELECT count(cmp_seq_region_id) from assembly where asm_seq_region_id " + query + ")";
                log.info("\n\n countquery = " + SQL);
                int count = template.queryForInt(SQL, new Object[]{});

                if (count > 0) {
                    query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id " + query + ")";
                    log.info("\n\n new query = " + query);
                    assemblyTracks.addAll(recursiveHit(query, start_pos, id, trackId, 0, 0, delta));
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
    public JSONArray getHitLevel(int start_pos, List<Map<String, Object>> maps_two, long start, long end, int delta, long id) throws Exception {
        try {
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            JSONObject eachTrack_temp = new JSONObject();
            JSONArray hitTracks = new JSONArray();

            for (Map map_temp : maps_two) {
                String GET_HIT_addition = "SELECT if(seq_region_id =  " + id + ", 0 , get_ref_coord(seq_region_id,  " + id + "))  AS start " +
                        "FROM simple_feature " +
                        "WHERE simple_feature_id = " + map_temp.get("id");

                int start_addition = 0;//template.queryForInt(GET_HIT_addition, new Object[]{});


                int track_start = start_pos + Integer.parseInt(map_temp.get("start").toString());
                int track_end = start_pos + Integer.parseInt(map_temp.get("end").toString());
                if (start_addition + track_start >= start && start_addition + track_end <= end || start_addition + track_start <= start && start_addition + track_end >= end || start_addition + track_end >= start && start_addition + track_end <= end || start_addition + track_start >= start && start_addition + track_start <= end) {
                    eachTrack_temp.put("id", map_temp.get("id"));
                    eachTrack_temp.put("start", start_addition + track_start);
                    eachTrack_temp.put("end", start_addition + track_end);
                    eachTrack_temp.put("flag", false);
                    if (track_end - track_start > 1) {
                        eachTrack_temp.put("layer", util.stackLayerInt(ends, Integer.parseInt(map_temp.get("start").toString()), delta, Integer.parseInt(map_temp.get("end").toString())));
                        ends = util.stackLayerList(ends, Integer.parseInt(map_temp.get("start").toString()), delta, Integer.parseInt(map_temp.get("end").toString()));
                    }
                    eachTrack_temp.put("desc", map_temp.get("display_label"));
                    String colour = "black";
                    if(map_temp.get("display_label").toString().toUpperCase().contains("75K")){
                        colour = "purple";
                    }else if(map_temp.get("display_label").toString().toUpperCase().contains("100K")){
                        colour = "orange";
                    }else if(map_temp.get("display_label").toString().toUpperCase().contains("200K")){
                        colour = "green";
                    } else if(map_temp.get("display_label").toString().toUpperCase().contains("200K")){
                        colour = "brown";
                    }
                    eachTrack_temp.put("colour" , colour);
                    eachTrack_temp.put("log10P", map_temp.get("score"));

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

            if (template.queryForObject(GET_HIT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {

                if (maps.size() > 0) {
                    trackList = getHitLevel(0, maps, start, end, delta, id);
                } else {
                }
            } else {
                String query = " in (SELECT cmp_seq_region_id from assembly where asm_seq_region_id = " + id + " and ((asm_start >= " + start + " AND asm_end <= " + end + ") OR (asm_start <= " + start + " AND asm_end >= " + end + ") OR (asm_end >= " + start + "  AND  asm_end <= " + end + ") OR (asm_start >= " + start + " AND asm_start <= " + end + "))";
                trackList = recursiveHit(query, 0, id, trackId, start, end, delta);
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

    public JSONArray getallSNPsonGene(int query, String coord, long start, long end) throws Exception {
        JSONArray snpkList = new JSONArray();

        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }

        log.info("\n\n\n\n\nanalysis_obj " + analysis_obj);
//        String SQL_query = "select * from simple_feature where seq_region_id = ? and (seq_region_start >= ? and seq_region_end <= ?) and  analysis_id in (select analysis_id from analysis where logic_name like \"%SNP%\")";
        String SQL_query = "select d.*, SUBSTRING(sequence,seq_region_start,1) as ref from simple_feature d, dna where d.seq_region_id = ? and dna.seq_region_id = d.seq_region_id and (d.seq_region_start >= ? and d.seq_region_end <= ?) and  d.analysis_id in (select analysis_id from analysis where logic_name like \"%SNP%\")";
        List<Map<String, Object>> SNPs = template.queryForList(SQL_query, new Object[]{query, start, end});

        if (SNPs.size() > 0) {
            for (Map SNP : SNPs) {
                SNP.put("info", analysis_obj.get(SNP.get("analysis_id").toString()));
            }
        } else {
            snpkList.addAll(recurvsive_getallSNPsonGene(0, query, coord, start, end));
        }
        snpkList.addAll(SNPs);
        log.info("\n\n\n\n\t final "+snpkList.toString());
        return snpkList;

    }

    public JSONArray recurvsive_getallSNPsonGene(int start_pos, int query, String coord, long start, long end) throws Exception {

        log.info("\n\n\n\n\nrecursive  " + query);


        JSONArray snpkList = new JSONArray();

        List<Map<String, Object>> assembly = template.queryForList(GET_Assembly_for_reference, new Object[]{query});

        String SQL_query = "select d.*, SUBSTRING(sequence,seq_region_start,1) as ref from simple_feature d, dna where d.seq_region_id = ? and dna.seq_region_id = d.seq_region_id and (d.seq_region_start >= ? and d.seq_region_end <= ?) and  d.analysis_id in (select analysis_id from analysis where logic_name like \"%SNP%\")";

        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }

        for (Map map : assembly) {
            start_pos += Integer.parseInt(map.get("asm_start").toString());
            int id = Integer.parseInt(map.get("cmp_seq_region_id").toString());
            List<Map<String, Object>> SNPs = template.queryForList(SQL_query, new Object[]{id, start - start_pos, end - start_pos});
            if (SNPs.size() > 0) {
                for (Map SNP : SNPs) {
                    SNP.put("info", analysis_obj.get(SNP.get("analysis_id").toString()));
                    SNP.put("seq_region_start", start_pos + Integer.parseInt(SNP.get("seq_region_start").toString()));
                }
            } else {
                snpkList.addAll(recurvsive_getallSNPsonGene(start_pos, id, coord, start, end));
            }
            snpkList.addAll(SNPs);
        }

        return snpkList;

    }


    public JSONArray getallSNPsonSNP(int query, String coord, long start) throws Exception {
        JSONArray snpkList = new JSONArray();
        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }

//        String SQL_query = "select * from simple_feature where seq_region_id = ? and seq_region_start = ?";
        String SQL_query = "select d.*, SUBSTRING(sequence,seq_region_start,1) as ref from simple_feature d, dna where d.seq_region_id = ? and dna.seq_region_id = d.seq_region_id and (d.seq_region_start = ?) and  d.analysis_id in (select analysis_id from analysis where logic_name like \"%SNP%\")";
        List<Map<String, Object>> maps_one = template.queryForList(SQL_query, new Object[]{query, start});
        for (Map map : maps_one) {
            map.put("info", analysis_obj.get(map.get("analysis_id").toString()));
        }
        snpkList.addAll(maps_one);
        return snpkList;

    }

    public List<Map<String, Object>> getSNPs(String[] list) throws Exception {
        JSONArray snpList = new JSONArray();
        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }


        String ids = "(" + list[0];
        for (int i = 1; i < list.length; i++) {
            ids += "," + list[1];
        }
        ids += ")";

        String SQL_query = "select d.simple_feature_id, d.analysis_id, a.display_label, d.seq_region_start, d.seq_region_id, s.name  " +
                "from simple_feature d, analysis_description a, seq_region s  " +
                "where  d.analysis_id in " + ids + " and a.analysis_id = d.analysis_id and d.seq_region_id = s.seq_region_id;";

        List<Map<String, Object>> maps_one = template.queryForList(SQL_query, new Object[]{});

        return maps_one;

    }

    public JSONArray getSNPs(String list) throws Exception {
        JSONArray snpList = new JSONArray();
        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }


//        String SQL_query = "select * from simple_feature where seq_region_id = ? and seq_region_start = ?";
//        String SQL_query = "select d.*, SUBSTRING(sequence,seq_region_start,1) as ref from simple_feature d, dna where  dna.seq_region_id = d.seq_region_id and  analysis_id =  " + list + ";";
        String SQL_query = "select d.*, SUBSTRING(sequence,seq_region_start,1) as ref from simple_feature d, dna where  dna.seq_region_id = d.seq_region_id and  analysis_id =  " + list + ";";
        log.info("\n\n\n\n SQL_Querytest 2 " + SQL_query);

        List<Map<String, Object>> maps_one = template.queryForList(SQL_query, new Object[]{});
        for (Map map : maps_one) {
            map.put("info", analysis_obj.get(map.get("analysis_id").toString()));
        }
        snpList.addAll(maps_one);
        return snpList;

    }


    public JSONArray getUniqueSNPs(String[] list1, String[] list2) throws Exception {
        JSONArray snpList = new JSONArray();
        String analysis_query = "select analysis_id, display_label from analysis_description";
        List<Map<String, Object>> analysis = template.queryForList(analysis_query, new Object[]{});
        log.info("\n\n\n\n\nanalysis_obj " + analysis.toString());

        JSONObject analysis_obj = new JSONObject();
        for (Map map : analysis) {
            analysis_obj.put(map.get("analysis_id"), map.get("display_label"));
        }


        String ids_1 = "(" + list1[0];
        for (int i = 1; i < list1.length; i++) {
            ids_1 += "," + list1[1];
        }
        ids_1 += ")";

        String ids_2 = "(" + list2[0];
        for (int i = 1; i < list2.length; i++) {
            ids_2 += "," + list2[1];
        }
        ids_2 += ")";

        String SQL_query = "select d1.seq_region_start as start1, d2.seq_region_start as start2, d1.analysis_id as id1, d2.analysis_id as id2, d1.cigar_line as cigar1, d2.cigar_line as cigar2 " +
                "from simple_feature d1, simple_feature d2 " +
                "where d1.seq_region_id = 1 " +
                "and d1.analysis_id in " + ids_1 + " " +
                "and d2.analysis_id in " + ids_2 + " " +
                "and d1.seq_region_start = d2.seq_region_start";

        List<Map<String, Object>> maps_one = template.queryForList(SQL_query, new Object[]{});
//        for (Map map : maps_one) {
//            map.put("info", analysis_obj.get(map.get("analysis_id").toString()));
//        }
        snpList.addAll(maps_one);
        return snpList;

    }


}