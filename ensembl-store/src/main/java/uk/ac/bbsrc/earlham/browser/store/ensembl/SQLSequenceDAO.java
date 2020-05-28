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
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.earlham.browser.core.store.SequenceStore;

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


    public static final String GET_SEQ_FROM_SEQ_REGION_ID = "SELECT sequence FROM dna WHERE seq_region_id = ?";
    public static final String GET_SEQ_REGION_ID_FROM_NAME = "SELECT seq_region_id FROM seq_region WHERE name  = ?";
    public static final String GET_SEQ_REGION_ID_FROM_NAME_AND_COORD = "SELECT seq_region_id FROM seq_region WHERE name  = ? and coord_system_id = ?";

    public static final String GET_SEQ_REGION_ID_SEARCH = "SELECT s.seq_region_id, s.name, s.length, cs.name as Type FROM seq_region s, coord_system cs WHERE s.name like ? and cs.coord_system_id = s.coord_system_id;";

    public static final String GET_SIZE_SEQ_REGION_ID_SEARCH = "SELECT name FROM seq_region WHERE name like ? limit 10";
    public static final String GET_SIZE_SEQ_REGION_ID_SEARCH_FOR_MATCH = "SELECT count(length) FROM seq_region WHERE name = ?";
    public static final String GET_SEQ_REGION_ID_SEARCH_all = "SELECT * FROM seq_region WHERE coord_system_id = ?";
    public static final String GET_CHR_MAP = "SELECT * FROM seq_region WHERE coord_system_id = ? and seq_region_id in (select distinct seq_region_id from karyotype)";
    public static final String COUNT_KARYOTYPE = "SELECT count(*) FROM karyotype";
    public static final String GET_SEQ_REGION_ID_IN_KARYOTYPE = "SELECT seq_region_id from karyotype";
    public static final String GET_SEQ_REGION_NAME_FROM_ID = "SELECT name FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_REGION_NAME_FROM_ID_AND_COORD = "SELECT name FROM seq_region WHERE seq_region_id = ? and coord_system_id = ?";

    public static final String GET_SEQ_LENGTH_FROM_ID = "SELECT length FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_SEQ_LENGTH_FROM_ID_AND_COORD = "SELECT length FROM seq_region WHERE seq_region_id = ? and coord_system_id = ?";
    public static final String GET_SEQ_LENGTH_FROM_NAME_AND_COORD = "SELECT length FROM seq_region WHERE name = ? and coord_system_id = ?";

    public static final String GET_SEQ_REGION_ID_SEARCH_For_One = "SELECT seq_region_id FROM seq_region WHERE name = ?";

    public static final String GET_Domain_per_Gene = "SELECT * FROM transcript_attrib where transcript_id =?";

    public static final String GET_START_END_ANALYSIS_ID_FROM_SEQ_REGION_ID = "SELECT seq_region_start,seq_region_end,analysis_id FROM dna_align_feature where req_region_id =?";


    public static final String Get_Database_information = "SELECT meta_key,meta_value from meta";// + var1;

    public static final String GET_Seq_API = "SELECT SUBSTRING(sequence, ?, ?) FROM dna where seq_region_id = ?";

    public static final String GET_reference_for_Assembly = "SELECT * FROM assembly where cmp_seq_region_id =?";
    public static final String GET_SEQS_LIST_API = "SELECT *  FROM assembly a, seq_region s, coord_system cs  where a.asm_seq_region_id = ? AND s.seq_region_id = a.cmp_seq_region_id AND cs.coord_system_id = s.coord_system_id AND cs.attrib like '%sequence%' AND   ((a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";


    public static final String GET_coord_attrib = "SELECT attrib FROM coord_system where coord_system_id =?";
    public static final String GET_coord_sys_name = "SELECT name FROM coord_system where coord_system_id =?";
    public static final String GET_coord_sys_id_by_name = "SELECT coord_system_id FROM seq_region where name =?";
    public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
    public static final String CHECK_Coord_sys_attr = "select * from coord_system where coord_system_id = ? and (name like ? OR attrib like ?);";
    public static final String GET_coord_attrib_chr = "SELECT * FROM coord_system where name like ? || attrib like ?";

    //  public static final String GET_GENOME_MARKER = "SELECT * from marker_feature";
    public static final String GET_GENOME_MARKER = "select mf.marker_feature_id as id, sr.name as reference, mf.marker_id as marker_id, mf.seq_region_start as start, mf.seq_region_end as end, mf.analysis_id as analysis_id from marker_feature mf, seq_region sr where mf.seq_region_id = sr.seq_region_id  and sr.name = ? and sr.coord_system_id = ? and (mf.seq_region_start >= ? and mf.seq_region_end <= ?);";

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
            throw new IOException(" getSeqBySeqRegionId no result found" + e.getMessage());

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
            throw new IOException(" getSeqRegionCoordId" + e.getMessage());
