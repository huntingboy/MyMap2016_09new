package com.nomad.login;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.nomad.unity.User;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-3-4.
 */
public class IsPasswordValid implements Callable<Boolean> {

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";

    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private User user;


    public IsPasswordValid(User user) {
        this.user = user;
    }



    @Override
    public Boolean call() throws Exception {
        String sql = "select Upassword from User where Uname = ? and Upassword = ?";
        String name = user.getUname();
        String password = user.getUpassword();
        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                Log.d("Tag", "用户不存在/密码错误！");
                tag = false;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            Log.d("Tag", "错误：用户不存在/密码错误！");
            e.printStackTrace();
            tag = false;
        } catch (ClassNotFoundException e) {
            Log.d("Tag", "错误：用户不存在/密码错误！");
            e.printStackTrace();
            tag = false;
        }
        if(tag)
	    Log.d("Tag", "用户存在&密码正确！");
        return tag;
    }
}
