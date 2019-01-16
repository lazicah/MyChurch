package com.disciplesbay.latterhousehq.mychurch.helper;

import android.support.v4.view.ViewPager;

import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.BannerItem;
import com.flyco.banner.transform.DepthTransformer;
import com.flyco.banner.transform.FadeSlideTransformer;
import com.flyco.banner.transform.FlowTransformer;
import com.flyco.banner.transform.RotateDownTransformer;
import com.flyco.banner.transform.RotateUpTransformer;
import com.flyco.banner.transform.ZoomOutSlideTransformer;


import java.util.ArrayList;

public class DataProvider {
    public static String[] titles = new String[]{
            "The Cross",
            "Yes He Loves You!",
            "Come dine with the Lord",
            "He's Word",

    };

    public static String[] times = new String[]{
            "2018-07-14 00:22:28",
            "2018-07-14 00:22:28",
            "2018-07-14 00:22:28",
            "2018-07-14 00:22:28",

    };

    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "https://images.unsplash.com/photo-1509221292074-fca34c749cb0?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=fa0f2b02ec2e9445f4371a2e4e3ee848&auto=format&fit=crop&w=500&q=60",//伪装者:胡歌演绎"痞子特工"
            "https://images.unsplash.com/photo-1536126750180-3c7d59643f99?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c8e7c3ceb2852c61b79f7d726f42d2b4&auto=format&fit=crop&w=500&q=60",//无心法师:生死离别!月牙遭虐杀
            "https://images.unsplash.com/photo-1519773250401-ecb76e52506e?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f30b31f890cc8063e54222d6d35f4c50&auto=format&fit=crop&w=500&q=60",//花千骨:尊上沦为花千骨
            "https://images.unsplash.com/photo-1524492514790-8310bf594ea4?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c8d6fc5571a1b66d1b5a4e0161609fbe&auto=format&fit=crop&w=500&q=60",//综艺饭:胖轩偷看夏天洗澡掀波澜
              };

    public static ArrayList<BannerItem> getList() {
        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];
            item.time = times[i];

            list.add(item);
        }

        return list;
    }



    public static Class<? extends ViewPager.PageTransformer> transformers[] = new Class[]{
            DepthTransformer.class,
            FadeSlideTransformer.class,
            FlowTransformer.class,
            RotateDownTransformer.class,
            RotateUpTransformer.class,
            ZoomOutSlideTransformer.class,
    };
}
