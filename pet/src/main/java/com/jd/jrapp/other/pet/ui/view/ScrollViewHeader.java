package com.jd.jrapp.other.pet.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.utils.TimeUtils;

/**
 * author : JOJO
 * e-mail : zhoujuan26@jd.com
 * date   : 2021/3/7 2:56 PM
 * desc   : Scrollview的下拉刷新头部
 */
public class ScrollViewHeader extends RelativeLayout {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_REFRESH_FINISH = 3;

    private final int ROTATE_ANIM_DURATION = 180;
    private int topMargin = 0;
    private int state = STATE_NORMAL;
    private TextView refreshTv = null;
    private TextView refreshTimeTv = null;
    private ProgressBar refreshProgress = null;
    private ImageView refreshArrow = null;
    private Animation animationUp = null;
    private Animation animationDown = null;

    public ScrollViewHeader(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        if (!isInEditMode())
            initView(context);
    }

    public ScrollViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        if (!isInEditMode())
            initView(context);
    }

    public ScrollViewHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        if (!isInEditMode())
            initView(context);
    }

    /**
     * 初始化相关的view
     */
    public void initView(Context context) {
        animationDown = new RotateAnimation(-180f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationDown.setDuration(ROTATE_ANIM_DURATION);
        animationDown.setFillAfter(true);
        animationUp = new RotateAnimation(0, -180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationUp.setDuration(ROTATE_ANIM_DURATION);
        animationUp.setFillAfter(true);
        setPadding(10, 25, 10, 25);
        View view = LayoutInflater.from(context).inflate(R.layout.scrollview_header, this, true);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
        refreshTv = (TextView) view.findViewById(R.id.refresh_text);
        refreshTimeTv = (TextView) view.findViewById(R.id.refresh_time);
        refreshProgress = (ProgressBar) view.findViewById(R.id.refresh_progress);
        refreshArrow = (ImageView) view.findViewById(R.id.refresh_arrow);
    }

    /**
     * 设置scrollviewHeader的状态
     *
     * @param state
     */

    public void setState(int state) {
        if (this.state == state) {
            return;
        }
        switch (state) {
            case STATE_NORMAL:
                refreshTv.setText("下拉刷新");
                refreshArrow.setVisibility(View.VISIBLE);
                refreshProgress.setVisibility(View.INVISIBLE);
                if (this.state == STATE_READY) {
                    refreshArrow.startAnimation(animationDown);
                } else if (this.state == STATE_REFRESHING) {
                    refreshArrow.clearAnimation();
                }
                break;
            case STATE_READY:
                refreshTimeTv.setText(TimeUtils.getTimeAgo(TimeUtils.getLastRefreshTime(getContext()))+"更新");
                refreshTv.setText("松开刷新");
                refreshArrow.setVisibility(View.VISIBLE);
                refreshProgress.setVisibility(View.INVISIBLE);
                refreshArrow.startAnimation(animationUp);
                break;
            case STATE_REFRESHING:
                refreshTimeTv.setText(TimeUtils.getTimeAgo(TimeUtils.getLastRefreshTime(getContext()))+"更新");
                refreshTv.setText("正在加载...");
                refreshProgress.setVisibility(View.VISIBLE);
                refreshArrow.clearAnimation();
                refreshArrow.setVisibility(View.INVISIBLE);
                break;
            case STATE_REFRESH_FINISH:
                refreshTv.setText("刷新完成");
                refreshProgress.setVisibility(View.INVISIBLE);
                refreshArrow.clearAnimation();
                refreshArrow.setVisibility(View.INVISIBLE);
                TimeUtils.saveLastRefreshTime(getContext(), System.currentTimeMillis());
                break;
            default:
                break;
        }
        this.state = state;
    }

    /**
     * 更新header的margin
     *
     * @param margin
     */
    public void updateMargin(int margin) {
        //这里用Linearlayout的原因是Headerview的父控件是scrollcontainer是一个linearlayout
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.getLayoutParams();
        params.topMargin = margin;
        topMargin = margin;
        setLayoutParams(params);
    }

    /**
     * 获取header的margin
     *
     * @return
     */
    public int getTopMargin() {
        return topMargin;
    }
}
