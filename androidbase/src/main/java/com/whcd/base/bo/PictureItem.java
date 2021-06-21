package com.whcd.base.bo;

import java.io.Serializable;

public class PictureItem implements Serializable {
	private static final long serialVersionUID = 1461342115430727984L;

	public boolean isChecked;// 判断该选项是否被选上的标志量
	public String folderLabel = null;
	public int imageId = -1;
	public int imageType = 0;// 图片类型
	public String imagePath = null;
	public int count = 0;

	public PictureItem() {
	}

	public PictureItem(int imageType) {
		super();
		this.imageType = imageType;
	}

	public PictureItem(String imagePath) {
		super();
		this.imagePath = imagePath;
	}

	public PictureItem(int imageId, String imagePath) {
		this.imageId = imageId;
		this.imagePath = imagePath;
	}

	public PictureItem(String folderLabel, int imageId, String imagePath) {
		this.folderLabel = folderLabel;
		this.imageId = imageId;
		this.imagePath = imagePath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getFolderLabel() {
		return folderLabel;
	}

	public void setFolderLabel(String folderLabel) {
		this.folderLabel = folderLabel;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
