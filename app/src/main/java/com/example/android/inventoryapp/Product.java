package com.example.android.inventoryapp;

import android.databinding.ObservableField;

public class Product {

    public final ObservableField<String> name = new ObservableField<String>();
    public final ObservableField<String> price = new ObservableField<String>();
    public final ObservableField<String> quantity = new ObservableField<String>();

}
