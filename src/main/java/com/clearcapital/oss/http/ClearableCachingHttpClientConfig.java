package com.clearcapital.oss.http;

import java.io.File;

import org.apache.http.impl.client.cache.CacheConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClearableCachingHttpClientConfig {

    @JsonProperty
    private CacheConfig cacheConfig;

    @JsonProperty
    private File cacheDir;

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        ClearableCachingHttpClientConfig result;

        Builder() {
            result = new ClearableCachingHttpClientConfig();
        }

        ClearableCachingHttpClientConfig build() {
            return result;
        }

        Builder setCacheConfig(CacheConfig value) {
            result.cacheConfig = value;
            return this;
        }

        Builder setCacheDir(File value) {
            result.cacheDir = value;
            return this;
        }
    }

}