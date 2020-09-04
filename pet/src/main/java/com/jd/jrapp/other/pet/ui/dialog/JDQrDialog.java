package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
/**
 * Author: chenghuan15
 * Date: 2020/9/2
 * Time: 8:33 PM
 */

public class JDQrDialog extends Dialog {

    private final Context mContext;
    private int width;

    public JDQrDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = (int) DisplayUtil.getScreenWidth(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_qr_dialog, null);
        ImageView iv_close = contentView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCanceledOnTouchOutside(true);
        setContentView(contentView);

        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
    }


}
