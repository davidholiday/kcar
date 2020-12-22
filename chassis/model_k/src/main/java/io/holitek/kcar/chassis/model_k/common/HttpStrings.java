package io.holitek.kcar.chassis.model_k.common;


import java.net.HttpURLConnection;


/**
 * a few things needed by multiple classes. here to keep the code DRY
 */
public class HttpStrings {

    public static final String HTTP_OK = Integer.valueOf(java.net.HttpURLConnection.HTTP_OK).toString();
    public static final String HTTP_ACCEPTED = Integer.valueOf(HttpURLConnection.HTTP_ACCEPTED).toString();
    public static final String JSON_MEDIA_TYPE = "application/json";

}
