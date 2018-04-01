package com.nomad.geofence;

/**
 * Created by nomad on 17-3-7.
 */

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.nomad.geocode.GeoCoding;
import com.nomad.sharedres.SharedRes;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;


/**
 * Created by nomad on 17-3-7.
 */
public class Select implements Callable<Boolean> {

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private String name;


    public Select(String name) {
        this.name = name;
    }



    @Override
    public Boolean call() throws Exception {
        String sql = "select * from Info where Uname = ? order by Udate desc;";
        Boolean tag = true;
        SharedRes.fenceList = new ArrayList<>();

        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("Date", SimpleDateFormat.getDateTimeInstance().format(resultSet.getTimestamp(1)));
                hashMap.put("Latitude", resultSet.getDouble(2));
                hashMap.put("Longitude", resultSet.getDouble(3));
                hashMap.put("Radius", resultSet.getDouble(4));
                //将地址存储到hashmap中,虽然对地址的解析是异步回调方法中执行的，但是在该方法中同时又更新了数据库地址，所以最后该地址一定不为空
                hashMap.put("Address", resultSet.getString(5));

                Log.d("Tag", " " + SimpleDateFormat.getDateTimeInstance().format(resultSet.getTimestamp(1)));
                Log.d("Tag", " " + resultSet.getTimestamp(1));
                Log.d("Tag", " " + resultSet.getDate(1));
                Log.d("Tag", " " + resultSet.getDouble(2));
                Log.d("Tag", " " + resultSet.getDouble(3));
                Log.d("Tag", " " + resultSet.getDouble(4));
                Log.d("Tag", " " + resultSet.getString(5));

                SharedRes.fenceList.add(hashMap);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "查询围栏信息错误!请重试！");
            e.printStackTrace();
            tag = false;
        }
        if (tag) {
            Log.d("Tag", "查询围栏信息成功！");
        }
        return tag;
    }
}

