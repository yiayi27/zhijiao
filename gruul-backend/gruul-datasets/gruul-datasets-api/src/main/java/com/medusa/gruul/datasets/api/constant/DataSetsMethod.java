package com.medusa.gruul.datasets.api.constant;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 年月日方法转换
 * @Author: xiaoq
 * @Date: 2024/5/9 14:07
 */
public class DataSetsMethod {

	/**
	 * 分装方法 年
	 *
	 * @return
	 */
	public static Integer communalYear() {
		return Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
	}

	/**
	 * 分装方法 年月
	 *
	 * @return
	 */
	public static Integer communalYearAndMonth() {
		return Integer.valueOf(new SimpleDateFormat("yyyyMM").format(new Date()));
	}

	/**
	 * 分装方法 年月日
	 *
	 * @return
	 */
	public static Integer communalDate() {
		return Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date()));
	}
}
