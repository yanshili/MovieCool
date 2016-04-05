package com.coolcool.moviecool.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanshili on 2016/4/2.
 */
public class RegexUtils {

    /**
     * 检查邮箱拼写是否有效
     * @param email     需要检查的邮箱
     * @return          如果拼写无误就返回null
     *                  如果拼写有误就返回错误信息，可直接将错误信息反馈给用户
     */
    public static String checkEmailText(String email) {
        if (email.length()==0){
            return "邮箱信息为空";
        }
        if (!email.contains("@")){
            return "无效的邮箱地址";
        }

        //检测输入的EMAIL地址是否以 非法符号"."或"@"作为起始字符
        Pattern p = Pattern.compile("^\\.|^@");
        Matcher m = p.matcher(email);
        if (m.find()) {
            return "邮箱地址不能以'.'或'@'作为起始字符";
        }
        //检测是否以"www."为起始
        p = Pattern.compile(".+@(.*)");
        m = p.matcher(email);
        if (m.find()) {
            String s=m.group(1);
            s=s.replaceAll(" ","");
            if (s.length()==0)
            return "邮箱地址无效";
        }
        p = Pattern.compile("@.+\\..+");
        m = p.matcher(email);
        if (!m.find()) {
            return "邮箱地址无效";
        }
        //检测是否以"www."为起始
        p = Pattern.compile("^www\\.");
        m = p.matcher(email);
        if (m.find()) {
            return "邮箱地址不能以'www.'起始";
        }
        //检测是否包含非法字符
        p = Pattern.compile("[^A-Za-z0-9\\.@_-~#]+");
        m = p.matcher(email);
        if (m.find()){
            return "邮箱地址里包含有非法字符，请修改";
        }

//        StringBuffer sb = new StringBuffer();
//        boolean result = m.find();
//        boolean deletedIllegalChars = false;
//        while (result) {
//            //如果找到了非法字符那么就设下标记
//            deletedIllegalChars = true;
//            //如果里面包含非法字符如冒号双引号等，那么就把他们消去，加到SB里面
//            m.appendReplacement(sb, "");
//            result = m.find();
//        }
//        m.appendTail(sb);
//        email = sb.toString();
//        if (deletedIllegalChars) {
//            System.out.println("输入的EMAIL地址里包含有冒号、逗号等非法字符，请修改");
//            System.out.println("修改后合法的地址应类似: " + email);
//        }
        return null;
    }

    /**
     * 确认密码是否有效
     * @param password      需要检查的秘密
     * @param minLength     密码的最小长度
     * @param maxLength     密码的最大长度
     * @return              如果拼写无误就返回null
     *                      如果拼写有误就返回错误信息，可直接将错误信息反馈给用户
     */
    public static String checkPassword(String password,int minLength,int maxLength){
        String errorText;
        if (password.length()==0){
            return "密码不能为空";
        }

        boolean isValidate;
        Pattern p = Pattern.compile("[^A-Za-z0-9]+");
        Matcher m = p.matcher(password);
        if (m.find()){
            isValidate=false;
        }else {
            isValidate=true;
        }

        if (password.length()<minLength&&isValidate){
            errorText= "密码长度太短，请输入6至9位有效密码";
        }else if (password.length()>maxLength&&isValidate){
            errorText= "密码长度太长，请输入"+minLength+"至"+maxLength+"位有效密码";
        }else if (!isValidate){
            errorText= "密码里包含有非法字符，请输入"+minLength+"至"+maxLength+"位有效密码";
        }else {
            errorText=null;
        }

        return errorText;
    }

    public static String checkUserName(String userName,int minLength,int maxLength){
        if (userName==null||userName.replaceAll(" ","").length()==0) {
            return "名字不能为空";
        }
        String errorText;
        boolean isValidate;
        Pattern p = Pattern.compile("[^A-Za-z0-9\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(userName);
        if (m.find()){
            isValidate=false;
        }else {
            isValidate=true;
        }
        if (m.find()){
            return "名字内部含有非法字符，请按要求输入名字";
        }

        if (userName.length()<minLength&&isValidate){
            errorText= "名字太短，请输入6至9位有效名字";
        }else if (userName.length()>maxLength&&isValidate){
            errorText= "名字太长，请输入"+minLength+"至"+maxLength+"位有效名字";
        }else if (!isValidate){
            errorText= "名字内部含有非法字符，请输入"+minLength+"至"+maxLength+"位有效名字";
        }else {
            errorText=null;
        }


        return errorText;
    }

}
