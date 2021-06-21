package com.wehang.ystv.dbutils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by hezongze on 2016/8/18.
 */
@DatabaseTable(tableName="download_bean")
public class DownloadBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = "fileName")
    private String fileName;

    @DatabaseField(columnName = "imgpath")
    private String imgpath;

    @DatabaseField(columnName = "moviename")
    private String moviename;
    @DatabaseField(columnName = "iswait")
    private boolean iswait=false;

    public boolean iswait() {
        return iswait;
    }

    public void setIswait(boolean iswait) {
        this.iswait = iswait;
    }

    @DatabaseField(columnName = "total_length")
    private int total_length;

    @DatabaseField(columnName = "download_length")
    private int download_length;

    @DatabaseField(id = true, columnName = "url")
    private String url;

    @DatabaseField(columnName = "percent")
    private int percent;
    @DatabaseField(columnName = "date")
    private long date=0;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @DatabaseField(columnName = "isOver")
    private boolean isOver;

    private boolean newTask;

    public boolean isNewTask() {
        return newTask;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getMoviename() {
        return moviename;
    }

    public void setMoviename(String moviename) {
        this.moviename = moviename;
    }

    public void setNewTask(boolean newTask) {
        this.newTask = newTask;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotal_length() {
        return total_length;
    }

    public void setTotal_length(int total_length) {
        this.total_length = total_length;
    }

    public int getDownload_length() {
        return download_length;
    }

    public void setDownload_length(int download_length) {
        this.download_length = download_length;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    @Override
    public String toString() {
        return "DownloadBean{" +
                "moviename='" + moviename + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
