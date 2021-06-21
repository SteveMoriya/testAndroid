package com.wehang.ystv.dbutils;

import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.wehang.ystv.utils.Utils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by hezongze on 2016/8/18.
 */
public class DownloadDao {

    private static final String TAG = "DownloadDao";

    private Dao<DownloadBean, String> downloadDao;
    private Dao<KejianBean,String>kejianDao;

    private static DownloadDao mInstance;

    public static DownloadDao getInstance(OrmLiteSqliteOpenHelper helper) {
        if (mInstance == null) {
            synchronized (DownloadDao.class) {
                if (mInstance == null) {
                    mInstance = new DownloadDao(helper);
                }
            }
        }
        return mInstance;
    }
/*增加，刷新
* */

    public DownloadDao(OrmLiteSqliteOpenHelper helper) {
        try {
            downloadDao = helper.getDao(DownloadBean.class);
            kejianDao=helper.getDao(KejianBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "init DownloadDao fail");
        }
    }

    /**
     * 添加一个Article
     * @param bean
     */
    public void add(DownloadBean bean)
    {
        try
        {
            downloadDao.create(bean);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 添加一个Article
     * @param bean
     */

    public void add(KejianBean bean)
    {
        try
        {
            kejianDao.createOrUpdate(bean);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void delete(DownloadBean bean){
        try
        {
            downloadDao.delete(bean);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void deleteAll(List<DownloadBean>list){
        try
        {
            downloadDao.delete(list);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public List<DownloadBean> GetAllBean(){
        try
        {
           return Utils.addAllDoxu(downloadDao.queryForAll());
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public List<KejianBean> GetAllkejianBean(){
        try
        {
            return kejianDao.queryForAll();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public KejianBean queryKejian(String kejianId){
        try
        {
            return kejianDao.queryForId(kejianId);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
