package com.example.nimei1.ranba.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qy on 2018/6/28.
 * desc
 */

public class DateUtils {

    public static String covertToDate(long duration){
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
}
