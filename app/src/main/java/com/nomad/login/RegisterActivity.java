package com.nomad.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;
import com.nomad.unity.User;
import com.nomad.web.WebService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private Button cancalButton;
    private EditText nameEdit;
    private EditText passwordEdit;
    private UserLoginTask mAuthTask = null;
    // 创建等待框
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerButton = (Button) findViewById(R.id.bt_register_ack);
        cancalButton = (Button) findViewById(R.id.bt_register_can);
        nameEdit = (EditText) findViewById(R.id.edit_register_name);
        passwordEdit = (EditText) findViewById(R.id.edit_register_password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptReg();
            }
        });
        cancalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void attemptReg() {
        if (!checkNetwork()) {
            Toast toast = Toast.makeText(RegisterActivity.this,"网络未连接", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        if (mAuthTask != null) {
            Log.d("Tag", "异步任务对象不为空！");
            return;
        }

        // Reset errors.
        nameEdit.setError(null);
        passwordEdit.setError(null);

        View focusView = null;
        boolean cancel = false;
        String name = nameEdit.getText().toString().trim();
        String mPassword = passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameEdit.setError(getString(R.string.error_field_required));
            focusView = nameEdit;
            cancel = true;
        } else if (TextUtils.isEmpty(mPassword)) {
            passwordEdit.setError(getString(R.string.error_field_required));
            focusView = passwordEdit;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(name, mPassword);
            mAuthTask.execute((Void) null);
            Log.d("Tag", "异步任务对象开始执行任务！");
        }
    }

    // 检测网络
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 提示框
            dialog = new ProgressDialog(RegisterActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("正在注册，请稍后...");
            dialog.setCancelable(false);
            dialog.show();
            Log.d("Tag", "onPreExecute！");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String info = WebService.executeHttpGet(mEmail, mPassword, 2);
            Log.d("Tag", "doInBackground！");
            if (info != null) {
                if (info.equals("success")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            Log.d("Tag", "onPostExecute！");
            dialog.dismiss();
            if (success) {
                MainActivity.showAccount(mEmail);
                Toast.makeText(MainActivity.getContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(MainActivity.getContext(), "登录失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
