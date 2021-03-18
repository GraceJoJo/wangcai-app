package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.CategoryData;
import com.jd.jrapp.other.pet.ui.dialog.bean.MoneyManagementData;
import com.jd.jrapp.other.pet.ui.view.DragView;
import com.jd.jrapp.other.pet.ui.view.LeftListView;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SharedPrefsMgr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhoujuan26
 * Date: 2021/1/3
 * Time: 9:05 PM
 * 社区
 */

public class CommunityDialog extends Dialog implements View.OnClickListener {

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

    public CommunityDialog(Context context, int zjsy) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        this.zjsy = zjsy;
    }
    boolean isChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpen = SharedPrefsMgr.getInstance(mContext).getBoolean(ISOPEN, false);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        height = (int) DisplayUtil.getScreenHeight(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View contentView = inflater.inflate(R.layout.layout_common_drag_dia, null);
        dragView = contentView.findViewById(R.id.dragView);
        //add的子view的高度需match_parent
        dragView.addDragView(R.layout.layout_community_dialog_new, 0, height - DisplayUtil.dip2px(mContext, 450), width, height, false, false);

//        final View contentView = inflater.inflate(R.layout.layout_community_dialog_new, null);

        lfListview = contentView.findViewById(R.id.lf_listview);
        final ImageView ivList = contentView.findViewById(R.id.iv_list);

        final ImageView ivDanmu = contentView.findViewById(R.id.iv_danmu);
        ivDanmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked){
                    ivDanmu.setImageResource(R.drawable.ic_danmu_on);
                }else {
                    ivDanmu.setImageResource(R.drawable.ic_danmu_off);
                }
                isChecked = !isChecked;
            }
        });

//        rbDanmu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i("TAG","isChecked="+isChecked);
//            }
//        });
//        rbDanmu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        for (int i = 0; i < categoryString.length; i++) {
            CategoryData categoryData = new CategoryData();
            categoryData.content = categoryString[i];
            categoryList.add(categoryData);
        }

        lfListview.setData(categoryList);
        DisplayUtil.fitImage((Activity) mContext, ivList, DisplayUtil.px2dip(mContext, 560), DisplayUtil.px2dip(mContext, 1300));
        lfListview.setOnRightListViewItemClickListener(new LeftListView.OnRightListViewItemClickListener() {
            @Override
            public void onItemClick(boolean isCheck, String title, int position) {
                Log.i("TAG", "---->" + title + "----position-->" + position);
                switch (position) {
                    case 0:
                        ivList.setImageResource(R.drawable.bg_sougou_hot_sale);
                        break;
                    case 1:
                        ivList.setImageResource(R.drawable.bg_sougou_vegetable);
                        break;
                    case 2:
                        ivList.setImageResource(R.drawable.bg_sougou_fruit);
                        break;
                    case 3:
                        ivList.setImageResource(R.drawable.bg_sougou_cold_storage);
                        break;
                    case 4:
                        ivList.setImageResource(R.drawable.bg_sougou_rice);
                        break;
                    case 5:
                        ivList.setImageResource(R.drawable.bg_sougou_seafood);
                        break;
                    case 6:
                        ivList.setImageResource(R.drawable.bg_sougou_meat_egg);
                        break;
                    case 7:
                        ivList.setImageResource(R.drawable.bg_sougou_baking);
                        break;
                }
            }
        });
        scrollview = contentView.findViewById(R.id.scrollview);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);

        tv_cancel.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(contentView);

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
        }
    }

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
