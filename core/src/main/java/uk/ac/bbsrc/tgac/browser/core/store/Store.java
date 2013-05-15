package uk.ac.bbsrc.tgac.browser.core.store;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.orbutil.fsm.Guard;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.set.SynchronizedSortedSet;
import uk.ac.bbsrc.tgac.browser.core.store.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 15-Sep-2011
 * Time: 11:03:11
 * To change this template use File | Settings | File Templates.
 */
public interface Store<T> {
  public T getSeqBySeqRegionId(int query) throws IOException;
  public Integer getSeqRegion(T query) throws IOException;
  public T getSeqLengthbyId(int query) throws IOException;
  public T getSeqRegionName(int query) throws IOException;
  public T getLogicNameByAnalysisId(int query) throws IOException;
  public T getDescriptionByAnalysisId(int query) throws IOException;
  public Map<String, Object> getStartEndAnalysisIdBySeqRegionId(int query) throws IOException;
  //  public Map<String,Object> getHit(int query) throws IOException;
  public JSONArray getGenesSearch(T query) throws IOException;
  public JSONArray getTranscriptSearch(T query) throws IOException;
  public JSONArray getGOSearch(T query) throws IOException;
  public List<Map<String, Object>> getHit(int query, String trackId, long start, long end) throws IOException;
  public JSONArray processHit(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException;
  public JSONArray getHitGraph(int id, String trackId, long start, long end) throws IOException;
  public JSONArray getAnnotationId(int query) throws IOException;
  public JSONArray getAnnotationIdList(int query) throws IOException;
  public String getTrackDesc(String query) throws IOException;
  public List<Map> getTrackInfo() throws IOException;
  public List<Map<String, Object>> getGenes(int query, String trackId) throws IOException;
  public JSONArray processGenes(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException;
  public JSONArray getGeneGraph(int id, String trackId, long start, long end) throws IOException;
  public JSONArray getTableswithanalysis_id() throws IOException;
  public JSONArray getdbinfo() throws IOException;
  public T getDomains(T geneid) throws IOException;
  public T getSeq(String query, int from, int to) throws IOException;
  public JSONArray getAssembly(int query, String trackId, int delta) throws IOException;
  public JSONArray getSeqRegionSearch(T query) throws IOException;
  public JSONArray getSeqRegionSearchMap(T query) throws IOException;

  public JSONArray getSeqRegionIdSearch(T query) throws IOException;
  public int getSeqRegionearchsize(T query) throws IOException;
  public Integer getSeqRegionforone(String searchQuery) throws IOException;
  public Integer getSeqRegionCoordId(String query) throws IOException;
  public String getGeneNamefromId(int geneID) throws IOException;
  public String getTranscriptNamefromId(int transcriptID) throws IOException;
  public String getTrackIDfromName(String trackName) throws IOException;
  public String getHitNamefromId(int hitID) throws IOException;
  public int countHit(int id, String trackId, long start, long end);
  public int countGene(int id, String trackId, long start, long end);

  public int countRepeat(int id, String trackId, long start, long end);
  public List<Map<String, Object>> getRepeat(int query, String trackId, long start, long end) throws IOException;
  public JSONArray processRepeat(List<Map<String, Object>> maps, long start, long end, int delta, int id, String trackId) throws IOException;
  public JSONArray getRepeatGraph(int id, String trackId, long start, long end) throws IOException;
  public JSONArray getMarker() throws IOException;
  public boolean checkChromosome() throws Exception;
  public String getCoordSys(String query) throws Exception;
}
