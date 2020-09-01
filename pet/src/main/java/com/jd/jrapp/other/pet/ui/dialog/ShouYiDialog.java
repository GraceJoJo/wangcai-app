package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class ShouYiDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private int width;
    private EditText et_num;
    int num = 100;

    public ShouYiDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_shouyi_dialog, null);
        TextView tv_sssy = contentView.findViewById(R.id.tv_sssy);
        TextView tv_ssnh = contentView.findViewById(R.id.tv_ssnh);
        TextView tv_hbjj = contentView.findViewById(R.id.tv_hbjj);
        TextView tv_gpjj = contentView.findViewById(R.id.tv_gpjj);
        TextView tv_zqjj = contentView.findViewById(R.id.tv_zqjj);
        et_num = contentView.findViewById(R.id.et_num);
        ImageView tv_add = contentView.findViewById(R.id.tv_add);
        ImageView tv_minus = contentView.findViewById(R.id.tv_minus);
        TextView tv_confirm = contentView.findViewById(R.id.tv_confirm);
        TextView tv_cancel = contentView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_dialog = contentView.findViewById(R.id.rl_dialog);
        ListView lv = contentView.findViewById(R.id.lv);

        List<EarningsInfo> earningsInfos = new ArrayList<>();
        earningsInfos.add(new EarningsInfo("一颗柠檬靖", "235.22%"));
        earningsInfos.add(new EarningsInfo("争当小锦鲤", "+194.93%"));
        earningsInfos.add(new EarningsInfo("睡到自然醒", "+94.12%"));
        lv.setAdapter(new MyAdapter(getContext(), R.layout.item_shouyi_layout, earningsInfos));
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
        }else if (v.getId() == R.id.rl_dialog) {
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

    public class MyAdapter extends ArrayAdapter<EarningsInfo> {
        private List<EarningsInfo> objects;
        private int resourceId;

        public MyAdapter(Context context, int resourceId, List<EarningsInfo> objects) {
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
            EarningsInfo earningsInfo = objects.get(position);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_shouyi = view.findViewById(R.id.tv_shouyi);
            View view_line = view.findViewById(R.id.view_line);
            if(position==objects.size()-1){
                view_line.setVisibility(View.INVISIBLE);
            }
            tv_name.setText(earningsInfo.getName());
            tv_shouyi.setText(earningsInfo.getSyl());

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

}
