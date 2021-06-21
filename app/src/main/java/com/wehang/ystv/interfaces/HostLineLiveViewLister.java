package com.wehang.ystv.interfaces;

/**
 * @author 钟林宏 2017/1/9 10:31
 * @version V1.0
 * @ProjectName: ToLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/9 10:31
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/9
 */

public interface HostLineLiveViewLister {
    /**
     * 请求连麦
     *
     * @param userId
     */
    public void requestLineMic(String userId, String userName);

    /**
     * 成员推流成功
     *
     * @param userId        用户id
     * @param memberPullUrl 成员拉流地址
     */
    public void memberPushFlowSuc(String userId, String memberPullUrl);

    /**
     * 成员退出连麦
     *
     * @param userId 用户id
     */
    public void memberExitNotify(String userId);
}
