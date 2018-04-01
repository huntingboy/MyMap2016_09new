package com.nomad.geofence;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-6-21.
 */
public class UpdateAddress implements Callable<Boolean> {
    private String uAddress;
    private double uLatitude;
    private double uLongitude;

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    public UpdateAddress(String addressName, double latitude, double longitude) {
        uAddress = addressName;
        uLatitude = latitude;
        uLongitude = longitude;
    }


    @Override
    public Boolean call() throws Exception {
        String sql = "update Info set Uaddress = ? where Ulatitude = ? and Ulongitude = ?;";
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, uAddress);
            preparedStatement.setDouble(2, uLatitude);
            preparedStatement.setDouble(3, uLongitude);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "更新围栏地址错误!请重试！");
            e.printStackTrace();
            tag = false;
        }
        if (tag) {
            Log.d("Tag", "更新围栏地址信息成功！" + uAddress);
        }
        return tag;
    }
}
