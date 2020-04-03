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

package uk.ac.bbsrc.earlham.browser.blastmanager.store;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.earlham.browser.core.store.Store;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 05/12/2013
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public interface BLASTManagerStore extends Store {
    public boolean checkDatabase(String query, String db, String link, String type, String filter, String format) throws Exception;
    public boolean checkResultDatabase(String query) throws Exception;
    public  String getSeq(String id) throws ClassNotFoundException;
    public String getIDFromDatabase(String query, String db, String link, String type, String filter, String format) throws Exception;
    public void setResultToDatabase(String blastAccession, JSONArray result) throws Exception;
    public void updateDatabase(String taskId, String status) throws Exception;
    public JSONArray getFromDatabase(String id, String link) throws Exception;
    public JSONArray getBLASTEntryFromDatabase(String id, String seqRegion) throws Exception;
    public JSONArray getTrackFromDatabase(String id, int query_start) throws Exception;
    public void insertintoDatabase(String taskId, String query, String db, String link, String type, String filter, String format) throws Exception;
    public String getStatusFromDatabase(String query) throws Exception;
    public JSONObject getConnectioInfo() throws Exception;

    }
