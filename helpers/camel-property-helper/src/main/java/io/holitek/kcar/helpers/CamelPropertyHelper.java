package io.holitek.kcar.helpers;


import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * helpers that make it easier to access properties through the camel context
 */
public class CamelPropertyHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CamelPropertyHelper.class);


    /**
     * takes [n] String arguments and returns a Camel property placeholder. passing strings "property", "key", "foo"
     * will return "{{property.key.foo}}"
     *
     * @param propertyKey
     * @return
     */
    public static String getPropertyPlaceholder(String... propertyKey) {
        String propertyPlaceholder = "{{";
        boolean first = true;
        for (String subkey : propertyKey) {
            if (first) {
                propertyPlaceholder += subkey;
                first = false;
            } else {
                propertyPlaceholder += "." + subkey;
            }
        }
        propertyPlaceholder += "}}";
        return propertyPlaceholder;
    }

    /**
     * takes [n] String arguments and returns a Camel property key. passing strings "property", "key", "foo" will
     * return "property.key.foo"
     *
     * @param propertyKey
     * @return
     */
    public static String getPropertyKey(String... propertyKey) {
        String propertyPlaceholder = "";
        boolean first = true;
        for (String subkey : propertyKey) {
            if (first) {
                propertyPlaceholder += subkey;
                first = false;
            } else {
                propertyPlaceholder += "." + subkey;
            }
        }
        return propertyPlaceholder;
    }


    /**
     * helper to take some of the excess typing out of a resolving a property from the guts of the camel context
     *
     * @param camelContext
     * @param propertyKey
     * @return
     */
    public static String resolvePropertyOrElseEmpty(CamelContext camelContext, String propertyKey) {
        return camelContext.getPropertiesComponent()
                           .resolveProperty(propertyKey)
                           .orElse("");
    }


    /**
     * searches the classpath for a properties file named {NAMESPACE.application.properties} and, if found, loads it
     * into the camel context. returns a list with filename in aforementioned format.
     *
     * @apiNote the reason for the return List is, as of this writing, the camel context appears only able to load
     * properties files via a single call to a method that tells camel where to look properties. as such the way to
     * load more than one properties file into the camel context is to pass a comma-delimited list of them to that
     * method call. moreover, because the method call sets a path string, each subsequent call must include all the
     * previous property file locations if they are to be included.
     *
     * @param camelContext
     * @param namespace
     * @return
     */
    public static List<String> loadPropertyFileForNamespace(CamelContext camelContext, String namespace) {
        String propertyFileLocation = "classpath:" + namespace + ".application.properties";
        camelContext.getPropertiesComponent().setLocation(propertyFileLocation);
        List<String> namespaces = new ArrayList<>();
        namespaces.add(namespace);
        return namespaces;
    }


    /**
     * takes a set of namespaces and loads every properties file in the class path with that namespace key into
     * the camel context properties component.
     *
     * @param camelContext
     * @param namespaces
     * @return
     */
    public static void loadPropertyFilesForNamespaces(CamelContext camelContext, List<String> namespaces) {
        String propertyFileLocations = new String();
        for (String namespace : namespaces) {
            String propertyFileLocation = "classpath:" + namespace + ".application.properties";
            propertyFileLocations = propertyFileLocations + "," + propertyFileLocation;

        }
        camelContext.getPropertiesComponent().setLocation(propertyFileLocations);
    }


    /**
     * will load the properties application.properties file associated with the provided namespace, as well as all
     * routes and all properties files associated with those routes, into the camel context.
     *
     * @param camelContext
     * @param namespace
     * @param routesPropertySubKey
     * @return
     */
    public static void loadPropertiesAndInjectRoutes(
            CamelContext camelContext, String namespace, String routesPropertySubKey) {

        // first load the property file for the namespace we're in
        List<String> propertyNamespaces = loadPropertyFileForNamespace(camelContext, namespace);

        // now parse what *should* be a csv into a list of route classnames
        String routesPropertyKey = getPropertyKey(namespace, routesPropertySubKey);
        String routeNames = resolvePropertyOrElseEmpty(camelContext, routesPropertyKey);
        List<String> routeNamesList = Arrays.asList(routeNames.split(","));

        // here goes nothing...
        // https://youtu.be/gRvu0yHoHy8?t=49
        for (String routeName : routeNamesList) {
            LOG.info("found route class name {} in properties file. attempting to register...", routeName);

            try {
                // if the route name isn't a class name representing something in the class path this is where things
                // will go boom.
                Class<?> clazz = Class.forName(routeName);

                // grab namespace value for the route. *!* remember *!* by convention, everything in kcar-land uses
                // their classname (as camelCase) for their namespace.
                // for example, "io.holitek.kcar.routes.HealthCheckRoute" has a classname of "HealthCheckRoute". Its
                // namespace is therefore "healthCheckRoute".
                String propertiesNamespace = Introspector.decapitalize(clazz.getSimpleName());
                propertyNamespaces.add(propertiesNamespace);

                // look for and, if found, load the properties file associated with the route's namespace into the camel
                // context. *!* by convention, the classpath will be searched for a file named
                // {NAMESPACE}.application.properties
                loadPropertyFilesForNamespaces(camelContext, propertyNamespaces);

                // now that we have loaded the route's properties we can create and inject the route object itself
                Constructor<?> clazzConstructor = clazz.getConstructor();
                RoutesBuilder route = (RoutesBuilder) clazzConstructor.newInstance();
                camelContext.addRoutes(route); // this guy forces us to catch the generic Exception
                LOG.info("route {} registered to camel context!", propertiesNamespace);
            } catch (Exception e) {
                LOG.error("something went wrong auto-injecting routes and route properties.", e);
            }

        }

    }


}









