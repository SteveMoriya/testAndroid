package com.whcd.base.interfaces;

import java.io.Serializable;

/**
 * 
 * <p>
 * {解析基类}
 * </p>
 * 
 * @author 刘树宝 2016年4月6日 下午5:00:59
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================创建
 * @modify by user: 刘树宝 2016年4月6日
 */
public class BaseResult<T extends BaseData> implements Serializable {
	private static final long serialVersionUID = 7414872861501563574L;
	public int status;
	public String msg;
	public T data;
}
