package com.wehang.ystv.logic;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 钟林宏 2017/1/22 16:26
 * @version V1.0
 * @ProjectName: ToLiveTencentIM
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/22 16:26
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:用户信息获取
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/22
 */

public class UsersProfilePresenter {
    private static UsersProfilePresenter mUsersProfilePresenter;
    private Map<String, TIMUserProfile> userInfoMap;

    public static UsersProfilePresenter getInstance() {
        if (mUsersProfilePresenter == null) {
            mUsersProfilePresenter = new UsersProfilePresenter();
        }
        return mUsersProfilePresenter;
    }

    public UsersProfilePresenter() {
        userInfoMap = new HashMap<>();
    }

    public void getUsersProfiles(List<String> userList, final UsersProfileCallBack mUsersProfileCallBack) {
        TIMFriendshipManager.getInstance().getUsersProfile(userList, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                if (mUsersProfileCallBack != null) {
                    mUsersProfileCallBack.onError();
                }
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                parseUserProfiles(timUserProfiles, mUsersProfileCallBack);
            }
        });
    }

    private void parseUserProfiles(List<TIMUserProfile> timUserProfiles, UsersProfileCallBack mUsersProfileCallBack) {
        for (TIMUserProfile timUserProfile : timUserProfiles) {
            if (userInfoMap != null) {
                String str=timUserProfile.getIdentifier();
                userInfoMap.put(timUserProfile.getIdentifier(), timUserProfile);
            }
        }
        if (mUsersProfileCallBack != null) {
            mUsersProfileCallBack.onSuccess();
        }
    }

    public TIMUserProfile getUsersProfile(String id) {
        if (userInfoMap.containsKey(id)) {
            return userInfoMap.get(id);
        }
        return null;
    }

    public interface UsersProfileCallBack {
        void onError();

        void onSuccess();
    }
}
