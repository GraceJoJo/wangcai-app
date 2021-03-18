package com.jd.jrapp.other.pet.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.dialog.bean.CategoryData;
import com.jd.jrapp.other.pet.ui.dialog.bean.MoneyManagementData;
import com.jd.jrapp.other.pet.ui.view.DragView;
import com.jd.jrapp.other.pet.ui.view.LeftListView;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SharedPrefsMgr;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: zhoujuan26
 * Date: 2021/3/21
 * Time: 9:05 PM
 * 红包
 */

public class RedEnvelopDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private static final String ISOPEN = "isOpen";
    private int width;
    private ScrollView scrollview;
    private LeftListView lfListview;
    private String[] categoryString = new String[]{"热卖推荐", "安心蔬菜", "新鲜水果", "冷冻冰藏", "米面粮油", "水产海鲜", "肉禽蛋类", "鲜花烘焙", "酒水乳饮", "日用百货"};
    private List<CategoryData> categoryList = new ArrayList<>();
    private boolean isOpen = false;
    private int zjsy = 50;
    private DragView dragView;
    private int height;
    private ImageView ivGrapRedEnvelop;
    private TextView tvAmount;

    public RedEnvelopDialog(Context context, int zjsy) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        this.zjsy = zjsy;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen = SharedPrefsMgr.getInstance(mContext).getBoolean(ISOPEN, false);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        height = (int) DisplayUtil.getScreenHeight(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View contentView = inflater.inflate(R.layout.layout_red_envelop_dia, null);

        ivGrapRedEnvelop = contentView.findViewById(R.id.iv_grap);
        tvAmount = contentView.findViewById(R.id.tv_amount);
        contentView.findViewById(R.id.iv_close).setOnClickListener(this);

        ivGrapRedEnvelop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int amount=(int)(Math.random()*100);;
                grapEnvelope();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGragSuccess(amount);
                    }
                },1000);
            }
        });

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(contentView);

//         设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width =DisplayUtil.dip2px(mContext, 300);
        lp.height = DisplayUtil.dip2px(mContext, 400);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

//        setCameraDistance(); // 设置镜头距离

//        getWindow().setGravity(Gravity.CENTER);
//        getWindow().setWindowAnimations(R.style.style_custom_dialog_fade);
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        // lp.dimAmount = 0.0f; 背景灰度
//        lp.width = DisplayUtil.getScreenWidth((Activity) mContext) / 10 * 9; // 设置宽度
//        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);
    }
    /**
     * 抢红包失败
     */
    public void showGragFail(String messge){
        ivGrapRedEnvelop.setVisibility(View.GONE);
//        tvAmountHint.setText(messge);
        tvAmount.setText("空空如也~");
    }

    /**
     * 抢红包成功
     * @param amount
     */
    public void showGragSuccess(int amount){
        ivGrapRedEnvelop.setVisibility(View.GONE);
        tvAmount.setText(amount+"元");
    }

    // 改变视角距离, 贴近屏幕
    private void setCameraDistance() {
        int distance = 16000;
        float scale = mContext.getResources().getDisplayMetrics().density * distance;
        ivGrapRedEnvelop.setCameraDistance(scale);
    }


    /**
     * 显示抢红包动画
     */
    @SuppressLint("WrongConstant")
    private void grapEnvelope(){
        ivGrapRedEnvelop.setImageResource(R.drawable.ic_red_envelope_grap_open);
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(ivGrapRedEnvelop, "rotationY", 0.0f, 180.0f);
        translationXAnim.setDuration(800);
        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        translationXAnim.setRepeatMode(ValueAnimator.INFINITE);//
        translationXAnim.start();
        animators.add(translationXAnim);
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(ivGrapRedEnvelop, "rotationY", 180.0f, 0.0f);
        translationYAnim.setDuration(800);
        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
        translationYAnim.setRepeatMode(ValueAnimator.INFINITE);
        translationYAnim.start();
        animators.add(translationYAnim);
        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
        btnSexAnimatorSet.playTogether(animators);
        btnSexAnimatorSet.start();
        ivGrapRedEnvelop.setClickable(false);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.iv_close) {
            dismiss();
        } else if (v.getId() == R.id.rl_dialog) {
//            dismiss();
        }
    }

}
