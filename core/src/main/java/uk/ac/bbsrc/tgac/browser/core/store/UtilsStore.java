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

package uk.ac.bbsrc.tgac.browser.core.store;

import net.sf.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 05/12/2013
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public interface UtilsStore extends Store<String>{
    public int stackLayerInt(List<Integer> ends, int start_pos, int delta, int end_pos) throws Exception;
    public List<Integer> stackLayerList(List<Integer> ends, int start_pos, int delta, int end_pos) throws Exception;
}
