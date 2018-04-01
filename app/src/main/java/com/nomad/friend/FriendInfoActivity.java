package com.nomad.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nomad.mymap2016_09.R;

public class FriendInfoActivity extends AppCompatActivity {
  //  private String friName;
    private Button btFence;
    private Button btTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        //得到好友名信息
       // friName = this.getIntent().getStringExtra("FriUsername");
        btFence = (Button) findViewById(R.id.bt_friend_fence);
        btTrace = (Button) findViewById(R.id.bt_friend_trace);
        btFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(FriendInfoActivity.this, FriendFenceInfoActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        btTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(FriendInfoActivity.this, FriendTraceInfoActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }
}
