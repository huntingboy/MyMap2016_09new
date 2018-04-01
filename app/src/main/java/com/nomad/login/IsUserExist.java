package com.nomad.login;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-3-4.
 */
public class IsUserExist implements Callable<Boolean> {
    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private String name;

    public IsUserExist(String name) {
        this.name = name;
    }




    @Override
    public Boolean call() throws Exception {
        String sql = "select Uname from User where Uname = ?";
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                Log.d("Tag", "用户不存在！");
                tag = false;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            Log.d("Tag", "错误：用户不存在！");
            e.printStackTrace();
            tag = false;
        } catch (ClassNotFoundException e) {
            Log.d("Tag", "错误：用户不存在！");
            e.printStackTrace();
            tag = false;
        }
        if (tag) {
            Log.d("Tag", "用户存在！");
        }
        return tag;
    }
}
