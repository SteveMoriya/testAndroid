package com.wehang.ystv.bo;

import java.io.Serializable;

public class PhotoInfo implements Serializable {
	private static final long serialVersionUID = -1379256620776496708L;
	public String name;
	public String path;

	public PhotoInfo() {
		super();
	}

	public PhotoInfo(String name) {
		super();
		this.name = name;
	}

	public PhotoInfo(String name, String path) {
		super();
		this.name = name;
		this.path = path;
	}


	@Override
	public String toString() {
		return "PhotoInfo{" +
				"name='" + name + '\'' +
				", path='" + path + '\'' +
				'}';
	}
}
