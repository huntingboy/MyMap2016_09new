package com.nomad.geofence;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.nomad.unity.Info;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-3-7.
 */
public class Delete implements Callable<Boolean> {

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private Info info;
    private String name;


    public Delete(Info info, String name) {
        this.info = info;
        this.name = name;
    }



    @Override
    public Boolean call() throws Exception {
        String sql = "delete from Info where Uname = ? and Ulatitude = ? and Ulongitude = ? and " +
                "Uradius = ?;";
        double latitude = info.getUlatitude();
        double longitude = info.getUlongitude();
        double radius = info.getUradius();
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, latitude);
            preparedStatement.setDouble(3, longitude);
            preparedStatement.setDouble(4, radius);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "删除围栏错误!请重试！");
            e.printStackTrace();
            tag = false;
        }
        Log.d("Tag", "删除围栏信息成功！");

        return tag;
    }
}
