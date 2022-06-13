package com.wooeen.model.api.utils;

public class ApiCallReturn<E> {
	
	private boolean result;
	private String message;
	private E callback;
	
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public E getCallback() {
		return callback;
	}
	public void setCallback(E callback) {
		this.callback = callback;
	}
	
	public ApiCallReturn<E> withResult(boolean result) {
		this.result = result;
		return this;
	}
	public ApiCallReturn<E> withMessage(String message) {
		this.message = message;
		return this;
	}
	
}