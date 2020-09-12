package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.CustomData;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

/**
 * Author: chenghuan15
 * Date: 2020/9/2
 * Time: 8:33 PM
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int width;
    private List<CustomData> customData;
    private ItemClickCallback itemClickCallback;
    private int[] icons1 = {R.drawable.icon_syms, R.drawable.icon_syms, R.drawable.icon_syms};
    private int[] icons2 = {R.drawable.icon_bt, R.drawable.icon_bt, R.drawable.icon_jt, R.drawable.icon_jj, R.drawable.icon_bx};
    private int[] icons3 = {R.drawable.icon_zxyh, R.drawable.icon_zxyh, R.drawable.icon_zxyh, R.drawable.icon_bjyh, R.drawable.icon_add};

    public CustomDialog(Context context, ItemClickCallback itemClickCallback) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        this.itemClickCallback=itemClickCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_custom_dialog, null);
        TextView tv_close = contentView.findViewById(R.id.tv_close);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        customData = getJsonData("custom.json");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myAdapter);
        myAdapter.addAll(customData);

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
        if (id == R.id.tv_close || id == R.id.rl_dialog) {
            dismiss();
        }
    }

    RecycleAdapter<CustomData> myAdapter = new RecycleAdapter<CustomData>(mContext, R.layout.item_custom_text_layout, customData) {
        @Override
        protected void convert(BaseAdapterHelper helper, final CustomData customItemData, int position) {
            helper.setText(R.id.tv_title, customItemData.getTitle());
            RecyclerView recyclerView = helper.getView(R.id.recyclerView_item);
            GridLayoutManager manager = new GridLayoutManager(mContext, 5);
            recyclerView.setLayoutManager(manager);
            RecycleAdapter<CustomData.RecordsBean> myItemAdapter = new RecycleAdapter<CustomData.RecordsBean>(mContext, R.layout.item_custom_layout) {
                @Override
                protected void convert(BaseAdapterHelper helper, CustomData.RecordsBean item, final int position) {
                    if (item.getType() == 0) {
                        if (position == 3 || position == 4) {
                            helper.setInVisible(R.id.ll_sign);
                        }else {
                            helper.setVisible(R.id.ll_sign, true);
                            helper.setBackgroundRes(R.id.iv_ms, icons1[position]);
                        }
                        helper.setOnClickListener(R.id.ll_sign, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemClickCallback.clickCallback(position);
                            }
                        });
                    } else if (item.getType() == 1) {
                        helper.setBackgroundRes(R.id.iv_ms, icons2[position]);
                    } else if (item.getType() == 2) {
                        helper.setBackgroundRes(R.id.iv_ms, icons3[position]);
                    }
                    helper.setText(R.id.tv_ms, item.getIconStr());

                }
            };
            recyclerView.setAdapter(myItemAdapter);
            myItemAdapter.addAll(customItemData.getRecords());
        }
    };

    public List<CustomData> getJsonData(String fileName) {
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
            List<CustomData> moneyManagementData = (List<CustomData>) AppManager.fromJson(countryJson, new TypeToken<List<CustomData>>() {
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

    public interface ItemClickCallback{
        void clickCallback(int type);
    }

}
