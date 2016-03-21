package com.clearcapital.oss.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clearcapital.oss.commands.Command;
import com.clearcapital.oss.commands.CommandExecutionException;
import com.clearcapital.oss.java.StackHelpers;


public class HttpCommand implements Command {

    private static Logger log = LoggerFactory.getLogger(HttpCommand.class);

    private String location;
    private HttpUriRequest httpUriRequest;
    
    @Override
    public void execute() throws CommandExecutionException {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpUriRequest);
            int httpStatusCode = response.getStatusLine().getStatusCode();
            if (httpStatusCode < 200 || httpStatusCode > 299) {
                throw new CommandExecutionException("Request failed with status code: " + httpStatusCode + ". Response [" + response + "]. POST ["
                        + httpUriRequest + "].");
            }
        } catch (IOException e) {
            throw new CommandExecutionException("Command threw an exception", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.warn("Could not close http client", e);
                }
            }
        }
    }

    @Override
    public String getLocation() {
        return location;
    }

    public HttpUriRequest getHttpUriRequest() {
        return httpUriRequest;
    }

    public static Builder builder() {
        String location = StackHelpers.getRelativeStackLocation(1);
        return new Builder(location);
    }

    public static class Builder {

        HttpCommand result = new HttpCommand();

        public Builder(String location) {
            setLocation(location);
        }

        public Builder setLocation(String location) {
            result.location = location;
            return this;
        }

        public Builder setRequest(HttpUriRequest httpUriRequest ) {
            result.httpUriRequest = httpUriRequest;
            return this;
        }

        public HttpCommand build() {
            return result;
        }
    }

}
