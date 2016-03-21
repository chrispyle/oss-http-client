package com.clearcapital.oss.http;

import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clearcapital.oss.java.exceptions.AssertException;

public class ClearableCachingHttpClientBuilder extends CachingHttpClientBuilder {

    private static Logger log = LoggerFactory.getLogger(ClearableCachingHttpClientBuilder.class);

    ClearableCachingHttpClientConfig clearableCachingHttpClientConfig;
    ClearableHttpCacheStorage clearableHttpCacheStorage;

    public ClearableCachingHttpClientBuilder(ClearableCachingHttpClientConfig clearableCachingHttpClientConfig) {
        this.clearableCachingHttpClientConfig = clearableCachingHttpClientConfig;
    }

    public ClearableCachingHttpClient build() {
        try {
            return throwingBuild();
        } catch (AssertException e) {
            log.error("Assertion failed while creating ClearableCachingHttpClient.", e);
            return null;
        }
    }

    public ClearableCachingHttpClient throwingBuild() throws AssertException {
        clearableHttpCacheStorage = new ClearableHttpCacheStorage(clearableCachingHttpClientConfig);
        addCloseable(clearableHttpCacheStorage);
        setHttpCacheStorage(clearableHttpCacheStorage);
        if (clearableCachingHttpClientConfig != null) {
            setCacheDir(clearableCachingHttpClientConfig.getCacheDir());
            setCacheConfig(clearableCachingHttpClientConfig.getCacheConfig());
        }
        return new ClearableCachingHttpClient(super.build(), clearableHttpCacheStorage);
    }
}
