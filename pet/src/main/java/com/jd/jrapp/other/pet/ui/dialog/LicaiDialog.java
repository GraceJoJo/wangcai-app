package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.PetFloatWindow;
import com.jd.jrapp.other.pet.ui.dialog.bean.MoneyManagementData;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SharedPrefsMgr;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

/**
 * Author: chenghuan15
 * Date: 2020/9/8
 * Time: 4:05 PM
 */

public class LicaiDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private static final String ISOPEN = "isOpen";
    private int width;
    private TextView tv_tips, tv_tips_view, tv_total_assets, tv_zxsy;
    private CheckBox cb_rxph, cb_jxjj, cb_dtyy, iv_eye_zzc, iv_kjxj;
    private View view_rxph, view_jxjj, view_dtyy;
    private ScrollView scrollview;
    private List<MoneyManagementData.RecordsBean> recordsBeanList;
    private List<MoneyManagementData> moneyManagementData;
    private String[] recordStatusDescription = new String[]{"紧跟平台购买趋势，跟着大家买理财", "精选绩优好基，适合长期持有", "专业团队严选，追求收益"};
    private boolean isOpen = false;
    private int zjsy = 50;

    public LicaiDialog(Context context, int zjsy) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        this.zjsy=zjsy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen = SharedPrefsMgr.getInstance(mContext).getBoolean(ISOPEN, false);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_licai_dialog, null);
        iv_eye_zzc = contentView.findViewById(R.id.iv_eye_zzc);
        iv_kjxj = contentView.findViewById(R.id.iv_kjxj);

        tv_zxsy = contentView.findViewById(R.id.tv_zxsy);
        cb_rxph = contentView.findViewById(R.id.cb_rxph);
        cb_jxjj = contentView.findViewById(R.id.cb_jxjj);
        cb_dtyy = contentView.findViewById(R.id.cb_dtyy);
        view_rxph = contentView.findViewById(R.id.view_rxph);
        view_jxjj = contentView.findViewById(R.id.view_jxjj);
        view_dtyy = contentView.findViewById(R.id.view_dtyy);
        tv_tips = contentView.findViewById(R.id.tv_tips);
        tv_tips_view = contentView.findViewById(R.id.tv_tips_view);
        tv_total_assets = contentView.findViewById(R.id.tv_total_assets);
        scrollview = contentView.findViewById(R.id.scrollview);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        moneyManagementData = getJsonData("licai.json");
        recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(0).getRecords();
        hideTotalAssets();
        tv_zxsy.setText("+" +zjsy+".00");
        iv_eye_zzc.setOnClickListener(this);
        iv_kjxj.setOnClickListener(this);
        cb_rxph.setOnClickListener(this);
        cb_jxjj.setOnClickListener(this);
        cb_dtyy.setOnClickListener(this);
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

    public void hideTotalAssets() {
        iv_eye_zzc.setChecked(isOpen);
        if (isOpen) {
            tv_total_assets.setText("28,000.00");
        } else {
            tv_total_assets.setText("****");
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.rl_dialog) {
            dismiss();
        } else if (v.getId() == R.id.iv_eye_zzc) {
            isOpen = !isOpen;
            hideTotalAssets();
        } else if (v.getId() == R.id.iv_kjxj) {
        } else if (v.getId() == R.id.cb_rxph) {
            cb_rxph.setChecked(true);
            cb_dtyy.setChecked(false);
            cb_jxjj.setChecked(false);
            view_rxph.setVisibility(View.VISIBLE);
            view_dtyy.setVisibility(View.GONE);
            view_jxjj.setVisibility(View.GONE);
            tv_tips.setText(recordStatusDescription[0]);
            tv_tips_view.setText(recordStatusDescription[0] + "  ");
            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(0).getRecords();
            myAdapter.addAll(recordsBeanList);

        } else if (v.getId() == R.id.cb_jxjj) {
            cb_rxph.setChecked(false);
            cb_dtyy.setChecked(false);
            cb_jxjj.setChecked(true);
            view_rxph.setVisibility(View.GONE);
            view_dtyy.setVisibility(View.GONE);
            view_jxjj.setVisibility(View.VISIBLE);
            tv_tips.setText(recordStatusDescription[1]);
            tv_tips_view.setText(recordStatusDescription[1] + "  ");
            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(1).getRecords();
            myAdapter.addAll(recordsBeanList);

        } else if (v.getId() == R.id.cb_dtyy) {
            cb_rxph.setChecked(false);
            cb_dtyy.setChecked(true);
            cb_jxjj.setChecked(false);
            view_rxph.setVisibility(View.GONE);
            view_dtyy.setVisibility(View.VISIBLE);
            view_jxjj.setVisibility(View.GONE);
            tv_tips.setText(recordStatusDescription[2]);
            tv_tips_view.setText(recordStatusDescription[2] + "  ");
            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(2).getRecords();
            myAdapter.addAll(recordsBeanList);
        }
    }

    RecycleAdapter<MoneyManagementData.RecordsBean> myAdapter = new RecycleAdapter<MoneyManagementData.RecordsBean>(mContext, R.layout.item_licai_one, recordsBeanList) {
        @Override
        protected void convert(BaseAdapterHelper helper, MoneyManagementData.RecordsBean item, int position) {
            if (position == recordsBeanList.size() - 1) {
                helper.setInVisible(R.id.view_line);
            }
            helper.setText(R.id.tv_rx1, item.getTypeStr());
            helper.setText(R.id.tv_rx2, item.getRank());
            helper.setText(R.id.tv_rx3, item.getInterestRate());
            helper.setText(R.id.tv_rx4, item.getInterestRateDate());
            helper.setText(R.id.tv_rx5, item.getManageType());
            helper.setText(R.id.tv_rx6, item.getManageTypeLimit());
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
