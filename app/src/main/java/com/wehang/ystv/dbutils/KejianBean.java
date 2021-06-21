package com.wehang.ystv.dbutils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by hezongze on 2016/8/18.
 */
@DatabaseTable(tableName="kejian_bean")
public class KejianBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = "page")
    private int page;


    @DatabaseField(id = true, columnName = "url")
    private String url;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "KejianBean{" +
                "page=" + page +
                ", url='" + url + '\'' +
                '}';
    }
}
