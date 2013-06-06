package uk.ac.bbsrc.tgac.browser.web;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import uk.ac.bbsrc.tgac.browser.core.store.SequenceStore;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Date;
import java.lang.management.*;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 1/8/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class BrowserAppListener implements ServletContextListener {
  protected static final Logger log = LoggerFactory.getLogger(BrowserAppListener.class);

  /**
   * Called on webapp context init
   *
   * @param event of type ServletContextEvent
   */
  public void contextInitialized(ServletContextEvent event) {
    ServletContext application = event.getServletContext();

    XmlWebApplicationContext context = (XmlWebApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    //resolve property file configuration placeholders
    //MisoPropertyExporter exporter = (MisoPropertyExporter) context.getBean("propertyConfigurer");
    //Map<String, String> misoProperties = exporter.getResolvedProperties();
    SequenceStore ss = (SequenceStore) context.getBean("sequenceStore");

    try {
//      JSONArray reference_list = ss.getSeqRegionIdSearch("");

//      for (int i = 0; i < reference_list.size(); i++) {
//        JSONArray tracks = ss.getAnnotationIdList(Integer.parseInt(reference_list.get(i).toString()));
//        for (int j = 0; j < tracks.size(); j++) {
////          log.info("ADDING: " + reference_list.getInt(i) + " : " + tracks.getString(j) +" \t" );
//////          ss.getHit(reference_list.getInt(i), tracks.getString(j));
//////          System.gc();
//          ss.getGenes(reference_list.getInt(i), tracks.get(j).toString());
//          System.gc();
//        }

//      }

      CacheManager c = (CacheManager) context.getBean("cacheManager");
      Cache a = c.getCache("hitCache");
      log.info("hitCache size: "+a.getKeys().size());
    }
    catch (Exception e) {
      log.info(e.toString());
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

  }

  public void contextDestroyed(ServletContextEvent event) {
    log.info("Browser Application Context Destroyed: " + new Date());
  }
}
