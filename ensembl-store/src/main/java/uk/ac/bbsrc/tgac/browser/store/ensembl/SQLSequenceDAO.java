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
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.set.SynchronizedSortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.bbsrc.tgac.browser.core.store.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT * FROM seq_region WHERE name like ? limit 100";
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
    public static final String GET_Gene_SIZE_SLICE = "SELECT COUNT(*) FROM gene where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_EXON = "SELECT seq_region_start,seq_region_end,seq_region_strand FROM exon where seq_region_id =?";
    public static final String GET_EXON_per_Gene = "SELECT e.exon_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand FROM exon e, exon_transcript et where et.exon_id = e.exon_id and et.transcript_id =  ?";
    public static final String GET_Domain_per_Gene = "SELECT * FROM transcript_attrib where transcript_id =?";
    public static final String GET_CDS_start_per_Gene = "SELECT seq_start FROM translation where transcript_id =?";
    public static final String GET_CDS_end_per_Gene = "SELECT seq_end FROM translation where transcript_id =?";
    public static final String GET_GO_Genes = "select * from gene_attrib where value like ?";
    public static final String GET_GO_Transcripts = "select * from transcript_attrib where value like ?";
    public static final String GET_GENE_SIZE = "SELECT COUNT(*) FROM gene where seq_region_id =? and analysis_id = ?";
    public static final String GET_Gene_name_from_ID = "SELECT description FROM gene where gene_id =?";
    public static final String GET_Transcript_name_from_ID = "SELECT description FROM transcript where transcript_id =?";
    public static final String GET_GO_for_Genes = "select value from gene_attrib where gene_id = ?";
    public static final String GET_GO_for_Transcripts = "select value from transcript_attrib where transcript_id =  ?";

    public static final String GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID = "SELECT seq_region_start,seq_region_end,analysis_id FROM dna_align_feature where req_region_id =?";
    public static final String GET_HIT_SIZE = "SELECT COUNT(*) FROM dna_align_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_HIT_SIZE_SLICE = "SELECT COUNT(*) FROM dna_align_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_HIT = "SELECT dna_align_feature_id as id,cast(seq_region_start as signed) as start, cast(seq_region_end as signed) as end,seq_region_strand as strand,hit_start as hitstart, hit_end as hitend, hit_name as 'desc', cigar_line as cigarline FROM dna_align_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start >= ? AND seq_region_end <= ?) OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ? AND seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)) ORDER BY (end-start) desc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";


    public static final String Get_Database_information = "SELECT meta_key,meta_value from meta";// + var1;

    public static final String GET_Seq_API = "SELECT sequence FROM dna where seq_region_id = ?";

    public static final String GET_reference_for_Assembly = "SELECT * FROM assembly where cmp_seq_region_id =?";
    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_ASSEMBLY_SIZE_SLICE = "SELECT count(*) FROM assembly a, seq_region s where a.asm_seq_region_id = ? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? and a.asm_start >= ? and a.asm_start <= ?";
    public static final String GET_ASSEMBLY_SIZE = "SELECT COUNT(*) FROM assembly where asm_seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
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
    public static final String GET_REPEAT_SIZE = "SELECT COUNT(*) FROM repeat_feature where seq_region_id =? and analysis_id = ?";
    public static final String GET_REPEAT_SIZE_SLICE = "SELECT COUNT(*) FROM repeat_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_coord_attrib_chr = "SELECT coord_system_id FROM coord_system where name like ? || attrib like ?";

    //  public static final String GET_GENOME_MARKER = "SELECT * from marker_feature";
    public static final String GET_GENOME_MARKER = "select mf.marker_feature_id as id, sr.name as reference, mf.marker_id as marker_id, mf.seq_region_start as start, mf.seq_region_end as end, mf.analysis_id as analysis_id from marker_feature mf, seq_region sr where mf.seq_region_id = sr.seq_region_id;";

    public static final String GET_Tables_with_analysis_id_column = "SELECT DISTINCT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME IN ('analysis_id') AND TABLE_SCHEMA='wrightj_brachypodium_distachyon_core_10_63_12'";
    public static final String Check_feature_available = "SELECT DISTINCT analysis_id from ";// + var1;

    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public String getTrackIDfromName(String trackName) throws IOException {
        try {
            String str = template.queryForObject(GET_Tracks_Name, new Object[]{trackName}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class));
            throw new IOException(" getTrackIDfromName no result found");

        }
    }


    public String getHitNamefromId(int hitID) throws IOException {
        try {
            String str = template.queryForObject(GET_hit_name_from_ID, new Object[]{hitID}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class));
            throw new IOException(" getHitNamefromId no result found");

        }
    }

    public String getGeneNamefromId(int geneID) throws IOException {
        try {
            String str = template.queryForObject(GET_Gene_name_from_ID, new Object[]{geneID}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class));
            throw new IOException(" getGeneNamefromId no result found");

        }
    }

    public String getTranscriptNamefromId(int transcriptID) throws IOException {
        try {
            String str = template.queryForObject(GET_Transcript_name_from_ID, new Object[]{transcriptID}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class));
            throw new IOException(" getTranscriptNamefromId no result found");

        }
    }

    public String getSeqBySeqRegionId(int searchQuery) throws IOException {
        try {
            String str = template.queryForObject(GET_SEQ_FROM_SEQ_REGION_ID, new Object[]{searchQuery}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class));
            throw new IOException(" getSeqBySeqRegionId no result found");

        }
    }
