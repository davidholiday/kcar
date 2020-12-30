package io.holitek.kcar.chassis.model_k;


import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * fires up camel in a servlet
 */
public class App implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final String PROPERTIES_NAMESPACE_KEY = "modelK";
    public static final String ROUTES_PROPERTY_KEY = "routes";


    // TODO move this into it's own helper
    private final List<String> propertyFileLocationsList = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started! Firing up Camel... ");

            //
            CamelContext camelContext = new DefaultCamelContext();
            propertyFileLocationsList.add("classpath:modelK.application.properties");
            String propertyFileLocations = new String();
            for (String propertyFileLocation : propertyFileLocationsList) {
                propertyFileLocations = propertyFileLocations + "," + propertyFileLocation;
            }

            camelContext.getPropertiesComponent().setLocation(propertyFileLocations);

            // TODO this feels as clunky as it did in the routes file. seriously consider making some kind of
            // TODO     common properties resolver...
            String routeNames =
                    camelContext.getPropertiesComponent()
                                .resolveProperty(PROPERTIES_NAMESPACE_KEY + "." + ROUTES_PROPERTY_KEY)
                                .orElse("");

            List<String> routeNamesList = Arrays.asList(routeNames.split(","));
            for (String routeName : routeNamesList) {
                LOG.info("found route class name {} in properties file. attempting to register...", routeName);
                Class<?> clazz = Class.forName(routeName);
                LOG.info(clazz.getSimpleName());
                String propertiesNamespace = Introspector.decapitalize(clazz.getSimpleName());
                Constructor<?> clazzConstructor = clazz.getConstructor();
                RoutesBuilder route = (RoutesBuilder) clazzConstructor.newInstance();

                // TODO fix this hack by creating a common interface for routes that ensure all routesbuilder implementors have a handle that
                // TODO     retrieves the namespace for the route
                propertyFileLocationsList.add("classpath:" + propertiesNamespace + ".application.properties");
                propertyFileLocations = new String();
                for (String propertyFileLocation : propertyFileLocationsList) {
                    propertyFileLocations = propertyFileLocations + "," + propertyFileLocation;
                }

                camelContext.getPropertiesComponent().setLocation(propertyFileLocations);

                camelContext.addRoutes(route);
            }

            /*

            TODO
            services that implement a given chassis need to add an additional property in the chassis namespace
            that the chassis code knows to look for to load additional routes.

            A service doesn't have code. it's a bare bones project that has the chassis and route(s) as dependencies along with a docker file
            (heroku) that ensures prod-ready build (meaning there's probably a hook into apache http as well or nginx or whatever)


             */


            //
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