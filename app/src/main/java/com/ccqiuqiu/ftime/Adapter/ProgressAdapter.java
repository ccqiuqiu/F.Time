package com.ccqiuqiu.ftime.Adapter;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.DateUtils;
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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.MyViewHolder>
        implements SwipeableItemAdapter<ProgressAdapter.MyViewHolder>
        , DraggableItemAdapter<ProgressAdapter.MyViewHolder> {

    private List<Progress> mItems;
    private EventListener mEventListener;

    public ProgressAdapter(List<Progress> items) {
        this.mItems = items;
        setHasStableIds(true);
    }

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemClick(int position);

        void onMoveItem(int fromPosition, int toPosition);

        void onItemSkip(int position);
    }

    protected class MyViewHolder extends AbstractDraggableSwipeableItemViewHolder {

        ImageView mDelImg, mProgressImg;
        CardView mCardView;
        TextView mTvNextDate, mTitle, mEnd, mTip, mTianshu;
        FrameLayout mContainer;
        View mDragHandle, mProgress, mbg;
        ImageView mImg;
        com.rey.material.widget.Button mSkipBtn;

        public MyViewHolder(View v) {
            super(v);
            mDelImg = (ImageView) v.findViewById(R.id.img_del);
            mProgressImg = (ImageView) v.findViewById(R.id.progress_img);
            mCardView = (CardView) v.findViewById(R.id.cardView);
            mTvNextDate = (TextView) v.findViewById(R.id.tv_next_date);
            mTianshu = (TextView) v.findViewById(R.id.tv_tianshu);
            mTitle = (TextView) v.findViewById(R.id.title);
            mEnd = (TextView) v.findViewById(R.id.tv_end);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTip = (TextView) v.findViewById(R.id.tips);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mbg = v.findViewById(R.id.bg);
            mProgress = v.findViewById(R.id.progress);
            mSkipBtn = (Button) v.findViewById(R.id.btn_skip);
            mImg = (ImageView) v.findViewById(R.id.progress_img);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_progress, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Progress progress = mItems.get(position);
        Resources resources = holder.itemView.getResources();
        int color_open = resources.getColor(R.color.white_t2);
        int color_close = Color.parseColor("#10FFFFFF");

        if (!TextUtils.isEmpty(progress.getName())) {
            holder.mTitle.setText(progress.getName());
        } else {
            if (progress.getType() == App.PROGRESS_TYPE_FENQI) {
                holder.mTitle.setText(resources.getString(R.string.type_fenqi));
            } else {
                holder.mTitle.setText(resources.getString(R.string.progress));
            }
        }
        long startTime = 0, endTime = 0, curTime;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        curTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String tip = null;
        int progress_num = 0, count, guoqu = 0, shenxia = 0;
        if (progress.getType() == App.PROGRESS_TYPE_TIME) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(progress.getStart().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(progress.getStart().substring(3)));
            startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(progress.getEnd().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(progress.getEnd().substring(3)));
            endTime = calendar.getTimeInMillis();

            count = (int) ((endTime - startTime) / (1000 * 60));
            guoqu = (int) ((curTime - startTime) / (1000 * 60));
            shenxia = count - guoqu;
            progress_num = (int) (guoqu * 100.00 / count);

            tip = resources.getString(R.string.tip_unit_time);
            holder.mSkipBtn.setVisibility(View.GONE);
        } else if (progress.getType() == App.PROGRESS_TYPE_DATE) {
            calendar.set(Calendar.YEAR, Integer.parseInt(progress.getStart().substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(progress.getStart().substring(5, 7)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getStart().substring(8)));
            startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.YEAR, Integer.parseInt(progress.getEnd().substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(progress.getEnd().substring(5, 7)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getEnd().substring(8)));
            endTime = calendar.getTimeInMillis();


            count = DateUtils.getIntervalDays(new Date(endTime), new Date(startTime)) + 1;
            guoqu = DateUtils.getIntervalDays(new Date(curTime), new Date(startTime)) + 1;
            shenxia = count - guoqu;
            progress_num = (int) (guoqu * 100.00 / count);

            tip = resources.getString(R.string.tip_unit_date);
            holder.mSkipBtn.setVisibility(View.GONE);
        } else if (progress.getType() == App.PROGRESS_TYPE_FENQI) {

            calendar.set(Calendar.YEAR, Integer.parseInt(progress.getStart().substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(progress.getStart().substring(5, 7)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getStart().substring(8)));
            startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.YEAR, Integer.parseInt(progress.getEnd().substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(progress.getEnd().substring(5, 7)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getEnd().substring(8)));
            endTime = calendar.getTimeInMillis();

            count = DateUtils.getIntervalMonth(new Date(endTime), new Date(startTime));
            //计算下期时间
            long xiaqi = App.getProgressService().getNext(progress);
            //没有跳过
            if (progress.getNextDate() <= xiaqi) {
                holder.mTianshu.setVisibility(View.VISIBLE);
                holder.mSkipBtn.setVisibility(View.VISIBLE);
            } else {
                xiaqi = progress.getNextDate();
                holder.mTianshu.setVisibility(View.GONE);
                holder.mSkipBtn.setVisibility(View.GONE);
            }
            guoqu = DateUtils.getIntervalMonth(new Date(xiaqi), new Date(startTime));
            //计算本期剩余天数
            int tianshu = DateUtils.getIntervalDays(new Date(curTime), new Date(xiaqi));
            holder.mTianshu.setText(MessageFormat.format(resources.getString(R.string.progress_shengyu_tianshu)
                    , tianshu));
            if (tianshu <= 3) {
                holder.mTianshu.setTextColor(resources.getColor(R.color.colorAccent));
            } else {
                holder.mTianshu.setTextColor(resources.getColor(R.color.white_t));
            }

            holder.mTvNextDate.setText(MessageFormat.format(resources.getString(R.string.progress_next),
                    DateUtils.DateToString(new Date(xiaqi), "yyyy-MM-dd EE")));

            shenxia = count - guoqu;
            progress_num = (int) (guoqu * 100.00 / count);
            tip = resources.getString(R.string.tip_unit_fenqi);
        }
        if (progress.getType() != App.PROGRESS_TYPE_FENQI) {
            //没有开始
            if (curTime < startTime) {
                progress_num = 0;
                tip = resources.getString(R.string.un_start);
                holder.mEnd.setText(MessageFormat.format(resources.getString(R.string.end_progress_0), progress.getEnd()));
                //color_open = color_close;
            }
        }
        if (curTime >= endTime) {
            //已经结束
            progress_num = 100;
            tip = resources.getString(R.string.is_end);
            holder.mEnd.setText(MessageFormat.format(resources.getString(R.string.end_progress_1), progress.getEnd()));
            //color_open = color_close;
        } else {
            tip = MessageFormat.format(tip, guoqu, shenxia);
            holder.mEnd.setText(MessageFormat.format(resources.getString(R.string.end_progress), progress.getEnd()));
        }
        holder.mImg.setImageDrawable(resources.getDrawable(App.getImgIds().get(progress.getIcon())));

        final int progressWidth = ViewUtils.getScreemWidth() * progress_num / 100;
        //文本框点击时间，交替显示百分比和详细信息
        final String finalTip = tip;
        final int finalProgress_num = progress_num;
        holder.mTip.setText(progress.isShowTipDesc() ? finalTip : finalProgress_num + "%");
        holder.mTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setIsShowTipDesc(!progress.isShowTipDesc());
                holder.mTip.setText(progress.isShowTipDesc() ? finalTip : finalProgress_num + "%");
                //重新计算tips框的位置
                int tipWidth = ViewUtils.getViewWidth(holder.mTip);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mTip.getLayoutParams();
                layoutParams.width = tipWidth;
                int left = progressWidth - tipWidth / 2 + ViewUtils.dp2px(12);
                left = Math.max(ViewUtils.dp2px(2), left);
                left = Math.min(left, ViewUtils.getScreemWidth() - tipWidth - ViewUtils.dp2px(2));
                layoutParams.leftMargin = left;
                holder.mTip.setLayoutParams(layoutParams);
            }
        });

