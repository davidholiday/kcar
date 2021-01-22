#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.Properties;
import java.util.Map;

import java.beans.Introspector;


/**
 * fires up camel in a servlet
 */
public class ${artifactIdCamelCase} implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(${artifactIdCamelCase}.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY =
        Introspector.decapitalize(${artifactIdCamelCase}.class.getSimpleName());

    public static final String ROUTES_PROPERTY_KEY = "routes";

    public static final String PROPERTY_OVERRIDES_KEY = "propertyOverrides";


    @Override
    public void contextInitialized(ServletContextEvent sce) {

    try {
        // create camel context, load default properties, and inject routes
        LOG.info("ServletContextListener started! Firing up Camel... ");
        CamelContext camelContext = new DefaultCamelContext();
        CamelPropertyHelper.loadPropertiesAndInjectRoutes(camelContext, NAMESPACE_KEY, ROUTES_PROPERTY_KEY);

        // load override properties. this is the mechanism by which an element or routes default properties
        // are not desirable for this runtime context. the developer will set override properties at the service
        // level that will change the behavior or element.
        String propertyOverrideKey = CamelPropertyHelper.getPropertyKey(NAMESPACE_KEY, PROPERTY_OVERRIDES_KEY);
        Map<String, String> propertyOverrideMap =
                CamelPropertyHelper.resolvePropertyMapOrElseEmpty(camelContext, propertyOverrideKey);

        for (String k : propertyOverrideMap.keySet()) {
            String v = propertyOverrideMap.get(k);
            LOG.info("setting override property {} -> {}", k, v);

            Properties overrideProperty = new Properties();
            overrideProperty.setProperty(k, v);
            camelContext.getPropertiesComponent().setOverrideProperties(overrideProperty);
        }

        // https://www.youtube.com/watch?v=TCqH26PzUvA
        camelContext.start();
        LOG.info("*!* Camel is up! *!*");
        } catch (Exception e) {
            LOG.error("something went wrong during context initialization!", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("ServletContextListener destroyed... ");
    }

}