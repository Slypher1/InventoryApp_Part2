package com.example.android.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView = null;

    public void setAdapter(RecyclerView.Adapter adapter){
        getRecyclerView().setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return(getRecyclerView().getAdapter());
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        getRecyclerView().setLayoutManager(layoutManager);
    }

    public RecyclerView getRecyclerView() {
        if (recyclerView==null) {
            recyclerView=new RecyclerView(this);
            setContentView(recyclerView);
        }

        return(recyclerView);
    }
}
