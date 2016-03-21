package com.clearcapital.oss.http;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.clearcapital.oss.java.AssertHelpers;
import com.clearcapital.oss.java.exceptions.AssertException;

/**
 * Provide a decorator around CloseableHttpClient which retains knowledge of its cache storage in order to provide the
 * ability to flush the entire cache.
 */
@SuppressWarnings("deprecation")
public class ClearableCachingHttpClient extends CloseableHttpClient {

    private final CloseableHttpClient impl;
    private final ClearableHttpCacheStorage clearableHttpCacheStorage;

    public ClearableCachingHttpClient(CloseableHttpClient impl, ClearableHttpCacheStorage clearableHttpCacheStorage)
            throws AssertException {
        AssertHelpers.notNull(impl, "impl");
        AssertHelpers.notNull(clearableHttpCacheStorage, "clearableHttpCacheStorage");
        this.impl = impl;
        this.clearableHttpCacheStorage = clearableHttpCacheStorage;
    }

    public void flushCache() throws IOException {
        clearableHttpCacheStorage.flush();
    }

    @Override
    public HttpParams getParams() {
        return impl.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return impl.getConnectionManager();
    }

    @Override
    public void close() throws IOException {
        impl.close();
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context)
            throws IOException, ClientProtocolException {
        return impl.execute(target, request, context);
    }

}
