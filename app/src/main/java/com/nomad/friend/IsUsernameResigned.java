package com.nomad.friend;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.PreparedStatement;
import com.nomad.mymap2016_09.MainActivity;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

/**
 * Created by nomad on 17-6-22.
 * 用户是不是已经是别人的好友
 * true 是
 */
public class IsUsernameResigned implements Callable<Boolean>{

    //private final static String URL = "jdbc:mysql://192.168.1.105:3306/amap";
    private final static String URL = "jdbc:mysql://n17459068v.iask.in:14934/amap";
    private final static String USERNAME = "test1";
    private final static String PASSWORD = "test1";
    private final static String DRIVER = "com.mysql.jdbc.Driver";

    private String name;

    public IsUsernameResigned(String name) {
        this.name = name;
    }

    @Override
    public Boolean call() throws Exception {

       /* String sql = "select Uname from User where ? in (select Uname\n" +
                "from User\n" +
                "where Uname in \n" +
                "\t(select Uname \n" +
                "    from User)\n" +
                "    and Uname not in\n" +
                "    (select Ufriend\n" +
                "    from User));"; */

        String sql = "select Ufriend from User where Ufriend = ? and Uname = ?;";//用户是别人的好友

        Boolean tag = true;
        try {
            Class.forName(DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);//仅适用于参数不同，sql语句相同的批处理
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, MainActivity.getUserName());
            //preparedStatement.addBatch(sql1);
            //preparedStatement.setString(1, name);
            //preparedStatement.addBatch(sql1);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                tag = false;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            Log.d("Tag", "用户是否已经是自己的好友sql错误！");
            e.printStackTrace();
            tag = false;
        }
        if (tag) {
            Log.d("Tag", "用户已经是自己的好友！");
        } else {
            Log.d("Tag", "用户不是自己的好友！");
        }
        return tag;
    }

    /**
     * 判断数据库是否支持批处理
     **/
    public boolean supportBatch(Connection con) {
        try {
            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();
            return md.supportsBatchUpdates();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  false;
    }

    /**
     * 执行一批SQL语句
     **/
    public int[] goBatch(Connection con, String[] sqls) {
        if (sqls == null) {
            return  null;
        }
        Statement sm = null;
        try {
            sm = con.createStatement();
            for (int i = 0; i < sqls.length; i++) {
                sm.addBatch(sqls[i]);
            }
            return sm.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
