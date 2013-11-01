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
import uk.ac.bbsrc.tgac.browser.core.store.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:10:23
 * To change this template use File | Settings | File Templates.
 */

public class SQLSequenceDAO implements SequenceStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLSequenceDAO.class);

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







    public String getSeqBySeqRegionId(int searchQuery) throws IOException {
        try {
            String str = template.queryForObject(GET_SEQ_FROM_SEQ_REGION_ID, new Object[]{searchQuery}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getSeqBySeqRegionId no result found"+e.getMessage());

        }
    }

    private boolean checkCoord(int id, String str) {
        boolean check = false;
        int cood_sys_id = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{id}, Integer.class);
        List<Map<String, Object>> maps = template.queryForList(CHECK_Coord_sys_attr, new Object[]{cood_sys_id, '%' + str + '%', '%' + str + '%'});
        if (maps.size() > 0) {
            check = true;
        }
        return check;
    }


    public Integer getSeqRegionCoordId(String query) throws IOException {
        try {
            int coord_id = Integer.parseInt(template.queryForObject(GET_coord_sys_id_by_name, new Object[]{query}, String.class));
            return coord_id;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getSeqRegionCoordId"+e.getMessage());
//            return 0;
        }
    }

    public int getPositionOnReference(int id, int pos) {
        log.info("getpositiononref " + id + ":" + pos);
        if (checkCoord(id, "chr")) {

        } else {
            List<Map<String, Object>> maps = template.queryForList(GET_reference_for_Assembly, new Object[]{id});
            for (Map map : maps) {
                if (checkCoord(Integer.parseInt(map.get("asm_seq_region_id").toString()), "chr")) {
                    pos += Integer.parseInt(map.get("asm_start").toString());
                } else {
                    pos += getPositionOnReference(Integer.parseInt(map.get("asm_seq_region_id").toString()), Integer.parseInt(map.get("asm_start").toString()));
                }
            }
        }
        return pos;
    }

    public int getAssemblyReference(int id) {
        log.info("getassemblyreference" + id);
        int ref_id = 0;
        if (checkCoord(id, "chr")) {
            ref_id = id;
        } else {
            List<Map<String, Object>> maps = template.queryForList(GET_reference_for_Assembly, new Object[]{id});
            for (Map map : maps) {
                if (checkCoord(Integer.parseInt(map.get("asm_seq_region_id").toString()), "chr")) {
                    log.info("if" + map.get("asm_seq_region_id").toString());
                    ref_id = Integer.parseInt(map.get("asm_seq_region_id").toString());
                } else {
                    log.info("else" + map.get("asm_seq_region_id").toString());

                    ref_id = getAssemblyReference(Integer.parseInt(map.get("asm_seq_region_id").toString()));
                }
            }
        }
        return ref_id;
    }



    public JSONArray getSeqRegionSearchMap(String searchQuery) throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> attrib_temp = template.queryForList(GET_coord_attrib_chr, new Object[]{"%chr%", "%chr%"});
            JSONObject eachName = new JSONObject();
            if (attrib_temp.size() > 0) {
                List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH_all, new Object[]{attrib_temp.get(0).get("coord_system_id").toString()});
                for (Map map : maps) {
                    eachName.put("name", map.get("name"));
                    eachName.put("seq_region_id", map.get("seq_region_id"));
                    eachName.put("length", map.get("length"));
                    names.add(eachName);
                }
            }
            return names;
        } catch (EmptyResultDataAccessException e) {
            //     return getGOSearch(searchQuery);
            e.printStackTrace();

            throw new IOException("Seq region search map result not found"+e.getMessage());
        }
    }

    public JSONArray getSeqRegionIdSearch(String searchQuery) throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});
            for (Map map : maps) {
                names.add(map.get("seq_region_id"));
            }
            return names;
        } catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            e.printStackTrace();
            throw new IOException("seqregion id search result not found"+e.getMessage());
        }
    }


    public int getSeqRegionearchsize(String searchQuery) throws IOException {
        try {
            int maps = template.queryForObject(GET_SIZE_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'}, Integer.class);
            return maps;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("seqregion search size result not found"+e.getMessage());
        }
    }



    public Integer getSeqRegion(String searchQuery) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_REGION_ID_FROM_NAME, new Object[]{searchQuery}, Integer.class);
            return i;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public String getSeqRegionName(int searchQuery) throws IOException {
        try {
            String str = template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqRegionName no result found");

        }
    }






    public Map<String, Object> getStartEndAnalysisIdBySeqRegionId(int id) throws IOException {
        try {
            Map<String, Object> map = template.queryForMap(GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID, new Object[]{id});
            return map;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getStartEndAnalysisIdBySeqRegionI no result found");

        }
    }

    public String getSeqLengthbyId(int query) throws IOException {
        try {
            String i = template.queryForObject(GET_SEQ_LENGTH_FROM_ID, new Object[]{query}, String.class);
            return i;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqlength no result found");

        }
    }

    public JSONArray getdbinfo() throws IOException {
        JSONArray metadata = new JSONArray();
        try {
            List<Map<String, Object>> maps = template.queryForList(Get_Database_information, new Object[]{});
            JSONObject eachMeta = new JSONObject();
            for (Map map : maps) {

                String metakey = map.get("meta_key").toString();
                if (metakey.contains("name")) {
                    eachMeta.put("name", map.get("meta_value"));
                }
                if (metakey.contains("version")) {
                    eachMeta.put("version", map.get("meta_value"));
                }


            }
            metadata.add(eachMeta);
            return metadata;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqlength no result found");

        }
    }





    public Integer getSeqRegionforone(String searchQuery) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_REGION_ID_SEARCH_For_One, new Object[]{searchQuery}, Integer.class);
            return i;
        } catch (EmptyResultDataAccessException e) {
//      throw new IOException(" getSeqRegion no result found");

            return 0;
        }
    }


    public JSONArray getTableswithanalysis_id() throws IOException {
        try {
            JSONArray tableList = new JSONArray();


            List<Map<String, Object>> maps = template.queryForList(GET_Tables_with_analysis_id_column);

            for (Map map : maps) {
                JSONObject eachTable = new JSONObject();
                eachTable.put("tables", map.get("TABLE_NAME"));
                var1 = Check_feature_available + map.get("TABLE_NAME").toString();
                tableList.add(eachTable);
            }
            return tableList;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }



    public String getDomains(String geneid) throws IOException {
        JSONArray domainlist = new JSONArray();
        String Domains = "";
        try {
            JSONObject eachDomain = new JSONObject();
            List<Map<String, Object>> domains = template.queryForList(GET_Domain_per_Gene, new Object[]{geneid});

            for (Map domain : domains) {
                Domains = domain.get("value").toString();

            }

            return Domains;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("Track Description no result found");

        }
    }


    public String getSeqLevel(String query, int from, int to) throws IOException {

        String seq = "";
        try {

            seq = template.queryForObject(GET_Seq_API, new Object[]{query}, String.class);
            if (from < 0) {
                from = 0;
            }
            if (to > seq.length()) {
                to = seq.length();
            }
            return seq.substring(from, to);
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }


    public String getSeqRecursive(String query, int from, int to, int asm_from, int asm_to) throws IOException {

        try {
            String seq = "";

            List<Map<String, Object>> maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{query, from, to, from, to, from, to, from, to});
            for (Map map : maps) {
                String query_coord_temp = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{map.get("cmp_seq_region_id")}, String.class);
                String attrib_temp = template.queryForObject(GET_coord_attrib, new Object[]{query_coord_temp}, String.class);
                if (attrib_temp.indexOf("sequence") >= 0) {
                    int asm_start = Integer.parseInt(map.get("asm_start").toString());
                    int asm_end = Integer.parseInt(map.get("asm_end").toString());
                    int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                    int end_cmp = Integer.parseInt(map.get("cmp_end").toString());
                    int start_temp;
                    int end_temp;
                    if (from <= asm_start) {
                        start_temp = start_cmp;
                    } else {
                        start_temp = end_cmp - (asm_end - from) + 1;
                    }
                    if (to >= asm_end) {
                        end_temp = end_cmp;
                    } else {
                        end_temp = to - asm_start + 1;
                    }
                    seq += getSeqLevel(map.get("cmp_seq_region_id").toString(), start_temp, end_temp);
                } else {

                    maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{map.get("cmp_seq_region_id"), from, to, from, to, from, to, from, to});
                    int asm_start = Integer.parseInt(map.get("asm_start").toString());
                    int asm_end = Integer.parseInt(map.get("asm_end").toString());
                    int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                    int end_cmp = Integer.parseInt(map.get("cmp_end").toString());

                    if (from <= asm_start) {
                        from = start_cmp;
                    } else {
                        from = from - start_cmp + 1;
                    }
                    if (to >= asm_end) {
                        to = end_cmp;
                    } else {
                        to = (to - from);

                    }
                    seq += getSeqRecursive(map.get("cmp_seq_region_id").toString(), from, to, asm_from, asm_to);
                }
            }


            return seq;
        } catch (EmptyResultDataAccessException e) {
            return "";
            //      throw new IOException("Sequence not found");
        }
    }


    public String getSeq(String query, int from, int to) throws IOException {
        try {
            String seq = "";
            String query_coord = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{query}, String.class);
            String attrib = template.queryForObject(GET_coord_attrib, new Object[]{query_coord}, String.class);
            if (attrib.indexOf("sequence") >= 0) {
                seq = getSeqLevel(query, from, to);
            } else {
                List<Map<String, Object>> maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{query, from, to, from, to, from, to, from, to});
                for (Map map : maps) {

                    String query_coord_temp = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{map.get("cmp_seq_region_id")}, String.class);
                    String attrib_temp = template.queryForObject(GET_coord_attrib, new Object[]{query_coord_temp}, String.class);

                    if (attrib_temp.indexOf("sequence") >= 0) {
                        int asm_start = Integer.parseInt(map.get("asm_start").toString());
                        int asm_end = Integer.parseInt(map.get("asm_end").toString());
                        int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                        int end_cmp = Integer.parseInt(map.get("cmp_end").toString());
                        int start_temp;
                        int end_temp;
                        if (from <= asm_start) {
                            start_temp = start_cmp;
                        } else {
                            start_temp = end_cmp - (asm_end - from) + 1;
                        }
                        if (to >= asm_end) {
                            end_temp = end_cmp;
                        } else {
                            end_temp = to - asm_start + 1;
                        }

                        seq = getSeqLevel(map.get("cmp_seq_region_id").toString(), start_temp, end_temp);
                    } else {

                        maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{map.get("cmp_seq_region_id"), from, to, from, to, from, to, from, to});
                        int asm_start = Integer.parseInt(map.get("asm_start").toString());
                        int asm_end = Integer.parseInt(map.get("asm_end").toString());
                        int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                        int end_cmp = Integer.parseInt(map.get("cmp_end").toString());

                        if (from <= asm_start) {
                            from = start_cmp;
                        } else {
                            from = from - start_cmp;
                        }
                        if (to >= asm_end) {
                            to = end_cmp;
                        }
                        //          else {
                        //            to = (to - from);
                        //          }
                        seq += getSeqRecursive(map.get("cmp_seq_region_id").toString(), from, to, asm_start, asm_end);
                    }
                }


            }
            return seq;
        } catch (EmptyResultDataAccessException e) {
            return "";
//      throw new IOException("Sequence not found");
        }
    }

    public JSONArray getMarker() throws IOException {
        try {
            JSONArray markerList = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GENOME_MARKER);
            for (Map map : maps) {
                markerList.add(map);
            }
            return markerList;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getMarker no result found");

        }
    }



    public String getCoordSys(String query) throws Exception {
        try {
            String coordSys = "";
            int coord_id = Integer.parseInt(template.queryForObject(GET_coord_sys_id_by_name, new Object[]{query}, String.class));
            coordSys = template.queryForObject(GET_coord_sys_name, new Object[]{coord_id}, String.class);
            return coordSys;
        } catch (EmptyStackException e) {
            throw new Exception("Chromosome not found");
        }
    }
}
