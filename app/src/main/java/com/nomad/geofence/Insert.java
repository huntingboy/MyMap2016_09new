package com.nomad.geofence;

/**
 * Created by nomad on 17-3-7.
 */

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.nomad.unity.Info;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;


/**
 * Created by nomad on 17-3-4.
 */
public class Insert implements Callable<Boolean> {

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private Info info;
    private String name;


    public Insert(Info info, String name) {
        this.info = info;
        this.name = name;
    }



    @Override
    public Boolean call() throws Exception {
        //使用SQL自带函数获取时间NOW()， CURRENT_TIMESTAMP()
        String sql = "insert into Info values(NOW(), ?, ?, ?, ?, ?);";
        double latitude = info.getUlatitude();
        double longitude = info.getUlongitude();
        double radius = info.getUradius();
        String address = info.getUaddress();
        //Date date1 = info.getUdate();
        //date输出带年月日，但是插入没有年月日，好像被preparedStatement过滤了
        //java.sql.Date date = new java.sql.Date(date1.getTime());
        //Log.d("Tag", "======date" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")被SimpleDateFormat.getDateTimeInstance()替代
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            //使用date没有效果
            //preparedStatement.setDate(1, date);
            //或者使用下面代码
            //preparedStatement.setTimestamp(1, new Timestamp(date1.getTime()));
            //注意？占位符，从1开始
            preparedStatement.setDouble(1, latitude);
            preparedStatement.setDouble(2, longitude);
            preparedStatement.setDouble(3, radius);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, name);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "错误!请重试！");
            e.printStackTrace();
            tag = false;
        }
        Log.d("Tag", "插入围栏信息成功！");

        return tag;
    }
}

