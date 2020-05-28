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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.earlham.browser.core.store.AnalysisStore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.ArrayUtils.contains;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 1-Nov-2013
 * Time: 10:26:53
 * To change this template use File | Settings | File Templates.
 */

public class SQLAnalysisDAO implements AnalysisStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLAnalysisDAO.class);

    @Autowired
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public JdbcTemplate getJdbcTemplate() {
        return template;
    }

    public static final String GET_DISPLAYABLE_FROM_ANALYSIS_ID = "SELECT displayable FROM analysis_description where analysis_id =?";
    public static final String GET_DISPLAYLABLE_FROM_ANALYSIS_ID = "SELECT display_label FROM analysis_description where analysis_id =?";
    public static final String GET_LOGIC_NAME_FROM_ANALYSIS_ID = "SELECT logic_name FROM analysis where analysis_id =?";
    public static final String GET_DESCRIPTION_FROM_ANALYSIS_ID = "SELECT description FROM analysis_description where analysis_id =?";
    public static final String GET_Tracks_API = "select analysis_id, logic_name from analysis";
    public static final String Get_Tracks_Desc = "select description from analysis_description where analysis_id = ?";
    public static final String Get_Tracks_Info = "select * from analysis_description";
    public static final String GET_ANALYSIS_ID_FROM_LOGIC_NAME = "select analysis_id from analysis where logic_name = ?";
    public static final String GET_TRACKS_VIEW = "select a.logic_name as name, a.analysis_id as id, ad.description, ad.display_label, ad.displayable, ad.web_data from analysis a, analysis_description ad where a.analysis_id = ad.analysis_id;";
    public static final String GET_Coords_sys_API = "SELECT coord_system_id,name,rank, version FROM coord_system where rank > ?";
    public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_RANK_for_COORD_SYSTEM_ID = "SELECT rank FROM coord_system where coord_system_id =?";
    public static final String GET_DISTINCT_ANALYSIS_ID_FROM_GENE = "SELECT DISTINCT analysis_id from gene where analysis_id = ? LIMIT 1";
    public static final String GET_DISTINCT_ANALYSIS_ID_FROM_Marker = "SELECT DISTINCT analysis_id from marker_feature where analysis_id = ? LIMIT 1";
    public static final String GET_DISTINCT_ANALYSIS_ID_FROM_DAF = "SELECT DISTINCT analysis_id from dna_align_feature where analysis_id = ? LIMIT 1";
    public static final String GET_DISTINCT_ANALYSIS_ID_FROM_Repeat = "SELECT DISTINCT analysis_id from repeat_feature where analysis_id = ? LIMIT 1";
    public static final String GET_SNPS = "SELECT a.analysis_id, ad.display_label FROM analysis a, analysis_description ad where a.logic_name like '%SNP%' and a.analysis_id = ad.analysis_id";
    public static final String COUNT_MISC_FEATURE = "SELECT count(*) from misc_feature";
    private JdbcTemplate template;

    static final String[] ALLOWED_DATATYPES = {"BAM", "bam", "gff3"};

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Get description for tracks
     *
     * @param id
     * @return String with description information
     * @throws IOException
     */
    public String getTrackDesc(String id) throws IOException {
        try {
            String description = "";
            List<Map<String, Object>> rows = template.queryForList(Get_Tracks_Desc, new Object[]{id});

            for (Map row : rows) {
                description = row.get("description").toString();
            }
            return description;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Track Description no result found for track id" + id + "-" + e.getMessage());
        }
    }


    /**
     * Get analysis_id from logic_name
     *
     * @param trackName
     * @return analysis_id
     * @throws IOException
     */
    public String getTrackIDfromName(String trackName) throws IOException {
        try {
            String str = template.queryForObject(GET_ANALYSIS_ID_FROM_LOGIC_NAME, new Object[]{trackName}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Analysis id not found for logic name " + trackName + "-" + e.getMessage());
        }
    }


    /**
     * Get all tracks from analysis and analysis_description table and lower level assembly details from cood_system tab,e
     *
     * @param query
     * @return JSONArray of track information
     * @throws IOException
     */
    public JSONArray getAnnotationId(int query) throws IOException {
        try {
            int coord = template.queryForObject(GET_Coord_systemid_FROM_ID, new Object[]{query}, Integer.class);
            int rank = template.queryForObject(GET_RANK_for_COORD_SYSTEM_ID, new Object[]{coord}, Integer.class);

            JSONArray annotationlist = new JSONArray();
            List<Map<String, Object>> maps = template.queryForList(GET_TRACKS_VIEW);

            for (Map map : maps) {
                JSONObject annotationid = new JSONObject();


                try {
                    JSONObject explrObject = JSONObject.fromObject(map.get("web_data"));

                    if (explrObject.get("source") != null && explrObject.get("source").toString().equals("file")) {
                        annotationid.put("id", explrObject.get("filepath"));
                    } else {
                        annotationid.put("id", map.get("id"));
                    }

                    annotationid.put("web", map.get("web_data"));
                } catch (Exception e) {
                    annotationid.put("id", map.get("id"));
                }

                annotationid.put("desc", map.get("description"));
                annotationid.put("disp", 0);//map.get("displayable"));
                annotationid.put("display_label", map.get("display_label").toString().replaceAll("[^A-Za-z0-9]+", "_"));
                annotationid.put("merge", "0");
                annotationid.put("label", "0");
                annotationid.put("graph", "false");

                JSONObject web = annotationid.getJSONObject("web");
                if (presentInGene(map.get("id").toString())) {
                    web.put("trackgroup", "Genes");
                    annotationid.put("name", map.get("name").toString().replaceAll("[^A-Za-z0-9]+", "_") + "_gene");
                    annotationlist.add(annotationid);
                } else if (presentInDAF(map.get("id").toString())) {
                    web.put("trackgroup", "Alignment features");
                    annotationid.put("name", map.get("name").toString().replaceAll("[^A-Za-z0-9]+", "_"));
                    annotationlist.add(annotationid);

                } else if (presentInRepeat(map.get("id").toString())) {
                    web.put("trackgroup", "Repeats");
                    annotationid.put("name", map.get("name").toString().replaceAll("[^A-Za-z0-9]+", "_") + "_repeat");
                    annotationlist.add(annotationid);

                } else if (presentInMarker(map.get("id").toString())) {
                    web.put("trackgroup", "Markers");
                    annotationid.put("name", map.get("name").toString().replaceAll("[^A-Za-z0-9]+", "_") + "_repeat");
                    annotationlist.add(annotationid);

                } else {
//                    annotationid.put("name", map.get("name").toString().replaceAll("[^A-Za-z0-9]+", "_"));
//                    annotationlist.add(annotationid);

                }


            }
            List<Map<String, Object>> coords = template.queryForList(GET_Coords_sys_API, new Object[]{rank});

            JSONObject web = new JSONObject();
            web.put("trackgroup", "Assembly_Tracks");
            for (Map map : coords) {
                JSONObject annotationid = new JSONObject();

                annotationid.put("name", map.get("name") + "-" + map.get("version"));
                annotationid.put("id", "cs" + map.get("coord_system_id"));
                annotationid.put("desc", "Coordinate System Rank-" + map.get("rank"));
                annotationid.put("disp", 1);
                annotationid.put("display_label", map.get("name") + "-" + map.get("version"));
                annotationid.put("merge", "0");
                annotationid.put("label", "0");
                annotationid.put("graph", "false");
                annotationid.put("web", web);
                annotationlist.add(annotationid);
            }

            web = new JSONObject();
            web.put("trackgroup", "Miscellaneous");

            int misc_feature = template.queryForInt(COUNT_MISC_FEATURE, new Object[]{});
            if (misc_feature > 0) {
                JSONObject annotationid = new JSONObject();

                annotationid.put("name", "misc_feature");
                annotationid.put("id", "ms1");
                annotationid.put("desc", "Miscellaneous Features");
                annotationid.put("disp", 0);
                annotationid.put("display_label", "Misc Feature");
                annotationid.put("merge", "0");
                annotationid.put("label", "0");
                annotationid.put("graph", "false");
                annotationid.put("web", web);
                annotationlist.add(annotationid);
            }


            return annotationlist;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("no result found for tracks for " + query + "-" + e.getMessage());
        }
    }

    /**
     * Get logic_name from analysis_id
     *
     * @param id
     * @return Logic Name
     * @throws IOException
     */
    public String getLogicNameByAnalysisId(int id) throws IOException {
        try {
            String str = template.queryForObject(GET_LOGIC_NAME_FROM_ANALYSIS_ID, new Object[]{id}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("Logic name not found for analysis id " + id + "-" + e.getMessage());
        }
    }

    public boolean presentInGene(String id) {
        List<Map<String, Object>> distinct_id = template.queryForList(GET_DISTINCT_ANALYSIS_ID_FROM_GENE, new Object[]{id});
        if (distinct_id.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean presentInDAF(String id) {
        List<Map<String, Object>> distinct_id = template.queryForList(GET_DISTINCT_ANALYSIS_ID_FROM_DAF, new Object[]{id});
        if (distinct_id.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean presentInMarker(String id) {
        List<Map<String, Object>> distinct_id = template.queryForList(GET_DISTINCT_ANALYSIS_ID_FROM_Marker, new Object[]{id});
        if (distinct_id.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean presentInRepeat(String id) {
        List<Map<String, Object>> distinct_id = template.queryForList(GET_DISTINCT_ANALYSIS_ID_FROM_Repeat, new Object[]{id});
        if (distinct_id.size() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public List<Map<String, Object>> listSNPs() throws IOException {
        try {
            List<Map<String, Object>> str = template.queryForList(GET_SNPS, new Object[]{});
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("listSNPs not found -" + e.getMessage());
        }
    }

    public JSONArray tracksFromDir(String dir) throws IOException {
        File f = null;
        String[] paths;

        JSONArray annotationlist = new JSONArray();

        f = new File(dir);

        paths = f.list();

        for (String path : paths) {

            JSONObject annotation = new JSONObject();
            JSONObject web = new JSONObject();

            String ext = FilenameUtils.getExtension(path);
            String name = FilenameUtils.getBaseName(path);

            if (contains(ALLOWED_DATATYPES, ext)) {
                annotation.put("name", name + "_" + ext);
                annotation.put("type", ext);
                annotation.put("path", dir);
                annotation.put("id", dir + "/" + path);
                annotation.put("display_label", name);
                annotation.put("disp", 0);
                annotation.put("merge", 0);
                annotation.put("graph", false);
                annotation.put("desc", "");
                web.put("colour", "green");
                web.put("filepath", dir + "/" + path);
                web.put("source", "file");
                web.put("trackgroup", ext);
                annotation.put("web", web);
                annotationlist.add(annotation);
//                break;
            }

        }

        return annotationlist;
    }
}
