package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        ListView lv = contentView.findViewById(R.id.lv);

        List<EarningsInfo> earningsInfos = new ArrayList<>();
        earningsInfos.add(new EarningsInfo("一颗柠檬靖", "235.22%"));
        earningsInfos.add(new EarningsInfo("争当小锦鲤", "+194.93%"));
        earningsInfos.add(new EarningsInfo("睡到自然醒", "+94.12%"));
        lv.setAdapter(new MyAdapter(getContext(), R.layout.item_shouyi_layout, earningsInfos));

        tv_add.setOnClickListener(this);
        tv_minus.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

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
            tv_name.setText(earningsInfo.getName());
            tv_shouyi.setText(earningsInfo.getSyl());

            return view;

        }
    }

}
