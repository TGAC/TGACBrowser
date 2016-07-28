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

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 25-Oct-2013
 * Time: 11:00:38
 * To change this template use File | Settings | File Templates.
 */
public interface AssemblyStore extends Store {
    public int countAssembly(int id, String trackId, long start, long end) throws Exception;
    public JSONArray getAssemblyGraph(int id, String trackId, long start, long end) throws IOException;
    public JSONArray getAssembly(int query, String trackId, int delta, long start, long end) throws Exception;
    public JSONArray getAssemblyOverviewGraph(int id, String trackId, long start, long end) throws IOException;


}
