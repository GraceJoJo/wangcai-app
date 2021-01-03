package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
 * Author: zhoujuan26
 * Date: 2021/1/3
 * Time: 11:12 PM
 */

public class SearchDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private static final String ISOPEN = "isOpen";
    private int width;
    private TextView cb_all, cb_sales, cb_price;
    private View view_all, view_sales, view_price;
    private ScrollView scrollview;
    private List<MoneyManagementData.RecordsBean> recordsBeanList;
    private List<MoneyManagementData> moneyManagementData;
    private boolean isOpen = false;
    private int zjsy = 50;

    public SearchDialog(Context context, int zjsy) {
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
        View contentView = inflater.inflate(R.layout.layout_search_dialog, null);

        cb_all = contentView.findViewById(R.id.cb_all);
        cb_sales = contentView.findViewById(R.id.cb_sales);
        cb_price = contentView.findViewById(R.id.cb_price);

        view_all = contentView.findViewById(R.id.view_all);
        view_sales= contentView.findViewById(R.id.view_sales);
        view_price = contentView.findViewById(R.id.view_price);

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
        cb_sales.setOnClickListener(this);
        cb_price.setOnClickListener(this);

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
            cb_sales.setSelected(false);
            cb_price.setSelected(false);

            cb_all.getPaint().setFakeBoldText(true);
            cb_sales.getPaint().setFakeBoldText(false);
            cb_price.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.VISIBLE);
            view_sales.setVisibility(View.GONE);
            view_price.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(0).getRecords();
            myAdapter.addAll(recordsBeanList);

            scrollview.scrollTo(0, 0);

        } else if (v.getId() == R.id.cb_sales) {
            cb_all.setSelected(false);
            cb_sales.setSelected(true);
            cb_price.setSelected(false);

            cb_all.getPaint().setFakeBoldText(false);
            cb_sales.getPaint().setFakeBoldText(true);
            cb_price.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.GONE);
            view_sales.setVisibility(View.VISIBLE);
            view_price.setVisibility(View.GONE);

            myAdapter.clear();
            recordsBeanList = (List<MoneyManagementData.RecordsBean>) moneyManagementData.get(1).getRecords();
            myAdapter.addAll(recordsBeanList);
            scrollview.scrollTo(0, 0);

        } else if (v.getId() == R.id.cb_price) {
            cb_all.setSelected(false);
            cb_sales.setSelected(false);
            cb_price.setSelected(true);

            cb_all.getPaint().setFakeBoldText(false);
            cb_sales.getPaint().setFakeBoldText(false);
            cb_price.getPaint().setFakeBoldText(true);


            view_all.setVisibility(View.GONE);
            view_sales.setVisibility(View.GONE);
            view_price.setVisibility(View.VISIBLE);

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
