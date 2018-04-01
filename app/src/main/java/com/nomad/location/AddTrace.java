package com.nomad.location;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-6-21.
 * 给Trace表插值，用户到过的位置，时间
 */
public class AddTrace implements Callable<Boolean> {
    private String uName;
    //private Date datetime;
    private double uLatitude;
    private double uLongitude;
    private String uAddress;

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    public AddTrace(String uName, double uLatitude, double uLongitude, String uAddress) {

        this.uName = uName;
        this.uLatitude = uLatitude;
        this.uLongitude = uLongitude;
        this.uAddress = uAddress;
    }

    @Override
    public Boolean call() throws Exception {
        String sql = "insert into Trace values(?, NOW(), ?, ?, ?);";
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, uName);
            preparedStatement.setDouble(2, uLatitude);
            preparedStatement.setDouble(3, uLongitude);
            preparedStatement.setString(4, uAddress);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "插入定位信息错误!请重试！");
            e.printStackTrace();
            tag = false;
        }
        if (tag) {
            Log.d("Tag", "插入定位信息成功！");
        }

        return tag;
    }
}
