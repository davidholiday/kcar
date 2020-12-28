package io.holitek.kcar;

import org.apache.camel.BindToRegistry;
import org.apache.camel.PropertyInject;

/**
 * Class to configure the Camel application.
 */
public class MyConfiguration {

    @BindToRegistry
    public MyBean myBean(@PropertyInject("hi") String hi, @PropertyInject("bye") String bye) {
        // this will create an instance of this bean with the name of the method (eg myBean)
        return new MyBean(hi, bye);
    }

    public void configure() {
        // this method is optional and can be removed if no additional configuration is needed.
    }

}
