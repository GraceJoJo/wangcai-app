package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class ShouYiDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private int width;
    private EditText et_num;
    private TextView tv_sssy, tv_ssnh, tv_hbjj, tv_gpjj, tv_zqjj;
    private int num = 100;
    private EarningsData earningsData;
    public ImageLoader imageLoader;
    private CountDownTimer mTimer;
    private MyAdapter myAdapter;

    public ShouYiDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                setData(false);

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_shouyi_dialog, null);
        tv_sssy = contentView.findViewById(R.id.tv_sssy);
        tv_ssnh = contentView.findViewById(R.id.tv_ssnh);
        tv_hbjj = contentView.findViewById(R.id.tv_hbjj);
        tv_gpjj = contentView.findViewById(R.id.tv_gpjj);
        tv_zqjj = contentView.findViewById(R.id.tv_zqjj);
        et_num = contentView.findViewById(R.id.et_num);
        ImageView tv_add = contentView.findViewById(R.id.tv_add);
        ImageView tv_minus = contentView.findViewById(R.id.tv_minus);
        TextView tv_confirm = contentView.findViewById(R.id.tv_confirm);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        ListView lv = contentView.findViewById(R.id.lv);

        setData(true);
        myAdapter = new MyAdapter(getContext(), R.layout.item_shouyi_layout, earningsData.getRecords());
        lv.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(lv);
        et_num.setCursorVisible(false);

        tv_add.setOnClickListener(this);
        tv_minus.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rl_dialog.setOnClickListener(this);
        et_num.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    et_num.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });

        setCanceledOnTouchOutside(true);
        setContentView(contentView);

        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        startTimer();
    }


    public void startTimer() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(100);
            }
        }, 10 * 1000);
    }

    public void setData(boolean isFirst) {
        int number = (int) Math.floor(new Random().nextInt(10) + 1);
        earningsData = getJsonData("record" + number + ".json");
        if (myAdapter != null && tv_sssy != null) {
            tv_sssy.setText(earningsData.getSssy());
            tv_ssnh.setText(earningsData.getSsnh());
            tv_hbjj.setText(earningsData.getHbjj());
            tv_gpjj.setText(earningsData.getGpjj());
            tv_zqjj.setText(earningsData.getZqjj());
            if (!isFirst) {
                myAdapter.setData(earningsData.getRecords());
                startTimer();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_minus) {
            if (num > 1) {
                num--;
                setNum(num);
            } else {
                setNum(1);
            }
        } else if (v.getId() == R.id.tv_add) {
            num = getNum() / 100;
            if (num > 1000) {
                return;
            }
            num++;
            setNum(num);

        } else if (v.getId() == R.id.tv_confirm) {

        } else if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.rl_dialog) {
            dismiss();
        }
    }

    public int getNum() {
        String numText = et_num.getText().toString().trim();
        if (TextUtils.isEmpty(numText)) {
            return 0;
        }
        return Integer.valueOf(numText);
    }

    public void setNum(int num) {
        et_num.setText(num * 100 + "");
    }

    public class MyAdapter extends ArrayAdapter<EarningsData.RecordsBean> {
        private List<EarningsData.RecordsBean> objects;
        private int resourceId;

        public void setData(List<EarningsData.RecordsBean> objects) {
            this.objects = objects;
            notifyDataSetChanged();
        }

        public MyAdapter(Context context, int resourceId, List<EarningsData.RecordsBean> objects) {
            super(context, resourceId);
            this.objects = objects;
            this.resourceId = resourceId;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            final EarningsData.RecordsBean earningsInfo = objects.get(position);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_shouyi = view.findViewById(R.id.tv_shouyi);
            ImageView iv_header = view.findViewById(R.id.iv_header);
            View view_line = view.findViewById(R.id.view_line);
            if (position == objects.size() - 1) {
                view_line.setVisibility(View.INVISIBLE);
            }
            tv_name.setText(earningsInfo.getNickName());
            tv_shouyi.setText(earningsInfo.getSssyl());
            imageLoader.displayImage(earningsInfo.getHeaderUrl(), iv_header);

            return view;

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

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
