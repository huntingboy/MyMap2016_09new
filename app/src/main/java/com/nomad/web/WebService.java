package com.nomad.web;

/**
 * Created by nomad on 17-6-29.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService {

    //private static String IP = "n17459068v.iask.in:14934";
    private static String IP = "192.168.1.105:8080";

    // 通过Get方式获取HTTP服务器数据  i=1:登录 2:注册
    public static String executeHttpGet(String username, String password, int i) {

        HttpURLConnection conn = null;
        InputStream is = null;
        String servlet = null;

        try {
            // 用户名 密码
            // URL 地址
            if (i == 1) {
                servlet = "LogLet";
            } else if (i == 2) {
                servlet = "RegLet";
            }
            String path = "http://" + IP + "/mapserver/" + servlet;
            path = path + "?username=" + username + "&password=" + password + ";";

            System.out.print("path" + path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000); // 设置超时时间
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                System.out.print("conn.getResponseCode() == 200");
                return parseInfo(is);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            System.out.print("意外退出时进行连接关闭保护");
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }



    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        System.out.print("response====>" + new String(data, "UTF-8"));
        return new String(data, "UTF-8");
    }

    // 将输入流转化为byte型
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
