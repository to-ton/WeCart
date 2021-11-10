package com.lobomarket.wecart;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public abstract class MyRecyclerScroll extends OnScrollListener {

    int scorllDist = 0;
    boolean isVisble = true;
    static final float MINIMUM = 25;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(isVisble && scorllDist > MINIMUM){
            hide();
            scorllDist = 0;
            isVisble = false;
        } else if (!isVisble && scorllDist < -MINIMUM){
            show();
            scorllDist = 0;
            isVisble = true;
        }

        if ((isVisble && dy > 0) || (!isVisble && dy < 0)){
            scorllDist += dy;
        }
    }

    public abstract void show();
    public abstract void hide();
}
