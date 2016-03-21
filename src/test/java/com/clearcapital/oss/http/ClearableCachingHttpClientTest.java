package com.clearcapital.oss.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ClearableCachingHttpClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8675);

    @Test
    public void testFlushCache() throws Exception {
        stubFor(get(urlEqualTo("/")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml")
                        .withHeader("Cache-Control", "max-age=2048").withBody("A")));

        ClearableCachingHttpClientConfig config = ClearableCachingHttpClientConfig.builder().build();
        ClearableCachingHttpClient client = new ClearableCachingHttpClientBuilder(config).build();

        HttpUriRequest request = new HttpGet("http://localhost:8675/");
        CloseableHttpResponse response = client.execute(request);
        assertNotNull(response);
        assertEquals("A", IOUtils.toString(response.getEntity().getContent()));

        stubFor(get(urlEqualTo("/")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml")
                        .withHeader("Cache-Control", "max-age=2048").withBody("B")));

        // Should have used the cache, so even though the wiremock server is returning "B" now, we shouldn't have hit
        // the server. Well, unless you pause for 2048 seconds!
        CloseableHttpResponse response2 = client.execute(request);
        assertEquals("A", IOUtils.toString(response2.getEntity().getContent()));

        // Try again, but flush the cache first.
        client.flushCache();
        CloseableHttpResponse response3 = client.execute(request);
        assertEquals("B", IOUtils.toString(response3.getEntity().getContent()));
    }

    @Test
    public void testNoCache() throws Exception {
        stubFor(get(urlEqualTo("/")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml")
                        .withHeader("Cache-Control", "no-cache").withBody("A")));

        ClearableCachingHttpClientConfig config = ClearableCachingHttpClientConfig.builder().build();
        ClearableCachingHttpClient client = new ClearableCachingHttpClientBuilder(config).build();

        HttpUriRequest request = new HttpGet("http://localhost:8675/");
        CloseableHttpResponse response = client.execute(request);
        assertNotNull(response);
        assertEquals("A", IOUtils.toString(response.getEntity().getContent()));

        stubFor(get(urlEqualTo("/")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml")
                        .withHeader("Cache-Control", "no-cache").withBody("B")));

        // Should not have cached "A", so now that the wiremock server is returning "B," we should have hit
        // the server.
        CloseableHttpResponse response2 = client.execute(request);
        assertEquals("B", IOUtils.toString(response2.getEntity().getContent()));
    }
}
