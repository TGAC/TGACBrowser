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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.core.store.GeneStore;
import uk.ac.bbsrc.tgac.browser.core.store.SearchStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:10:23
 * To change this template use File | Settings | File Templates.
 */

public class SQLSeachDAO implements SearchStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLSeachDAO.class);

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
    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT s.seq_region_id, s.name, s.length, cs.name as Type, s.coord_system_id as coord FROM seq_region s, coord_system cs WHERE cs.coord_system_id = s.coord_system_id and  s.name like ? LIMIT 100;";
    //    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT * FROM seq_region WHERE name like ? limit 100";
    public static final String GET_SEQ_REGION_ID_SEARCH_FOR_MATCH = "SELECT s.seq_region_id, s.name, s.length, cs.name as Type, s.coord_system_id as coord FROM seq_region s, coord_system cs WHERE s.name = ? and cs.coord_system_id = s.coord_system_id limit 100;";

    public static final String GET_SIZE_SEQ_REGION_ID_SEARCH = "SELECT count(length) FROM seq_region WHERE name like ?";
    public static final String GET_SEQ_REGION_ID_SEARCH_all = "SELECT * FROM seq_region WHERE coord_system_id = ?";
    public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_LENGTH_FROM_ID = "SELECT length FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_length_from_seqreg_id = "SELECT length FROM seq_region where seq_region_id =?";
    public static final String GET_SEQ_REGION_ID_SEARCH_For_One = "SELECT seq_region_id FROM seq_region WHERE name = ?";

    public static final String GET_SEQ_REGION_ATTRIB_FROM_ID = "SELECT * FROM seq_region_attrib WHERE seq_region_id = ? and attrib_type_id = 3";


    public static final String GET_MARKER_SEARCH = "SELECT marker_id FROM marker_synonym WHERE name like ? LIMIT 100";
    public static final String GET_MARKER_FEATURE = "SELECT marker_feature_id, analysis_id, seq_region_start, seq_region_end, seq_region_id FROM marker_feature WHERE marker_id = ?";

    public static final String GET_GENE_SEARCH = "SELECT gene_id, analysis_id, seq_region_start, seq_region_end, seq_region_id, description FROM gene WHERE description like ? LIMIT 100";
    public static final String GET_TRANSCRIPT_SEARCH = "SELECT transcript_id, analysis_id, seq_region_start, seq_region_end, seq_region_id, description FROM transcript WHERE description LIKE ? LIMIT 100";
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
    public static final String GET_GO_Genes = "select gene_id, value from gene_attrib where value like ? limit 100";
    public static final String GET_GO_Transcripts = "select transcript_id, value from transcript_attrib where value like ? limit 100";
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

    public JSONArray getGenesSearch(String searchQuery) throws IOException {
        try {
            JSONArray genes = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GENE_SEARCH, new Object[]{'%' + searchQuery + '%'});
            int i = 0;
            for (Map map : maps) {
                JSONObject eachGene = new JSONObject();
                eachGene.put("Type", "Gene_" + getLogicNameByAnalysisId(Integer.parseInt(map.get("analysis_id").toString())));
                eachGene.put("name", map.get("description"));
                if (checkChromosome()) {
                    int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                    eachGene.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                    eachGene.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                    eachGene.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
//                    eachGene.put("coord", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                } else {
                    eachGene.put("start", map.get("seq_region_start"));
                    eachGene.put("end", map.get("seq_region_end"));
                    eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                    eachGene.put("coord", template.queryForObject(GET_coord_sys_id, new Object[]{map.get("seq_region_id")}, String.class));
                }
                log.info("\n\n\t\tanalyisis " + map.get("analysis_id"));

                eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
                genes.add(eachGene);
                i++;
                if (i > 100) {
                    break;
                }
            }
            return genes;
        } catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            e.printStackTrace();
            throw new IOException("Get gene search empty resposnse" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Get gene search result not found" + e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public JSONArray getMarkerSearch(String searchQuery) throws IOException {
        try {
            JSONArray result_markers = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_MARKER_SEARCH, new Object[]{'%' + searchQuery + '%'});
            int i = 0;
            for (Map map : maps) {
                JSONObject eachMarkerResult = new JSONObject();
                List<Map<String, Object>> markers = template.queryForList(GET_MARKER_FEATURE, new Object[]{map.get("marker_id")});
                for (Map marker : markers) {
                    eachMarkerResult.put("Type", "Gene_" + getLogicNameByAnalysisId(Integer.parseInt(marker.get("analysis_id").toString())));
                    eachMarkerResult.put("name", map.get("name"));
                    if (checkChromosome()) {
                        int pos = getPositionOnReference(Integer.parseInt(marker.get("seq_region_id").toString()), 0);
                        eachMarkerResult.put("start", pos + Integer.parseInt(marker.get("seq_region_start").toString()));
                        eachMarkerResult.put("end", pos + Integer.parseInt(marker.get("seq_region_end").toString()));
                        eachMarkerResult.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(marker.get("seq_region_id").toString()))));
                    } else {
                        eachMarkerResult.put("start", marker.get("seq_region_start"));
                        eachMarkerResult.put("end", marker.get("seq_region_end"));
                        eachMarkerResult.put("parent", getSeqRegionName(Integer.parseInt(marker.get("seq_region_id").toString())));
                        eachMarkerResult.put("coord", template.queryForObject(GET_coord_sys_id, new Object[]{marker.get("seq_region_id")}, String.class));
                    }
                    log.info("\n\n\t\tanalyisis " + map.get("analysis_id"));

                    eachMarkerResult.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{marker.get("analysis_id")}, String.class));
                    result_markers.add(eachMarkerResult);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            }
            return result_markers;
        } catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            e.printStackTrace();
            throw new IOException("Get gene search empty resposnse" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Get gene search result not found" + e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public JSONArray getSeqRegionSearch(String searchQuery) throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});

            boolean chr = checkChromosome();
            int i = 1;
            if (chr) {
                for (Map map : maps) {
                    if (chr) {
                        if (getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString())) != 0) {
                            int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                            map.put("start", pos);
                            map.put("end", pos + Integer.parseInt(map.get("length").toString()));
                            map.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                            map.put("coord", map.get("coord"));
                        }
                    }
                    names.add(map);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            } else {
                for (Map map : maps) {
                    names.add(map);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            }

            return names;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("empty result exception result not found " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("result not found " + e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public JSONArray getSeqRegionSearchformatch(String searchQuery) throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH_FOR_MATCH, new Object[]{searchQuery});

            boolean chr = checkChromosome();
            int i = 1;
            if (chr) {
                for (Map map : maps) {
                    if (chr) {
                        if (getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString())) != 0) {
                            int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                            map.put("start", pos);
                            map.put("end", pos + Integer.parseInt(map.get("length").toString()));
                            map.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                            map.put("coord", map.get("coord"));
                        }
                    }
                    names.add(map);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            } else {
                for (Map map : maps) {
                    names.add(map);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            }

            return names;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("empty result exception result not found " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("result not found " + e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
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

    public int getPositionOnReference(int id, int pos) {
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

    private boolean checkCoord(int id, String str) {
        boolean check = false;
        log.info("\n\n\ncheckCoord = " + id);
        int cood_sys_id = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{id}, Integer.class);
        log.info("\n\n\ncheckCoord = " + cood_sys_id);

        List<Map<String, Object>> maps = template.queryForList(CHECK_Coord_sys_attr, new Object[]{cood_sys_id, '%' + str + '%', '%' + str + '%'});
        if (maps.size() > 0) {
            check = true;
        }
        return check;
    }

    public boolean checkChromosome() throws Exception {
        try {
            Boolean check;
            List<Map<String, Object>> attrib_temp = template.queryForList(GET_coord_attrib_chr, new Object[]{"%chr%", "%chr%"});
            if (attrib_temp.size() > 0) {
                check = true;
            } else {
                check = false;
            }
            return check;
        } catch (EmptyStackException e) {
            e.printStackTrace();
            throw new Exception("Chromosome not found");
        }
    }

    public boolean checkChromosome(int id) throws Exception {
        try {
            Boolean check;
            int coord_sys_id = template.queryForObject(GET_coord_sys_id, new Object[]{id}, Integer.class);

            String coord_sys_name = template.queryForObject(GET_coord_sys_name, new Object[]{coord_sys_id}, String.class);
            String coord_attrib_name = template.queryForObject(GET_coord_attrib, new Object[]{coord_sys_id}, String.class);
            if ((coord_attrib_name != null && coord_attrib_name.toLowerCase().contains("chr")) || (coord_sys_name != null && coord_sys_name.toLowerCase().contains("chr"))) {
                check = true;
            } else {
                check = false;
            }
            return check;
        } catch (EmptyStackException e) {
            e.printStackTrace();
            throw new Exception("Chromosome not found");
        }
    }

    public int getAssemblyReference(int id) {
        int ref_id = 0;
        if (checkCoord(id, "chr")) {
            ref_id = id;
        } else {
            log.info("\n\n\t\tgetassemblyref " + id);
            List<Map<String, Object>> maps = template.queryForList(GET_reference_for_Assembly, new Object[]{id});
            for (Map map : maps) {
                if (checkCoord(Integer.parseInt(map.get("asm_seq_region_id").toString()), "chr")) {
                    ref_id = Integer.parseInt(map.get("asm_seq_region_id").toString());
                } else {
                    ref_id = getAssemblyReference(Integer.parseInt(map.get("asm_seq_region_id").toString()));
                }
            }
        }
        return ref_id;
    }

    public String getLogicNameByAnalysisId(int id) throws IOException {
        try {
            log.info("\n\n\t\tanalyisis " + id);
            String str = template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getLogicNameByAnalysisId no result found");

        }
    }

    public JSONArray getTranscriptSearch(String searchQuery) throws IOException {
        try {
            JSONArray genes = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_TRANSCRIPT_SEARCH, new Object[]{'%' + searchQuery + '%'});
            int i = 0;

            for (Map map : maps) {
                log.info("\n\n\ntranscript " + map.toString());

                JSONObject eachGene = new JSONObject();
                eachGene.put("Type", "Transcript_" + getLogicNameByAnalysisId(Integer.parseInt(map.get("analysis_id").toString())));
                eachGene.put("name", map.get("description"));
                if (checkChromosome()) {
                    int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                    eachGene.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                    eachGene.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                    eachGene.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                } else {
                    eachGene.put("start", map.get("seq_region_start"));
                    eachGene.put("end", map.get("seq_region_end"));
                    eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                    eachGene.put("coord", template.queryForObject(GET_coord_sys_id, new Object[]{map.get("seq_region_id")}, String.class));
                }
                log.info("\n\n\ntranscript " + eachGene.toString());
                log.info("\n\n\t\tanalyisis " + map.get("analysis_id"));

                eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
                genes.add(eachGene);
                i++;
                if (i > 100) {
                    break;
                }
            }
            return genes;
        } catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            e.printStackTrace();
            throw new IOException("result not found");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public JSONArray getGOSearch(String searchQuery) throws IOException {
        try {
            JSONArray GOs = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GO_Genes, new Object[]{'%' + searchQuery + '%'});
            int i = 0;
            for (Map map : maps) {

                List<Map<String, Object>> genes = template.queryForList(GET_GO_Gene_Details, new Object[]{map.get("gene_id").toString()});
                for (Map gene : genes) {
                    JSONObject eachGo = new JSONObject();
                    eachGo.put("name", gene.get("description"));
                    if (checkChromosome()) {
                        int pos = getPositionOnReference(Integer.parseInt(gene.get("seq_region_id").toString()), 0);
                        eachGo.put("start", pos + Integer.parseInt(gene.get("seq_region_start").toString()));
                        eachGo.put("end", pos + Integer.parseInt(gene.get("seq_region_end").toString()));
                        eachGo.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(gene.get("seq_region_id").toString()))));
                    } else {
                        eachGo.put("start", gene.get("seq_region_start"));
                        eachGo.put("end", gene.get("seq_region_end"));
                        eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
                        eachGo.put("coord", template.queryForObject(GET_coord_sys_id, new Object[]{gene.get("seq_region_id")}, String.class));
                    }
                    eachGo.put("value", map.get("value"));
                    eachGo.put("Type", "Gene_" + getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    eachGo.put("analysis_id", getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    GOs.add(eachGo);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            }

            i = 0;
            List<Map<String, Object>> transcripts = template.queryForList(GET_GO_Transcripts, new Object[]{'%' + searchQuery + '%'});
            for (Map map : transcripts) {

                List<Map<String, Object>> genes = template.queryForList(GET_GO_Transcript_Details, new Object[]{map.get("transcript_id").toString()});
                for (Map gene : genes) {
                    JSONObject eachGo = new JSONObject();
                    eachGo.put("name", gene.get("description"));
                    if (checkChromosome()) {
                        int pos = getPositionOnReference(Integer.parseInt(gene.get("seq_region_id").toString()), 0);
                        eachGo.put("start", pos + Integer.parseInt(gene.get("seq_region_start").toString()));
                        eachGo.put("end", pos + Integer.parseInt(gene.get("seq_region_end").toString()));
                        eachGo.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(gene.get("seq_region_id").toString()))));
                    } else {
                        eachGo.put("start", map.get("seq_region_start"));
                        eachGo.put("end", map.get("seq_region_end"));
                        eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
                    }
                    eachGo.put("Type", "Transcript_" + getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    eachGo.put("value", map.get("value"));
                    eachGo.put("analysis_id", getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    GOs.add(eachGo);
                    i++;
                    if (i > 100) {
                        break;
                    }
                }
            }
            return GOs;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("result not found");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
