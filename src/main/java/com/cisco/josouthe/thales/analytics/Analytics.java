package com.cisco.josouthe.thales.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;


public class Analytics {
    private static final Logger logger = LogManager.getFormatterLogger();

    public String baseUrl, APIAccountName, APIKey;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private OkHttpClient okHttpClient;

    public Analytics(String urlString, String APIAccountName, String APIKey) throws MalformedURLException {
        if( !urlString.endsWith("/") ) urlString+="/";
        this.baseUrl = urlString;
        this.APIAccountName = APIAccountName;
        this.APIKey = APIKey;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        okHttpClient = builder.build();
    }

    protected String getRequest( String urlRequest ) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url( this.baseUrl + urlRequest)
                .method("GET", null)
                .addHeader("X-Events-API-AccountName", this.APIAccountName)
                .addHeader("X-Events-API-Key", this.APIKey)
                .addHeader("Accept","application/vnd.appd.events+json;v=2")
                .build();
        logger.trace("Request %s",request.toString());
        Response response = okHttpClient.newCall(request).execute();
        String json = response.body().string();
        logger.trace("Response Body: %s",json);
        return json;
    }

    protected String postRequest( String urlRequest, String body) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder()
                .url( this.baseUrl + urlRequest)
                .method("POST", requestBody)
                .addHeader("X-Events-API-AccountName", this.APIAccountName)
                .addHeader("X-Events-API-Key", this.APIKey)
                .addHeader("Content-type","application/vnd.appd.events+json;v=2")
                .addHeader("Accept","application/vnd.appd.events+json;v=2")
                .build();
        logger.trace("Request %s",request.toString());
        Response response = okHttpClient.newCall(request).execute();
        String json = response.body().string();
        logger.trace("Response Body: %s",json);
        return json;
    }


}
