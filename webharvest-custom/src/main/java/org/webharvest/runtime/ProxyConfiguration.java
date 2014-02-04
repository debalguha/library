package org.webharvest.runtime;

public class ProxyConfiguration {
	private final String proxyHost;
	private final String proxyPort;
	private final String username;
	private final String password;
	private final String domain;
	private final String workStation;
	public ProxyConfiguration(String proxyHost, String proxyPort, String username, String password, String domain, String workStation) {
		super();
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.username = username;
		this.password = password;
		this.domain = domain;
		this.workStation = workStation;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public String getProxyPort() {
		return proxyPort;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getDomain() {
		return domain;
	}
	public String getWorkStation() {
		return workStation;
	}
	
}