//
//  public JSONArray getGenesforSearch(String searchQuery) throws IOException {
//    try {
//      JSONArray genes = new JSONArray();
//      List<Map<String, Object>> maps = template.queryForList(GET_GENE_SEARCH, new Object[]{'%' + searchQuery + '%'});
//       for (Map map : maps) {
//        JSONObject eachGene = new JSONObject();
//        eachGene.put("id", map.get("gene_id"));
//        eachGene.put("start", map.get("seq_region_start"));
//        eachGene.put("end", map.get("seq_region_end"));
//        eachGene.put("parent", map.get("seq_region_id"));
//        eachGene.put("analysis_id", map.get("analysis_id"));
//        genes.add(eachGene);
//      }
//      return genes;
//    }
//    catch (EmptyResultDataAccessException e){
//     throw new IOException("result not found");
//    }
//  }
//

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
        }
        catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public int getPositionOnReference(int id, int pos) {
        log.info("getpositiononref " + id + ":" + pos);
        if (checkCoord(id, "chr")) {

        }
        else {
            List<Map<String, Object>> maps = template.queryForList(GET_reference_for_Assembly, new Object[]{id});
            for (Map map : maps) {
                if (checkCoord(Integer.parseInt(map.get("asm_seq_region_id").toString()), "chr")) {
                    pos += Integer.parseInt(map.get("asm_start").toString());
                }
                else {
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
        }
        else {
            List<Map<String, Object>> maps = template.queryForList(GET_reference_for_Assembly, new Object[]{id});
            for (Map map : maps) {
                if (checkCoord(Integer.parseInt(map.get("asm_seq_region_id").toString()), "chr")) {
                    log.info("if" + map.get("asm_seq_region_id").toString());
                    ref_id = Integer.parseInt(map.get("asm_seq_region_id").toString());
                }
                else {
                    log.info("else" + map.get("asm_seq_region_id").toString());

                    ref_id = getAssemblyReference(Integer.parseInt(map.get("asm_seq_region_id").toString()));
                }
            }
        }
        return ref_id;
    }

    public JSONArray getGenesSearch(String searchQuery) throws IOException {
        try {
            JSONArray genes = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GENE_SEARCH, new Object[]{'%' + searchQuery + '%'});
            for (Map map : maps) {
                JSONObject eachGene = new JSONObject();
                eachGene.put("Type", getLogicNameByAnalysisId(Integer.parseInt(map.get("analysis_id").toString())));
                eachGene.put("name", map.get("description"));
                if (checkChromosome()) {
                    int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                    eachGene.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                    eachGene.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                    eachGene.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                }
                else {
                    eachGene.put("start", map.get("seq_region_start"));
                    eachGene.put("end", map.get("seq_region_end"));
                    eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                }
                eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
                genes.add(eachGene);
            }
            return genes;
        }
        catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            throw new IOException("result not found");
        }
        catch (Exception e) {
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public JSONArray getSeqRegionSearch(String searchQuery) throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});
            for (Map map : maps) {
                JSONObject eachName = new JSONObject();
                eachName.put("name", map.get("name"));
                eachName.put("seq_region_id", map.get("seq_region_id"));
                if (checkChromosome()) {
                    int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                    log.info("pos" + pos);
                    eachName.put("start", pos);
                    eachName.put("end", pos + Integer.parseInt(map.get("length").toString()));
                    eachName.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                }
                else {
//                    eachName.put("start", map.get("seq_region_start"));
//                    eachName.put("end", map.get("seq_region_end"));
//                    eachName.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                }
                eachName.put("Type", template.queryForObject(GET_coord_sys_name, new Object[]{map.get("coord_system_id").toString()}, String.class));
                eachName.put("length", map.get("length"));
                names.add(eachName);
            }
            return names;
        }
        catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            throw new IOException("result not found");
        }
        catch (Exception e) {
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
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
        }
        catch (EmptyResultDataAccessException e) {
            //     return getGOSearch(searchQuery);
            throw new IOException("result not found");
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
        }
        catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            throw new IOException("result not found");
        }
    }


    public int getSeqRegionearchsize(String searchQuery) throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});
            return maps.size();
        }
        catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
