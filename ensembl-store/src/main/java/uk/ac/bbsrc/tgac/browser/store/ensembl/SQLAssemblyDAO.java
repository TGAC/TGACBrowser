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
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:10:23
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

    static String var1;
    public static final String GET_DISPLAYABLE_FROM_ANALYSIS_ID = "SELECT displayable FROM analysis_description where analysis_id =?";
    public static final String GET_DISPLAYLABLE_FROM_ANALYSIS_ID = "SELECT display_label FROM analysis_description where analysis_id =?";
    public static final String GET_LOGIC_NAME_FROM_ANALYSIS_ID = "SELECT logic_name FROM analysis where analysis_id =?";
    public static final String GET_DESCRIPTION_FROM_ANALYSIS_ID = "SELECT description FROM analysis_description where analysis_id =?";
    public static final String GET_Tracks_API = "select analysis_id, logic_name from analysis";
    public static final String Get_Tracks_Desc = "select description from analysis_description where analysis_id = ?";
    public static final String Get_Tracks_Info = "select * from analysis_description";
    public static final String GET_Tracks_Name = "select analysis_id from analysis where logic_name = ?";
    public static final String GET_TRACKS_VIEW = "select a.logic_name as name, a.analysis_id as id, ad.description, ad.display_label, ad.displayable, ad.web_data from analysis a, analysis_description ad where a.analysis_id = ad.analysis_id;";


    public static final String GET_SEQ_FROM_SEQ_REGION_ID = "SELECT sequence FROM dna WHERE seq_region_id = ?";
    public static final String GET_SEQ_REGION_ID_FROM_NAME = "SELECT seq_region_id FROM seq_region WHERE name  = ?";
    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT s.seq_region_id, s.name, s.length, cs.name as Type FROM seq_region s, coord_system cs WHERE s.name like ? and cs.coord_system_id = s.coord_system_id;";
