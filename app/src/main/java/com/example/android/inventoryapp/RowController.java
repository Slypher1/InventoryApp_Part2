package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.inventoryapp.databinding.ItemProductListBinding;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class RowController extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ItemProductListBinding rowBinding;
    private Uri currentUri = null;

    public RowController(ItemProductListBinding rowBinding) {
        super(rowBinding.getRoot());
        this.rowBinding = rowBinding;
        rowBinding.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), EditorProductActivity.class);
        intent.setData(currentUri);
        view.getContext().startActivity(intent);
    }

    void bindModel(Cursor cursor){

        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
        String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        int productPriceInt = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        // convert cents --> unit
        double productPriceFloat = productPriceInt / 100.00;

        String productPrice = correctPriceString(String.valueOf(productPriceFloat));

        final int productQuantityInt = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String productQuantity = String.valueOf(productQuantityInt);

        final Product product = new Product();
        product.name.set(productName);
        product.price.set(correctPriceString(productPrice));
        product.quantity.set(productQuantity);

        rowBinding.setProduct(product);

        rowBinding.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) v.getContext();
                activity.productSale(productId, productQuantityInt);
            }
        });

        rowBinding.executePendingBindings();
    }

    public String correctPriceString(String price){
        int delta = price.length() - price.indexOf(".");
        if (delta == 2){
            price = price + "0";
        }
        return price;
    }
}