//      throw new IOException("result not found");
            return 0;
        }
    }

    public JSONArray getTranscriptSearch(String searchQuery) throws IOException {
        try {
            JSONArray genes = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_TRANSCRIPT_SEARCH, new Object[]{'%' + searchQuery + '%'});
            for (Map map : maps) {
                JSONObject eachGene = new JSONObject();
                eachGene.put("Type", getLogicNameByAnalysisId(Integer.parseInt(map.get("analysis_id").toString())));
                eachGene.put("name", map.get("description"));
                if (checkChromosome()) {
                    int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                    eachGene.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                    eachGene.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                    eachGene.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                }
                else {
                    eachGene.put("start", map.get("seq_region_start"));
                    eachGene.put("end", map.get("seq_region_end"));
                    eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                }
                eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
                genes.add(eachGene);
            }
            return genes;
        }
        catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
            throw new IOException("result not found");
        }
        catch (Exception e) {
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public JSONArray getGOSearch(String searchQuery) throws IOException {
        try {
            JSONArray GOs = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GO_Genes, new Object[]{'%' + searchQuery + '%'});
            for (Map map : maps) {

                List<Map<String, Object>> genes = template.queryForList(GET_GO_Gene_Details, new Object[]{map.get("gene_id").toString()});
                for (Map gene : genes) {
                    JSONObject eachGo = new JSONObject();
                    eachGo.put("name", gene.get("description"));
                    if (checkChromosome()) {
                        int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                        eachGo.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                        eachGo.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                        eachGo.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                    }
                    else {
                        eachGo.put("start", map.get("seq_region_start"));
                        eachGo.put("end", map.get("seq_region_end"));
                        eachGo.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                    }
                    eachGo.put("Type", "Gene");
//                    eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
                    eachGo.put("analysis_id", getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    GOs.add(eachGo);
                }
            }

            List<Map<String, Object>> transcripts = template.queryForList(GET_GO_Transcripts, new Object[]{'%' + searchQuery + '%'});
            for (Map map : transcripts) {

                List<Map<String, Object>> genes = template.queryForList(GET_GO_Transcript_Details, new Object[]{map.get("transcript_id").toString()});
                for (Map gene : genes) {
                    JSONObject eachGo = new JSONObject();
                    eachGo.put("name", gene.get("description"));
                    if (checkChromosome()) {
                        int pos = getPositionOnReference(Integer.parseInt(map.get("seq_region_id").toString()), 0);
                        eachGo.put("start", pos + Integer.parseInt(map.get("seq_region_start").toString()));
                        eachGo.put("end", pos + Integer.parseInt(map.get("seq_region_end").toString()));
                        eachGo.put("parent", getSeqRegionName(getAssemblyReference(Integer.parseInt(map.get("seq_region_id").toString()))));
                    }
                    else {
                        eachGo.put("start", map.get("seq_region_start"));
                        eachGo.put("end", map.get("seq_region_end"));
                        eachGo.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
                    }
                    eachGo.put("Type", "Transcript");
//                    eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
                    eachGo.put("analysis_id", getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
                    GOs.add(eachGo);
                }
            }
            return GOs;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("result not found");
        }
        catch (Exception e) {
            throw new IOException("result not found");  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Integer getSeqRegion(String searchQuery) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_REGION_ID_FROM_NAME, new Object[]{searchQuery}, Integer.class);
            return i;
        }
        catch (EmptyResultDataAccessException e) {
//      getGenesforSearch(searchQuery);
//      throw new IOException(" getSeqRegion no result found");
            return 0;
        }
    }

    public String getSeqRegionName(int searchQuery) throws IOException {
        try {
            String str = template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{searchQuery}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqRegionName no result found");

        }
    }

    public String getLogicNameByAnalysisId(int id) throws IOException {
        try {
            String str = template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getLogicNameByAnalysisId no result found");

        }
    }

    public String getDescriptionByAnalysisId(int id) throws IOException {
        try {
            String str = template.queryForObject(GET_DESCRIPTION_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getDescriptionByAnalysisId no result found");

        }
    }

    public Map<String, Object> getStartEndAnalysisIdBySeqRegionId(int id) throws IOException {
        try {
            Map<String, Object> map = template.queryForMap(GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID, new Object[]{id});
            return map;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getStartEndAnalysisIdBySeqRegionI no result found");

        }
    }

    public String getSeqLengthbyId(int query) throws IOException {
        try {
            String i = template.queryForObject(GET_SEQ_LENGTH_FROM_ID, new Object[]{query}, String.class);
            return i;
        }
        catch (EmptyResultDataAccessException e) {
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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqlength no result found");

        }
    }


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
//            start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(getHitGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }
                    else {
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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

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
            }
            else {


            }

            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

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
            }
            else {
                trackList.addAll(recursiveHitGraph(0, id, trackId, start, end));
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }


    public int countRecursiveHit(int id, String trackId, long start, long end) {
        int hit_size = 0;
        JSONArray assemblyTracks = new JSONArray();
        List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

        if (maps_one.size() > 0) {
            for (int j = 0; j < maps_one.size(); j++) {
                long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                if (template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class) > 0) {
                    hit_size += template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                }
                else {
                    hit_size += countRecursiveHit(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                }
            }
        }


        return hit_size;
    }

    public int countHit(int id, String trackId, long start, long end) {
        int hit_size = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);

        if (hit_size == 0) {
            hit_size = countRecursiveHit(id, trackId, start, end);
        }
        return hit_size;
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
    public List<Map<String, Object>> getHit(int id, String trackId, long start, long end) throws IOException {
        log.info("get hit " + id + ":" + trackId + ":" + start + ":" + end);
        return template.queryForList(GET_HIT, new Object[]{id, trackId, start, end, start, end, end, end, start, start});
    }

    public JSONArray recursiveHit(int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_HIT_SIZE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId});
                    JSONObject eachTrack_temp = new JSONObject();
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(getHitLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), getHit(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end), start, end, delta));
                    }
                    else {
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveHit(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end, delta));
                    }

                }
            }

            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }

    }

    public JSONArray getHitLevel(int start_pos, List<Map<String, Object>> maps_two, long start, long end, int delta) {
        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);
        JSONObject eachTrack_temp = new JSONObject();
        JSONArray assemblyTracks = new JSONArray();
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
                    for (int i = 0; i < ends.size(); i++) {
                        if ((Integer.parseInt(map_temp.get("start").toString()) - ends.get(i)) > delta) {
                            ends.remove(i);
                            ends.add(i, Integer.parseInt(map_temp.get("end").toString()));
                            eachTrack_temp.put("layer", i + 1);
                            break;
                        }
                        else if ((Integer.parseInt(map_temp.get("start").toString()) - ends.get(i) <= delta && (i + 1) == ends.size()) || Integer.parseInt(map_temp.get("start").toString()) == ends.get(i)) {

                            if (i == 0) {
                                ends.add(i, Integer.parseInt(map_temp.get("end").toString()));
                                eachTrack_temp.put("layer", ends.size());
                            }
                            else {
                                eachTrack_temp.put("layer", ends.size());
                                ends.add(ends.size(), Integer.parseInt(map_temp.get("end").toString()));

                            }
                            break;
                        }
                        else {

                        }
                    }
                }
                eachTrack_temp.put("desc", map_temp.get("desc"));
                assemblyTracks.add(eachTrack_temp);
            }
        }
        return assemblyTracks;
    }

    public JSONArray getHitLevel(List<Map<String, Object>> maps, List<Integer> ends, long start, long end, int delta) {

        JSONObject eachTrack_temp = new JSONObject();
        JSONArray assemblyTracks = new JSONArray();

        for (Map map : maps) {
            JSONObject eachTrack = new JSONObject();
            int track_start = Integer.parseInt(map.get("start").toString()) - 1;
            int track_end = Integer.parseInt(map.get("end").toString()) - 1;
            if (track_start >= start && track_end <= end || track_start <= start && track_end >= end || track_end >= start && track_end <= end || track_start >= start && track_start <= end) {
                if (track_end - track_start > 1) {
                    for (int i = 0; i < ends.size(); i++) {
                        if ((Integer.parseInt(map.get("start").toString()) - ends.get(i)) > delta) {
                            ends.remove(i);
                            ends.add(i, Integer.parseInt(map.get("end").toString()));
                            map.put("layer", i + 1);
                            break;

                        }
                        else if ((Integer.parseInt(map.get("start").toString()) - ends.get(i) <= delta) && (i + 1) == ends.size()) {

                            if (i == 0) {

                                map.put("layer", ends.size());
                                ends.add(i, Integer.parseInt(map.get("end").toString()));
                            }
                            else {
                                ends.add(ends.size(), Integer.parseInt(map.get("end").toString()));

                                map.put("layer", ends.size());
                            }
                            break;
                        }
                        else {

                            //             continue;
                        }
                    }
                }
//        eachTrack.put("desc", map.get("desc"));
                assemblyTracks.add(map);
            }
        }
        return assemblyTracks;
    }


    public JSONArray processHit(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException {
        try {
            JSONArray trackList = new JSONArray();

            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            if (template.queryForObject(GET_HIT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {

                if (maps.size() > 0) {
                    ends.add(0, 0);
                    trackList = getHitLevel(maps, ends, start, end, delta);
                }
                else {
//          trackList = recursiveHit(0, id, trackId, start, end, delta);
                }
            }
            else {
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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

    public JSONArray recursiveRepeatGraph(int start_pos, int id, String trackId, long start, long end) throws IOException {
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
                    int no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
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
                        assemblyTracks.addAll(getRepeatGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }
                    else {
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
                        assemblyTracks.addAll(recursiveRepeatGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }

                }
            }
            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

    public JSONArray getRepeatGraphLevel(int start_pos, int id, String trackId, long start, long end) throws IOException {
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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

    public JSONArray getRepeatGraph(int id, String trackId, long start, long end) throws IOException {
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
            }
            else {
                trackList.addAll(recursiveRepeatGraph(0, id, trackId, start, end));
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

    public int countRecursiveRepeat(int id, String trackId, long start, long end) {
        int repeat_size = 0;
        List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

        if (maps_one.size() > 0) {
            for (int j = 0; j < maps_one.size(); j++) {
                long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                if (template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class) > 0) {
                    repeat_size += template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                }
                else {
                    repeat_size += countRecursiveRepeat(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                }
            }
        }


        return repeat_size;
    }

    public int countRepeat(int id, String trackId, long start, long end) {
        int count_size = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
        if (count_size == 0) {
            count_size = countRecursiveRepeat(id, trackId, start, end);
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
    public List<Map<String, Object>> getRepeat(int id, String trackId, long start, long end) throws IOException {
        return template.queryForList(GET_REPEAT, new Object[]{id, trackId, start, end, start, end, start, end, start, end});
    }

    public JSONArray recursiveRepeat(int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_REPEAT_SIZE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId});
                    JSONObject eachTrack_temp = new JSONObject();
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        assemblyTracks.addAll(getRepeatLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), getRepeat(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end), start, end, delta));
                    }
                    else {
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveRepeat(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end, delta));
                    }

                }
            }

            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }

    }

    public JSONArray getRepeatLevel(int start_add, List<Map<String, Object>> maps, long start, long end, int delta) {
        List<Integer> ends = new ArrayList<Integer>();
        JSONObject eachTrack_temp = new JSONObject();
        ends.add(0, 0);
        JSONArray trackList = new JSONArray();
        for (Map<String, Object> map : maps) {
            int start_pos = start_add + Integer.parseInt(map.get("start").toString());
            int end_pos = start_add + Integer.parseInt(map.get("end").toString());
            if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                eachTrack_temp.put("id", map.get("id"));
                eachTrack_temp.put("start", start_pos);
                eachTrack_temp.put("end", end_pos);
                eachTrack_temp.put("flag", false);
                eachTrack_temp.put("strand", map.get("strand"));
                eachTrack_temp.put("repeatstart", start_add + Integer.parseInt(map.get("repeatstart").toString()));
                eachTrack_temp.put("repeatend", start_add + Integer.parseInt(map.get("repeatstart").toString()));
                eachTrack_temp.put("score", map.get("score"));

                for (int i = 0; i < ends.size(); i++) {
                    if (start_pos - ends.get(i) > delta) {
                        ends.remove(i);
                        ends.add(i, end_pos);
                        eachTrack_temp.put("layer", i + 1);
                        break;
                    }
                    else if ((start_pos - ends.get(i) <= delta && (i + 1) == ends.size()) || start_pos == ends.get(i)) {

                        if (i == 0) {
                            eachTrack_temp.put("layer", ends.size());
                            ends.add(i, Integer.parseInt(map.get("end").toString()));
                        }
                        else {
                            ends.add(ends.size(), Integer.parseInt(map.get("end").toString()));
                            eachTrack_temp.put("layer", ends.size());
                        }


                        break;
                    }
                    else {
                        continue;
                    }
                }

                trackList.add(eachTrack_temp);
            }
        }
        return trackList;
    }


    public JSONArray getRepeatLevel(List<Map<String, Object>> maps, List<Integer> ends, long start, long end, int delta) {
        JSONArray trackList = new JSONArray();
        for (Map<String, Object> map : maps) {
            int start_pos = Integer.parseInt(map.get("start").toString());
            int end_pos = Integer.parseInt(map.get("end").toString());
            if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {

                for (int i = 0; i < ends.size(); i++) {
                    if (start_pos - ends.get(i) > delta) {
                        ends.remove(i);
                        ends.add(i, end_pos);
                        map.put("layer", i + 1);
                        break;
                    }
                    else if ((start_pos - ends.get(i) <= delta && (i + 1) == ends.size()) || start_pos == ends.get(i)) {

                        if (i == 0) {
                            map.put("layer", ends.size());
                            ends.add(i, Integer.parseInt(map.get("end").toString()));
                        }
                        else {
                            ends.add(ends.size(), Integer.parseInt(map.get("end").toString()));
                            map.put("layer", ends.size());
                        }
                        break;
                    }
                    else {
                        continue;
                    }
                }

                trackList.add(map);
            }
        }
        return trackList;
    }

    public JSONArray processRepeat(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException {
        try {
            JSONArray trackList = new JSONArray();

            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);

            if (template.queryForObject(GET_REPEAT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
                if (maps.size() > 0) {
                    ends.add(0, 0);
                    trackList = getRepeatLevel(maps, ends, start, end, delta);

                }
            }
            else {
                trackList = recursiveRepeat(0, id, trackId, start, end, delta);
            }
            int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
            int countrepeat = countRepeat(id, trackId, 0, length);
            if (countrepeat < 1) {
                trackList.add("getHit no result found");
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
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
                    }
                    else {
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
        }
        catch (EmptyResultDataAccessException e) {
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
            }
            else {


            }

            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
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
            }
            else {
                trackList.addAll(recursiveAssemblyGraph(0, id, trackId, start, end));
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
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
                }
                else {
                    hit_size += countRecursiveAssembly(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                }
            }
        }

        return hit_size;
    }

    public int countAssembly(int id, String trackId, long start, long end) {
        int hit_size = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId.replace("cs", ""), start, end}, Integer.class);

        if (hit_size == 0) {
            hit_size = countRecursiveAssembly(id, trackId, start, end);
        }
        return hit_size;
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
            }
            else {
                trackList = recursiveAssembly(0, id, trackId, delta);
            }
            if (trackList.size() == 0) {
                trackList.add("getHit no result found");
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
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
                    }
                    else {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveAssembly(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, delta));
                    }
                }

            }
            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
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
                    }
                    else {
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

                }
                else if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i) <= delta) && (i + 1) == ends.size()) {

                    if (i == 0) {
                        log.info("else if" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);
                        eachTrack.put("layer", ends.size());
                        ends.add(i, Integer.parseInt(map.get("asm_end").toString()));

                    }
                    else {
                        log.info("else else" + Integer.parseInt(map.get("asm_start").toString()) + ":" + ends.get(i) + ">" + delta);
                        ends.add(ends.size(), Integer.parseInt(map.get("asm_end").toString()));
                        eachTrack.put("layer", ends.size());

                    }
                    break;
                }
                else {
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


    public JSONArray recursiveGeneGraph(int start_pos, int id, String trackId, long start, long end) throws IOException {
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
                    int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
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
                        assemblyTracks.addAll(getGeneGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }
                    else {
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
                        assemblyTracks.addAll(recursiveGeneGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }

                }
            }
            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

    public JSONArray getGeneGraphLevel(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", start_pos + from);
                    eachTrack.put("end", start_pos + to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);

                    trackList.add(eachTrack);
                    from = to;
                }
            }
            else {
            }

            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

    public JSONArray getGeneGraph(int id, String trackId, long start, long end) throws IOException {
        try {

            JSONArray trackList = new JSONArray();
            long from = start;
            long to;
            int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, end}, Integer.class);

            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    trackList.add(eachTrack);
                    from = to;
                }
            }
            else {
                trackList.addAll(recursiveGeneGraph(0, id, trackId, start, end));
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");
        }
    }

    public int countRecursiveGene(int id, String trackId, long start, long end) {
        int gene_size = 0;
        JSONArray assemblyTracks = new JSONArray();
        List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

        if (maps_one.size() > 0) {
            for (int j = 0; j < maps_one.size(); j++) {
                long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                if (template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class) > 0) {
                    gene_size += template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                }
                else {
                    gene_size += countRecursiveGene(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                }
            }
        }


        return gene_size;
    }

    public int countGene(int id, String trackId, long start, long end) {
        int gene_size = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
        if (gene_size == 0) {
            gene_size = countRecursiveGene(id, trackId, start, end);
        }
        return gene_size;
    }


    @Cacheable(cacheName = "geneCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator",
                    properties = {
                            @Property(name = "includeMethod", value = "false"),
                            @Property(name = "includeParameterTypes", value = "false")
                    }
            )
    )

    public List<Map<String, Object>> getGenes(int id, String trackId) throws IOException {

        return template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }

