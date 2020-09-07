package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.SignCouponInfo;
import com.jd.jrapp.other.pet.ui.dialog.bean.SignInfo;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SharedPrefsMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/2
 * Time: 8:33 PM
 */

public class SignDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int width;
    private List<SignInfo> signInfos;
    private List<SignCouponInfo> signCouponInfos;
    private static final String SIGNTODAY = "signToday";//今日签到
    private static final String RETROACTIVE = "retroactive";//补签
    private int signToday;

    public SignDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_sign_dialog, null);
        TextView tv_close = contentView.findViewById(R.id.tv_close);
        TextView tv_share = contentView.findViewById(R.id.tv_share);
        TextView tv_sign = contentView.findViewById(R.id.tv_sign);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        RecyclerView recyclerView1 = contentView.findViewById(R.id.recyclerView1);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        signToday = SharedPrefsMgr.getInstance(mContext).getInt(SIGNTODAY, 1);
        int retroactive = SharedPrefsMgr.getInstance(mContext).getInt(RETROACTIVE, 2);
        //0已签到 1未签到 2补签
        signInfos = new ArrayList<>();
        signInfos.add(new SignInfo("周一", 0));
        signInfos.add(new SignInfo("补签", retroactive));
        signInfos.add(new SignInfo("周三", 0));
        signInfos.add(new SignInfo("今天", signToday));
        signInfos.add(new SignInfo("周五", 1));
        signInfos.add(new SignInfo("周六", 1));
        signInfos.add(new SignInfo("周日", 1));

        signCouponInfos = new ArrayList<>();
        signCouponInfos.add(new SignCouponInfo(R.drawable.icon_coupon_20, "金条利息券"));
        signCouponInfos.add(new SignCouponInfo(R.drawable.icon_coupon_15, "白条支付券"));
        signCouponInfos.add(new SignCouponInfo(R.drawable.icon_coupon_5, "自营全类券"));

        LinearLayoutManager manager1 = new LinearLayoutManager(mContext);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(manager1);
        recyclerView.setAdapter(myAdapter);
        recyclerView1.setAdapter(myAdapter1);

        myAdapter.addAll(signInfos);
        myAdapter1.addAll(signCouponInfos);

        tv_close.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_sign.setOnClickListener(this);
        setCanceledOnTouchOutside(true);
        setContentView(contentView);

        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_close) {
            dismiss();
        } else if (id == R.id.tv_share) {
            SharedPrefsMgr.getInstance(mContext).putInt(RETROACTIVE, 0);
            SignInfo signInfo=signInfos.get(1);
            signInfo.setSigntype(0);
            myAdapter.notifyItemChanged(1);
        } else if (id == R.id.tv_sign) {
            SharedPrefsMgr.getInstance(mContext).putInt(SIGNTODAY, 0);
            SignInfo signInfo=signInfos.get(3);
            signInfo.setSigntype(0);
            myAdapter.notifyItemChanged(3);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    signToday=0;
                    myAdapter1.notifyDataSetChanged();
                }
            },800);
        }
    }

    RecycleAdapter<SignCouponInfo> myAdapter1 = new RecycleAdapter<SignCouponInfo>(mContext, R.layout.item_sign_coupon_layout, signCouponInfos) {
        @Override
        protected void convert(BaseAdapterHelper helper, SignCouponInfo item, int position) {
            RelativeLayout view = helper.getView(R.id.rl_coupon);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (position == signCouponInfos.size() - 1) {
                view.setPadding(0, 0, 0, 0);
            } else {
                view.setPadding(0, 0, DisplayUtil.dip2px(mContext, 12), 0);
            }
            view.setLayoutParams(params);
            if(signToday==0){
                helper.setBackgroundRes(R.id.iv_coupon, item.getMoney());
                helper.setText(R.id.tv_pay_type, item.getCouponType());
            }else {
                helper.setBackgroundRes(R.id.iv_coupon, R.drawable.icon_hongbao);
                helper.setText(R.id.tv_pay_type, "拆开有惊喜");
            }
        }
    };

    RecycleAdapter<SignInfo> myAdapter = new RecycleAdapter<SignInfo>(mContext, R.layout.item_sign_layout, signInfos) {
        @Override
        protected void convert(BaseAdapterHelper helper, SignInfo item, int position) {
            LinearLayout view = helper.getView(R.id.rl_sign);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (position == signInfos.size() - 1) {
                view.setPadding(0, 0, 0, 0);
            } else {
                view.setPadding(0, 0, DisplayUtil.dip2px(mContext, 6), 0);
            }
            helper.setText(R.id.tv_week, item.getWeek());
            if (item.getSigntype() == 0) {
                helper.setBackgroundRes(R.id.iv_sign, R.drawable.icon_signed);
                helper.setBackgroundRes(R.id.ll_sign, R.drawable.bg_shape_b90c00);
                helper.setTextColor(R.id.tv_week, mContext.getResources().getColor(R.color.white));
            } else if (item.getSigntype() == 2) {
                helper.setBackgroundRes(R.id.iv_sign, R.drawable.icon_unsign);
                helper.setBackgroundRes(R.id.ll_sign, R.drawable.bg_shape_ffdb00);
                helper.setTextColor(R.id.tv_week, mContext.getResources().getColor(R.color.color_0a0a0d));
            } else {
                helper.setBackgroundRes(R.id.iv_sign, R.drawable.icon_unsign);
                helper.setBackgroundRes(R.id.ll_sign, R.drawable.bg_shape_b90c00);
                helper.setTextColor(R.id.tv_week, mContext.getResources().getColor(R.color.white));
            }
        }
    };

}
