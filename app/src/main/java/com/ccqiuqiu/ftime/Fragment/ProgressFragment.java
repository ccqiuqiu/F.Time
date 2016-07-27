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
import com.ccqiuqiu.ftime.Adapter.ProgressAdapter;
import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.rey.material.widget.FloatingActionButton;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
@ContentView(R.layout.fragment_alarm)
public class ProgressFragment extends BaseFragment {

    @ViewInject(R.id.recyclerView)
    private RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private RecyclerView.Adapter mAdapter;
    private List<Progress> mItems;
    private boolean mDel = true;
    private Progress mProgressDel;
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

        mItems = App.getProgressService().getAll();

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
        final ProgressAdapter myItemAdapter = new ProgressAdapter(mItems);
        myItemAdapter.setEventListener(new ProgressAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {
                notifyItemRemoved(position);
            }

            @Override
            public void onItemClick(int position) {
                mMainActivity.mMainFragment.mViewPager.setCurrentItem(3);
                mMainActivity.mMainFragment.mAddProgressFragment.editProgress(mItems.get(position), position);
            }

            @Override
            public void onMoveItem(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemSkip(final int position) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.skip_progress)
                        .content(getString(R.string.skip_progress_con))
                        .positiveText(R.string.commit)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                notifyItemSkip(position);
                            }
                        }).show();
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

    public static ProgressFragment newInstance() {
        ProgressFragment fragment = new ProgressFragment();
        return fragment;
    }

    @Override
    public void onFabClick(FloatingActionButton mFab) {
        if(snackbar != null){
            snackbar.dismiss();
        }
        mMainActivity.mMainFragment.mViewPager.setCurrentItem(3);
        mMainActivity.mMainFragment.mAddProgressFragment.initViewDate();
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

    public void notifyItemInserted(Progress progress) {
        mItems.add(0, progress);
        mAdapter.notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }

    public void notifyItemChanged(Progress progress, int position) {
        mItems.remove(position);
        mItems.add(position, progress);
        mAdapter.notifyItemChanged(position);
    }

    private void notifyItemSkip(int position) {
        Progress progress = mItems.get(position);
        long time = progress.getNextDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //跳过一期
        calendar.add(Calendar.MONTH, 1);
        progress.setNextDate(calendar.getTimeInMillis());
        App.getProgressService().update(progress);
        AlarmUtils.setAlarm(progress);
        mAdapter.notifyItemChanged(position);

    }

    public void notifyItemRemoved(final int position) {
        mDel = true;
        mProgressDel = mItems.remove(position);
        mAdapter.notifyItemRemoved(position);
        App.getProgressService().del(mProgressDel);

        snackbar = ViewUtils.snackbar(mMainActivity.mMainFragment.mFab, getString(R.string.del_progress_qr), getString(R.string.reset),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.getProgressService().saveProgress_undo(mProgressDel);
                        mItems.add(position, mProgressDel);
                        mAdapter.notifyItemInserted(position);
                    }
                });
    }

    private void notifyItemMoved(int fromPosition, int toPosition) {
        final Progress item = mItems.remove(fromPosition);
        mItems.add(toPosition, item);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        App.getProgressService().move(mItems, fromPosition, toPosition);
    }
}
