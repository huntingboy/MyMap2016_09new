package com.nomad.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.nomad.location.SelectTrace;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;
import com.nomad.sharedres.SharedRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SelectFriendActivity extends AppCompatActivity {

    private ListView lvFriend;  //好友列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        lvFriend = (ListView) findViewById(R.id.lv_friends);

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ArrayList<HashMap<String, Object>>> future = executorService.submit(new SelectFriend(MainActivity.getUserName()));
        Log.d("Tag", "用户名：" + MainActivity.getUserName());
        ArrayList<HashMap<String, Object>> friUsernames = new ArrayList<>();
        try {
            friUsernames = future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("Tag", "更新数据库User好友信息出错！！！");
            e.printStackTrace();
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                friUsernames,
                R.layout.friend_item,
                new String[]{"FriUsername"},
                new int[]{R.id.tv_friend});
        lvFriend.setAdapter(simpleAdapter);
        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>) lvFriend.getItemAtPosition(i);
                if (!hashMap.equals(null)) {
                    //把用户好友的用户名传到下一个activity
                    //在下一个activity里面显示“围栏信息”、“个人轨迹”两个button
                    String friName = (String) hashMap.get("FriUsername");
                    Intent intent = new Intent(SelectFriendActivity.this, FriendInfoActivity.class);
                    intent.putExtra("FriUsername", friName);
                    startActivity(intent);
                    //finish();
                }
            }
        });
    }
}
