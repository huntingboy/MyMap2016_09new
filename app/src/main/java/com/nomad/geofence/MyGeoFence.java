package com.nomad.geofence;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.sharedres.SharedRes;

import java.util.List;

/**
 * Created by nomad on 17-2-10.
 */
public class MyGeoFence implements GeoFenceListener {
    private DPoint centerPoint;
    private GeoFenceClient mGeoFenceClient;
    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";

    public MyGeoFence() {
    }

    public MyGeoFence(double latitude, double longitude, float radius) {
        /**
         * 实例化地理围栏客户端
         */
        mGeoFenceClient = new GeoFenceClient(MainActivity.getContext());
//        设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
//        public static final int GEOFENCE_IN 进入地理围栏
//        public static final int GEOFENCE_OUT 退出地理围栏
//        public static final int GEOFENCE_STAYED 停留在地理围栏内10分钟
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN | GeoFenceClient.GEOFENCE_OUT | GeoFenceClient.GEOFENCE_STAYED);
        /**
         * 根据关键字创建围栏
         */
//        mGeoFenceClient.addGeoFence("首开广场","写字楼","北京",1,"000FATE23（考勤打卡）");
        /**
         * 根据周边POI创建围栏
         */
//        创建一个中心点坐标
//        centerPoint = new DPoint();
////        设置中心点纬度经度(可以由用户传入)
//        centerPoint.setLatitude(30.567302);
//        centerPoint.setLongitude(114.913138);
////        执行添加围栏的操作
//        mGeoFenceClient.addGeoFence("肯德基","餐饮",centerPoint,1000F,10,"自有ID");
        /**
         * 创建行政区划围栏
         */
//        mGeoFenceClient.addGeoFence("海淀区","00FDTW103（在北京海淀的化妆品促销活动）");
        /**
         * 创建自定义围栏
         */
//        创建一个中心点坐标
        centerPoint = new DPoint();
//        设置中心点纬度经度(可以由用户传入)
        centerPoint.setLatitude(latitude);
        centerPoint.setLongitude(longitude);
        mGeoFenceClient.addGeoFence (centerPoint,radius,"自有业务Id");
        /**
         * 多边形围栏
         */
//        List<DPoint> points = new ArrayList<DPoint>();
//
//        points.add(new DPoint(39.992702, 116.470470));
//        points.add(new DPoint(39.994387, 116.472498));
//        points.add(new DPoint(39.994478, 116.474161));
//        points.add(new DPoint(39.993163, 116.474504));
//        points.add(new DPoint(39.991363, 116.472605));
//
//        mGeoFenceClient.addGeoFence(points,"自有业务ID");
        mGeoFenceClient.setGeoFenceListener(this);
        //创建并设置PendingIntent
        mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);


    }

    /**
     * 标出地理围栏的位置
     */
    public static void addCircle(LatLng latLng, double radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(4);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(Color.BLUE);
        SharedRes.aMap.addCircle(circleOptions);
    }

    /**
     * 接收围栏创建后的回调
     * @param list
     * @param i
     * @param s
     */
    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
            Toast.makeText(MainActivity.getContext(), "添加围栏成功!!", Toast.LENGTH_SHORT).show();
            MyGeoFence.addCircle(new LatLng(centerPoint.getLatitude(), centerPoint.getLongitude()), 1000);
            //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
        } else {
            Toast.makeText(MainActivity.getContext(), "添加围栏失败!!", Toast.LENGTH_SHORT).show();
            Log.d("Tag", "围栏创建出错码：：：：" + i + "<=============");
        }
    }

    /**
     * 自定义函数
     * 清除所有围栏(也可以清除某一个具体的围栏，需要传围栏参数)
     */
    public void clearAllFences() {
        mGeoFenceClient.removeGeoFence();
    }
}
