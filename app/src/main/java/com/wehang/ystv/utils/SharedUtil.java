package com.wehang.ystv.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wehang.ystv.VideoApplication;


/***
 * <p>
 * 控制shared存储文件
 * </p>
 *
 * @author 钟林宏 2016-6-8 下午5:04:55
 * @version V1.0
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2016-6-8
 */
public class SharedUtil {

    /**
     * 读取shared存储值的方法
     *
     * @param sharedname    shared文件名
     * @param key           存储的键值名
     * @param defaultObject 对应读取类型的默认值（Boolean:false;String:"";int:0;Long:0L;Float:0.0f）
     * @return 读取到的数据
     */
    public static Object readSharedMethod(String sharedname, String key,
                                          Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 写入shared存储值的方法
     *
     * @param sharedname  shared文件名
     * @param key         存储的键值名
     * @param objectvalue 写入的数据值
     */
    public static void writeSharedMethod(String sharedname, String key,
                                         Object objectvalue) {
        if (objectvalue != null) {
            String type = objectvalue.getClass().getSimpleName();
            SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                    sharedname, 0);
            Editor editor = sp.edit();
            if ("String".equals(type)) {
                editor.putString(key, (String) objectvalue);
            } else if ("Integer".equals(type)) {
                editor.putInt(key, (Integer) objectvalue);
            } else if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) objectvalue);
            } else if ("Float".equals(type)) {
                editor.putFloat(key, (Float) objectvalue);
            } else if ("Long".equals(type)) {
                editor.putLong(key, (Long) objectvalue);
            }
            editor.commit();
        }
    }

    /**
     * 写入Shared String共享的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     * @param value      写入的数据值
     */
    public static void writeStringMethod(String sharedname, String key,
                                         String value) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取Shared String共享的方法
     *
     * @param sharedname   shared文件名
     * @param key          存储的键值名
     * @param defaultvalue 默认值
     */
    public static String readStringMethod(String sharedname, String key,
                                          String defaultvalue) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        return sp.getString(key, defaultvalue);
    }

    /**
     * 写入Shared Int共享的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     * @param value      写入的数据值
     */
    public static void writeIntMethod(String sharedname, String key, int value) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 读取Shared Int共享的方法
     *
     * @param sharedname   shared文件名
     * @param key          存储的键值名
     * @param defaultvalue 默认值
     */
    public static int readIntMethod(String sharedname, String key,
                                    int defaultvalue) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        return sp.getInt(key, defaultvalue);
    }

    /**
     * 写入Shared Boolean共享的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     * @param value      写入的数据值
     */
    public static void writeBooleanMethod(String sharedname, String key,
                                          boolean value) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 读取Shared Boolean共享的方法
     *
     * @param sharedname   shared文件名
     * @param key          存储的键值名
     * @param defaultvalue 默认值
     */
    public static boolean readBooleanMethod(String sharedname, String key,
                                            boolean defaultvalue) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        return sp.getBoolean(key, defaultvalue);
    }

    /**
     * 写入Shared Float共享的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     * @param value      写入的数据值
     */
    public static void writeFloatMethod(String sharedname, String key,
                                        float value) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 读取Shared Float共享的方法
     *
     * @param sharedname   shared文件名
     * @param key          存储的键值名
     * @param defaultvalue 默认值
     */
    public static float readFloatMethod(String sharedname, String key,
                                        float defaultvalue) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        return sp.getFloat(key, defaultvalue);
    }

    /**
     * 写入Shared Long共享的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     * @param value      写入的数据值
     */
    public static void writeLongMethod(String sharedname, String key, long value) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 读取Shared Long共享的方法
     *
     * @param sharedname   shared文件名
     * @param key          存储的键值名
     * @param defaultvalue 默认值
     */
    public static long readLongMethod(String sharedname, String key,
                                      long defaultvalue) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        return sp.getLong(key, defaultvalue);
    }

    /**
     * 移除存储文件的方法
     *
     * @param sharedname shared文件名
     * @param key        存储的键值名
     */
    public static void removeSharedMethod(String sharedname, String key) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 移除指定文件下面所有存储值的方法
     *
     * @param sharedname shared文件名
     */
    public static void removeSharedMethod(String sharedname) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 清空指定shared文件里面的所有存储内容
     *
     * @param sharedname shared文件名
     */
    public static void clearSharedMethod(String sharedname) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 判断存储值是否为空的方法
     *
     * @param sharedname 存储文件名
     * @param key        key值名称
     * @return 为空返回true 否则返回false
     */
    public static boolean isEmpty(String sharedname, String key) {
        SharedPreferences sp = VideoApplication.instance.getSharedPreferences(
                sharedname, 0);
        boolean isempty = sp.contains(key);
        return !isempty;

    }
}