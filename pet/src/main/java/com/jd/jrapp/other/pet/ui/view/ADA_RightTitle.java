package com.jd.jrapp.other.pet.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.dialog.bean.CategoryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2017/6/22.
 * 邮箱：zjuan@yonyou.com
 * 描述：右侧导航栏-Adapter
 * 此版本解决了左侧列表点菜时，右侧刷新的title位置如果不可见时，单条刷新不起作用的bug.
 * 思路:当匹配的右侧刷新的位置不在列表可见范围内时，先滚动到可见范围内，再调用getView()进行单条刷新
 */
public class ADA_RightTitle extends BaseAdapter {
    private Context mContext;
    private List<CategoryData> mDishTypes = new ArrayList<>();
    private int mSelectedPos;
    private boolean isRefreshCount;
    //标记是否是因为刷新造成的滑动，去掉滑动右侧标题栏的刷新影响
    private boolean isRefresh;

    public ADA_RightTitle(Context context, List<CategoryData> datas) {
        this.mContext = context;
        this.mDishTypes = datas;
    }

    @Override
    public int getCount() {
        return mDishTypes == null ? 0 : mDishTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mDishTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_title, null);
            holder.rlBadgt = (RelativeLayout) convertView.findViewById(R.id.rl_badge);
            holder.ll_item_tab = convertView.findViewById(R.id.ll_item_tab);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.right_line = (View) convertView.findViewById(R.id.right_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        //设置初始值
        holder.tv_title.setText(mDishTypes.get(position).content);


        //更新右上角标的数量
        if (isRefreshCount) {
            holder.rlBadgt.setVisibility(View.VISIBLE);
            holder.tv_title.setText(mDishTypes.get(position).content);
            String s = holder.tv_count.getText().toString();
            if (!TextUtils.isEmpty(s)) {
                holder.tv_count.setText(Integer.parseInt(s) + 1 + "");
                //回传右侧标题当前对应的角标的数量
                if (refreshCurrentItemListener != null) {
                    refreshCurrentItemListener.onRefreshCount(position, Integer.parseInt(s) + 1);
                }
            }
            isRefreshCount = false;
        } else {
            //不刷新有角标才设置title的选中效果
            if (mSelectedPos == position) {
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_3AC270));
                holder.ll_item_tab.setBackgroundResource(R.color.color_ffffff);
                holder.right_line.setVisibility(View.VISIBLE);
            } else {
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_222222));
                holder.ll_item_tab.setBackgroundResource(R.color.color_eeeeee);
                holder.right_line.setVisibility(View.GONE);
            }
        }
//        //控制角标是否显示
        if (holder.tv_count.getText().equals("0")) {
            holder.rlBadgt.setVisibility(View.INVISIBLE);
        } else {
            holder.rlBadgt.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * 设置当前选中的item的位置
     *
     * @param position
     */

    public void setSelectItem(int position) {
        this.mSelectedPos = position;
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_count;
        View right_line;
        RelativeLayout rlBadgt;
        LinearLayout ll_item_tab;
    }

//    /**
//     * listView单条刷新
//     *
//     * @param listView
//     * @param id
//     */
//    public void updateSingleRow(final ListView listView, final String id) {
//        //找到id匹配的item的位置
//        int start = listView.getFirstVisiblePosition();
//        int end = listView.getLastVisiblePosition();
//        if (listView != null) {
//            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
//                DishTypesEntity dishTypesEntity = (DishTypesEntity) listView.getItemAtPosition(i);
//                if (dishTypesEntity == null || dishTypesEntity.relateId == null) {
//                    break;
//                }
//                if (id == null) {
//                    break;
//                }
//                if (id.equals(dishTypesEntity.relateId)) {
//                    //如果刷新的位置在可见范围内
//                    if (i >= start && i <= end) {
//                        View view = listView.getChildAt(i - start);
//                        isRefreshCount = true;
//                        getView(i, view, listView);
//                    } else {
//                        listView.smoothScrollToPosition(i);
//                        isRefresh = true;
//                        isRefreshCount = false;
//                    }
//                }
//            }
//        }
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (isRefresh) {
//                    if (scrollState == SCROLL_STATE_IDLE) {
//                        //单条刷新
//                        int start = listView.getFirstVisiblePosition();
//                        for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++) {
//                            DishTypesEntity dishTypesEntity = (DishTypesEntity) listView.getItemAtPosition(i);
//                            if (id.equals(dishTypesEntity.relateId)) {
//                                View views = listView.getChildAt(i - start);
//                                isRefreshCount = true;
//                                getView(i, views, listView);
//                                isRefresh = false;
//                                if (onDoAnimListener != null) {
//                                    onDoAnimListener.doAnim();
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
//    }

//    /**
//     * 刷新右上角标的数量
//     */
//    public void refreshCount(ListView listView, String id, boolean refreshCount) {
//        this.isRefreshCount = refreshCount;
//        updateSingleRow(listView, id);
//    }

    /**
     * 刷新数据
     *
     * @param mData
     */
    public void refreshData(List<CategoryData> mData) {
        this.mDishTypes = mData;
        notifyDataSetChanged();
    }

    private OnRefreshCurrentItemListener refreshCurrentItemListener;

    public interface OnRefreshCurrentItemListener {
        void onRefreshCount(int refreshPos, int curCount);
    }

    public void setRefreshCurrentItemListener(OnRefreshCurrentItemListener refreshCurrentItemListener) {
        this.refreshCurrentItemListener = refreshCurrentItemListener;
    }

    public interface OnDoAnimListener {
        void doAnim();
    }

    private OnDoAnimListener onDoAnimListener;

    public void setOnDoAnimListener(OnDoAnimListener onDoAnimListener) {
        this.onDoAnimListener = onDoAnimListener;
    }
}
