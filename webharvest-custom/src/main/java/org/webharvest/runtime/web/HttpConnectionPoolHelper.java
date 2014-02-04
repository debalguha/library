package org.webharvest.runtime.web;

import org.apache.http.HttpHost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpConnectionPoolHelper {
	protected static HttpConnectionPoolHelper me = new HttpConnectionPoolHelper();
	private HttpClientConnectionManager connectionManager;
	protected HttpConnectionPoolHelper(){
		createConnectionManager();
	}
	public static HttpClientConnectionManager getConnectionManager(){
		return me.connectionManager;
	}
	private void createConnectionManager(){
		connectionManager = new PoolingHttpClientConnectionManager();
		((PoolingHttpClientConnectionManager)connectionManager).setMaxTotal(55);
		((PoolingHttpClientConnectionManager)connectionManager).setDefaultMaxPerRoute(50);
		HttpHost localhost = new HttpHost("http://gimme.io");
		((PoolingHttpClientConnectionManager)connectionManager).setMaxPerRoute(new HttpRoute(localhost), 50);
	}
}
