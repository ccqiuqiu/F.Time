package com.ccqiuqiu.ftime.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Service.ProgressService;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.SoftKeyboardUtil;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cc on 2016/3/14.
 */
@ContentView(R.layout.fragment_add_progress)
public class AddProgressFragment extends BaseFragment
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @ViewInject(R.id.tv_type_time)
    private TextView mTvTypeTime;
    @ViewInject(R.id.tv_type_date)
    private TextView mTvTypeDate;
    @ViewInject(R.id.tv_type_fenqi)
    private TextView mTvTypeFenqi;
    @ViewInject(R.id.type_time)
    private View mTypeTime;
    @ViewInject(R.id.type_date)
    private View mTypeDate;
    @ViewInject(R.id.type_fenqi)
    private View mTypeFenqi;
    @ViewInject(R.id.view_type_time)
    private View mViewTypeTime;
    @ViewInject(R.id.view_type_date)
    private View mViewTypeDate;
    @ViewInject(R.id.view_tixing)
    private View mViewTixing;
    @ViewInject(R.id.tixing)
    private com.ccqiuqiu.ftime.View.WheelView mTingxing;
    @ViewInject(R.id.imgs)
    private com.ccqiuqiu.ftime.View.WheelViewImg mImgs;
    @ViewInject(R.id.date_qj)
    private TextView mTvDateQj;
    @ViewInject(R.id.time_qj)
    private TextView mTvTimeQj;
    @ViewInject(R.id.title)
    private com.rengwuxian.materialedittext.MaterialEditText mProgressTitle;

    private View mSelectedTvType;
    private int mType;
    private boolean mIsTimeOrDateEnd;
    private String mStartDate, mEndDate, mStartTime, mEndTime;
    private String mStartDateTemp, mStartTimeTemp;
    private int mEditPosition;
    private Progress mEditProgress;
    private ImageView mDelBtn;
    private TextView mTitle;

    public static AddProgressFragment newInstance() {
        return new AddProgressFragment();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = (TextView) mMainActivity.mMainFragment.getView().findViewById(R.id.add_progress_title);
        mDelBtn = (ImageView) mMainActivity.mMainFragment.getView().findViewById(R.id.add_progress_del);

        //设置控件颜色
        mTvTypeTime.setTextColor(App.mColorPrimary);
        mTvTypeDate.setTextColor(App.mColorPrimary);
        mTvTypeFenqi.setTextColor(App.mColorPrimary);
        //设置单击监听
        mTypeTime.setOnClickListener(this);
        mTypeDate.setOnClickListener(this);
        mTypeFenqi.setOnClickListener(this);
        mViewTypeTime.setOnClickListener(this);
        mViewTypeDate.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);

        mTingxing.setData(new ArrayList<>(App.mTixings));
        mTingxing.setDefault(0);

        mImgs.setItems(App.getImgIds());
