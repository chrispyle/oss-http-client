package com.clearcapital.oss.http;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.ManagedHttpCacheStorage;

public class ClearableHttpCacheStorage implements HttpCacheStorage, Closeable {

    HttpCacheStorage impl;
    ClearableCachingHttpClientConfig config;

    public ClearableHttpCacheStorage(ClearableCachingHttpClientConfig config) {
        this.config = config;
        buildImpl();
    }

    public void flush() throws IOException {
        close();

        buildImpl();
    }

    private void buildImpl() {
        if (config == null || config.getCacheDir() == null) {
            impl = new BasicHttpCacheStorage(getCacheConfig());
        } else {
            impl = new ManagedHttpCacheStorage(getCacheConfig());
        }
    }

    private CacheConfig getCacheConfig() {
        if (config == null || config.getCacheConfig() == null) {
            return CacheConfig.custom().build();
        } else {
            return config.getCacheConfig();
        }

    }

    @Override
    public void putEntry(String key, HttpCacheEntry entry) throws IOException {
        impl.putEntry(key, entry);
    }

    @Override
    public HttpCacheEntry getEntry(String key) throws IOException {
        return impl.getEntry(key);
    }

    @Override
    public void removeEntry(String key) throws IOException {
        impl.removeEntry(key);
    }

    @Override
    public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
        impl.updateEntry(key, callback);
    }

    @Override
    public void close() throws IOException {
        if (impl instanceof Closeable) {
            ((Closeable) impl).close();
        }
    }
}