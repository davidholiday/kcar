package io.holitek.kcar.helpers;


import org.apache.camel.RuntimeCamelException;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.holitek.kcar.helpers.CamelPropertyHelper.resolvePropertyMapOrElseEmpty;


/**
 *
 */
public class CamelPropertyHelperTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelPropertyHelper.class);

    //
    // test setup and configuration

    /**
     * tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
     * doesn't start before we've set up the camel registry and routes.
     *
     * @return
     */
    @Override
    public boolean isUseAdviceWith() { return true; }

    @BeforeEach
    void beforeEach() {
        String location = "classpath:" + CamelPropertyHelper.NAMESPACE_KEY + ".application.test.properties";
        context.getPropertiesComponent().setLocation(location);
        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }


    //
    // tests

    //
    @Test
    @DisplayName("should wrap the provided string in a double set of curly braces")
    public void testGetPropertyPlaceholderSingleKey() {
        String propertyKey = "foo";
        String expectedResult = "{{" + propertyKey + "}}";
        String actualResult = CamelPropertyHelper.getPropertyPlaceholder(propertyKey);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("should concatenate the provided strings with periods and wrap the result in a double set of " +
                 "curly braces")
    public void testGetPropertyPlaceholderMultiKey() {
        String expectedResult = "{{foo.bar.baz}}";
        String actualResult = CamelPropertyHelper.getPropertyPlaceholder("foo", "bar", "baz");
        Assertions.assertEquals(expectedResult, actualResult);
    }


    //
    @Test
    @DisplayName("should return the single string intact")
    public void testGetPropertySingleKey() {
        String expectedResult = "foo";
        String actualResult = CamelPropertyHelper.getPropertyKey(expectedResult);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("should concatenate the provided strings with periods")
    public void testGetPropertyMultiKey() {
        String expectedResult = "foo.bar.baz";
        String actualResult = CamelPropertyHelper.getPropertyKey(expectedResult);
        Assertions.assertEquals(expectedResult, actualResult);
    }


    //
    @Test
    @DisplayName("should return a map containing the kv pairs associated with the property key")
    public void testResolvePropertyMapOrElseEmptyValuePresent() {
        Map<String, String> kvPropertiesMap =
                CamelPropertyHelper.resolvePropertyMapOrElseEmpty(
                    context(),
                    "camelPropertyHelper.kvProperties"
                );

        Map<String, String> expectedMap = Map.of("foo", "bar", "moo", "goo");
        Assertions.assertEquals(expectedMap, kvPropertiesMap);
    }

    @Test
    @DisplayName("should return an empty string if property not present")
    public void testResolvePropertyMapOrElseEmptyNoValuePresent() {
        Map<String, String> kvPropertiesMap =
                CamelPropertyHelper.resolvePropertyMapOrElseEmpty(
                        context(),
                        "camelPropertyHelper.wokkawokka"
                );

        // map should be empty for non-existent property key
        Map<String, String> expectedMap = Map.of();
        Assertions.assertEquals(expectedMap, kvPropertiesMap);
    }

    @Test
    @DisplayName("should return an empty string if property not present")
    public void testResolvePropertyMapOrElseEmptySpacesStripped() {
        Map<String, String> kvPropertiesMap =
                CamelPropertyHelper.resolvePropertyMapOrElseEmpty(
                        context(),
                        "camelPropertyHelper.kvPropertiesWithSpace"
                );

        // map should be empty for non-existent property key
        Map<String, String> expectedMap = Map.of("moo", "goo");
        Assertions.assertEquals(expectedMap, kvPropertiesMap);
    }


    //
    @Test
    @DisplayName("tests that test property gets loaded")
    public void testLoadPropertyFileForNamespace() {

        // load mock {NAMESPACE}.application.properties file from ./src/test/resources
        List<String> namespaces =
                CamelPropertyHelper.loadPropertyFileForNamespace(context, CamelPropertyHelper.NAMESPACE_KEY);

        // only one thing should come back
        int expectedListSize = 1;
        int actualListSize = namespaces.size();
        Assertions.assertEquals(expectedListSize, actualListSize);

        // ensure what came back is what we expected
        String expectedNamespacesElement = CamelPropertyHelper.NAMESPACE_KEY;
        String actualNamespacesElement = namespaces.get(0);
        Assertions.assertEquals(expectedNamespacesElement, actualNamespacesElement);

        // ensure the property value is accessible from the camel context
        String expectedPropertyValue = "baz";
        String actualPropertyValue = context.getPropertiesComponent().resolveProperty("foo.bar").orElse("");
        Assertions.assertEquals(expectedPropertyValue, actualPropertyValue);
    }

    @Test
    @DisplayName("tests that things break when a property file for a given namespace isn't found")
    public void testLoadNonExistentPropertyFileForNamespace() {
        Assertions.assertThrows(RuntimeCamelException.class, () -> {
            CamelPropertyHelper.loadPropertyFileForNamespace(context, "flip_flarp_gogurt_ghost_dad");
        });
    }


    //
    @Test
    @DisplayName("tests happy path for load test properties")
    public void testLoadTestPropertyFileForNamespace() {

        // clear out the properties file loaded in the '@Before` method
        context.getPropertiesComponent().setLocation("");

        // check that test value has been cleared from classpath
        boolean classpathClearedOfProperties = false;
        try {
            context.getPropertiesComponent().resolveProperty("foo.bar").get();
        } catch (NoSuchElementException e) {
            classpathClearedOfProperties = true;
        }
        Assertions.assertEquals(true, classpathClearedOfProperties);

        // load test properties file and ensure property value can be loaded from camel context
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, CamelPropertyHelper.NAMESPACE_KEY);

        String actualPropertyValue =
                context.getPropertiesComponent()
                       .resolveProperty("camelPropertyHelper.foo")
                       .orElse("");


        String expectedPropertyValue = "bar";
        Assertions.assertEquals(expectedPropertyValue, actualPropertyValue);

    }

    @Test
    @DisplayName("tests that things break when a test property file for a given namespace isn't found.")
    public void testLoadNonExistentTestPropertyFileForNamespace() {
        Assertions.assertThrows(RuntimeCamelException.class, () -> {
            CamelPropertyHelper.loadTestPropertyFileForNamespace(context, "mr_moo_face");
        });
    }


    //
    @Test
    @DisplayName("checks that all namespaced properties files get loaded into the camel context")
    public void testLoadPropertiesFilesForNamespacesHappyPath() {
        List<String> namespaces = Stream.of(CamelPropertyHelper.NAMESPACE_KEY, "wokkawokka")
                                        .collect(Collectors.toList());

        CamelPropertyHelper.loadPropertyFilesForNamespaces(context, namespaces);

        // check property from CamelPropertyHelper namespace property file is present
        String actualPropertyValue = context.getPropertiesComponent().resolveProperty("foo.bar").orElse("");
        String expectedPropertyValue = "baz";
        Assertions.assertEquals(expectedPropertyValue, actualPropertyValue);

        // check property from wokkawokka namespace property file is present
        actualPropertyValue = context.getPropertiesComponent().resolveProperty("pirate").orElse("");
        expectedPropertyValue = "yar";
        Assertions.assertEquals(expectedPropertyValue, actualPropertyValue);
    }

    @Test
    @DisplayName("checks that things break when a properties file can't be found for a given namespace")
    public void testLoadPropertiesFilesForNonExistentNamespace() {
        List<String> namespaces = Stream.of(CamelPropertyHelper.NAMESPACE_KEY, "samuel l bronkowitz presents")
                                        .collect(Collectors.toList());

        Assertions.assertThrows(RuntimeCamelException.class, () -> {
           CamelPropertyHelper.loadPropertyFilesForNamespaces(context, namespaces);
        });
    }


    //
    @Test
    @DisplayName("tests happy path for loading a route from a namespaced properties file")
    public void testLoadPropertiesAndInjectRoutesHappyPath() {
        // check that no routes exist in the camel context
        Assertions.assertEquals(context.getRoutes().size(), 0);

        CamelPropertyHelper.loadPropertiesAndInjectRoutes(
                context, CamelPropertyHelper.NAMESPACE_KEY, "mockroute");

        // now check that there's a single route in the camel context
        Assertions.assertEquals(context.getRoutes().size(), 1);
    }

    @Test
    @DisplayName("tests that a properly identified route without a properties file gets loaded")
    public void testLoadPropertiesAndInjectRoutesNoRoutePropertiesFile() {
        // check that no routes exist in the camel context
        Assertions.assertEquals(context.getRoutes().size(), 0);

        CamelPropertyHelper.loadPropertiesAndInjectRoutes(
                context, CamelPropertyHelper.NAMESPACE_KEY, "mockroute2");

        // now check that there's a single route in the camel context
        Assertions.assertEquals(context.getRoutes().size(), 1);
    }

    @Test
    @DisplayName("tests that a non existent route doesn't cause anything to blow up")
    public void testLoadPropertiesAndInjectRoutesInvalidRoute() {
            CamelPropertyHelper.loadTestPropertyFileForNamespace(context, "mockRouteBad");
    }

    @Test
    @DisplayName("tests that a non-existent namespace doesn't cause anything to blow up")
    public void testLoadPropertiesAndInjectRoutesNonExistentNamespace() {
        Assertions.assertThrows(RuntimeCamelException.class, () -> {
            CamelPropertyHelper.loadTestPropertyFileForNamespace(context, "international-space-eagle!");
        });
    }


}






