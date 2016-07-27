package com.ccqiuqiu.ftime.Adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.rey.material.widget.Button;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder>
        implements SwipeableItemAdapter<AlarmAdapter.MyViewHolder>
        , DraggableItemAdapter<AlarmAdapter.MyViewHolder> {

    public List<Alarm> mItems;
    private EventListener mEventListener;

    public AlarmAdapter(List<Alarm> items) {
        this.mItems = items;
        setHasStableIds(true);
    }

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemClick(int position);

        void onMoveItem(int fromPosition, int toPosition);

        void onItemSkip(int position);

        void onItemToggOpen(int position);
    }

    protected class MyViewHolder extends AbstractDraggableSwipeableItemViewHolder {

        ImageView mDelImg;
        CardView mCardView;
        TextView mTvNextDate, mTitle, mTime, mXunhuan;
        FrameLayout mContainer;
        Switch mOpen;
        View mDragHandle;
        com.rey.material.widget.Button mSkipBtn;

        public MyViewHolder(View v) {
            super(v);
            mDelImg = (ImageView) v.findViewById(R.id.img_del);
            mCardView = (CardView) v.findViewById(R.id.cardView);
            mTitle = (TextView) v.findViewById(R.id.title);
            mTvNextDate = (TextView) v.findViewById(R.id.tv_next_date);
            mTime = (TextView) v.findViewById(R.id.time);
            mXunhuan = (TextView) v.findViewById(R.id.xunhuan);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mSkipBtn = (Button) v.findViewById(R.id.btn_skip);
            mOpen = (Switch) v.findViewById(R.id.open);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_alarm, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Alarm alarm = mItems.get(position);
        final Resources resources = holder.itemView.getResources();

        if (TextUtils.isEmpty(alarm.getName())) {
            holder.mTitle.setText(resources.getString(R.string.alarm));
        } else {
            holder.mTitle.setText(alarm.getName());
        }
        holder.mTime.setText(alarm.getTime());

        if (alarm.getIsOpen()) {
            holder.mOpen.setChecked(true);
            holder.mTvNextDate.setVisibility(View.VISIBLE);
            //响铃时间在1天内，可以忽略
            if (alarm.getNextDate() - System.currentTimeMillis() <= 1000 * 60 * 60 * 24) {
                holder.mSkipBtn.setVisibility(View.VISIBLE);
            }else{
                holder.mSkipBtn.setVisibility(View.INVISIBLE);
            }

            Date date = new Date(alarm.getNextDate());
//            if (alarm.getType() == App.ALARM_TYPE_YEAR_CN) {
//                //公历转农历
//                String lunar = LunarCalendar.solarToLunar(new SimpleDateFormat("yyyy-MM-dd").format(date));
//                holder.mTvNextDate.setText(resources.getString(R.string.next_date)
//                        + lunar + " " + new SimpleDateFormat("EE").format(date));
//            } else {
            holder.mTvNextDate.setText("（" +
                    new SimpleDateFormat("yyyy-MM-dd").format(date) + " "
                    + new SimpleDateFormat("EE").format(date) + "）");
//            }

        } else {
            holder.mOpen.setChecked(false);
            holder.mTvNextDate.setVisibility(View.INVISIBLE);
            holder.mSkipBtn.setVisibility(View.INVISIBLE);
        }
        if (alarm.getType() == App.ALARM_TYPE_ONE) {
            holder.mXunhuan.setText(resources.getString(R.string.type_one));
        } else if (alarm.getType() == App.ALARM_TYPE_WORK) {
            holder.mXunhuan.setText(resources.getString(R.string.type_work));
        } else if (alarm.getType() == App.ALARM_TYPE_WEEK) {
            String re = "";
            String[] weeks = alarm.getWeeks().split(",");
            for (int i = 0; i < weeks.length; i++) {
                int w = Integer.parseInt(weeks[i].trim());
                if (w == 1) {
                    re += App.mWeek[i] + " ";
                }
            }
            holder.mXunhuan.setText(re);
        } else if (alarm.getType() == App.ALARM_TYPE_MONTH) {
            holder.mXunhuan.setText(resources.getString(R.string.type_month) + alarm.getDay()
                    + resources.getString(R.string.day));
        } else if (alarm.getType() == App.ALARM_TYPE_YEAR) {
            holder.mXunhuan.setText(resources.getString(R.string.type_year_2) + alarm.getDate());
        } else if (alarm.getType() == App.ALARM_TYPE_YEAR_CN) {
            holder.mXunhuan.setText(resources.getString(R.string.type_year_cn) + alarm.getDate());
        } else if (alarm.getType() == App.ALARM_TYPE_ZDY) {
            holder.mXunhuan.setText(resources.getString(R.string.xiangling) + alarm.getWorkDays()
                    + resources.getString(R.string.day2) + resources.getString(R.string.zanting0)
                    + alarm.getUnWorkDays() + resources.getString(R.string.day2));
        }

        final int color_open = resources.getColor(R.color.white_t2);
        final int color_close = Color.parseColor("#10FFFFFF");
        holder.mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color_f = color_open;
                int color_t = color_close;
                if (!alarm.getIsOpen()) {
                    color_f = color_close;
                    color_t = color_open;

                    holder.mTvNextDate.setVisibility(View.VISIBLE);
                    //响铃时间在1天内，可以忽略
                    if (alarm.getNextDate() - System.currentTimeMillis() <= 1000 * 60 * 60 * 24) {
                        holder.mSkipBtn.setVisibility(View.VISIBLE);
                    }else{
                        holder.mSkipBtn.setVisibility(View.INVISIBLE);
                    }
                    //如果是单次闹钟，打开就计算下一次
                    if (alarm.getType() == App.ALARM_TYPE_ONE) {
                        alarm.setNextDate(App.getAlarmService().getNextDate(alarm, new java.util.Date(),false));
                        App.getAlarmService().updateAlarm(alarm);
                    }
                    Date date = new Date(alarm.getNextDate());
                    holder.mTvNextDate.setText("（" +
                            new SimpleDateFormat("yyyy-MM-dd").format(date) + " "
                            + new SimpleDateFormat("EE").format(date) + "）");
                } else {
                    holder.mTvNextDate.setVisibility(View.GONE);
                    holder.mSkipBtn.setVisibility(View.INVISIBLE);
                }
                ObjectAnimator animator = ObjectAnimator.ofInt(holder.mContainer,
                        "backgroundColor", color_f, color_t);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setDuration(1000).start();

                alarm.setIsOpen(!alarm.getIsOpen());
                if (mEventListener != null) {
                    mEventListener.onItemToggOpen(holder.getLayoutPosition());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onItemClick(holder.getLayoutPosition());
                }
            }
        });

        holder.mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onItemSkip(holder.getLayoutPosition());
                }
            }
        });


        holder.mDelImg.setVisibility(View.INVISIBLE);
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        //获取条目状态
        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();
        //设置条目背景
        if (((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId = 0;
            //拖动时背景
            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = resources.getColor(R.color.close);
                // need to clear drawable state here to get correct appearance of the dragging item.
                //holder.mContainer.getForeground().setState(new int[] {});
            } else if ((dragState & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0) {
                //拖动时其他条目背景
                bgResId = alarm.getIsOpen() ? color_open : color_close;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                //滑动时背景
                bgResId = resources.getColor(R.color.white_t);
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                //滑动时其他条目背景
                bgResId = alarm.getIsOpen() ? color_open : color_close;
            } else {
                bgResId = alarm.getIsOpen() ? color_open : color_close;
            }

            holder.mContainer.setBackgroundColor(bgResId);
        }else{
            int bgResId = alarm.getIsOpen() ? color_open : color_close;
            holder.mContainer.setBackgroundColor(bgResId);
        }

        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    //设置滑动时候的背景
    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        //System.out.println("-------------type---------------"+type);
        int bgRes = 0;
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = Color.TRANSPARENT;
                break;
//            case SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND:
//                    holder.mCloseImg.setVisibility(View.VISIBLE);
//                    bgRes = holder.itemView.getResources().getColor(R.color.close);
//                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                holder.mDelImg.setVisibility(View.VISIBLE);
                bgRes = holder.itemView.getResources().getColor(R.color.colorAccent);
                break;
        }
        holder.mCardView.setBackgroundColor(bgRes);
    }

    //滑动完成时调用
    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) {
        //System.out.println("onSwipeItem(position = " + position + ", result = " + result + ")");
        switch (result) {
            //右滑
            case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
                //mIsDel = true;
                return new SwipeRightResultAction(this, holder.getLayoutPosition());
            //左滑
//            case SwipeableItemConstants.RESULT_SWIPED_LEFT:
//                return new SwipeLeftResultAction(this, position);
            //取消
            case SwipeableItemConstants.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    //return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
        return null;
    }

    //返回条目是否允许拖动
    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        boolean re = isCanStartDrag(holder, position, x, y);
        if (re) {
            ViewUtils.Vibrate(50);
        }
        return re;
    }

    private boolean isCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    //返回允许拖动的范围
    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return null;
    }

    //拖动结束时候调用
    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        if (mEventListener != null) {
            mEventListener.onMoveItem(fromPosition, toPosition);
        }
    }

    //滑动类型
    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        if (isCanStartDrag(holder, position, x, y)) {
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
        } else {
            return SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT |
                    SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT |
                    SwipeableItemConstants.REACTION_MASK_START_SWIPE_LEFT;
        }
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //右滑
    private static class SwipeRightResultAction extends SwipeResultActionRemoveItem {
        private AlarmAdapter mAdapter;
        private final int mPosition;

        SwipeRightResultAction(AlarmAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemRemoved(mPosition);
            }
//            mAdapter.mItems.remove(mPosition);
//            mAdapter.notifyItemRemoved(mPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            mAdapter = null;
        }
    }

    public void setEventListener(EventListener mEventListener) {
        this.mEventListener = mEventListener;
    }
}
