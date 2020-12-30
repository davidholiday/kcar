package io.holitek.kcar.helpers;


import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CamelPropertyHelperTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelPropertyHelper.class);

    @Test
    @DisplayName("should wrap the provided string in a double set of curly braces")
    public void testGetPropertyPlaceholderSingleKey() {
        String propertyKey = "foo";
        String expectedResult = "{{" + propertyKey + "}}";
        String actualResult = CamelPropertyHelper.getPropertyPlaceholder(propertyKey);
        Assertions.assertEquals(actualResult, expectedResult);
    }

    @Test
    @DisplayName("should concatinate the provided strings with periods and wrap the result in a double set of " +
                 "curly braces")
    public void testGetPropertyPlaceholderMultiKey() {
        String expectedResult = "{{foo.bar.baz}}";
        String actualResult = CamelPropertyHelper.getPropertyPlaceholder("foo", "bar", "baz");
        Assertions.assertEquals(actualResult, expectedResult);
    }

}
