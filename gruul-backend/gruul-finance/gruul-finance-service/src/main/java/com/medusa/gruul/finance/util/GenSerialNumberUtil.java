package com.medusa.gruul.finance.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenSerialNumberUtil {

    public final static int FINANCIAL = 1;
    public final static int BROKERAGE = 2;
    public final static int WITHDRAWAL = 3;
    private static Map<String, GenSerialNumberUtil> MAP4FINANCIAL = new ConcurrentHashMap<>();
    private static Map<String, GenSerialNumberUtil> MAP4BROKERAGE = new ConcurrentHashMap<>();
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private GenSerialNumberUtil() {

    }

    public static GenSerialNumberUtil initGenSerialNumber(int type, String shopId) {
        GenSerialNumberUtil genSerialNumberUtil;
        switch (type) {
            case FINANCIAL:
                genSerialNumberUtil = MAP4FINANCIAL.get(shopId);
                if (ObjectUtil.isNull(genSerialNumberUtil)) {
                    genSerialNumberUtil = new GenSerialNumberUtil();
                    MAP4FINANCIAL.put(shopId, genSerialNumberUtil);
                }
                break;
            case BROKERAGE:
                genSerialNumberUtil = MAP4BROKERAGE.get(shopId);
                if (ObjectUtil.isNull(genSerialNumberUtil)) {
                    genSerialNumberUtil = new GenSerialNumberUtil();
                    MAP4BROKERAGE.put(shopId, genSerialNumberUtil);
                }
                break;
            default:
                genSerialNumberUtil = new GenSerialNumberUtil();
        }
        return genSerialNumberUtil;
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    public synchronized String nextId() {
        long timestamp = genTime();
        if (timestamp < lastTimestamp) {
            //如果服务器时间有问题(时钟后退) 报错。
            throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms",
                    lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) < 999999L ? (sequence + 1) : 0L;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        String seqStr = sequence + "";
        for (int i = 0; i <= 6 - seqStr.length(); i++) {
            seqStr = "0".concat(seqStr);
        }
        String yyyyMMddHHmm = DateUtil.format(DateUtil.date(), "yyyyMMddHHmm");
        return "" + yyyyMMddHHmm + seqStr;
    }

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    /**
     * 生成时间戳 分钟级
     *
     * @return 时间戳
     */
    private long genTime() {
        return System.currentTimeMillis() / 1000 / 60;
    }
}
