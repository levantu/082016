package com.julio.smartkey.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tulv2 on 8/11/2016.
 */
public class BaseHolder extends RecyclerView.ViewHolder {

    private View itemView;

    public BaseHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public View getItemView() {
        return itemView;
    }
}
