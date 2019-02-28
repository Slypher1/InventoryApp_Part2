package com.example.android.inventoryapp;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.inventoryapp.databinding.ItemProductListBinding;

public class ProductAdapter extends RecyclerView.Adapter<RowController> {
    LayoutInflater layoutInflater;
    Cursor cursor = null;

    public ProductAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public RowController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductListBinding rowBinding = ItemProductListBinding.inflate(layoutInflater, parent, false);
        return new RowController(rowBinding);
    }

    void setProducts (Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RowController holder, int position) {
        cursor.moveToPosition(position);
        holder.bindModel(cursor);
    }

    @Override
    public int getItemCount() {
        if (cursor == null){
            return 0;
        }
        return cursor.getCount();
    }
}
