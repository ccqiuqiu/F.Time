/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ccqiuqiu.ftime.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.ccqiuqiu.ftime.Fragment.BaseFragment;
import com.ccqiuqiu.ftime.Activity.MainActivity;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private MainActivity mMainActivity;
    private List<BaseFragment> mFragments;

    public MyPagerAdapter(MainActivity mainActivity, List<BaseFragment> fragments) {
        super(mainActivity.getSupportFragmentManager());
        mMainActivity = mainActivity;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
////        String title = "";
////        switch (position) {
////            case 0:
////                title = "haha";
////                break;
////            case 1:
////                title = "heihei";
////        }
//
//        Drawable dImage = mMainActivity.getResources().getDrawable(R.drawable.ic_add);
//        dImage.setBounds(0, 0, dImage.getIntrinsicWidth(), dImage.getIntrinsicHeight());
//        //这里前面加的空格就是为图片显示
//        SpannableString sp = new SpannableString("  " + "haha");
//        ImageSpan imageSpan = new ImageSpan(dImage, ImageSpan.ALIGN_BOTTOM);
//        sp.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sp;
//    }
}
