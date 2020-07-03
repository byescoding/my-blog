package com.bai.blogadmin.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 *时间格式处理
 **/
public class DateUtils {

    /**
     * 获得本地当前时间
     * @param
     * @return java.sql.Timestamp
     * @date 2019/8/28 13:03
     */
    public static Timestamp getLocalCurrentDate(){
        return new Timestamp(new Date().getTime());
    }
}
