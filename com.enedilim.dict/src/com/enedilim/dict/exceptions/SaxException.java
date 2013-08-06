package com.enedilim.dict.exceptions;

public class SaxException extends Exception{

	private static final long serialVersionUID = 1L;

	public SaxException() {
		super();
	}

	public SaxException(String detailMessage, Throwable throwable) {
		super("SAXException: " + detailMessage, throwable);
	}

	public SaxException(String detailMessage) {
		super("SAXException: " + detailMessage);
	}

	public SaxException(Throwable throwable) {
		super(throwable);
	}
}