//
//        mImgs.setOnWheelItemSelectedListener(new WheelViewImg.OnWheelItemSelectedListener() {
//            @Override
//            public void onWheelItemChanged(WheelViewImg wheelView, int position) {
//
//            }
//            @Override
//            public void onWheelItemSelected(WheelViewImg wheelView, int position) {
//                System.out.println("============position==========" + position);
//            }
//        });
    }

    @Override
    public void observeSoftKeyboard() {
        super.observeSoftKeyboard();
        //软键盘弹出监听
        SoftKeyboardUtil.observeSoftKeyboard(getActivity(), new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                int h = ViewUtils.getScreemHeight() - mProgressTitle.getBottom() -
                        mMainActivity.mMainFragment.mFab.getHeight() - ViewUtils.dp2px(16);
                h = Math.min(h, softKeybardHeight);
                if (visible) {
                    //mMainActivity.mMainFragment.mFab.animate().translationY(-softKeybardHeight).setDuration(150);
                    mMainActivity.mMainFragment.mFab.animate().translationY(-h)
                            .setDuration(150);
                } else {
                    mMainActivity.mMainFragment.mFab.animate().translationY(0).setDuration(200);
                }
            }
        });
    }

    @Override
    public void onFabClick(FloatingActionButton fab) {
        save(fab);
    }

    private void save(FloatingActionButton fab) {
        Progress progress = new Progress();
        progress.setType(mType);
        if (mType == App.PROGRESS_TYPE_TIME) {
            if (TextUtils.isEmpty(mStartTime) || TextUtils.isEmpty(mEndTime)) {
                ViewUtils.snackbar(fab, getString(R.string.err_time_null));
                return;
            }
            //选择的结束时间不大于开始时间
            Date start = DateUtils.StringToDate("2016-01-01 " + mStartTime + ":00");
            Date end = DateUtils.StringToDate("2016-01-01 " + mEndTime + ":00");
            if (!start.before(end)) {
                ViewUtils.snackbar(fab, getString(R.string.err_endtinme));
                return;
            }
            progress.setStart(mStartTime);
            progress.setEnd(mEndTime);
        } else {
            if (TextUtils.isEmpty(mStartDate) || TextUtils.isEmpty(mEndDate)) {
                ViewUtils.snackbar(fab, getString(R.string.err_date_null));
                return;
            }
            //选择的结束时间不大于开始时间
            if (DateUtils.StringToDate(mEndDate).getTime() <= DateUtils.StringToDate(mStartDate).getTime()) {
                ViewUtils.snackbar(fab, getString(R.string.err_endtdate));
                return;
            }
            progress.setStart(mStartDate);
            progress.setEnd(mEndDate);

            progress.setTixing(mTingxing.getSelected());
        }
        progress.setIcon(mImgs.getSelectedPosition());
        if (!TextUtils.isEmpty(mProgressTitle.getText().toString())) {
            progress.setName(mProgressTitle.getText().toString());
        }
        String s = getString(R.string.add_progress_success);
        if (mEditProgress != null) {//修改
            progress.setId(mEditProgress.getId());
            progress.setOrder(mEditProgress.getOrder());
            progress.setIsShowTipDesc(mEditProgress.isShowTipDesc());
            App.getProgressService().updateProgress(progress);
            mMainActivity.mMainFragment.mProgressFragment.notifyItemChanged(progress, mEditPosition);
            s = getString(R.string.edit_progress_success);
        } else {//新增
            App.getProgressService().saveProgress(progress);
            mMainActivity.mMainFragment.mProgressFragment.notifyItemInserted(progress);
        }

        mMainActivity.mMainFragment.mViewPager.setCurrentItem(2);
        ViewUtils.toast(getString(R.string.add_progress_success));
        ViewUtils.toast(s);
        ViewUtils.HideKeyboard(mProgressTitle);
        //fab按钮回到初始位置
        mMainActivity.mMainFragment.mFab.animate().translationY(0).setDuration(200);
    }

    public void editProgress(Progress progress, int position) {
        mType = progress.getType();
        mEditPosition = position;
        mEditProgress = progress;
        mTitle.setText(getString(R.string.edit_alarm));
        mDelBtn.setVisibility(View.VISIBLE);
        mTitle.setText(getString(R.string.edit_progress));

        if (mType == App.PROGRESS_TYPE_TIME) {
            changeType(mTypeTime);
            mStartTime = progress.getStart();
            mEndTime = progress.getEnd();
            mTvTimeQj.setText(mStartTime + getString(R.string.to) + mEndTime);
        } else if (mType == App.PROGRESS_TYPE_DATE) {
            changeType(mTypeDate);
            mStartDate = progress.getStart();
            mEndDate = progress.getEnd();
            mTvDateQj.setText(mStartDate + getString(R.string.to) + mEndDate);
        } else if (mType == App.PROGRESS_TYPE_FENQI) {
            changeType(mTypeFenqi);
            mStartDate = progress.getStart();
            mEndDate = progress.getEnd();
            mTvDateQj.setText(mStartDate + getString(R.string.to) + mEndDate);
        }
        mImgs.selectIndex(progress.getIcon());
        mTingxing.setDefault(progress.getTixing());

        if (!TextUtils.isEmpty(progress.getName())) {
            mProgressTitle.setText(progress.getName());
        } else {
            mProgressTitle.setText("");
        }
    }

    private void delProgress() {
        mMainActivity.mMainFragment.mViewPager.setCurrentItem(2);
        mMainActivity.mMainFragment.mProgressFragment.notifyItemRemoved(mEditPosition);
    }

    public void initViewDate() {
        mEditProgress = null;
        mTitle.setText(getString(R.string.add_progress));
        mDelBtn.setVisibility(View.GONE);
        //初始化默认选择的类型
        changeType(mTypeDate);
        mTvDateQj.setText(getString(R.string.date_qujian));
        mTvTimeQj.setText(getString(R.string.time_qujian));
        mImgs.selectIndex(0);
        mProgressTitle.setText("");
        mStartTime = null;
        mEndTime = null;
        mStartDate = null;
        mEndDate = null;
        mTingxing.setDefault(0);

    }

    private void changeType(View v) {
        Drawable drawable_selected = getResources().getDrawable(R.drawable.btn_background_white_1);
        if (mSelectedTvType != null) {
            mSelectedTvType.setBackgroundDrawable(null);
        }
        v.setBackground(drawable_selected);
        mSelectedTvType = v;

        int id = v.getId();
        switch (id) {
            case R.id.type_time:
                mType = App.PROGRESS_TYPE_TIME;
                mViewTypeTime.setVisibility(View.VISIBLE);
                mViewTypeDate.setVisibility(View.GONE);
                mViewTixing.setVisibility(View.GONE);
                break;
            case R.id.type_date:
                mType = App.PROGRESS_TYPE_DATE;
                mViewTypeDate.setVisibility(View.VISIBLE);
                mViewTixing.setVisibility(View.VISIBLE);
                mViewTypeTime.setVisibility(View.GONE);

                break;
            case R.id.type_fenqi:
                mType = App.PROGRESS_TYPE_FENQI;
                mViewTypeTime.setVisibility(View.GONE);
                mViewTypeDate.setVisibility(View.VISIBLE);
                mViewTixing.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.view_type_time:
                selectTime(false);
                break;
            case R.id.view_type_date:
                selectDate(false);
                break;
            case R.id.add_progress_del:
                delProgress();
                break;
            default:
                changeType(v);
        }
    }

    private void selectDate(boolean isEnd) {
        mIsTimeOrDateEnd = isEnd;
        Calendar now = Calendar.getInstance();
        if (mIsTimeOrDateEnd) {
            if (mEndDate != null) {
                now.set(Calendar.YEAR, Integer.parseInt(mEndDate.substring(0, 4)));
                now.set(Calendar.MONTH, Integer.parseInt(mEndDate.substring(5, 7)) - 1);
                now.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mEndDate.substring(8)));
            } else {
                now.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            if (mStartDate != null) {
                now.set(Calendar.YEAR, Integer.parseInt(mStartDate.substring(0, 4)));
                now.set(Calendar.MONTH, Integer.parseInt(mStartDate.substring(5, 7)) - 1);
                now.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mStartDate.substring(8)));
            }
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        int id = mIsTimeOrDateEnd ? R.string.select_date_end : R.string.select_date_start;
        dpd.setTitle(getString(id));
        dpd.show(mMainActivity.getFragmentManager(), "Datepickerdialog");
    }

    private void selectTime(boolean isEnd) {
        mIsTimeOrDateEnd = isEnd;
        Calendar now = Calendar.getInstance();
        if (mIsTimeOrDateEnd) {
            if (mEndTime != null) {
                now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mEndTime.substring(0, 2)));
                now.set(Calendar.MINUTE, Integer.parseInt(mEndTime.substring(3)));
            } else {
                now.add(Calendar.HOUR_OF_DAY, 1);
            }
        } else {
            if (mStartTime != null) {
                now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mStartTime.substring(0, 2)));
                now.set(Calendar.MINUTE, Integer.parseInt(mStartTime.substring(3)));
            }
        }
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        int id = mIsTimeOrDateEnd ? R.string.select_time_end : R.string.select_time_start;
        tpd.setTitle(getString(id));
        tpd.show(mMainActivity.getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar mCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(mCalendar.getTime());
        if (!mIsTimeOrDateEnd) {
            mStartDateTemp = date;
            if (mType == App.PROGRESS_TYPE_DATE) {
                selectDate(true);
                ViewUtils.toast(getString(R.string.set_date_next));
            } else {
                //如果是分期，弹出设置期数的dialog
                SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (mEndDate != null && mStartDate != null) {
                            MaterialEditText mQishu = (MaterialEditText) dialog.findViewById(R.id.qishu);
                            int qishu = DateUtils.getIntervalMonth(DateUtils.StringToDate(mEndDate, "yyyy-MM-dd")
                                    , DateUtils.StringToDate(mStartDate, "yyyy-MM-dd"));
                            mQishu.setText(qishu + "");
                        }
                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        MaterialEditText mQishu = (MaterialEditText) fragment.getDialog().findViewById(R.id.qishu);
                        String qishu = mQishu.getText().toString();
                        if (TextUtils.isEmpty(qishu) || Integer.parseInt(qishu) <= 0) {
                            mQishu.setError(getString(R.string.err_qishu_null));
                            return;
                        }
                        Date date = DateUtils.StringToDate(mStartDateTemp, "yyyy-MM-dd");
                        date = DateUtils.addMonth(date, Integer.parseInt(mQishu.getText().toString()));
                        mIsTimeOrDateEnd = false;
                        mEndDate = DateUtils.DateToString(date, "yyyy-MM-dd");
                        mStartDate = mStartDateTemp;
                        mTvDateQj.setText(mStartDate + getString(R.string.to) + mEndDate);
                        super.onPositiveActionClicked(fragment);
                    }
                };

                builder.title(getString(R.string.qishu))
                        .positiveAction(getString(R.string.commit))
                        .contentView(R.layout.dialog_set_qishu);
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
            }
        } else {
            mIsTimeOrDateEnd = false;
            mEndDate = date;
            mStartDate = mStartDateTemp;
            mTvDateQj.setText(mStartDate + getString(R.string.to) + mEndDate);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = DateUtils.getTime("2016-01-01 " + hourOfDay + ":" + minute, "HH:mm");
        if (!mIsTimeOrDateEnd) {
            mStartTimeTemp = time;
            ViewUtils.toast(getString(R.string.set_time_next));
            selectTime(true);
        } else {
            mIsTimeOrDateEnd = false;
            mEndTime = time;
            mStartTime = mStartTimeTemp;
            mTvTimeQj.setText(mStartTime + getString(R.string.to) + mEndTime);
        }
    }
}