//    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT * FROM seq_region WHERE name like ? limit 100";

    public static final String GET_SIZE_SEQ_REGION_ID_SEARCH = "SELECT count(length) FROM seq_region WHERE name like ?";
    public static final String GET_SEQ_REGION_ID_SEARCH_all = "SELECT * FROM seq_region WHERE coord_system_id = ?";
    public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_LENGTH_FROM_ID = "SELECT length FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_length_from_seqreg_id = "SELECT length FROM seq_region where seq_region_id =?";
    public static final String GET_SEQ_REGION_ID_SEARCH_For_One = "SELECT seq_region_id FROM seq_region WHERE name = ?";

    public static final String GET_SEQ_REGION_ATTRIB_FROM_ID = "SELECT * FROM seq_region_attrib WHERE seq_region_id = ? and attrib_type_id = 3";


    public static final String GET_GENE_SEARCH = "SELECT * FROM gene WHERE description like ?";
    public static final String GET_TRANSCRIPT_SEARCH = "SELECT * FROM transcript WHERE description like ?";
    public static final String GET_transcript = "SELECT transcript_id, seq_region_start, seq_region_end,description,seq_region_strand FROM transcript where gene_id =? ORDER BY seq_region_start ASC";
    //  public static final String GET_transcript = "SELECT * FROM transcript where seq_region_id =? AND analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?))";
    public static final String GET_Genes = "SELECT gene_id,seq_region_start,seq_region_end, description,seq_region_strand FROM gene where seq_region_id =? and analysis_id = ? ";//AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?))";
    public static final String GET_Gene_Details = "SELECT * FROM gene where gene_id =? and analysis_id = ?";
    public static final String GET_GO_Gene_Details = "SELECT * FROM gene where gene_id =?";
    public static final String GET_GO_Transcript_Details = "SELECT * FROM transcript where transcript_id =?";
    public static final String GET_Gene_SIZE_SLICE = "SELECT COUNT(gene_id) FROM gene where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_EXON = "SELECT seq_region_start,seq_region_end,seq_region_strand FROM exon where seq_region_id =?";
    public static final String GET_EXON_per_Gene = "SELECT e.exon_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand FROM exon e, exon_transcript et where et.exon_id = e.exon_id and et.transcript_id =  ?";
    public static final String GET_Domain_per_Gene = "SELECT * FROM transcript_attrib where transcript_id =?";
    public static final String GET_CDS_start_per_Gene = "SELECT seq_start FROM translation where transcript_id =?";
    public static final String GET_CDS_end_per_Gene = "SELECT seq_end FROM translation where transcript_id =?";
    public static final String GET_GO_Genes = "select * from gene_attrib where value like ?";
    public static final String GET_GO_Transcripts = "select * from transcript_attrib where value like ?";
    public static final String GET_GENE_SIZE = "SELECT COUNT(gene_id) FROM gene where seq_region_id =? and analysis_id = ?";
    public static final String GET_Gene_name_from_ID = "SELECT description FROM gene where gene_id =?";
    public static final String GET_Transcript_name_from_ID = "SELECT description FROM transcript where transcript_id =?";
    public static final String GET_GO_for_Genes = "select value from gene_attrib where gene_id = ?";
    public static final String GET_GO_for_Transcripts = "select value from transcript_attrib where transcript_id =  ?";

    public static final String GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID = "SELECT seq_region_start,seq_region_end,analysis_id FROM dna_align_feature where req_region_id =?";
    public static final String GET_HIT_SIZE = "SELECT COUNT(dna_align_feature_id) FROM dna_align_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_HIT_SIZE_SLICE = "SELECT COUNT(dna_align_feature_id) FROM dna_align_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_HIT = "SELECT dna_align_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end,seq_region_strand as strand,hit_start as hitstart, hit_end as hitend, hit_name as 'desc', cigar_line as cigarline FROM dna_align_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start >= ? AND seq_region_end <= ?) OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ? AND seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)) ORDER BY (end-start) desc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";


    public static final String Get_Database_information = "SELECT meta_key,meta_value from meta";// + var1;

    public static final String GET_Seq_API = "SELECT sequence FROM dna where seq_region_id = ?";

    public static final String GET_reference_for_Assembly = "SELECT * FROM assembly where cmp_seq_region_id =?";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_ASSEMBLY_SIZE_SLICE = "SELECT count(a.asm_seq_region_id) FROM assembly a, seq_region s where a.asm_seq_region_id = ? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? and a.asm_start >= ? and a.asm_start <= ?";
    public static final String GET_ASSEMBLY_SIZE = "SELECT COUNT(asm_seq_region_id) FROM assembly where asm_seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_SEQS_LIST_API = "SELECT *  FROM assembly a, seq_region s, coord_system cs  where a.asm_seq_region_id = ? AND s.seq_region_id = a.cmp_seq_region_id AND cs.coord_system_id = s.coord_system_id AND cs.attrib like '%sequence%' AND   ((a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";


    public static final String GET_Coords_sys_API = "SELECT coord_system_id,name,rank FROM coord_system where rank > ?";
    public static final String GET_coord_attrib = "SELECT attrib FROM coord_system where coord_system_id =?";
    public static final String GET_coord_sys_id = "SELECT coord_system_id FROM seq_region where seq_region_id =?";
    public static final String GET_coord_sys_name = "SELECT name FROM coord_system where coord_system_id =?";
    public static final String GET_coord_sys_id_by_name = "SELECT coord_system_id FROM seq_region where name =?";
    public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_RANK_for_COORD_SYSTEM_ID = "SELECT rank FROM coord_system where coord_system_id =?";
    public static final String CHECK_Coord_sys_attr = "select * from coord_system where coord_system_id = ? and (name like ? OR attrib like ?);";
    public static final String GET_hit_name_from_ID = "SELECT hit_name FROM dna_align_feature where dna_align_feature_id =?";
    public static final String GET_Gene_by_view = "select g.gene_id, g.seq_region_start as gene_start, g.seq_region_end as gene_end, g.seq_region_strand as gene_strand, g. description as gene_name, t.transcript_id, t.seq_region_start as transcript_start, t.seq_region_end as transcript_end, t.description as transcript_name, e.exon_id, e.seq_region_start as exon_start, e.seq_region_end as exon_end from gene g, transcript t, exon_transcript et, exon e where t.gene_id = g.gene_id and t.transcript_id = et.transcript_id and et.exon_id = e.exon_id and  g.seq_region_id = ? and g.analysis_id = ?;";//"select * from gene_view where seq_region_id = ? and analysis_id = ?;";//
    public static final String GET_Assembly = "SELECT a.asm_seq_region_id,a.cmp_seq_region_id,a.asm_start,a.asm_end FROM assembly a, seq_region s where a.asm_seq_region_id =? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? ORDER BY asm_start ASC";
    public static final String GET_REPEAT = "SELECT repeat_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand, repeat_start as repeatstart,repeat_end as repeatend, score as score FROM repeat_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?)) ORDER BY start,(end-start) asc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
    public static final String GET_REPEAT_SIZE = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_REPEAT_SIZE_SLICE = "SELECT COUNT(repeat_feature_id) FROM repeat_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_coord_attrib_chr = "SELECT coord_system_id FROM coord_system where name like ? || attrib like ?";

    //  public static final String GET_GENOME_MARKER = "SELECT * from marker_feature";
    public static final String GET_GENOME_MARKER = "select mf.marker_feature_id as id, sr.name as reference, mf.marker_id as marker_id, mf.seq_region_start as start, mf.seq_region_end as end, mf.analysis_id as analysis_id from marker_feature mf, seq_region sr where mf.seq_region_id = sr.seq_region_id;";

    public static final String GET_Tables_with_analysis_id_column = "SELECT DISTINCT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME IN ('analysis_id') AND TABLE_SCHEMA='wrightj_brachypodium_distachyon_core_10_63_12'";
    public static final String Check_feature_available = "SELECT DISTINCT analysis_id from ";// + var1;
    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

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
                        //            start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
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
            throw new IOException("getHit no result found");

        }
    }

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
            } else {


            }

            return trackList;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

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
            throw new IOException("getHit no result found");
        }
    }


    public int countRecursiveAssembly(int id, String trackId, long start, long end) {
        int hit_size = 0;
        JSONArray assemblyTracks = new JSONArray();
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
    }

    public int countAssembly(int id, String trackId, long start, long end) throws Exception{
        log.info("countassembly");
        try{
        int hit_size = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end}, Integer.class);

        if (hit_size == 0) {
            hit_size = countRecursiveAssembly(id, trackId, start, end);
        }
        return hit_size;}
        catch (Exception e){
            e.printStackTrace();
            throw new Exception("getHit no result found");
        }
    }


    public JSONArray getAssembly(int id, String trackId, int delta) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            List<Integer> ends = new ArrayList<Integer>();
            int layer = 1;
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
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

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
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }

    }

    public JSONArray getAssemblyLevel(int start, List<Map<String, Object>> maps_two, int delta) {
        log.info("assembly level 1");
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
                    log.info("if" + Integer.parseInt(map_temp.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);

                    ends.remove(i);
                    ends.add(i, Integer.parseInt(map_temp.get("asm_end").toString()));
                    eachTrack_temp.put("layer", i + 1);
                    break;

                }
//        else if ((start_pos - ends.get(i) < delta && (i + 1) == ends.size()) || start_pos == ends.get(i) ) {
                else if ((Integer.parseInt(map_temp.get("asm_start").toString()) - ends.get(i) <= delta && (i + 1) == ends.size()) || Integer.parseInt(map_temp.get("asm_start").toString()) == ends.get(i)) {
                    log.info("else" + Integer.parseInt(map_temp.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);

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
    }

    public JSONArray getAssemblyLevel(List<Map<String, Object>> maps, List<Integer> ends, int delta) {
        log.info("assembly level 2");
        JSONObject eachTrack_temp = new JSONObject();
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
                    log.info("if" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);

                    ends.remove(i);
                    ends.add(i, Integer.parseInt(map.get("asm_end").toString()));
                    eachTrack.put("layer", i + 1);

                    break;

                } else if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i) <= delta) && (i + 1) == ends.size()) {

                    if (i == 0) {
                        log.info("else if" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);
                        eachTrack.put("layer", ends.size());
                        ends.add(i, Integer.parseInt(map.get("asm_end").toString()));

                    } else {
                        log.info("else else" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);
                        ends.add(ends.size(), Integer.parseInt(map.get("asm_end").toString()));
                        eachTrack.put("layer", ends.size());

                    }
                    break;
                } else {
                    log.info("else" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);

//             continue;
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
    }


}
