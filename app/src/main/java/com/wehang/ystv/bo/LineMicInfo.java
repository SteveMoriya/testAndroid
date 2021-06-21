package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * @author 钟林宏 2017/1/9 16:41
 * @version V1.0
 * @ProjectName: ToLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/9 16:41
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:连麦相关地址
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/9
 */

public class LineMicInfo extends BaseData {
    public String connectPushUrl;//连麦推流地址
    public String connectReduceUrl;//对应的低延迟拉流地址
}
