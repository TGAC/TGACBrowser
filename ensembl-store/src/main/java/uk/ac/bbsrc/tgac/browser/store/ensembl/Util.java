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

import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.core.store.UtilsStore;


/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 05/12/2013
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class Util implements UtilsStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLSequenceDAO.class);
    @Autowired
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private JdbcTemplate template;


    public JdbcTemplate getJdbcTemplate() {
        return template;
    }

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public static final String GET_ASSEMBLY_SIZE_SLICE = "SELECT COUNT(a.asm_seq_region_id) FROM assembly a, seq_region s WHERE a.asm_seq_region_id = ? AND a.cmp_seq_region_id = s.seq_region_id AND s.coord_system_id = ? AND ( (a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";
    public static final String GET_Coord_systemid_FROM_ID = "SELECT coord_system_id FROM seq_region WHERE seq_region_id = ?";
    public static final String GET_ASSEMBLY_SIZE_SLICE_for_ref = "SELECT count(a.asm_seq_region_id) FROM assembly a, seq_region s where a.asm_seq_region_id = ? and a.cmp_seq_region_id = s.seq_region_id and  a.asm_start >= ? and a.asm_start <= ?";
    public static final String GET_Assembly_for_reference_SIZE_SLICE = "SELECT * FROM assembly a, seq_region s WHERE a.asm_seq_region_id = ? AND a.cmp_seq_region_id = s.seq_region_id AND ( (a.asm_start >= ? AND a.asm_end <= ?) OR (a.asm_start <= ? AND a.asm_end >= ?) OR (a.asm_end >= ? AND a.asm_end <= ?) OR (a.asm_start >= ? AND a.asm_start <= ?))";


    /**
     * computer layer of the track to be shown
     *
     * @param ends
     * @param start_pos
     * @param delta
     * @param end_pos
     * @return number of layer as an integer
     */
    public int stackLayerInt(List<Integer> ends, int start_pos, int delta, int end_pos) throws Exception {
        try {
            int position = 0;
            for (int a = 0; a < ends.size(); a++) {
                if (start_pos - ends.get(a) >= delta) {
                    position = (a + 1);
                    break;
                } else if ((start_pos - ends.get(a) <= delta && (a + 1) == ends.size()) || start_pos == ends.get(a)) {
                    if (a == 0) {
                        position = ends.size();
                    } else {
                        position = ends.size()+1;
                    }
                    break;
                } else {
                    continue;
                }
            }
            return position;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * computer layer of the track to be shown
     *
     * @param ends
     * @param start_pos
     * @param delta
     * @param end_pos
     * @return List of layer list to be reuse
     */
    public List<Integer> stackLayerList(List<Integer> ends, int start_pos, int delta, int end_pos) throws Exception {
        try {
            for (int a = 0; a < ends.size(); a++) {
                if (start_pos - ends.get(a) >= delta) {
                    ends.set(a, end_pos);
                    break;
                } else if ((start_pos - ends.get(a) <= delta && (a + 1) == ends.size()) || start_pos == ends.get(a)) {
                    if (a == 0) {
                        ends.add(a, end_pos);
                    } else {
                        ends.add(ends.size(), end_pos);
                    }
                    break;
                } else {
                    continue;
                }
            }
            return ends;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
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
                        hit_size += countRecursiveAssembly(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
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
            if (trackId.contains("cs")) {
                trackId.replace("cs", "");
            }
            int hit_size = template.queryForObject(GET_ASSEMBLY_SIZE_SLICE, new Object[]{id, trackId, start, end, start, end, end, end, start, start}, Integer.class);

            if (hit_size == 0) {
                hit_size = countRecursiveAssembly(id, trackId, start, end);
            }
            return hit_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Count Assembly " + e.getMessage());
        }
    }

}
