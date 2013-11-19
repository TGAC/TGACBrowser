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


import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.browser.core.store.GeneStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 18-Nov-2013
 * Time: 20:40:20
 * To change this template use File | Settings | File Templates.
 */

public class SQLGeneDAO implements GeneStore {
    protected static final Logger log = LoggerFactory.getLogger(SQLGeneDAO.class);

    @Autowired
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public JdbcTemplate getJdbcTemplate() {
        return template;
    }


    public static final String GET_length_from_seqreg_id = "SELECT length FROM seq_region where seq_region_id =?";
    public static final String GET_Gene_SIZE_SLICE = "SELECT COUNT(gene_id) FROM gene where seq_region_id =? and analysis_id = ? and seq_region_start >= ? and seq_region_start <= ?";
    public static final String GET_CDS_start_per_Gene = "SELECT seq_start FROM translation where transcript_id =?";
    public static final String GET_CDS_end_per_Gene = "SELECT seq_end FROM translation where transcript_id =?";
    public static final String GET_GENE_SIZE = "SELECT COUNT(gene_id) FROM gene where seq_region_id =? and analysis_id = ?";
    public static final String GET_Gene_name_from_ID = "SELECT description FROM gene where gene_id =?";
    public static final String GET_Transcript_name_from_ID = "SELECT description FROM transcript where transcript_id =?";
    public static final String GET_GO_for_Genes = "select value from gene_attrib where gene_id = ?";
    public static final String GET_GO_for_Transcripts = "select value from transcript_attrib where transcript_id =  ?";

    public static final String GET_Assembly_for_reference = "SELECT * FROM assembly where asm_seq_region_id =?";
    public static final String GET_Gene_by_view = "select g.gene_id, g.seq_region_start as gene_start, g.seq_region_end as gene_end, g.seq_region_strand as gene_strand, g. description as gene_name, t.transcript_id, t.seq_region_start as transcript_start, t.seq_region_end as transcript_end, t.description as transcript_name, e.exon_id, e.seq_region_start as exon_start, e.seq_region_end as exon_end from gene g, transcript t, exon_transcript et, exon e where t.gene_id = g.gene_id and t.transcript_id = et.transcript_id and et.exon_id = e.exon_id and  g.seq_region_id = ? and g.analysis_id = ?;";//"select * from gene_view where seq_region_id = ? and analysis_id = ?;";//
    private JdbcTemplate template;

    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }


    /**
     * gets gene name by id
     *
     * @param geneID gene_id
     * @return Gene name as String format
     * @throws IOException
     */
    public String getGeneNamefromId(int geneID) throws IOException {
        try {
            String str = template.queryForObject(GET_Gene_name_from_ID, new Object[]{geneID}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getGeneNamefromId no result found" + e.getMessage());
        }
    }

    /**
     * get transcript name by id
     *
     * @param transcriptID transcript_id
     * @return transcript name as String
     * @throws IOException
     */
    public String getTranscriptNamefromId(int transcriptID) throws IOException {
        try {
            String str = template.queryForObject(GET_Transcript_name_from_ID, new Object[]{transcriptID}, String.class);
            return str;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException(" getTranscriptNamefromId no result found" + e.getMessage());
        }
    }

    /**
     * Generates gene Graph information
     * Calls Gene Graph Level when reaches to the bottom level or recursively call method itself
     *
     * @param start_pos to be added because of lower assembly level
     * @param id        reference id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with gene graph information
     * @throws IOException
     */
    public JSONArray recursiveGeneGraph(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (track_start < 0) {
                        track_start = 0;
                    }
                    if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                        track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                    }
                    int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    if (no_of_tracks > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                            track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                        }
                        assemblyTracks.addAll(getGeneGraphLevel(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    } else {
                        track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        if (track_start < 0) {
                            track_start = 0;
                        }
                        if (track_end > Integer.parseInt(maps_one.get(j).get("asm_end").toString())) {
                            track_end = Integer.parseInt(maps_one.get(j).get("asm_end").toString());
                        }
                        List<Integer> ends = new ArrayList<Integer>();
                        start_pos += Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveGeneGraph(Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end));
                    }
                }
            }
            return assemblyTracks;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getHit no result found");
        }
    }

    /**
     * Gets Graphical information for gene level graphs
     *
     * @param start_pos to be added because of lower assembly level
     * @param id        reference id
     * @param trackId
     * @param start
     * @param end
     * @return JSONArray with gene graph information
     * @throws IOException
     */
    public JSONArray getGeneGraphLevel(int start_pos, int id, String trackId, long start, long end) throws IOException {
        try {
            JSONArray trackList = new JSONArray();
            long from = start;
            long to = 0;
            int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", start_pos + from);
                    eachTrack.put("end", start_pos + to);
                    eachTrack.put("graph", no_of_tracks);
                    eachTrack.put("id", id);

                    trackList.add(eachTrack);
                    from = to;
                }
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getHit no result found");
        }
    }

    /**
     * Get graphical information for gene or call method recursive for lower level
     *
     * @param id      reference id
     * @param trackId analysis id
     * @param start
     * @param end
     * @return JSONArray with assembly graph information
     * @throws IOException
     */
    public JSONArray getGeneGraph(int id, String trackId, long start, long end) throws IOException {
        try {

            JSONArray trackList = new JSONArray();
            long from = start;
            long to;
            int no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, end}, Integer.class);

            if (no_of_tracks > 0) {
                for (int i = 1; i <= 200; i++) {
                    JSONObject eachTrack = new JSONObject();
                    to = start + (i * (end - start) / 200);
                    no_of_tracks = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, from, to}, Integer.class);
                    eachTrack.put("start", from);
                    eachTrack.put("end", to);
                    eachTrack.put("graph", no_of_tracks);
                    trackList.add(eachTrack);
                    from = to;
                }
            } else {
                trackList.addAll(recursiveGeneGraph(0, id, trackId, start, end));
            }
            return trackList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getHit no result found");
        }
    }

    /**
     * counts gene or call recursive method
     *
     * @param id      Reference Id
     * @param trackId
     * @param start
     * @param end
     * @return number of gene per region
     * @throws Exception
     */
    public int countRecursiveGene(int id, String trackId, long start, long end) throws Exception {
        try {
            int gene_size = 0;
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});

            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                    if (template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class) > 0) {
                        gene_size += template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId, track_start, track_end}, Integer.class);
                    } else {
                        gene_size += countRecursiveGene(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end);
                    }
                }
            }

            return gene_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("count recursive gene " + e.getMessage());
        }
    }

    /**
     * count no of genes present in the region
     *
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public int countGene(int id, String trackId, long start, long end) throws Exception {
        try {
            int gene_size = template.queryForObject(GET_Gene_SIZE_SLICE, new Object[]{id, trackId, start, end}, Integer.class);
            if (gene_size == 0) {
                gene_size = countRecursiveGene(id, trackId, start, end);
            }
            return gene_size;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Count Gene " + e.getMessage());
        }
    }


    /**
     * Gets gene information
     *
     * @param id
     * @param trackId
     * @return
     * @throws IOException
     */
    @Cacheable(cacheName = "geneCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator",
                    properties = {
                            @Property(name = "includeMethod", value = "false"),
                            @Property(name = "includeParameterTypes", value = "false")
                    }
            )
    )

    public List<Map<String, Object>> getGenes(int id, String trackId) throws IOException {
        return template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }

