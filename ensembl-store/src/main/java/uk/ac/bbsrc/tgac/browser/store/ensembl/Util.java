package uk.ac.bbsrc.tgac.browser.store.ensembl;

import java.util.List;

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

    /**
     * computer layer of the track to be shown
     * @param ends
     * @param start_pos
     * @param delta
     * @param end_pos
     * @return number of layer as an integer
     */
    public int stackLayerInt(List<Integer> ends, int start_pos, int delta, int end_pos) throws Exception {
        log.info("\n\n\n\nstackleyerInt "+start_pos+" "+delta+" "+end_pos);
        try {
            int position = 0;
            for (int a = 0; a < ends.size(); a++) {
                if (start_pos - ends.get(a) > delta) {
                    ends.set(a, end_pos);
                    position = (a + 1);
                    break;
                } else if ((start_pos - ends.get(a) <= delta && (a + 1) == ends.size()) || start_pos == ends.get(a)) {
                    position = ends.size();
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
        log.info("\n\n\n\nstackleyerList "+start_pos+" "+delta+" "+end_pos);
        try {
            for (int a = 0; a < ends.size(); a++) {
                if (start_pos - ends.get(a) > delta) {
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
}
