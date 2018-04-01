package com.nomad.friend;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-6-22.
 */
public class SelectFriendFence implements Callable<ArrayList<HashMap<String, Object>>> {
    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private String username;
    private ArrayList<HashMap<String, Object>> friendFences;
    public SelectFriendFence(String username) {
        this.username = username;
    }

    @Override
    public ArrayList<HashMap<String, Object>> call() throws Exception {
        String sql = "select * from Info where Uname = ? order by Udate desc;";
        friendFences = new ArrayList<>();
        boolean flag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("Date", SimpleDateFormat.getDateTimeInstance().format(resultSet.getTimestamp(1)));
                hashMap.put("Latitude", resultSet.getDouble(2));
                hashMap.put("Longitude", resultSet.getDouble(3));
                hashMap.put("Radius", resultSet.getDouble(4));
                hashMap.put("Address", resultSet.getString(5));

                friendFences.add(hashMap);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "查询好友围栏信息错误!请重试！");
            e.printStackTrace();
            flag = false;
        }
        if (flag) {
            Log.d("Tag", "查询好友围栏信息成功！");
            return friendFences;
        }

        return null;
    }
}
