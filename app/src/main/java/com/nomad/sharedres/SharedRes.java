package com.nomad.sharedres;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 被共享的变量，设置为静态，可被直接访问。
 * Created by nomad on 17-1-16.
 */
public class SharedRes {
    public static AMap aMap;
    public static Marker marker;
    //POI搜索中指定返回结果 第几页
    public static int currentPage = -1;
    //向MainActivity中的listview添加数据
    public static ArrayList<HashMap<String, String>> arrayList;
    //向FenceDeleteActivity中的listview添加数据
    public static ArrayList<HashMap<String, Object>> fenceList;

}
