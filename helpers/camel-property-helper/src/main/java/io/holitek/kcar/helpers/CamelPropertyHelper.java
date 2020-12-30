package io.holitek.kcar.helpers;


/**
 *
 */
public class CamelPropertyHelper {

    /**
     * takes [n] String arguments and returns a Camel property placeholder.
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

}
