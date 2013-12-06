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
# but WITHOUString ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with TGAC Browser.  If not, see <http://www.gnu.org/licenses/>.
#
# ***********************************************************************
#
 */

package uk.ac.bbsrc.tgac.browser.core.store;

import net.sf.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:03:58
 * To change this template use File | Settings | File Templates.
 */
public interface SequenceStore extends Store {
    public String getSeqBySeqRegionId(int query) throws IOException;

    public Integer getSeqRegion(String query) throws IOException;
    public String getSeqLengthbyId(int query) throws IOException;
    public String getSeqRegionName(int query) throws IOException;
    public Map<String, Object> getStartEndAnalysisIdBySeqRegionId(int query) throws IOException;


    public JSONArray getTableswithanalysis_id() throws IOException;
    public JSONArray getdbinfo() throws IOException;
    public String getDomains(String geneid) throws IOException;
    public String getSeq(String query, int from, int to) throws IOException;
    public JSONArray getSeqRegionSearchMap(String query) throws IOException;
    public JSONArray getSeqRegionIdSearch(String query) throws IOException;
    public int getSeqRegionearchsize(String query) throws IOException;
    public Integer getSeqRegionforone(String searchQuery) throws IOException;
    public Integer getSeqRegionCoordId(String query) throws IOException;

    public JSONArray getMarker() throws IOException;
    public String getCoordSys(String query) throws Exception;

}