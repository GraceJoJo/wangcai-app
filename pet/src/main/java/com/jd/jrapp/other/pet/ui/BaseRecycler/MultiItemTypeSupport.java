package com.jd.jrapp.other.pet.ui.BaseRecycler;

public interface MultiItemTypeSupport<T> {

    int getLayoutId(int viewType);

    int getItemViewType(int position, T t);

}
