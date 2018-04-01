package com.nomad.friend;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-6-22.
 * 实际是插入操作，一个用户可以有多个好友。
 */
public class UpdateFriend implements Callable<Boolean> {
    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private String curUsername;  //当前登录用户名
    private String friUsername;  //好友用户名
   // private String password;

    public UpdateFriend(String curUsername, String friUsername) {

        this.curUsername = curUsername;
        this.friUsername = friUsername;
    //    this.password = password;
    }

    @Override
    public Boolean call() throws Exception {
        String sql = "insert into User values(?, null, ?)";
        Boolean flag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            //此处可以用preparedStatement批处理，因为SQL语句一样，只是参数不同
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, friUsername);
            preparedStatement.setString(2, curUsername);
            preparedStatement.addBatch();
            preparedStatement.setString(1, curUsername);
            preparedStatement.setString(2, friUsername);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("Tag", "更新朋友信息错误!请重试！");
            e.printStackTrace();
            flag = false;
        }
        if (flag) {
            Log.d("Tag", "更新朋友信息成功！");
        }
        return flag;

    }
}
