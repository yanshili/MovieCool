package com.coolcool.moviecool.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：用于处理和时间、日期相关的转换
 * Created by yanshili on 2016/2/20.
 */
public class DateTools {

    //将日期毫秒数转成日期字符串文本形式
    public static String toText(long timestamp,DateFormat format){
        String date=null;
        date=DateTools.decodeDate(new Date(timestamp),format);
        return date;
    }

    //将日期字符串文本形式转成日期毫秒数
    public static long toDate(String dateText,DateFormat format){
        long timestamp=0L;
        timestamp=DateTools.encodeDate(dateText,format).getTime();
        return timestamp;
    }

    /**
     * 功能：提取一段文本内的第一个日期(格式为2016年2月2日，或者2016年02月02日)，
     *      并转换为指定格式的字符串类型
     * @param rawText   要提取日期的文本
     * @return  返回指定格式的日期字符串
     */
    public static String pickTimestamp(String rawText,DateFormat format){
        String year=null,month=null,day=null;
        String dateText=null;
        //(\\s*)表示有0个货多个空白字符
        //从一段文本中提取日期，日期格式如（2016年2月2日，或者2016年02月02日）
        Pattern pattern=Pattern.compile("(\\d{4})\\s*[-年]\\s*(\\d{1,2})\\s*[-月]\\s*(\\d{1,2})\\s*[\\s日]");
        Matcher matcher=pattern.matcher(rawText);
        if (matcher.find()){
            year=matcher.group(1);
            month=matcher.group(2);
            day=matcher.group(3);
        }
        dateText=year+"-"+month+"-"+day+" 12:00:00";

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        try {
            date=dateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateText=format.format(date);
        return dateText;
    }

    /**
     * 功能：将字符串日期按照相应的格式转换位Date类
     * @param dateText  有一定格式的字符串日期
     * @param format    与所传字符串日期相匹配的格式
     * @return
     */
    public static Date encodeDate(String dateText,DateFormat format){
        Date date=null;
        try {
            date=format.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 功能：将Date日期类，转换为指定格式的字符串形式
     * @param date
     * @param format
     * @return
     */
    public static String decodeDate(Date date,DateFormat format){
        String dateText=null;
        dateText=format.format(date);
        return dateText;
    }
}