//  @Cacheable(cacheName = "transcriptGoCache",
//                  keyGenerator = @KeyGenerator(
//                          name = "HashCodeCacheKeyGenerator",
//                          properties = {
//                                  @Property(name = "includeMethod", value = "false"),
//                                  @Property(name = "includeParameterTypes", value = "false")
//                          }
//                  )
//       )

    /**
     * Gets transcript information
     *
     * @param transcriptId
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> getTranscriptsGO(String transcriptId) throws IOException {

        return template.queryForList(GET_GO_for_Transcripts, new Object[]{transcriptId});//template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }


//  @Cacheable(cacheName = "geneGoCache",
//                 keyGenerator = @KeyGenerator(
//                         name = "HashCodeCacheKeyGenerator",
//                         properties = {
//                                 @Property(name = "includeMethod", value = "false"),
//                                 @Property(name = "includeParameterTypes", value = "false")
//                         }
//                 )
//      )

    /**
     * gets gene attrib information by gene id
     *
     * @param geneId
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> getGenesAttribs(String geneId) throws IOException {
        return template.queryForList(GET_GO_for_Genes, new Object[]{geneId});//template.queryForList(GET_Gene_by_view, new Object[]{id, trackId});
    }


    /**
     * Gets Gene information
     * Calls Gene Level when reaches to the bottom level or recursively call method itself
     *
     * @param start_pos
     * @param id
     * @param trackId
     * @param start
     * @param end
     * @param delta
     * @return JSONArray with Gene information
     * @throws IOException
     */
    public JSONArray recursiveGene(int start_pos, int id, String trackId, long start, long end, int delta) throws IOException {
        try {
            JSONArray assemblyTracks = new JSONArray();
            List<Map<String, Object>> maps_one = template.queryForList(GET_Assembly_for_reference, new Object[]{id});
            if (maps_one.size() > 0) {
                for (int j = 0; j < maps_one.size(); j++) {
                    List<Map<String, Object>> maps_two = template.queryForList(GET_GENE_SIZE, new Object[]{maps_one.get(j).get("cmp_seq_region_id"), trackId});
                    if (maps_two.size() > 0) {
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(getGeneLevel(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), getGenes(Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId), start, end, delta));
                    } else {
                        long track_start = start - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        long track_end = end - Integer.parseInt(maps_one.get(j).get("asm_start").toString());
                        List<Integer> ends = new ArrayList<Integer>();
                        ends.add(0, 0);
                        assemblyTracks.addAll(recursiveGene(start_pos + Integer.parseInt(maps_one.get(j).get("asm_start").toString()), Integer.parseInt(maps_one.get(j).get("cmp_seq_region_id").toString()), trackId, track_start, track_end, delta));
                    }
                }
            }

            return assemblyTracks;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getGene no result found " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("getGene exception " + e.getMessage());
        }
    }

    /**
     * Get gene Level tracks
     *
     * @param start_add
     * @param genes
     * @param start
     * @param end
     * @param delta
     * @return JSONAray with Assembly information
     * @throws Exception
     */
    public JSONArray getGeneLevel(int start_add, List<Map<String, Object>> genes, long start, long end, int delta) throws Exception {
        try {
            JSONArray GeneList = new JSONArray();
            JSONObject eachGene = new JSONObject();
            JSONObject eachTrack = new JSONObject();
            JSONArray exonList = new JSONArray();
            JSONArray transcriptList = new JSONArray();
            JSONArray filteredgenes = new JSONArray();

            String gene_id = "";
            String transcript_id = "";

            int layer = 1;
            int lastsize = 0;
            int thissize = 0;

            List<Integer> ends = new ArrayList<Integer>();
            List<Integer> ends_gene = new ArrayList<Integer>();

            ends.add(0, 0);
            ends_gene.add(0, 0);

            List<Map<String, Object>> domains, translation_start, translation_end;

            for (Map gene : genes) {
                int start_pos = start_add + Integer.parseInt(gene.get("gene_start").toString());
                int end_pos = start_add + Integer.parseInt(gene.get("gene_end").toString());
                if (start_pos >= start && end_pos <= end || start_pos <= start && end_pos >= end || end_pos >= start && end_pos <= end || start_pos >= start && start_pos <= end) {
                    filteredgenes.add(filteredgenes.size(), gene);
                }
            }

            for (int i = 0; i < filteredgenes.size(); i++) {

                if (!transcript_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("transcript_id").toString())) {
                    if (!transcript_id.equalsIgnoreCase("")) {
                        eachTrack.put("Exons", exonList);
                        transcriptList.add(eachTrack);
                    }
                    transcript_id = filteredgenes.getJSONObject(i).get("transcript_id").toString();
                    exonList = new JSONArray();

                    eachTrack = new JSONObject();

                    eachTrack.put("id", filteredgenes.getJSONObject(i).get("transcript_id"));
                    eachTrack.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_start").toString()));
                    eachTrack.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_end").toString()));

                    translation_start = template.queryForList(GET_CDS_start_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                    translation_end = template.queryForList(GET_CDS_end_per_Gene, new Object[]{filteredgenes.getJSONObject(i).get("transcript_id").toString()});
                    for (Map start_seq : translation_start) {
                        eachTrack.put("transcript_start", start_add + Integer.parseInt(start_seq.get("seq_start").toString()));

                    }

                    for (Map end_seq : translation_end) {
                        eachTrack.put("transcript_end", start_add + Integer.parseInt(end_seq.get("seq_end").toString()));

                    }
                    eachTrack.put("desc", filteredgenes.getJSONObject(i).get("transcript_name"));
                    int start_pos = Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_start").toString());
                    int end_pos = Integer.parseInt(filteredgenes.getJSONObject(i).get("transcript_end").toString());
                    if (start_pos > end_pos) {
                        int temp = end_pos;
                        end_pos = start_pos;
                        start_pos = temp;
                    }

                    for (int a = 0; a < ends.size(); a++) {

                        if (start_pos - ends.get(a) > delta) {
                            ends.remove(a);
                            ends.add(a, end_pos);
                            eachTrack.put("layer", a + 1);
                            break;
                        } else if ((start_pos - ends.get(a) <= delta && (a + 1) == ends.size()) || start_pos == ends.get(a)) {

                            if (a == 0) {
                                eachTrack.put("layer", ends.size());
                                ends.add(a, end_pos);
                            } else {
                                ends.add(ends.size(), end_pos);
                                eachTrack.put("layer", ends.size());
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                    eachTrack.put("domain", 0);
                    domains = getTranscriptsGO(filteredgenes.getJSONObject(i).get("transcript_id").toString());
                    for (Map domain : domains) {
                        eachTrack.put("domain", domain.get("value"));
                    }
                    eachTrack.put("flag", false);
                }
                if (!gene_id.equalsIgnoreCase(filteredgenes.getJSONObject(i).get("gene_id").toString())) {
                    if (!gene_id.equalsIgnoreCase("")) {
                        eachGene.put("transcript", transcriptList);
                        GeneList.add(eachGene);
                    }
                    gene_id = filteredgenes.getJSONObject(i).get("gene_id").toString();
                    transcriptList = new JSONArray();
                    eachGene.put("id", filteredgenes.getJSONObject(i).get("gene_id"));
                    eachGene.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_start").toString()));
                    eachGene.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_end").toString()));
                    eachGene.put("desc", filteredgenes.getJSONObject(i).get("gene_name"));
                    eachGene.put("strand", filteredgenes.getJSONObject(i).get("gene_strand"));

                    int start_pos = Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_start").toString());
                    int end_pos = Integer.parseInt(filteredgenes.getJSONObject(i).get("gene_end").toString());
                    if (start_pos > end_pos) {
                        int temp = end_pos;
                        end_pos = start_pos;
                        start_pos = temp;
                    }

                    for (int a = 0; a < ends_gene.size(); a++) {

                        if (start_pos - ends_gene.get(a) > delta) {
                            ends_gene.remove(a);
                            ends_gene.add(a, end_pos);
                            eachGene.put("layer", a + 1);
                            break;
                        } else if ((start_pos - ends_gene.get(a) <= delta && (a + 1) == ends_gene.size()) || start_pos == ends_gene.get(a)) {

                            if (a == 0) {
                                eachGene.put("layer", ends_gene.size());
                                ends_gene.add(a, end_pos);
                            } else {
                                ends_gene.add(ends_gene.size(), end_pos);
                                eachGene.put("layer", ends_gene.size());
                            }
                            break;
                        } else {
                            continue;
                        }
                    }

                    eachGene.put("domain", 0);
                    domains = getGenesAttribs(filteredgenes.getJSONObject(i).get("gene_id").toString());
                    for (Map domain : domains) {
                        eachGene.put("domain", domain.get("value"));
                    }
                    if (lastsize < 2 && layer > 2) {
                        layer = 1;
                    } else {
                        layer = layer;
                    }

                    if (thissize > 1) {
                        layer = 1;
                    }
                    layer++;
                }

                JSONObject eachExon = new JSONObject();
                eachExon.put("id", filteredgenes.getJSONObject(i).get("exon_id"));
                eachExon.put("start", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("exon_start").toString()));
                eachExon.put("end", start_add + Integer.parseInt(filteredgenes.getJSONObject(i).get("exon_end").toString()));
                exonList.add(eachExon);

                lastsize = thissize;
            }

            if (filteredgenes.size() > 0) {
                eachTrack.put("Exons", exonList);
                transcriptList.add(eachTrack);
                eachGene.put("transcript", transcriptList);
                GeneList.add(eachGene);
            }
            return GeneList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("getGeneLevel " + e.getMessage());
        }
    }

    /**
     * process Genes returns from getGenes
     *
     * @param genes   genes returned from getGene
     * @param start
     * @param end
     * @param delta
     * @param id
     * @param trackId
     * @return JSONArray with gene information
     * @throws Exception
     */
    public JSONArray processGenes(List<Map<String, Object>> genes, long start, long end, int delta, int id, String trackId) throws Exception {

        try {
            JSONArray GeneList = new JSONArray();
            List<Integer> ends = new ArrayList<Integer>();
            ends.add(0, 0);
            if (template.queryForObject(GET_GENE_SIZE, new Object[]{id, trackId}, Integer.class) > 0) {
                if (genes.size() > 0) {
                    ends.add(0, 0);
                    GeneList = getGeneLevel(0, genes, start, end, delta);
                }
            } else {
                GeneList = recursiveGene(0, id, trackId, start, end, delta);
            }
            if (GeneList.size() == 0) {
                int length = template.queryForObject(GET_length_from_seqreg_id, new Object[]{id}, Integer.class);
                int countgene = countGene(id, trackId, 0, length);
                if (countgene < 1) {
                    GeneList.add("getGene no result found");
                }
            }

            return GeneList;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new IOException("getGene no result found.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("processGene no result found " + e.getMessage());
        }
    }
}