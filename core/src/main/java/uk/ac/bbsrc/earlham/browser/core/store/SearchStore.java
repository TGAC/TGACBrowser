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
 * Time: 11:03:58
 * To change this template use File | Settings | File Templates.
 */
public interface SearchStore extends Store {
    public boolean checkChromosome() throws Exception;
    public boolean checkChromosome(int id) throws Exception;
    public JSONArray getGenesSearch(String query) throws IOException;
    public JSONArray getMarkerSearch(String query) throws IOException;
    public JSONArray getTranscriptSearch(String query) throws IOException;
    public JSONArray getGOSearch(String query) throws IOException;
    public JSONArray getSeqRegionSearch(String query) throws IOException;
    public JSONArray getSeqRegionSearchformatch(String query) throws IOException;



}
