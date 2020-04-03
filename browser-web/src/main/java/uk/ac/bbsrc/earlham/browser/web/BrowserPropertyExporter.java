package uk.ac.bbsrc.earlham.browser.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 06/11/14
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class BrowserPropertyExporter extends PropertyPlaceholderConfigurer {


    /**
     * A handy class that exposes property placeholders discovered at webapp init time by a PropertyPlaceholderConfigurer.
     * <p/>
     * This class accepts a list of available property files referenced in the Spring configs via a property list, or alternatively
     * you can supply a single browser browser.properties file that contains the base browser storage directory (where other property
     * files are housed). These additional properties files will be discovered at runtime, imported into the base properties,
     * and used by Spring to do its magic.
     * <p/>
     * As an aside, usually these properties are not available to beans, but this class exposes them via getResolvedProperties()
     *
     * @author Rob Davey
     * @date 01-Sep-2010
     * @since 0.0.2
     */
        private Map<String, String> resolvedProperties;

        @Override
        protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                         Properties browserProps) throws BeansException {



                super.processProperties(beanFactoryToProcess, browserProps);
                resolvedProperties = new HashMap<String, String>();
                for (Object key : browserProps.keySet()) {
                    String keyStr = key.toString();

                    //doesn't seem to resolve properties properly - just end up null
                    //resolvedProperties.put(keyStr, resolvePlaceholder(props.getProperty(keyStr), props, SYSTEM_PROPERTIES_MODE_OVERRIDE));
                    resolvedProperties.put(keyStr, browserProps.getProperty(keyStr));
                }
        }

        public Map<String, String> getResolvedProperties() {
            return Collections.unmodifiableMap(resolvedProperties);
        }

        public Properties getPropertiesAsProperties() {
            Properties props = new Properties();
            props.putAll(Collections.unmodifiableMap(resolvedProperties));
            return props;
        }

        protected class PropertiesFilenameFilter implements FilenameFilter {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".properties");
            }
        }
    }
