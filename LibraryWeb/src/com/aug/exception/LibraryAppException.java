package com.aug.exception;

public class LibraryAppException extends Exception {

	private Throwable cause;
	
	private String userMessage;

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public String getUserMessage() {
		return userMessage;
	}
	
	

}
