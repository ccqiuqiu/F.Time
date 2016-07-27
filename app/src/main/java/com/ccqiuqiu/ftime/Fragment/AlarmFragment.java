package com.ccqiuqiu.ftime.Fragment;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ccqiuqiu.ftime.Adapter.AlarmAdapter;
import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Service.AlarmService;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.FloatingActionButton;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
@ContentView(R.layout.fragment_alarm)
public class AlarmFragment extends BaseFragment {

    @ViewInject(R.id.recyclerView)
    private android.support.v7.widget.RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private RecyclerView.Adapter mAdapter;
    private List<Alarm> mItems;
    private boolean mDel = true;
    private Alarm mAlarmDel;
    private Snackbar snackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCanSwipe = getArguments().getBoolean(ARG_CAN_SWIPE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        mItems = App.getAlarmService().getAll();

        mLayoutManager = new LinearLayoutManager(getContext());

        //触摸管理器，用于抑制条目拖动的时候页面滚动
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        //拖动管理器
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        //设置拖动时候的阴影
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shadow));
        //开启长按拖动
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        //长按延时
        mRecyclerViewDragDropManager.setLongPressTimeout(250);

        //滑动管理器
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        initAdapter();
        final AlarmAdapter myItemAdapter = new AlarmAdapter(mItems);
        myItemAdapter.setEventListener(new AlarmAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {
                notifyItemRemoved(position);
            }

            @Override
            public void onItemClick(int position) {
                //调到修改页面
                //mMainActivity.mMainFragment.mAddAlarmFragment.mEditAlarm = mItems.get(position);
                mMainActivity.mMainFragment.mViewPager.setCurrentItem(0);
                mMainActivity.mMainFragment.mAddAlarmFragment.editAlarm(mItems.get(position), position);
            }

            @Override
            public void onMoveItem(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition,toPosition);
            }

            @Override
            public void onItemSkip(final int position) {
//                SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
//                    @Override
//                    public void onPositiveActionClicked(DialogFragment fragment) {
//                        super.onPositiveActionClicked(fragment);
//                        notifyItemSkip(position);
//                    }
//
//                    @Override
//                    public void onNegativeActionClicked(DialogFragment fragment) {
//                        super.onNegativeActionClicked(fragment);
//                    }
//                };
//
//                builder.message(getString(R.string.skip_alarm_con))
//                        .title(getString(R.string.skip_alarm))
//                        .positiveAction(getString(R.string.commit))
//                        .negativeAction(getString(R.string.cancel));
//                DialogFragment fragment = DialogFragment.newInstance(builder);
//                fragment.show(getFragmentManager(), null);
                new MaterialDialog.Builder(getContext())
                        .title(R.string.skip_alarm)
                        .content(getString(R.string.skip_alarm_con))
                        .positiveText(R.string.commit)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                notifyItemSkip(position);
                            }
                        }).show();
            }

            @Override
            public void onItemToggOpen(int position) {
                Alarm alarm = mItems.get(position);
                App.getAlarmService().updateAlarm(alarm);
                if (alarm.getIsOpen()) {
                    String s;
                    int[] re = DateUtils.getInterval(alarm.getNextDate(), System.currentTimeMillis());
                    StringBuffer sb = new StringBuffer();
                    if (re[0] != 0) {
                        sb.append(re[0] + getString(R.string.day2));
                    }
                    if (re[1] != 0) {
                        sb.append(re[1] + getString(R.string.hours));
                    }
                    if (re[2] != 0) {
                        sb.append(re[2] + getString(R.string.minutes));
                    }
                    if (re[3] != 0) {
                        sb.append(re[3] + getString(R.string.second));
                    }
                    s = getString(R.string.add_alarm_success_0) + sb.toString() + getString(R.string.add_alarm_success_1);
                    ViewUtils.toast(s);
                }
            }
        });

        mAdapter = myItemAdapter;
        //包装拖动功能
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);
        //包装滑动功能
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);

        //条目动画
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);

        //设置 RecyclerView的参数
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(true);

        //初始化触摸，拖动，滑动管理器，顺序必须是  触摸》滑动 》拖动
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mItems.size() > 1) {
                    if (dy > 10) {
                        mMainActivity.hideFab();
                    }
                    if (dy < -10) {
                        mMainActivity.showFab();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initAdapter() {

    }

    public static AlarmFragment newInstance(boolean canSwipe) {
        AlarmFragment fragment = new AlarmFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_CAN_SWIPE, canSwipe);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFabClick(FloatingActionButton mFab) {
        //mMainActivity.mMainFragment.mAddAlarmFragment.mEditAlarm = null;
        if(snackbar != null){
            snackbar.dismiss();
        }
        mMainActivity.mMainFragment.mViewPager.setCurrentItem(0);
        mMainActivity.mMainFragment.mAddAlarmFragment.initViewDate();
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    public void notifyItemInserted(Alarm alarm) {
        mItems.add(0, alarm);
        mAdapter.notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }

    private void notifyItemSkip(int position) {
        Alarm alarm = mItems.get(position);
        if(alarm.getType() ==  App.ALARM_TYPE_ONE){
            alarm.setIsOpen(false);
        }else{
            //将时间设置到闹钟时间之前一点点
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(alarm.getNextDate());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.getTime().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(alarm.getTime().substring(3, 5)));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND,0);
            calendar.add(Calendar.HOUR_OF_DAY,-1);
            //
            alarm.setNextDate(App.getAlarmService().getNextDate(alarm,calendar.getTime(),true));
        }
        App.getAlarmService().updateAlarm(alarm);
        mAdapter.notifyItemChanged(position);
    }
    public void notifyItemChanged(Alarm alarm, int position) {
        mItems.remove(position);
        mItems.add(position, alarm);
        mAdapter.notifyItemChanged(position);
        //mRecyclerView.scrollToPosition(position);
    }

    public void notifyItemRemoved(final int position) {
        mDel = true;
        mAlarmDel = mItems.remove(position);
        mAdapter.notifyItemRemoved(position);
        App.getAlarmService().del(mAlarmDel);

        snackbar = ViewUtils.snackbar(mMainActivity.mMainFragment.mFab, getString(R.string.del_qr), getString(R.string.reset),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.getAlarmService().saveAlarm_undo(mAlarmDel);
                        mItems.add(position, mAlarmDel);
                        mAdapter.notifyItemInserted(position);

                        //设置系统闹钟
                        if (mAlarmDel.getIsOpen()) {
                            AlarmUtils.setAlarm(mAlarmDel);
                        }
                    }
                });
    }

    private void notifyItemMoved(int fromPosition, int toPosition) {
        final Alarm item = mItems.remove(fromPosition);
        mItems.add(toPosition, item);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        App.getAlarmService().move(mItems, fromPosition, toPosition);
    }

    public void reload() {
        mItems.clear();
        mItems = App.getAlarmService().getAll();
        ((AlarmAdapter)mAdapter).mItems = mItems;
        mAdapter.notifyDataSetChanged();
    }
}
