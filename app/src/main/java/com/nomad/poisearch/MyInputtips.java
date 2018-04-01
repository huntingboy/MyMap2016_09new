package com.nomad.poisearch;

import android.content.Context;

import com.amap.api.services.help.*;
import com.amap.api.services.help.Inputtips;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.sharedres.SharedRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nomad on 17-1-23.
 */
public class MyInputtips implements Inputtips.InputtipsListener {

    private Inputtips inputtips;
    private InputtipsQuery inputtipsQuery;

    public MyInputtips(String keyWord) {
        inputtipsQuery = new InputtipsQuery(keyWord, null);
        inputtipsQuery.setCityLimit(true);

        inputtips = new Inputtips(MainActivity.getContext(), inputtipsQuery);
        inputtips.setInputtipsListener(this);
        inputtips.requestInputtipsAsyn();
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (i == 1000) {
            SharedRes.arrayList = new ArrayList<HashMap<String, String>>();
            for (int j = 0; j < list.size(); j++) {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("name", list.get(j).getName());
                hashMap.put("address", list.get(j).getAddress());
                SharedRes.arrayList.add(hashMap);
            }
            MainActivity.showPoi();
        }
    }
}
