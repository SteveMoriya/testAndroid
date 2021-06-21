/**
 * @ProjectName:
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address: 
 * @date: 2016-4-8 上午9:59:09
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 */
package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * 
 * <p>
 * {申请直播体类}
 * </p>
 * 
 * @author 包川 2016-6-3 下午3:53:43
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================创建
 * @modify by user: 包川 2016-6-3
 */
public class ApplyInfo extends BaseData {
	/**
	 * applyId : c45064367a5e49edab54d72fbb05bad4
	 * userId : 600018
	 * name : 唐
	 * identitycard : 510522199307053593
	 * sex : 1
	 * phone : 15328084473
	 * status : 0
	 * createTime : 2017-06-29 10:05:21
	 * idfront : 5d1821b9cc934797966e8c1ac11fa24c.jpg
	 * idseize : fffb009045db448f9ba105e5370eb56d.jpg
	 * idcon : 46d3e01ff0f14d979e06214a793aa390.jpg
	 */

	public String applyId;
	public String userId;
	public String name;
	public String identitycard;
	public int sex;
	public String phone;
	public int status;
	public String createTime;
	public String idfront;
	public String idseize;
	public String idcon;
    public int certificateType;// 证件类型
	public String iconUrl;
	/**
	 * IDCon : /gcrcsUploadFile/2016/07/12/114150/1.png
	 * IDSeize : /gcrcsUploadFile/2016/07/12/114150/1.png
	 * IDFront : /gcrcsUploadFile/2016/07/12/114150/1.png
	 * userName : asdas
	 * hospital : 市医院
	 * department : 外科
	 * title : 职称
	 */

	public String IDCon;
	public String IDSeize;
	public String IDFront;
	public String userName;
	public String hospital;
	public String department;
	public String title;
	/*private static final long serialVersionUID = -516875232270059948L;
	public String name;
	public int sex;
	public String phone;
	public String channelId;
	public String channelName;
	public String identitycard;
	public int status;// 审核状态：默认：0,待审核2,审核通过3,审核失败
	public int certificateType;// 证件类型
	public String applyId;
	public String userId;
	public String createTime;
	public String IDCon;
	public String IDFront;
	public String IDSeize;*/

	@Override
	public String toString() {
		return "ApplyInfo{" +
				"identitycard='" + identitycard + '\'' +
				", status=" + status +
				", IDCon='" + IDCon + '\'' +
				", IDSeize='" + IDSeize + '\'' +
				", IDFront='" + IDFront + '\'' +
				", userName='" + userName + '\'' +
				", hospital='" + hospital + '\'' +
				", department='" + department + '\'' +
				", title='" + title + '\'' +
				'}';
	}
}
