package com.wehang.ystv.interfaces;

/**
 * @author 钟林宏 2017/1/9 15:02
 * @version V1.0
 * @ProjectName: ToLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/9 15:02
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/9
 */

public interface MemberLineLiveViewLister {
    public void startPlay();

    public void stopLoading();

    public void stopPlay(boolean clearLastFrame);

    public void startLinkPush(String pushUrl);

    public void switchCamera();

    public void stopLinkPlay();

    public void stopLinkPush();
}
