package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.PtInfo;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/2
 * Time: 8:33 PM
 */

public class PtDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int width;
    private List<PtInfo> customData;

    public PtDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_pt_dialog, null);
        TextView tv_send = contentView.findViewById(R.id.tv_send);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        customData = new ArrayList<>();
        customData.add(new PtInfo("小顾", R.drawable.icon_pt_1, true));
        customData.add(new PtInfo("小智", R.drawable.icon_pt_2, false));
        customData.add(new PtInfo("小财", R.drawable.icon_pt_3, false));
        customData.add(new PtInfo("小维", R.drawable.icon_pt_4, false));
        customData.add(new PtInfo("小尼", R.drawable.icon_pt_5, false));
        customData.add(new PtInfo("小基", R.drawable.icon_pt_6, false));

        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myAdapter);
        myAdapter.addAll(customData);

        tv_send.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);
        setCanceledOnTouchOutside(true);
        setContentView(contentView);

        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_dialog || id == R.id.tv_send) {
            dismiss();
        }
    }

    RecycleAdapter<PtInfo> myAdapter = new RecycleAdapter<PtInfo>(mContext, R.layout.item_pt_layout) {
        @Override
        protected void convert(BaseAdapterHelper helper, PtInfo item, final int position) {
            helper.setText(R.id.tv_pt, item.getPtName());
            helper.setBackgroundRes(R.id.iv_pt, item.getIcon());
            if(item.isSelected()){
                helper.setBackgroundRes(R.id.rl_pt, R.drawable.icon_pt_select);
            }else {
                helper.setBackgroundRes(R.id.rl_pt, R.drawable.icon_pt_inselect);
            }
            myAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    for (int i = 0; i < customData.size(); i++) {
                        customData.get(i).setSelected(false);
                    }
                    customData.get(position).setSelected(true);
                    notifyDataSetChanged();
                }
            });
        }
    };
}
