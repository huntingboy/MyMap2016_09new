package com.nomad.geofence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;
import com.nomad.customview.popuplist.PopupList;
import com.nomad.sharedres.SharedRes;
import com.nomad.unity.Info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FenceDeleteActivity extends AppCompatActivity {

    private ListView listview;
//    private double latitude;
//    private double longitude;
//    private double radius;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_delete);

        if (selectFence() && MainActivity.isFlagSignIn()) {
            //TODO 加上逆地理编码，将经纬度坐标转换为地理名，然后显示在listview上
            //bug 地址为Null
            listview = (ListView) findViewById(R.id.lv_fence_del);
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    SharedRes.fenceList,
                    R.layout.fence_item,
                    new String[]{"Date", "Latitude", "Longitude", "Radius", "Address"},
                    new int[]{R.id.itemDat, R.id.itemLat, R.id.itemLon, R.id.itemRad, R.id.itemAddr});
            listview.setAdapter(simpleAdapter);
           // listview.setBackgroundColor(Color.RED);
            //单击跳转在地图上显示围栏
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) listview.getItemAtPosition(i);
                    if (!hashMap.equals(null)) {
                        double latitude = (double) hashMap.get("Latitude");
                        double longitude = (double) hashMap.get("Longitude");
                        double radius = (double) hashMap.get("Radius");
                        //todo addCircle没有反应
                        SharedRes.aMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(radius));
                        Log.d("Tag", "跳转到地图上面显示围栏！");
//                        Intent intent = new Intent(FenceDeleteActivity.this, MainActivity.class);
//                        //intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        Toast.makeText(FenceDeleteActivity.this, "围栏已经显示到了地图上面", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

            List<String> popupMenuItemList = new ArrayList<>();
            popupMenuItemList.add("删除");
            PopupList popupList = new PopupList();
            popupList.init(this, listview, popupMenuItemList, new PopupList.OnPopupListClickListener() {
                @Override
                public void onPopupListClick(View contextView, int contextPosition, int position) {

                    HashMap<String, Object> hashMap = SharedRes.fenceList.get(contextPosition);
                    double latitude = (double) hashMap.get("Latitude");
                    double longitude = (double) hashMap.get("Longitude");
                    double radius = (double) hashMap.get("Radius");
                    String address = (String) hashMap.get("Address");
                    Toast.makeText(FenceDeleteActivity.this, contextPosition + ", " + position, Toast.LENGTH_SHORT).show();
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    Future<Boolean> future = executorService.submit(new Delete(new Info(new Date(),
                            latitude,
                            longitude,
                            radius,
                            address),
                            MainActivity.getUserName()));
                    Boolean tag = false;
                    try {
                        tag = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (tag) {
                        Toast.makeText(FenceDeleteActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FenceDeleteActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ImageView indicator = new ImageView(this);
            indicator.setImageResource(android.R.drawable.arrow_down_float);
            popupList.setIndicatorView(indicator);
            popupList.setIndicatorSize(popupList.dp2px(16), popupList.dp2px(8));
        } else {
            Toast.makeText(this, "请先登录/删除失败！", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public boolean selectFence() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Boolean> future = executorService.submit(new Select(MainActivity.getUserName()));
        Boolean tag = false;
        Log.d("Tag", "用户名：" + MainActivity.getUserName());
        try {
            tag = future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("Tag", "出错！！！");
            e.printStackTrace();
        }
        return tag;
    }
}