//  @Cacheable(cacheName = "transcriptGoCache",
//                  keyGenerator = @KeyGenerator(
//                          name = "HashCodeCacheKeyGenerator",
//                          properties = {
//                                  @Property(name = "includeMethod", value = "false"),
//                                  @Property(name = "includeParameterTypes", value = "false")
//                          }
//                  )
//       )

    public List<Map<String, Object>> getTranscriptsGO(String transcriptId) throws IOException {

        return template.queryForList(GET_GO_for_Transcripts, new Object[]{transcriptId});//template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }


//  @Cacheable(cacheName = "geneGoCache",
//                 keyGenerator = @KeyGenerator(
//                         name = "HashCodeCacheKeyGenerator",
//                         properties = {
//                                 @Property(name = "includeMethod", value = "false"),
//                                 @Property(name = "includeParameterTypes", value = "false")
//                         }
//                 )
//      )

    public List<Map<String, Object>> getGenesGO(String geneId) throws IOException {

        return template.queryForList(GET_GO_for_Genes, new Object[]{geneId});//template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }


    public JSONArray recursiveGene(int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_GENE_SIZE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId});
                    JSONObject eachTrack_temp = new JSONObject();
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(getGeneLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), getGenes(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId), start, end, delta));
                    }
                    else {
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveGene(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end, delta));
                    }
                }
            }

            return assemblyTracks;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }

    }

    public JSONArray getGeneLevel(int start_add, List<Map<String, Object>> genes, long start, long end, int delta) throws IOException {
        JSONArray GeneList = new JSONArray();
        JSONObject eachGene = new JSONObject();
        JSONObject eachTrack = new JSONObject();
        JSONArray exonList = new JSONArray();
        JSONArray transcriptList = new JSONArray();
        JSONArray filteredgenes = new JSONArray();
        String gene_id = "";
        String transcript_id = "";
        int layer = 1;
        int lastsize = 0;
        int thissize = 0;
        List<Map<String, Object>> domains;
        List<Map<String, Object>> translation_start;
        List<Map<String, Object>> translation_end;

        for (Map gene : genes) {
            int start_pos = start_add + Integer.parseInt(gene.get("gene_start").toString());
            int end_pos = start_add + Integer.parseInt(gene.get("gene_end").toString());
            if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                filteredgenes.add(filteredgenes.size(), gene);
            }
        }
        for (int i = 0; i < filteredgenes.size(); i++) {

            if (!transcript_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("transcript_id").toString())) {
                if (!transcript_id.equalsIgnoreCase("")) {
                    eachTrack.put("Exons", exonList);
                    transcriptList.add(eachTrack);
                }
                transcript_id = filteredgenes.getJSONObject(i).get("transcript_id").toString();
                exonList = new JSONArray();

                eachTrack = new JSONObject();

                eachTrack.put("id", filteredgenes.getJSONObject(i).get("transcript_id"));
                eachTrack.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_start").toString()));
                eachTrack.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_end").toString()));

                translation_start = template.queryForList(GET_CDS_start_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                translation_end = template.queryForList(GET_CDS_end_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                for (Map start_seq : translation_start) {
                    eachTrack.put("transcript_start", start_add + Integer.parseInt(start_seq.get("seq_start").toString()));

                }

                for (Map end_seq : translation_end) {
                    eachTrack.put("transcript_end", start_add + Integer.parseInt(end_seq.get("seq_end").toString()));

                }
                eachTrack.put("desc", filteredgenes.getJSONObject(i).get("transcript_name"));

                eachTrack.put("layer", layer);
                eachTrack.put("domain", 0);
                domains = getTranscriptsGO(filteredgenes.getJSONObject(i).get("transcript_id").toString());
                for (Map domain : domains) {
                    eachTrack.put("domain", domain.get("value"));
                }
                eachTrack.put("flag", false);
            }
            if (!gene_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("gene_id").toString())) {
                if (!gene_id.equalsIgnoreCase("")) {
                    eachGene.put("transcript", transcriptList);
                    GeneList.add(eachGene);
                }
                gene_id = filteredgenes.getJSONObject(i).get("gene_id").toString();
                transcriptList = new JSONArray();
                eachGene.put("id", filteredgenes.getJSONObject(i).get("gene_id"));
                eachGene.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_start").toString()));
                eachGene.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_end").toString()));
                eachGene.put("desc", filteredgenes.getJSONObject(i).get("gene_name"));
                eachGene.put("strand", filteredgenes.getJSONObject(i).get("gene_strand"));
                eachGene.put("layer", i % 2 + 1);
                eachGene.put("domain", 0);
                domains = getGenesGO(filteredgenes.getJSONObject(i).get("gene_id").toString());
                for (Map domain : domains) {
                    eachGene.put("domain", domain.get("value"));
                }
                if (lastsize < 2 && layer > 2) {
                    layer = 1;
                }
                else {
                    layer = layer;
                }

                if (thissize > 1) {
                    layer = 1;
                }
                layer++;
            }

            JSONObject eachExon = new JSONObject();
            eachExon.put("id", filteredgenes.getJSONObject(i).get("exon_id"));
            eachExon.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("exon_start").toString()));
            eachExon.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("exon_end").toString()));
            exonList.add(eachExon);

            lastsize = thissize;
        }

        if (filteredgenes.size() > 0) {
            eachTrack.put("Exons", exonList);
            transcriptList.add(eachTrack);
            eachGene.put("transcript", transcriptList);
            GeneList.add(eachGene);
        }
        return GeneList;
    }

    public JSONArray getGeneLevel(List<Map<String, Object>> genes, List<Integer> ends, long start, long end, int delta) throws IOException {
        JSONArray GeneList = new JSONArray();
        JSONObject eachGene = new JSONObject();
        JSONObject eachTrack = new JSONObject();
        JSONArray exonList = new JSONArray();
        JSONArray transcriptList = new JSONArray();
        JSONArray filteredgenes = new JSONArray();
        String gene_id = "";
        String transcript_id = "";
        int layer = 1;
        int lastsize = 0;
        int thissize = 0;
        List<Map<String, Object>> domains;
        List<Map<String, Object>> translation_start;
        List<Map<String, Object>> translation_end;

        for (Map gene : genes) {
            int start_pos = Integer.parseInt(gene.get("gene_start").toString());
            int end_pos = Integer.parseInt(gene.get("gene_end").toString());
            if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                filteredgenes.add(filteredgenes.size(), gene);
            }
        }
        for (int i = 0; i < filteredgenes.size(); i++) {

            if (!transcript_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("transcript_id").toString())) {
                if (!transcript_id.equalsIgnoreCase("")) {
                    eachTrack.put("Exons", exonList);
                    transcriptList.add(eachTrack);
                }
                transcript_id = filteredgenes.getJSONObject(i).get("transcript_id").toString();
                exonList = new JSONArray();

                eachTrack = new JSONObject();

                eachTrack.put("id", filteredgenes.getJSONObject(i).get("transcript_id"));
                eachTrack.put("start", filteredgenes.getJSONObject(i).get("transcript_start"));
                eachTrack.put("end", filteredgenes.getJSONObject(i).get("transcript_end"));

                translation_start = template.queryForList(GET_CDS_start_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                translation_end = template.queryForList(GET_CDS_end_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                for (Map start_seq : translation_start) {
                    eachTrack.put("transcript_start", start_seq.get("seq_start"));

                }

                for (Map end_seq : translation_end) {
                    eachTrack.put("transcript_end", end_seq.get("seq_end"));

                }
                eachTrack.put("desc", filteredgenes.getJSONObject(i).get("transcript_name"));

                eachTrack.put("layer", layer);
                eachTrack.put("domain", 0);
                domains = getTranscriptsGO(filteredgenes.getJSONObject(i).get("transcript_id").toString());
                for (Map domain : domains) {
                    eachTrack.put("domain", domain.get("value"));
                }
                eachTrack.put("flag", false);
            }
            if (!gene_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("gene_id").toString())) {
                if (!gene_id.equalsIgnoreCase("")) {
                    eachGene.put("transcript", transcriptList);
                    GeneList.add(eachGene);
                }
                gene_id = filteredgenes.getJSONObject(i).get("gene_id").toString();
                transcriptList = new JSONArray();
                eachGene.put("id", filteredgenes.getJSONObject(i).get("gene_id"));
                eachGene.put("start", filteredgenes.getJSONObject(i).get("gene_start"));
                eachGene.put("end", filteredgenes.getJSONObject(i).get("gene_end"));
                eachGene.put("desc", filteredgenes.getJSONObject(i).get("gene_name"));
                eachGene.put("strand", filteredgenes.getJSONObject(i).get("gene_strand"));
                eachGene.put("layer", i % 2 + 1);
                eachGene.put("domain", 0);
                domains = getGenesGO(filteredgenes.getJSONObject(i).get("gene_id").toString());
                for (Map domain : domains) {
                    eachGene.put("domain", domain.get("value"));
                }
                if (lastsize < 2 && layer > 2) {
                    layer = 1;
                }
                else {
                    layer = layer;
                }

                if (thissize > 1) {
                    layer = 1;
                }
                layer++;

            }


            JSONObject eachExon = new JSONObject();
            eachExon.put("id", filteredgenes.getJSONObject(i).get("exon_id"));
            eachExon.put("start", filteredgenes.getJSONObject(i).get("exon_start"));
            eachExon.put("end", filteredgenes.getJSONObject(i).get("exon_end"));
            exonList.add(eachExon);

            lastsize = thissize;
        }

        if (filteredgenes.size() > 0) {
            eachTrack.put("Exons", exonList);
            transcriptList.add(eachTrack);
            eachGene.put("transcript", transcriptList);
            GeneList.add(eachGene);
        }
        return GeneList;
    }


    public JSONArray processGenes(List<Map<String, Object>> genes, long start, long end, int delta, int id, String trackId) throws IOException {

        try {
            JSONArray GeneList = new JSONArray();
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            if (template.queryForObject(GET_GENE_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
                if (genes.size() > 0) {
                    ends.add(0, 0);
                    GeneList = getGeneLevel(genes, ends, start, end, delta);
                }
                else {
                }
            }
            else {
                GeneList = recursiveGene(0, id, trackId, start, end, delta);
            }
            if (GeneList.size() == 0) {
                int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
                int countgene = countGene(id, trackId, 0, length);
                if (countgene < 1) {
                    GeneList.add("getGene no result found");
                }
            }

            return GeneList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getGene no result found.");

        }
    }

    public Integer getSeqRegionforone(String searchQuery) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_REGION_ID_SEARCH_For_One, new Object[]{searchQuery}, Integer.class);
            return i;
        }
        catch (EmptyResultDataAccessException e) {
//      throw new IOException(" getSeqRegion no result found");

            return 0;
        }
    }


    public JSONArray getFromExon(int id, String trackId) throws IOException {
        try {
            JSONArray trackList = new JSONArray();

            List<Map<String, Object>> maps = template.queryForList(GET_EXON, new Object[]{id});

            for (Map map : maps) {
                JSONObject eachTrack = new JSONObject();
                eachTrack.put("start", map.get("seq_region_start"));
                eachTrack.put("end", map.get("seq_region_end"));
                eachTrack.put("desc", map.get("seq_region_strand"));
                eachTrack.put("flag", false);

                trackList.add(eachTrack);
            }
            return trackList;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

    public JSONArray getAnnotationId(int query) throws IOException {
        try {
            int coord = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{query}, Integer.class);
            int rank = template.queryForObject(GET_RANK_for_COORD_SYSTEM_ID, new Object[]{coord}, Integer.class);

            JSONArray annotationlist = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_TRACKS_VIEW);

            for (Map map : maps) {
                JSONObject annotationid = new JSONObject();
                annotationid.put("name", map.get("name").toString().replaceAll("\\s+", "_").replaceAll("[.]","_"));
                annotationid.put("id", map.get("id"));
                annotationid.put("desc", map.get("description"));
                annotationid.put("disp", map.get("displayable"));
                annotationid.put("display_label", map.get("display_label").toString().replaceAll("\\s+", "_").replaceAll("[.]","_"));
                annotationid.put("merge", "0");
                annotationid.put("label", "0");
                annotationid.put("graph", "false");
                annotationid.put("colour", map.get("web_data"));
                annotationlist.add(annotationid);
            }
            List<Map<String, Object>> coords = template.queryForList(GET_Coords_sys_API, new Object[]{rank});

            for (Map map : coords) {
                JSONObject annotationid = new JSONObject();

                annotationid.put("name", map.get("name"));
                annotationid.put("id", "cs" + map.get("coord_system_id"));
                annotationid.put("desc", "Coordinate System Rank:" + map.get("rank"));
                annotationid.put("disp", "0");
                annotationid.put("display_label", map.get("name"));
                annotationid.put("merge", "0");
                annotationid.put("label", "0");
                annotationid.put("graph", "false");
//                annotationid.put("colour", "blue");
                annotationlist.add(annotationid);
            }
            return annotationlist;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getAnnotationID no result found");

        }
    }

    public JSONArray getAnnotationIdList(int query) throws IOException {
        try {
            int coord = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{query}, Integer.class);
            int rank = template.queryForObject(GET_RANK_for_COORD_SYSTEM_ID, new Object[]{coord}, Integer.class);

            JSONArray annotationlist = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_Tracks_API);

            for (Map map : maps) {
                JSONObject annotationid = new JSONObject();
                annotationlist.add(map.get("analysis_id"));
            }
            List<Map<String, Object>> coords = template.queryForList(GET_Coords_sys_API, new Object[]{rank});

            for (Map map : coords) {
                JSONObject annotationid = new JSONObject();
                annotationlist.add("cs" + map.get("coord_system_id"));
            }
            return annotationlist;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getAnnotationID no result found");

        }
    }

    public String getDisplayableByAnalysisId(String id) throws IOException {
        try {
            String str = template.queryForObject(GET_DISPLAYABLE_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getDescriptionByAnalysisId no result found");

        }
    }

    public String getDisplayLableByAnalysisId(String id) throws IOException {
        try {
            String str = template.queryForObject(GET_DISPLAYLABLE_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException(" getDescriptionByAnalysisId no result found");

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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getHit no result found");

        }
    }

    public String getTrackDesc(String id) throws IOException {
        try {

            String description = "";

            List<Map<String, Object>> rows = template.queryForList(Get_Tracks_Desc, new Object[]{id});

            for (Map row : rows) {
                description = row.get("description").toString();
            }
            return description;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("Track Description no result found");

        }
    }

    public List<Map> getTrackInfo() throws IOException {
        try {
            List map = template.queryForList(Get_Tracks_Info);
            return map;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("Track Info no result found");

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
        }
        catch (EmptyResultDataAccessException e) {
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
        }
        catch (EmptyResultDataAccessException e) {
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
                    }
                    else {
                        start_temp = end_cmp - (asm_end - from) + 1;
                    }
                    if (to >= asm_end) {
                        end_temp = end_cmp;
                    }
                    else {
                        end_temp = to - asm_start + 1;
                    }
                    seq += getSeqLevel(map.get("cmp_seq_region_id").toString(), start_temp, end_temp);
                }
                else {

                    maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{map.get("cmp_seq_region_id"), from, to, from, to, from, to, from, to});
                    int asm_start = Integer.parseInt(map.get("asm_start").toString());
                    int asm_end = Integer.parseInt(map.get("asm_end").toString());
                    int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                    int end_cmp = Integer.parseInt(map.get("cmp_end").toString());

                    if (from <= asm_start) {
                        from = start_cmp;
                    }
                    else {
                        from = from - start_cmp + 1;
                    }
                    if (to >= asm_end) {
                        to = end_cmp;
                    }
                    else {
                        to = (to - from);

                    }
                    seq += getSeqRecursive(map.get("cmp_seq_region_id").toString(), from, to, asm_from, asm_to);
                }
            }


            return seq;
        }
        catch (EmptyResultDataAccessException e) {
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
            }
            else {
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
                        }
                        else {
                            start_temp = end_cmp - (asm_end - from) + 1;
                        }
                        if (to >= asm_end) {
                            end_temp = end_cmp;
                        }
                        else {
                            end_temp = to - asm_start + 1;
                        }

                        seq = getSeqLevel(map.get("cmp_seq_region_id").toString(), start_temp, end_temp);
                    }
                    else {

                        maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{map.get("cmp_seq_region_id"), from, to, from, to, from, to, from, to});
                        int asm_start = Integer.parseInt(map.get("asm_start").toString());
                        int asm_end = Integer.parseInt(map.get("asm_end").toString());
                        int start_cmp = Integer.parseInt(map.get("cmp_start").toString());
                        int end_cmp = Integer.parseInt(map.get("cmp_end").toString());

                        if (from <= asm_start) {
                            from = start_cmp;
                        }
                        else {
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
        }
        catch (EmptyResultDataAccessException e) {
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
        }
        catch (EmptyResultDataAccessException e) {
            throw new IOException("getMarker no result found");

        }
    }

    public boolean checkChromosome() throws Exception {
        try {
            Boolean check;
            List<Map<String, Object>> attrib_temp = template.queryForList(GET_coord_attrib_chr, new Object[]{"%chr%", "%chr%"});
            if (attrib_temp.size() > 0) {
                check = true;
            }
            else {
                check = false;
            }
            return check;
        }
        catch (EmptyStackException e) {
            throw new Exception("Chromosome not found");
        }
    }

    public String getCoordSys(String query) throws Exception {
        try {
            String coordSys = "";
            int coord_id = Integer.parseInt(template.queryForObject(GET_coord_sys_id_by_name, new Object[]{query}, String.class));
            coordSys = template.queryForObject(GET_coord_sys_name, new Object[]{coord_id}, String.class);
            return coordSys;
        }
        catch (EmptyStackException e) {
            throw new Exception("Chromosome not found");
        }
    }
}