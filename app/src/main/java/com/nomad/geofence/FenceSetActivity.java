package com.nomad.geofence;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;

public class FenceSetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btAdd;
    private Button btDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_setting);
        btAdd = (Button) findViewById(R.id.bt_add_fence);
        btDel = (Button) findViewById(R.id.bt_del_fence);
        btAdd.setOnClickListener(this);
        btDel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add_fence:
                Toast.makeText(this, "添加围栏", Toast.LENGTH_SHORT).show();
                //map界面被隐藏的围栏设置界面设置可见
                MainActivity.lineFenceSetting.setVisibility(View.VISIBLE);
                finish();
                break;
            case R.id.bt_del_fence:
                Toast.makeText(this, "删除围栏", Toast.LENGTH_SHORT).show();
                //添加跳转到删除界面
                startActivity(new Intent(FenceSetActivity.this, FenceDeleteActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
