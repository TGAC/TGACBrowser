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
  public static final String GET_SEQ_FROM_SEQ_REGION_ID = "SELECT sequence FROM dna WHERE seq_region_id = ?";
  public static final String GET_SEQ_REGION_ID_FROM_NAME = "SELECT seq_region_id FROM seq_region WHERE name like ? limit 1";
  public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT * FROM seq_region WHERE name like ?";
  public static final String GET_GENE_SEARCH = "SELECT * FROM gene WHERE description like ?";
  public static final String GET_TRANSCRIPT_SEARCH = "SELECT * FROM transcript WHERE description like ?";
  public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
  public static final String GET_SEQ_LENGTH_FROM_ID = "SELECT length FROM seq_region WHERE seq_region_id = ?";
  public static final String GET_LOGIC_NAME_FROM_ANALYSIS_ID = "SELECT logic_name FROM analysis where analysis_id =?";
  public static final String GET_DESCRIPTION_FROM_ANALYSIS_ID = "SELECT description FROM analysis_description where analysis_id =?";
  public static final String GET_Coords_sys_API = "SELECT coord_system_id,name,rank FROM coord_system where rank > ?";
  public static final String GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID = "SELECT seq_region_start,seq_region_end,analysis_id FROM dna_align_feature where req_region_id =?";
  public static final String GET_transcript = "SELECT transcript_id, seq_region_start, seq_region_end,description,seq_region_strand FROM transcript where gene_id =? ORDER BY seq_region_start ASC";
  //  public static final String GET_transcript = "SELECT * FROM transcript where seq_region_id =? AND analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?))";
  public static final String GET_Genes = "SELECT gene_id,seq_region_start,seq_region_end, description,seq_region_strand FROM gene where seq_region_id =? and analysis_id = ? ";//AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?))";
  public static final String GET_Gene_Details = "SELECT * FROM gene where gene_id =? and analysis_id = ?";
  public static final String GET_GO_Gene_Details = "SELECT * FROM gene where gene_id =?";
  public static final String GET_GO_Transcript_Details = "SELECT * FROM transcript where transcript_id =?";
  public static final String GET_HIT_SIZE = "SELECT COUNT(*) FROM dna_align_feature where seq_region_id =? and analysis_id = ?";
  public static final String GET_HIT_SIZE_SLICE = "SELECT COUNT(*) FROM dna_align_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
  public static final String GET_Gene_SIZE_SLICE = "SELECT COUNT(*) FROM gene where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
  public static final String GET_HIT = "SELECT dna_align_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand,hit_start as hitstart, hit_end as hitend, hit_name as 'desc', cigar_line as cigarline FROM dna_align_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start >= ? AND seq_region_end <= ?) OR (seq_region_start <= ? AND seq_region_end >= ?) OR (seq_region_end >= ? AND seq_region_end <= ?) OR (seq_region_start >= ? AND seq_region_start <= ?)) ORDER BY start,(end-start) asc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
  public static final String GET_Tracks_API = "select analysis_id, logic_name from analysis";
  public static final String Get_Tracks_Desc = "select description from analysis_description where analysis_id = ?";
  public static final String Get_Tracks_Info = "select * from analysis_description";
  public static final String GET_EXON = "SELECT seq_region_start,seq_region_end,seq_region_strand FROM exon where seq_region_id =?";
  public static final String GET_EXON_per_Gene = "SELECT e.exon_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand FROM exon e, exon_transcript et where et.exon_id = e.exon_id and et.transcript_id =  ?";
  public static final String GET_Tables_with_analysis_id_column = "SELECT DISTINCT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME IN ('analysis_id') AND TABLE_SCHEMA='wrightj_brachypodium_distachyon_core_10_63_12'";
  public static final String Check_feature_available = "SELECT DISTINCT analysis_id from ";// + var1;
  public static final String Get_Database_information = "SELECT meta_key,meta_value from meta";// + var1;
  public static final String GET_Domain_per_Gene = "SELECT * FROM transcript_attrib where transcript_id =?";
  public static final String GET_CDS_per_Gene = "SELECT * FROM translation where transcript_id =?";
  public static final String GET_Seq_API = "SELECT sequence FROM dna where seq_region_id = ?";
  public static final String GET_GO_Genes = "select * from gene_attrib where value like ?";
  public static final String GET_GO_Transcripts = "select * from transcript_attrib where value like ?";
  public static final String GET_SEQS_LIST_API = "SELECT *  FROM assembly a, seq_region s, coord_system cs  where a.asm_seq_region_id = ? AND s.seq_region_id = a.cmp_seq_region_id AND cs.coord_system_id = s.coord_system_id AND cs.attrib like '%sequence%' AND   ((a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";
  public static final String GET_coord_attrib = "SELECT * FROM coord_system where coord_system_id =?";
  public static final String GET_coord_sys_id = "SELECT coord_system_id FROM seq_region where seq_region_id =?";
  public static final String GET_coord_sys_name = "SELECT name FROM coord_system where coord_system_id =?";
  public static final String GET_SEQ_REGION_ID_SEARCH_For_One = "SELECT seq_region_id FROM seq_region WHERE name = ?";
  public static final String GET_coord_sys_id_by_name = "SELECT coord_system_id FROM seq_region where name =?";
  public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
  public static final String GET_RANK_for_COORD_SYSTEM_ID = "SELECT rank FROM coord_system where coord_system_id =?";
  public static final String GET_Gene_name_from_ID = "SELECT description FROM gene where gene_id =?";
  public static final String GET_Transcript_name_from_ID = "SELECT description FROM transcript where transcript_id =?";
  public static final String GET_Tracks_Name = "select analysis_id from analysis where logic_name = ?";
  public static final String GET_hit_name_from_ID = "SELECT hit_name FROM dna_align_feature where dna_align_feature_id =?";
  public static final String GET_Gene_by_view = "select g.gene_id, g.seq_region_start as gene_start, g.seq_region_end as gene_end, g.seq_region_strand as gene_strand, g. description as gene_name, t.transcript_id, t.seq_region_start as transcript_start, t.seq_region_end as transcript_end, t.description as transcript_name, e.exon_id, e.seq_region_start as exon_start, e.seq_region_end as exon_end, tl.seq_start as translation_start, tl.seq_end as translation_end from gene g, transcript t, exon_transcript et, exon e, translation tl where t.gene_id = g.gene_id and t.transcript_id = et.transcript_id and et.exon_id = e.exon_id and tl.transcript_id = t.transcript_id and  g.seq_region_id = ? and g.analysis_id = ?;";
  public static final String GET_Assembly = "SELECT a.asm_seq_region_id,a.cmp_seq_region_id,a.asm_start,a.asm_end FROM assembly a, seq_region s where a.asm_seq_region_id =? and a.cmp_seq_region_id = s.seq_region_id and s.coord_system_id = ? ORDER BY asm_start ASC";
  public static final String GET_REPEAT = "SELECT repeat_feature_id as id,seq_region_start as start, seq_region_end as end,seq_region_strand as strand, repeat_start as repeatstart,repeat_end as repeatend, score as score FROM repeat_feature where seq_region_id =? and analysis_id = ? AND ((seq_region_start > ? AND seq_region_end < ?) OR (seq_region_start < ? AND seq_region_end > ?) OR (seq_region_end > ? AND seq_region_end < ?) OR (seq_region_start > ? AND seq_region_start < ?)) ORDER BY start,(end-start) asc"; //seq_region_start ASC";//" AND ((hit_start >= ? AND hit_end <= ?) OR (hit_start <= ? AND hit_end >= ?) OR (hit_end >= ? AND hit_end <= ?) OR (hit_start >= ? AND hit_start <= ?))";
  public static final String GET_REPEAT_SIZE = "SELECT COUNT(*) FROM repeat_feature where seq_region_id =? and analysis_id = ?";
  public static final String GET_REPEAT_SIZE_SLICE = "SELECT COUNT(*) FROM repeat_feature where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
  public static final String GET_GO_for_Transcripts = "select value from transcript_attrib where transcript_id =  ?";
  public static final String GET_GO_for_Genes = "select value from gene_attrib where gene_id = ?";




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

  public Integer getSeqRegionCoordId(String query) throws IOException {
    try {
      int coord_id = Integer.parseInt(template.queryForObject(GET_coord_sys_id_by_name, new Object[]{query}, String.class));
      return coord_id;
    }
    catch (EmptyResultDataAccessException e) {
      return 0;
//      throw new IOException("Sequence not found");
    }
  }

  public JSONArray getGenesSearch(String searchQuery) throws IOException {
    try {
      JSONArray genes = new JSONArray();
      List<Map<String, Object>> maps = template.queryForList(GET_GENE_SEARCH, new Object[]{'%' + searchQuery + '%'});
      for (Map map : maps) {
        JSONObject eachGene = new JSONObject();
        eachGene.put("Type", getLogicNameByAnalysisId(Integer.parseInt(map.get("analysis_id").toString())));
        eachGene.put("name", map.get("description"));
        eachGene.put("start", map.get("seq_region_start"));
        eachGene.put("end", map.get("seq_region_end"));
        eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
        eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
        genes.add(eachGene);
      }
      return genes;
    }
    catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
      throw new IOException("result not found");
    }
  }

  public JSONArray getSeqRegionearch(String searchQuery) throws IOException {
    try {
      JSONArray names = new JSONArray();
      List<Map<String, Object>> maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});
      for (Map map : maps) {
        JSONObject eachName = new JSONObject();
        eachName.put("name", map.get("name"));
        eachName.put("seq_region_id", map.get("seq_region_id"));
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
        eachGene.put("start", map.get("seq_region_start"));
        eachGene.put("end", map.get("seq_region_end"));
        eachGene.put("parent", getSeqRegionName(Integer.parseInt(map.get("seq_region_id").toString())));
        eachGene.put("analysis_id", template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{map.get("analysis_id")}, String.class));
        genes.add(eachGene);
      }
      return genes;
    }
    catch (EmptyResultDataAccessException e) {
//     return getGOSearch(searchQuery);
      throw new IOException("result not found");
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
          eachGo.put("start", gene.get("seq_region_start"));
          eachGo.put("end", gene.get("seq_region_end"));
          eachGo.put("Type", "Gene");
          eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
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
          eachGo.put("start", gene.get("seq_region_start"));
          eachGo.put("end", gene.get("seq_region_end"));
          eachGo.put("Type", "Transcript");
          eachGo.put("parent", getSeqRegionName(Integer.parseInt(gene.get("seq_region_id").toString())));
          eachGo.put("analysis_id", getLogicNameByAnalysisId(Integer.parseInt(gene.get("analysis_id").toString())));
          GOs.add(eachGo);
        }
      }
      return GOs;
    }
    catch (EmptyResultDataAccessException e) {
      throw new IOException("result not found");
    }
  }

  public Integer getSeqRegion(String searchQuery) throws IOException {
    try {
      int i = template.queryForObject(GET_SEQ_REGION_ID_FROM_NAME, new Object[]{'%' + searchQuery + '%'}, Integer.class);
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

  public JSONArray getHitGraph(int id, String trackId, long start, long end) throws IOException {
    try {
      JSONArray trackList = new JSONArray();
      long from = start;
      long to = 0;
      for (int i = 1; i <= 200; i++) {
        JSONObject eachTrack = new JSONObject();
        to = start + (i * (end - start) / 200);
        int no_of_tracks = template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
        eachTrack.put("start", from);
        eachTrack.put("end", to);
        eachTrack.put("graph", no_of_tracks);
        trackList.add(eachTrack);
        from = to;
      }
      return trackList;
    }
    catch (EmptyResultDataAccessException e) {
      throw new IOException("getHit no result found");
    }
  }

  public int countHit(int id, String trackId, long start, long end) {
    return template.queryForObject(GET_HIT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
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
  public List<Map<String, Object>> getHit(int id, String trackId, long start, long end) throws IOException {
    return template.queryForList(GET_HIT, new Object[]{id, trackId, start, end, start, end, end, end, start, start});
  }

  public JSONArray processHit(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException {
    try {
      JSONArray trackList = new JSONArray();

      List<Integer> ends = new ArrayList<Integer>();
      ends.add(0, 0);

      if (template.queryForObject(GET_HIT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
        if (maps.size() > 0) {
          if (Integer.parseInt(maps.get(0).get("end").toString()) - Integer.parseInt(maps.get(0).get("start").toString()) > 1) {
            for (Map<String, Object> map : maps) {
              int start_pos = Integer.parseInt(map.get("start").toString());
              int end_pos = Integer.parseInt(map.get("end").toString());
              if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                trackList.add(map);
              }
            }
          }
          else {
            for (Map map : maps) {
              JSONObject eachTrack = new JSONObject();
              int start_pos = Integer.parseInt(map.get("start").toString());
              int end_pos = Integer.parseInt(map.get("end").toString());
              if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                eachTrack.put("id", map.get("id"));
                eachTrack.put("start", map.get("start"));
                eachTrack.put("cigarline", map.get("cigarline"));
                eachTrack.put("flag", false);
                trackList.add(eachTrack);
              }
            }
          }
        }
      }
      else {
        trackList.add("getHit no result found");
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
        for (int i = 1; i <= 200; i++) {
          JSONObject eachTrack = new JSONObject();
          to = start + (i * (end - start) / 200);
          int no_of_tracks = template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
          eachTrack.put("start", from);
          eachTrack.put("end", to);
          eachTrack.put("graph", no_of_tracks);
          trackList.add(eachTrack);
          from = to;
        }
        return trackList;
      }
      catch (EmptyResultDataAccessException e) {
        throw new IOException("getHit no result found");
      }
    }
  public int countRepeat(int id, String trackId, long start, long end) {
      return template.queryForObject(GET_REPEAT_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
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
      return template.queryForList(GET_REPEAT, new Object[]{id, trackId, start, end, start, end, end, end, start, start});
    }

    public JSONArray processRepeat(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException {
      try {
        JSONArray trackList = new JSONArray();

        List<Integer> ends = new ArrayList<Integer>();
        ends.add(0, 0);

        if (template.queryForObject(GET_REPEAT_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
          if (maps.size() > 0) {
            if (Integer.parseInt(maps.get(0).get("end").toString()) - Integer.parseInt(maps.get(0).get("start").toString()) > 1) {
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
                              else if ((start_pos - ends.get(i) < delta) && (i + 1) == ends.size()) {
                                if (i == 0) {
//                                  read.put("layer", ends.size());
                                  ends.add(i, end_pos);
                                }
                                else {
                                  map.put("layer", ends.size());
                                  ends.add(ends.size(), end_pos);
                                }
                                break;
                              }
                              else {
                                continue;
                              }
                            }
                  log.info(map.get("strand")+"");

                  trackList.add(map);
                }
              }
            }
          }
        }
        else {
          trackList.add("getHit no result found");
        }
        return trackList;
      }
      catch (EmptyResultDataAccessException e) {
        throw new IOException("getHit no result found");
      }
    }

  public JSONArray getAssembly(int id, String trackId, int delta) throws IOException {
    try {
      JSONArray trackList = new JSONArray();

      List<Map<String, Object>> maps = template.queryForList(GET_Assembly, new Object[]{id, trackId.replace("cs", "")});
      List<Integer> ends = new ArrayList<Integer>();
      ends.add(0, 0);
      int layer = 1;
      for (Map map : maps) {
        JSONObject eachTrack = new JSONObject();
        eachTrack.put("start", map.get("asm_start"));
        eachTrack.put("end", map.get("asm_end"));
        eachTrack.put("flag", false);
        for (int i = 0; i < ends.size(); i++) {
          if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i)) > delta) {
            ends.remove(i);
            ends.add(i, Integer.parseInt(map.get("asm_end").toString()));
            eachTrack.put("layer", i + 1);
            break;
          }
          else if ((Integer.parseInt(map.get("asm_start").toString()) - ends.get(i) < delta) && (i + 1) == ends.size()) {
            if (i == 0) {
              eachTrack.put("layer", ends.size());
              ends.add(i, Integer.parseInt(map.get("asm_end").toString()));
            }
            else {
              eachTrack.put("layer", ends.size());
              ends.add(ends.size(), Integer.parseInt(map.get("asm_end").toString()));
            }
            break;
          }
          else {
//             continue;
          }
        }
        eachTrack.put("desc", template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID, new Object[]{map.get("cmp_seq_region_id")}, String.class));
        trackList.add(eachTrack);
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
      for (int i = 1; i <= 200; i++) {
        JSONObject eachTrack = new JSONObject();
        to = start + (i * (end - start) / 200);
        int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
        eachTrack.put("start", from);
        eachTrack.put("end", to);
        eachTrack.put("graph", no_of_tracks);
        trackList.add(eachTrack);
        from = to;
      }
      return trackList;
    }
    catch (EmptyResultDataAccessException e) {
      throw new IOException("getHit no result found");
    }
  }

  public int countGene(int id, String trackId, long start, long end) {
    return template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
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

  public JSONArray processGenes(List<Map<String, Object>> genes, long start, long end) throws IOException {

    try {
      JSONArray filteredgenes = new JSONArray();
      JSONArray GeneList = new JSONArray();
      JSONObject eachGene = new JSONObject();
      JSONObject eachTrack = new JSONObject();
      JSONArray exonList = new JSONArray();
      JSONArray transcriptList = new JSONArray();
      String gene_id = "";
      String transcript_id = "";
      int layer = 1;
      int lastsize = 0;
      int thissize = 0;
      List<Map<String, Object>> domains;

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
          eachTrack.put("transcript_start", filteredgenes.getJSONObject(i).get("translation_start"));
          eachTrack.put("transcript_end", filteredgenes.getJSONObject(i).get("translation_end"));
          eachTrack.put("desc", filteredgenes.getJSONObject(i).get("transcript_name"));

          eachTrack.put("layer", layer);
          eachTrack.put("domain", 0);
          domains = getTranscriptsGO(filteredgenes.getJSONObject(i).get("transcript_id").toString());
          for (Map domain : domains) {
            eachTrack.put("domain", domain.get("value"));
          }
//          log.info("transcript "+filteredgenes.getJSONObject(i).get("transcript_id"));
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
          log.info("gene "+filteredgenes.getJSONObject(i).get("gene_id"));

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
      List<Map<String, Object>> maps = template.queryForList(GET_Tracks_API);

      for (Map map : maps) {
        JSONObject annotationid = new JSONObject();

        annotationid.put("name", map.get("logic_name"));
        annotationid.put("id", map.get("analysis_id"));
        annotationid.put("desc", getTrackDesc(map.get("analysis_id").toString()));
        annotationid.put("disp", getDisplayableByAnalysisId(map.get("analysis_id").toString()));
        annotationid.put("display_label", getDisplayLableByAnalysisId(map.get("analysis_id").toString()));
        annotationid.put("merge", "0");
        annotationid.put("label", "0");
        annotationid.put("graph", "false");
        annotationlist.add(annotationid);
      }
      List<Map<String, Object>> coords = template.queryForList(GET_Coords_sys_API, new Object[]{rank});

      for (Map map : coords) {
        JSONObject annotationid = new JSONObject();

        annotationid.put("name", map.get("name"));
        annotationid.put("id", "cs" + map.get("coord_system_id"));
        annotationid.put("desc", "Coordinate System Rank:" + map.get("rank"));
        annotationid.put("disp", "0");
        annotationid.put("merge", "0");
        annotationid.put("label", "0");
        annotationid.put("graph", "false");
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
      log.info(id+"\t"+str);

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

  public String getSeq(String query, int from, int to) throws IOException {
    try {
      String seq = "";
      String coord_id = template.queryForObject(GET_coord_sys_id, new Object[]{query}, String.class);
      List<Map<String, Object>> maps = template.queryForList(GET_coord_attrib, new Object[]{coord_id});
      String attrib = "";
      for (Map map : maps) {
        attrib = map.get("attrib").toString();
      }
      if (attrib.indexOf("sequence") >= 0) {
        seq = template.queryForObject(GET_Seq_API, new Object[]{query}, String.class);
      }
      else {
        maps = template.queryForList(GET_SEQS_LIST_API, new Object[]{query, from, to, from, to, from, to, from, to});
        for (Map map : maps) {
          int start = Integer.parseInt(map.get("cmp_start").toString());
          int end = Integer.parseInt(map.get("cmp_end").toString());

          seq += (template.queryForObject(GET_Seq_API, new Object[]{map.get("cmp_seq_region_id")}, String.class)).substring(start, end);
        }
      }
      seq = seq.substring(from-1, to-1);
      return seq;
    }
    catch (EmptyResultDataAccessException e) {
      return "";
//      throw new IOException("Sequence not found");
    }
  }
}
