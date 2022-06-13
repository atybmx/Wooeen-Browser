package com.wooeen.model.to;

import java.io.Serializable;

public class UserTokenTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserTO user;
	private String idToken;
	private String accessToken;
	private String hostName;
	
	public UserTokenTO() {
	}
	
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

}
