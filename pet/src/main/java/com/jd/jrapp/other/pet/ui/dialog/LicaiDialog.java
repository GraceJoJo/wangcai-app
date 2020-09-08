package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.EarningsData;
import com.jd.jrapp.other.pet.ui.dialog.bean.SignInfo;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/8
 * Time: 4:05 PM
 */

public class LicaiDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int width;
    private TextView tv_sssy, tv_ssnh, tv_hbjj, tv_gpjj, tv_zqjj;
    private ImageView iv_eye_zzc, iv_kjxj;
    private CheckBox cb_rxph, cb_jxjj, cb_dtyy;
    private View view_rxph, view_jxjj, view_dtyy;
    private ScrollView scrollview;
    public ImageLoader imageLoader;
    private List<SignInfo> signInfos;

    public LicaiDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_licai_dialog, null);
        iv_eye_zzc = contentView.findViewById(R.id.iv_eye_zzc);
        iv_kjxj = contentView.findViewById(R.id.iv_kjxj);

        cb_rxph = contentView.findViewById(R.id.cb_rxph);
        cb_jxjj = contentView.findViewById(R.id.cb_jxjj);
        cb_dtyy = contentView.findViewById(R.id.cb_dtyy);
        view_rxph = contentView.findViewById(R.id.view_rxph);
        view_jxjj = contentView.findViewById(R.id.view_jxjj);
        view_dtyy = contentView.findViewById(R.id.view_dtyy);
        scrollview = contentView.findViewById(R.id.scrollview);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        //0已签到 1未签到 2补签
        signInfos = new ArrayList<>();
        signInfos.add(new SignInfo("周一", 0));
        signInfos.add(new SignInfo("周五", 1));
        signInfos.add(new SignInfo("周六", 1));
        signInfos.add(new SignInfo("周日", 1));

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
        myAdapter.addAll(signInfos);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.scrollTo(0,0);
            }
        },300);
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
            dismiss();
        }else if (v.getId() == R.id.cb_rxph){
            cb_rxph.setChecked(true);
            cb_dtyy.setChecked(false);
            cb_jxjj.setChecked(false);
            view_rxph.setVisibility(View.VISIBLE);
            view_dtyy.setVisibility(View.GONE);
            view_jxjj.setVisibility(View.GONE);
        }else if (v.getId() == R.id.cb_jxjj){
            cb_rxph.setChecked(false);
            cb_dtyy.setChecked(false);
            cb_jxjj.setChecked(true);
            view_rxph.setVisibility(View.GONE);
            view_dtyy.setVisibility(View.GONE);
            view_jxjj.setVisibility(View.VISIBLE);
        }else if (v.getId() == R.id.cb_dtyy){
            cb_rxph.setChecked(false);
            cb_dtyy.setChecked(true);
            cb_jxjj.setChecked(false);
            view_rxph.setVisibility(View.GONE);
            view_dtyy.setVisibility(View.VISIBLE);
            view_jxjj.setVisibility(View.GONE);
        }
    }

    RecycleAdapter<SignInfo> myAdapter = new RecycleAdapter<SignInfo>(mContext, R.layout.item_licai_one, signInfos) {
        @Override
        protected void convert(BaseAdapterHelper helper, SignInfo item, int position) {
        }
    };


    public EarningsData getJsonData(String fileName) {
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
            EarningsData earningsData = (EarningsData) AppManager.fromJson(countryJson, new TypeToken<EarningsData>() {
            }.getType());
            return earningsData;
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
