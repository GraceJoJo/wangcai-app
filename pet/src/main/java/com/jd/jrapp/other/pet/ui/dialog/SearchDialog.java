package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.MoneyManagementData;
import com.jd.jrapp.other.pet.ui.view.RefreshScrollView;
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
 * 搜购
 */

public class SearchDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private static final String ISOPEN = "isOpen";
    private int width;
    private ImageView iv_list;
    private TextView cb_all, cb_sales, cb_price;
    private View view_all, view_sales, view_price;
    private boolean isOpen = false;
    private int zjsy = 50;
    private ImageView ivSearch;
    private View llSearchTitle;
    private boolean isRefresh;
    private RefreshScrollView refreshScrollView;

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
        final View contentView = inflater.inflate(R.layout.layout_search_dialog, null);

        refreshScrollView = contentView.findViewById(R.id.refresh_scrollview);
        refreshScrollView.setEnableRefresh(true);
        LinearLayout scrollviewContent = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scrollview_content, null);
        refreshScrollView.setupContainer(mContext, scrollviewContent);
        iv_list = scrollviewContent.findViewById(R.id.iv_content);
        //处理图片拉伸问题
        DisplayUtil.fitImage((Activity) mContext, iv_list, DisplayUtil.px2dip(mContext, 750), DisplayUtil.px2dip(mContext, 3648));
        refreshScrollView.setOnRefreshScrollViewListener(new RefreshScrollView.OnRefreshScrollViewListener() {
            @Override
            public void onRefreshFinish() {
                Log.e("TAG", "刷新完成");
                if (!isRefresh) {
                    iv_list.setImageResource(R.drawable.bg_ulike_refresh_after);
                    isRefresh = true;
                } else {
                    iv_list.setImageResource(R.drawable.bg_ulike_refresh_before);
                    isRefresh = false;
                }
            }

            @Override
            public void refreshing() {
                Log.e("TAG", "正在刷新...");
            }

        });

        cb_all = contentView.findViewById(R.id.cb_all);
        cb_sales = contentView.findViewById(R.id.cb_sales);
        cb_price = contentView.findViewById(R.id.cb_price);


        //处理搜索按钮的状态
        contentView.findViewById(R.id.ll_subview_ulike).setVisibility(View.VISIBLE);
        contentView.findViewById(R.id.ll_subview_search).setVisibility(View.GONE);
        contentView.findViewById(R.id.iv_back).setVisibility(View.GONE);
        ivSearch = contentView.findViewById(R.id.iv_search);
        ivSearch.setImageResource(R.drawable.icon_search);
        ivSearch.setClickable(true);
        iv_list.setImageResource(R.drawable.bg_ulike_refresh_before);

        llSearchTitle = contentView.findViewById(R.id.ll_search_title);
        llSearchTitle.setVisibility(View.GONE);
        //展示搜索框
        contentView.findViewById(R.id.tv_do_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_subview_ulike).setVisibility(View.GONE);
                contentView.findViewById(R.id.ll_subview_search).setVisibility(View.VISIBLE);
                ivSearch.setClickable(false);
                ivSearch.setImageResource(R.drawable.icon_search_unable);
                iv_list.setImageResource(R.drawable.tab_search_all);
            }
        });

        //点击搜索时，展示搜索结果的推荐tab
        contentView.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSearchTitle.setVisibility(View.VISIBLE);
                refreshScrollView.setEnableRefresh(false);
            }
        });
        //返回时回到猜你喜欢
        contentView.findViewById(R.id.iv_back).findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llSearchTitle.getVisibility() == View.VISIBLE) {
                    llSearchTitle.setVisibility(View.GONE);
                    return;
                }
                contentView.findViewById(R.id.ll_subview_ulike).setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_subview_search).setVisibility(View.GONE);
                contentView.findViewById(R.id.iv_back).setVisibility(View.GONE);
                ivSearch = contentView.findViewById(R.id.iv_search);
                ivSearch.setImageResource(R.drawable.icon_search);
                ivSearch.setClickable(true);
                iv_list.setImageResource(R.drawable.bg_ulike_refresh_before);
                llSearchTitle.setVisibility(View.GONE);
                refreshScrollView.setEnableRefresh(true);
            }
        });

        view_all = contentView.findViewById(R.id.view_all);
        view_sales = contentView.findViewById(R.id.view_sales);
        view_price = contentView.findViewById(R.id.view_price);

        cb_all.setSelected(true);
        cb_all.getPaint().setFakeBoldText(true);

        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);

        contentView.findViewById(R.id.ll_price).setOnClickListener(this);
        contentView.findViewById(R.id.ll_sales).setOnClickListener(this);
        contentView.findViewById(R.id.ll_all).setOnClickListener(this);

        tv_cancel.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(contentView);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshScrollView.scrollTo(0, 0);
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
        } else if (v.getId() == R.id.ll_all) {
            iv_list.setImageResource(R.drawable.tab_search_all);

            cb_all.setSelected(true);
            cb_sales.setSelected(false);
            cb_price.setSelected(false);

            cb_all.getPaint().setFakeBoldText(true);
            cb_sales.getPaint().setFakeBoldText(false);
            cb_price.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.VISIBLE);
            view_sales.setVisibility(View.GONE);
            view_price.setVisibility(View.GONE);


//            refreshScrollView.scrollTo(0, 0);

        } else if (v.getId() == R.id.ll_sales) {
            iv_list.setImageResource(R.drawable.tab_search_sales);

            cb_all.setSelected(false);
            cb_sales.setSelected(true);
            cb_price.setSelected(false);

            cb_all.getPaint().setFakeBoldText(false);
            cb_sales.getPaint().setFakeBoldText(true);
            cb_price.getPaint().setFakeBoldText(false);

            view_all.setVisibility(View.GONE);
            view_sales.setVisibility(View.VISIBLE);
            view_price.setVisibility(View.GONE);

//            refreshScrollView.scrollTo(0, 0);

        } else if (v.getId() == R.id.ll_price) {
            iv_list.setImageResource(R.drawable.tab_search_price);

            cb_all.setSelected(false);
            cb_sales.setSelected(false);
            cb_price.setSelected(true);

            cb_all.getPaint().setFakeBoldText(false);
            cb_sales.getPaint().setFakeBoldText(false);
            cb_price.getPaint().setFakeBoldText(true);


            view_all.setVisibility(View.GONE);
            view_sales.setVisibility(View.GONE);
            view_price.setVisibility(View.VISIBLE);

//            refreshScrollView.scrollTo(0, 0);
        }
    }

}
