
package com.faxsun.myhttpclient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * An example that performs GETs from multiple threads.
 * copied from 
 * <a href="http://hc.apache.org/httpcomponents-client-4.3.x/examples.html">apache example</a>
 * 
 */
public class SimpleHttpTest {

	private String[] urisToGet = {
        "http://hc.apache.org/",
        "http://hc.apache.org/httpcomponents-core-ga/",
        "http://hc.apache.org/httpcomponents-client-ga/",
    };
	
	private long[] byteLengthsToGet = {15203,13614,17839};
	
    @Test
	public void testThreaded() throws Exception {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            // create a thread for each URI
            GetThread[] threads = new GetThread[urisToGet.length];
            for (int i = 0; i < threads.length; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                threads[i] = new GetThread(httpclient, httpget, i + 1);
            }

            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }
            
            for (int j = 0; j < threads.length; j++) {
                Assert.assertEquals(threads[j].getReslength(), byteLengthsToGet[j]);
            }

        } finally {
            httpclient.close();
        }
    }



}