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

package uk.ac.bbsrc.earlham.browser.core.store;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 25-Oct-2013
 * Time: 11:00:38
 * To change this template use File | Settings | File Templates.
 */
public interface MiscFeatureStore extends Store {
//    public int countMarker(int id, String trackId, long start, long end);
//    public List<Map<String, Object>> getMarker(int query, String trackId, long start, long end) throws Exception;
//    public JSONArray processMarker(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws Exception;
//    public JSONArray getMarkerGraph(int id, String trackId, long start, long end) throws Exception;

    public JSONObject getAllMiscFeatures() throws Exception;
    public JSONArray getAllAttrib_type() throws Exception;
    public JSONArray getMiscSet() throws Exception;
    public JSONArray getMiscFeaturesbyID(String name) throws Exception;
    public int countMiscFeature(int id, String trackId, long start, long end) throws Exception;
    public int countMiscFeature(int id, String trackId, long start, long end, int line_id) throws Exception;
    public List<Map<String, Object>> getFeatures(int query, String trackId, long start, long end) throws IOException;
    public List<Map<String, Object>> getFeatures(int id, String trackId, long start, long end, int line_id) throws IOException;
    public int getLineID(String line) throws IOException;
    public JSONArray processFeatures(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws Exception;
    public JSONArray getFeatureGraph(int id, String trackId, long start, long end) throws IOException;
    public JSONArray getFeatureGraph(int id, String trackId, long start, long end, int line_id) throws IOException;




}
