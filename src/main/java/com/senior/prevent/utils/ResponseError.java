package com.senior.prevent.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseError implements Serializable {

	private static final long serialVersionUID = 1L;
	private String code;
	private String description;
	
	public ResponseError() {
		super();
	}

	public ResponseError(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "ResponseError [code=" + code + ", description=" + description + "]";
	}

}
