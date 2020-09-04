package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.jd.jrapp.other.pet.utils.DisplayUtil;

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
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        RecyclerView recyclerView1 = contentView.findViewById(R.id.recyclerView1);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        //0已签到 1未签到 2补签
        signInfos = new ArrayList<>();
        signInfos.add(new SignInfo("周一", 0));
        signInfos.add(new SignInfo("补签", 2));
        signInfos.add(new SignInfo("周三", 0));
        signInfos.add(new SignInfo("今天", 1));
        signInfos.add(new SignInfo("周五", 1));
        signInfos.add(new SignInfo("周六", 1));
        signInfos.add(new SignInfo("周日", 1));

        signCouponInfos = new ArrayList<>();
        signCouponInfos.add(new SignCouponInfo("20元", "金条利息券", "领取7日内有效"));
        signCouponInfos.add(new SignCouponInfo("10元", "白条支付券", "领取7日内有效"));
        signCouponInfos.add(new SignCouponInfo("5元", "自营全类券", "领取7日内有效"));

        LinearLayoutManager manager1 = new LinearLayoutManager(mContext);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(manager1);
        recyclerView.setAdapter(myAdapter);
        recyclerView1.setAdapter(myAdapter1);

        myAdapter.addAll(signInfos);
        myAdapter1.addAll(signCouponInfos);

        tv_close.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);
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
        }
    }

    RecycleAdapter<SignCouponInfo> myAdapter1 = new RecycleAdapter<SignCouponInfo>(mContext, R.layout.item_sign_coupon_layout, signCouponInfos) {
        @Override
        protected void convert(BaseAdapterHelper helper, SignCouponInfo item, int position) {
            RelativeLayout view=helper.getView(R.id.rl_coupon);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if(position==signCouponInfos.size()-1){
                view.setPadding(0, 0, 0, 0);
            }else {
                view.setPadding(0, 0, DisplayUtil.dip2px(mContext, 12), 0);
            }
            view.setLayoutParams(params);
            helper.setText(R.id.tv_money, item.getMoney());
            helper.setText(R.id.tv_pay_type, item.getCouponType());
            helper.setText(R.id.tv_time_type, item.getUsetype());
        }
    };

    RecycleAdapter<SignInfo> myAdapter = new RecycleAdapter<SignInfo>(mContext, R.layout.item_sign_layout, signInfos) {
        @Override
        protected void convert(BaseAdapterHelper helper, SignInfo item, int position) {
            LinearLayout view=helper.getView(R.id.rl_sign);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if(position==signInfos.size()-1){
                view.setPadding(0, 0, 0, 0);
            }else {
                view.setPadding(0, 0, DisplayUtil.dip2px(mContext, 6), 0);
            }
            helper.setText(R.id.tv_week, item.getWeek());
        }
    };

}
