/*  Copyright (c) 2006-2007, Vladimir Nikic
    All rights reserved.

    Redistribution and use of this software in source and binary forms,
    with or without modification, are permitted provided that the following
    conditions are met:

 * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.

 * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.

 * The name of Web-Harvest may not be used to endorse or promote
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.

    You can contact Vladimir Nikic by sending e-mail to
    nikic_vladimir@yahoo.com. Please include the word "Web-Harvest" in the
    subject line.
 */
package org.webharvest.runtime.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.webharvest.runtime.ProxyConfiguration;
import org.webharvest.utils.CommonUtil;

/**
 * HTTP client functionality.
 */
public class HttpClientManager {

	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.1) Gecko/20060111 Firefox/1.5.0.1";

	/*
	 * static { // registers default handling for https
	 * Protocol.registerProtocol("https", new Protocol("https", new
	 * EasySSLProtocolSocketFactory(), 443)); }
	 */

	private CloseableHttpClient client;
	private HttpClientContext context;
	private HttpInfo httpInfo;
	private CookieStore cookieStore;
	private RequestConfig globalConfig;

	private HttpClientConnectionManager connectionManager;
	private String proxyHost;
	private String proxyPort;
	private String username;
	private String password;
	private String domain;
	private String workStation;
	boolean proxyEnabled;

	/**
	 * Constructor.
	 */
	public HttpClientManager() {
		this.proxyEnabled = false;
		connectionManager = HttpConnectionPoolHelper.getConnectionManager();
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
		cookieStore = new BasicCookieStore();
		context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
	}

	public HttpClientManager(ProxyConfiguration proxyConfiguration) {
		this();
		this.proxyEnabled = true;
		this.proxyHost = proxyConfiguration.getProxyHost();
		this.proxyPort = proxyConfiguration.getProxyPort();
		this.username = proxyConfiguration.getUsername();
		this.password = proxyConfiguration.getPassword();
		this.domain = proxyConfiguration.getDomain();
		this.workStation = proxyConfiguration.getWorkStation();
		this.client = getHttpClient();
		this.httpInfo = new HttpInfo(client);
	}


	public HttpResponseWrapper execute(String methodType, String url, String charset, String username, String password, List<NameValuePair> params, Map<String, String> headers) throws UnsupportedEncodingException {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}

		url = CommonUtil.encodeUrl(url, charset);

		if (username != null && password != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials(username, password));
			context.setCredentialsProvider(credsProvider);
		}

		HttpRequestBase method;
		if ("post".equalsIgnoreCase(methodType)) {
			method = createPostMethod(url, params);
		} else {
			method = createGetMethod(url, params, charset);
		}

		identifyAsDefaultBrowser(method);

		// define request headers, if any exist
		if (headers != null) {
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String headerName = (String) it.next();
				String headerValue = (String) headers.get(headerName);
				method.addHeader(new BasicHeader(headerName, headerValue));
			}
		}
		HttpResponseWrapper wrapper = null;
		try {
			wrapper = new HttpResponseWrapper(client.execute(method, context));

			// if there is redirection, try to download redirection page
			if ((wrapper.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) || (wrapper.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) || (wrapper.getStatusCode() == HttpStatus.SC_SEE_OTHER) || (wrapper.getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header header = (Header) wrapper.getHeaders().get("location");
				if (header != null) {
					String newURI = header.getValue();
					if (newURI != null && !newURI.equals("")) {
						newURI = CommonUtil.fullUrl(url, newURI);
						method.releaseConnection();
						// method = new HttpGet(newURI);
						// identifyAsDefaultBrowser(method);
						return execute("get", newURI, charset, username, password, params, headers);
					}
				}
			}

			this.httpInfo.setResponse(wrapper);

			return wrapper;
		} catch (IOException e) {
			throw new org.webharvest.exception.HttpException("IO error during HTTP execution for URL: " + url, e);
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Defines "User-Agent" HTTP header.
	 * 
	 * @param method
	 */
	private void identifyAsDefaultBrowser(HttpRequestBase method) {
		method.addHeader(new BasicHeader("User-Agent", DEFAULT_USER_AGENT));
	}

	private HttpRequestBase createPostMethod(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
		HttpPost method = new HttpPost(url);

		if (params != null) {
			NameValuePair[] paramArray = new NameValuePair[params.size()];
			Iterator<NameValuePair> it = params.iterator();
			int index = 0;
			while (it.hasNext()) {
				paramArray[index++] = (NameValuePair) it.next();
			}

			method.setEntity(new UrlEncodedFormEntity(params));
		}

		return method;
	}

	private HttpRequestBase createGetMethod(String url, List<NameValuePair> params, String charset) {
		if (params != null) {
			String urlParams = "";
			Iterator<NameValuePair> it = params.iterator();
			while (it.hasNext()) {
				NameValuePair pair = (NameValuePair) it.next();
				String value = pair.getValue();
				try {
					urlParams += pair.getName() + "=" + URLEncoder.encode(value == null ? "" : value, charset) + "&";
				} catch (UnsupportedEncodingException e) {
					throw new org.webharvest.exception.HttpException("Charset " + charset + " is not supported!", e);
				}
			}

			if (!"".equals(urlParams)) {
				if (url.indexOf("?") < 0) {
					url += "?" + urlParams;
				} else if (url.endsWith("&")) {
					url += urlParams;
				} else {
					url += "&" + urlParams;
				}
			}
		}

		return new HttpGet(url);
	}

	public CloseableHttpClient getHttpClient() {
		if (proxyEnabled) {
			HttpHost proxy = new HttpHost(this.proxyHost, Integer.parseInt(this.proxyPort));
			if (this.workStation != null) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(this.proxyHost, Integer.parseInt(this.proxyPort)), new NTCredentials(this.username, this.password, this.workStation, this.domain));
				context.setCredentialsProvider(credsProvider);
			}
			client = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).setConnectionManager(connectionManager).setProxy(proxy).build();
		} else
			client = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).setConnectionManager(connectionManager).build();
		return client;
	}

	public HttpInfo getHttpInfo() {
		return httpInfo;
	}

}