//            return 0;
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

    public int getAssemblyReference(int id) {
        int ref_id = 0;
        if (checkCoord(id, "chr")) {
            ref_id = id;
        } else {
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


    public JSONArray getSeqRegionSearchMap() throws IOException {
        try {
            JSONArray names = new JSONArray();
            List<Map<String, Object>> attrib_temp = template.queryForList(GET_coord_attrib_chr, new Object[]{"%chr%", "%chr%"});
            JSONObject eachName = new JSONObject();
            if (attrib_temp.size() > 0) {
                for (Map attrib : attrib_temp) {
                    List<Map<String, Object>> maps;
                    int count = template.queryForInt(COUNT_KARYOTYPE, new Object[]{});
                    if (count > 0){
                        maps = template.queryForList(GET_CHR_MAP, new Object[]{attrib.get("coord_system_id").toString()});
                    }else{
                        maps = template.queryForList(GET_SEQ_REGION_ID_SEARCH_all, new Object[]{attrib.get("coord_system_id").toString()});
                    }
                    for (Map map : maps) {
                            eachName.put("name", map.get("name"));
                            eachName.put("seq_region_id", map.get("seq_region_id"));
                            eachName.put("length", map.get("length"));
                            eachName.put("coord_name", attrib.get("name"));
                            eachName.put("coord", attrib.get("coord_system_id"));
                            names.add(eachName);
                    }
                }
            }
            return names;
        } catch (EmptyResultDataAccessException e) {
            //     return getGOSearch(searchQuery);
            e.printStackTrace();

            throw new IOException("Seq region search map result not found" + e.getMessage());
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
            throw new IOException("seqregion id search result not found" + e.getMessage());
        }
    }


    public int getSeqRegionearchsize(String searchQuery) throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(GET_SIZE_SEQ_REGION_ID_SEARCH, new Object[]{'%' + searchQuery + '%'});
            return maps.size();
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("seqregion search size result not found" + e.getMessage());
        }
    }

    public int getSeqRegionearchsizeformatch(String searchQuery) throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(GET_SIZE_SEQ_REGION_ID_SEARCH, new Object[]{searchQuery} );
            return maps.size();
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("seqregion search size result not found" + e.getMessage());
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


    public Integer getSeqRegionWithCoord(String searchQuery, String coord) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_REGION_ID_FROM_NAME_AND_COORD, new Object[]{searchQuery, coord}, Integer.class);
            return i;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public String getSeqRegionName(int searchQuery, String coord) throws IOException {
        try {
            String str = template.queryForObject(GET_SEQ_REGION_NAME_FROM_ID_AND_COORD, new Object[]{searchQuery, coord}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqRegionName no result found");

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

    public int getSeqLengthbyId(int query, String coord) throws IOException {
        try {
            int i = template.queryForObject(GET_SEQ_LENGTH_FROM_ID_AND_COORD, new Object[]{query, coord}, Integer.class);
            return i;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getSeqlength no result found");

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

    public String getUnit() throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(Get_Database_information, new Object[]{});
            String unit = "";
            for (Map map : maps) {
                String metakey = map.get("meta_key").toString();
                if (metakey.contains("unit")) {
                    unit =  map.get("meta_value").toString();
                }
            }
            return unit;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("  getUnit no result found");

        }
    }

    public String getScale() throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(Get_Database_information, new Object[]{});
            String scale = "";
            for (Map map : maps) {
                String metakey = map.get("meta_key").toString();
                if (metakey.contains("scale")) {
                    scale = map.get("meta_value").toString();
                }
            }
            return scale;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getScale no result found");
        }
    }

    public String getLink() throws IOException {
        try {
            List<Map<String, Object>> maps = template.queryForList(Get_Database_information, new Object[]{});
            String link = "";
            for (Map map : maps) {
                String metakey = map.get("meta_key").toString();
                if (metakey.contains("external_browser")) {
                    link = map.get("meta_value").toString();
                }
            }
            return link;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException(" getLink no result found");
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

            if (from < 0) {
                from = 0;
            }
            seq = template.queryForObject(GET_Seq_API, new Object[]{from, (to-from)+1,query}, String.class);

            return seq;//.substring(from-1, to-1);
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

    public JSONArray getMarker(String query,String coord) throws IOException {
        try {
            JSONArray markerList = new JSONArray();
            long from = 0;
            long to = 0;
            int no_of_tracks = 0;
                           int length = 0;
              length =  template.queryForObject(GET_SEQ_LENGTH_FROM_NAME_AND_COORD, new Object[]{query,coord}, Integer.class);
            int diff = length / 100;

            for (int i = 1; i <= 100; i++) {
                JSONObject eachTrack = new JSONObject();
                to = (i * diff);
                no_of_tracks = countMarker(query, coord, from, to);
                eachTrack.put("start", from);
                eachTrack.put("end", to);
                eachTrack.put("graph", no_of_tracks);
                markerList.add(eachTrack);
                from = to;
            }
            return markerList;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getMarker no result found");

        }
    }

    public JSONArray getMarkerforRegion(String query,String coord, long start, long end) throws IOException {
        try {
            JSONArray markerList = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_GENOME_MARKER, new Object[]{query,coord, start, end});
            for (Map map : maps) {
                markerList.add(map);
            }
            return markerList;
        } catch (EmptyResultDataAccessException e) {
            throw new IOException("getMarker no result found");

        }
    }

    public int countMarker(String query,String coord, long start, long end){
        List<Map<String, Object>> maps = template.queryForList(GET_GENOME_MARKER, new Object[]{query,coord, start, end});
        return maps.size();
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


