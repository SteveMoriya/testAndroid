package com.whcd.base.bo;

public enum PictureType {
	LOCAL("本地图片", 0), REMOTE("远程图片", 1), ICON("按钮图片", 99);

	private String name;
	private int index;

	private PictureType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static PictureType valueOf(int index) {
		for (PictureType type : PictureType.values()) {
			if (type.getIndex() == index) {
				return type;
			}
		}
		return null;
	}

}