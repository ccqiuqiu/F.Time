package com.ccqiuqiu.ftime.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccqiuqiu.ftime.Activity.MainActivity;
import com.ccqiuqiu.ftime.Utils.SoftKeyboardUtil;
import com.rey.material.widget.FloatingActionButton;

import org.xutils.x;

/**
 * Created by cc on 2015/12/17.
 */
public class BaseFragment extends Fragment {

    public MainActivity mMainActivity;

    private boolean injected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;
        mMainActivity = (MainActivity) getActivity();
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
    }

    public void onFabClick(FloatingActionButton mFab) {
    }

    public void observeSoftKeyboard() {
        SoftKeyboardUtil.observeSoftKeyboard(getActivity(),null);
    }
}
