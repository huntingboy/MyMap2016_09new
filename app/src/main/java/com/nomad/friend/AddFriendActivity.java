package com.nomad.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nomad.login.IsPasswordValid;
import com.nomad.login.IsUserExist;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;
import com.nomad.unity.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AddFriendActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button btAck;
    private Button btCan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        username = (EditText) findViewById(R.id.edit_username);
        password = (EditText) findViewById(R.id.edit_password);
        btAck = (Button) findViewById(R.id.bt_add_friend);
        btCan = (Button) findViewById(R.id.bt_can_friend);
        btAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                if (TextUtils.isEmpty(username.getText().toString().trim())) {
                    username.setError("不能为空");
                    focusView = username;
                    focusView.requestFocus();
                } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
                    password.setError("不能为空");
                    focusView = password;
                    focusView.requestFocus();
                } else if (!isUsernameValid(username.getText().toString().trim())) {
                    username.setError("用户无效");
                    focusView = username;
                    focusView.requestFocus();
                } else if (!isPasswordValid(password.getText().toString().trim())) {
                    //用户名有效，就判断是否密码正确
                    //不正确
                    password.setError("密码错误");
                    focusView = password;
                    focusView.requestFocus();
                } else {
                    //User表更新数据，提示加好友成功
                    //todo 同时更新当前用户和好友记录
                    new UpdateFriend(MainActivity.getUserName(), username.getText().toString().trim());
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    Future<Boolean> future = executorService.submit(new UpdateFriend(MainActivity.getUserName(), username.getText().toString().trim()));
                    Log.d("Tag", "用户名：" + MainActivity.getUserName());
                    boolean tag = false;
                    try {
                        tag = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.d("Tag", "更新数据库User好友信息出错！！！");
                        e.printStackTrace();
                    }
                    Log.d("Tag", "更新数据库User好友信息" + tag);
                    if (tag) {
                        Toast.makeText(AddFriendActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddFriendActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        btCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                password.setText("");
            }
        });
    }

    public boolean isUsernameValid(String uName) {
        if (uName.equals("0")) {
            return false;
        }
        boolean flag = false;
        if (!uName.equals(MainActivity.getUserName())) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<Boolean>[] futures = new Future[2];
            futures[0] = executorService.submit(new IsUsernameResigned(uName));//是别人的好友true
            futures[1] = executorService.submit(new IsUserExist(uName));//存在true
            try {
                if (futures[1].get()) {
                    flag = !(futures[0].get());
                } else {
                    Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
                }
                Log.d("Tag", "flag===>" + flag);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Tag", "好友名不可以是自己");
        }
        return flag;
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Boolean> future = executorService.submit(new IsPasswordValid(new User(username.getText().toString().trim(), password)));
        Boolean tag = false;
        try {
            tag = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return tag;
//        return password.length() > 4;
    }

}
