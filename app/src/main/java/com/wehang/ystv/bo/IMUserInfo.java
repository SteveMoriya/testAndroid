package com.wehang.ystv.bo;

/**
 * 用户数据
 */
public class IMUserInfo {

    private String id;
    private String userSig;

    private static IMUserInfo ourInstance = new IMUserInfo();

    public static IMUserInfo getInstance() {
        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

}