package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.MoneyManagementData;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SharedPrefsMgr;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/8
 * Time: 4:05 PM
 */

public class OrderDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private static final String ISOPEN = "isOpen";
    private int width;
    private TextView cb_all, cb_pay, cb_shouhuo, cb_pingjia, cb_tuihuan;
    private View view_all, view_pay, view_shouhuo, view_pingjia, view_tuihuan;
    private ScrollView scrollview;
    private List<MoneyManagementData.RecordsBean> recordsBeanList;
    private List<MoneyManagementData> moneyManagementData;
    private String[] recordStatusDescription = new String[]{"紧跟平台购买趋势，跟着大家买理财", "精选绩优好基，适合长期持有", "专业团队严选，追求收益"};
    private boolean isOpen = false;
    private int zjsy = 50;

    public OrderDialog(Context context, int zjsy) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        this.zjsy = zjsy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen = SharedPrefsMgr.getInstance(mContext).getBoolean(ISOPEN, false);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_order_dialog, null);

        ImageView iv_list = contentView.findViewById(R.id.iv_list);
        DisplayUtil.fitImage((Activity) mContext,iv_list,DisplayUtil.px2dip(mContext,750),DisplayUtil.px2dip(mContext,3648));

        cb_all = contentView.findViewById(R.id.cb_all);
        cb_pay = contentView.findViewById(R.id.cb_pay);
        cb_shouhuo = contentView.findViewById(R.id.cb_shouhuo);
        cb_pingjia = contentView.findViewById(R.id.cb_pingjia);
        cb_tuihuan = contentView.findViewById(R.id.cb_tuihuan);

        view_all = contentView.findViewById(R.id.view_all);
        view_pay = contentView.findViewById(R.id.view_pay);
        view_shouhuo = contentView.findViewById(R.id.view_shouhuo);
        view_pingjia = contentView.findViewById(R.id.view_pingjia);
        view_tuihuan = contentView.findViewById(R.id.view_tuihuan);

        cb_all.setSelected(true);
        cb_all.getPaint().setFakeBoldText(true);

        scrollview = contentView.findViewById(R.id.scrollview);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        moneyManagementData = getJsonData("order.json");
        recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(0).getRecords();

        cb_all.setOnClickListener(this);
        cb_pay.setOnClickListener(this);
        cb_shouhuo.setOnClickListener(this);
        cb_pingjia.setOnClickListener(this);
        cb_tuihuan.setOnClickListener(this);

        tv_cancel.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(contentView);
        recyclerView.setAdapter(myAdapter);
        myAdapter.addAll(recordsBeanList);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.scrollTo(0, 0);
            }
        }, 300);
        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
    }


    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.rl_dialog) {
//            dismiss();
        } else if (v.getId() == R.id.cb_all) {
            cb_all.setSelected(true);
            cb_pay.setSelected(false);
            cb_shouhuo.setSelected(false);
            cb_pingjia.setSelected(false);
            cb_tuihuan.setSelected(false);

            cb_all.getPaint().setFakeBoldText(true);
            cb_pay.getPaint().setFakeBoldText(false);
            cb_shouhuo.getPaint().setFakeBoldText(false);
            cb_pingjia.getPaint().setFakeBoldText(false);
            cb_tuihuan.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.VISIBLE);
            view_pay.setVisibility(View.GONE);
            view_shouhuo.setVisibility(View.GONE);
            view_pingjia.setVisibility(View.GONE);
            view_tuihuan.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(0).getRecords();
            myAdapter.addAll(recordsBeanList);

            scrollview.scrollTo(0, 0);

        } else if (v.getId() == R.id.cb_pay) {
            cb_all.setSelected(false);
            cb_pay.setSelected(true);
            cb_shouhuo.setSelected(false);
            cb_pingjia.setSelected(false);
            cb_tuihuan.setSelected(false);

            cb_all.getPaint().setFakeBoldText(false);
            cb_pay.getPaint().setFakeBoldText(true);
            cb_shouhuo.getPaint().setFakeBoldText(false);
            cb_pingjia.getPaint().setFakeBoldText(false);
            cb_tuihuan.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.GONE);
            view_pay.setVisibility(View.VISIBLE);
            view_shouhuo.setVisibility(View.GONE);
            view_pingjia.setVisibility(View.GONE);
            view_tuihuan.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(1).getRecords();
            myAdapter.addAll(recordsBeanList);
            scrollview.scrollTo(0, 0);

        } else if (v.getId() == R.id.cb_shouhuo) {
            cb_all.setSelected(false);
            cb_pay.setSelected(false);
            cb_shouhuo.setSelected(true);
            cb_pingjia.setSelected(false);
            cb_tuihuan.setSelected(false);

            cb_all.getPaint().setFakeBoldText(false);
            cb_pay.getPaint().setFakeBoldText(false);
            cb_shouhuo.getPaint().setFakeBoldText(true);
            cb_pingjia.getPaint().setFakeBoldText(false);
            cb_tuihuan.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.GONE);
            view_pay.setVisibility(View.GONE);
            view_shouhuo.setVisibility(View.VISIBLE);
            view_pingjia.setVisibility(View.GONE);
            view_tuihuan.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(2).getRecords();
            myAdapter.addAll(recordsBeanList);
            scrollview.scrollTo(0, 0);
        } else if (v.getId() == R.id.cb_pingjia) {
            cb_all.setSelected(false);
            cb_pay.setSelected(false);
            cb_shouhuo.setSelected(false);
            cb_pingjia.setSelected(true);
            cb_tuihuan.setSelected(false);

            cb_all.getPaint().setFakeBoldText(false);
            cb_pay.getPaint().setFakeBoldText(false);
            cb_shouhuo.getPaint().setFakeBoldText(false);
            cb_pingjia.getPaint().setFakeBoldText(true);
            cb_tuihuan.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.GONE);
            view_pay.setVisibility(View.GONE);
            view_shouhuo.setVisibility(View.GONE);
            view_pingjia.setVisibility(View.VISIBLE);
            view_tuihuan.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(2).getRecords();
            myAdapter.addAll(recordsBeanList);
            scrollview.scrollTo(0, 0);
        } else if (v.getId() == R.id.cb_tuihuan) {
            cb_all.setSelected(false);
            cb_pay.setSelected(false);
            cb_shouhuo.setSelected(false);
            cb_pingjia.setSelected(false);
            cb_tuihuan.setSelected(true);

            cb_all.getPaint().setFakeBoldText(false);
            cb_pay.getPaint().setFakeBoldText(false);
            cb_shouhuo.getPaint().setFakeBoldText(false);
            cb_pingjia.getPaint().setFakeBoldText(false);
            cb_tuihuan.getPaint().setFakeBoldText(true);

            view_all.setVisibility(View.GONE);
            view_pay.setVisibility(View.GONE);
            view_shouhuo.setVisibility(View.GONE);
            view_pingjia.setVisibility(View.GONE);
            view_tuihuan.setVisibility(View.VISIBLE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(2).getRecords();
            myAdapter.addAll(recordsBeanList);
            scrollview.scrollTo(0, 0);
        }
    }

    private void  updateData(){
        for (int i = 0; i < Math.random(); i++) {
            recordsBeanList.add(new MoneyManagementData.RecordsBean());
        }
    }
    RecycleAdapter<MoneyManagementData.RecordsBean> myAdapter = new RecycleAdapter<MoneyManagementData.RecordsBean>(mContext, R.layout.item_order_one, recordsBeanList) {
        @Override
        protected void convert(BaseAdapterHelper helper, MoneyManagementData.RecordsBean item, int position) {
            if (position == recordsBeanList.size() - 1) {
                helper.setInVisible(R.id.view_line);
            }
//            helper.setText(R.id.tv_rx1, item.getTypeStr());
//            helper.setText(R.id.tv_rx2, item.getRank());
//            helper.setText(R.id.tv_rx3, item.getInterestRate());
//            helper.setText(R.id.tv_rx4, item.getInterestRateDate());
//            helper.setText(R.id.tv_rx5, item.getManageType());
//            helper.setText(R.id.tv_rx6, item.getManageTypeLimit());
        }
    };


    public List<MoneyManagementData> getJsonData(String fileName) {
        InputStream in = null;
        try {
            in = getContext().getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            String countryJson = new String(buffer, "utf-8");
            List<MoneyManagementData> moneyManagementData = (List<MoneyManagementData>) AppManager.fromJson(countryJson, new TypeToken<List<MoneyManagementData>>() {
            }.getType());
            return moneyManagementData;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
