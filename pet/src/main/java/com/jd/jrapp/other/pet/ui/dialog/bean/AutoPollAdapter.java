package com.jd.jrapp.other.pet.ui.dialog.bean;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jd.jrapp.other.pet.R;

import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/12/31
 * Time: 3:49 PM
 */
public class AutoPollAdapter extends RecyclerView.Adapter<AutoPollAdapter.BaseViewHolder> {
    private final Context mContext;
    private final List<CommitInfo.CommitsBean> mData;

    public AutoPollAdapter(Context context, List<CommitInfo.CommitsBean> list) {
        this.mContext = context;
        this.mData = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.auto_list_item, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CommitInfo.CommitsBean datasBean = mData.get(position % mData.size());
        if (datasBean.isIsCurrentCommit()) {
            holder.tv_comments.setTextColor(mContext.getResources().getColor(R.color.color_ff0000));
        } else {
            holder.tv_comments.setTextColor(mContext.getResources().getColor(R.color.color_0a0a0d));
        }
        holder.tv_comments.setText(datasBean.getContent());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView tv_comments;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tv_comments = itemView.findViewById(R.id.tv_comments);
        }
    }
}