//        ViewTreeObserver vto = holder.mTip.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                holder.mTip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mTip.getLayoutParams();
//                int left = holder.mProgress.getWidth() - holder.mTip.getWidth() / 2 + holder.mProgressImg.getWidth() / 2;
//                left = Math.max(0, left);
//                layoutParams.leftMargin = left;
//                holder.mTip.setLayoutParams(layoutParams);
//            }
//        });
        //计算进度条的宽度并播放动画
        final int tipWidth = ViewUtils.getViewWidth(holder.mTip);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mProgress.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofFloat(0, progressWidth);
        animator.setTarget(holder.mProgress);
        animator.setDuration(1000).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = ((Float) animation.getAnimatedValue()).intValue();
                layoutParams.width = width;
                holder.mProgress.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mTip.getLayoutParams();
                int left = width - tipWidth / 2 + ViewUtils.dp2px(12);
                left = Math.max(ViewUtils.dp2px(2), left);
                left = Math.min(left, ViewUtils.getScreemWidth() - tipWidth - ViewUtils.dp2px(2));
                layoutParams.leftMargin = left;
                holder.mTip.setLayoutParams(layoutParams);
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
            int bgResId = color_close;
            //拖动时背景
            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = holder.itemView.getResources().getColor(R.color.close);
                // need to clear drawable state here to get correct appearance of the dragging item.
                //holder.mContainer.getForeground().setState(new int[] {});
            } else if ((dragState & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0) {
                //拖动时其他条目背景
                //bgResId = alarm.getIsOpen() ? color_open : color_close;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                //滑动时背景
                bgResId = holder.itemView.getResources().getColor(R.color.white_t);
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                //滑动时其他条目背景
                bgResId = color_open;
            } else {
                bgResId = color_open;
            }

            holder.mContainer.setBackgroundColor(bgResId);
        }

        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    //设置滑动时候的背景
    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        //System.out.println("==================="+type);
        int bgRes = 0;
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = Color.TRANSPARENT;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                holder.mDelImg.setVisibility(View.VISIBLE);
                bgRes = holder.itemView.getResources().getColor(R.color.colorAccent);
                break;
        }
        holder.mCardView.setBackground(new ColorDrawable(bgRes));
    }

    //滑动完成时调用
    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) {
        switch (result) {
            //右滑
            case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
                return new SwipeRightResultAction(this, holder.getLayoutPosition());
//            左滑
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
        //isAnim = false;
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
        private ProgressAdapter mAdapter;
        private final int mPosition;

        SwipeRightResultAction(ProgressAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
//            mAdapter.mItems.remove(mPosition);
//        mAdapter.notifyItemRemoved(mPosition);
            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemRemoved(mPosition);
            }
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
