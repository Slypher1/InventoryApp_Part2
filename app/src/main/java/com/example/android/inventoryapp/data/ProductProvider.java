package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    // constant code for uri matcher
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    // database heelper object
    private ProductDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize the provider and the database helper object.
        dbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                // read database
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_query_unknown_uri, uri));

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getString(R.string.error_type_unknown_uri, uri, String.valueOf(match)));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = uriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_unknown_uri, uri));
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        String productName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        Integer productPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        Integer productQuantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);

        //  check the product's name is not null
        if(productName == null){
            throw new IllegalArgumentException(getContext().getString(R.string.error_insert_name));
        }
        // check the product's price is not null and > 0
        if((productPrice != null) && (productPrice < 0)){
            throw new IllegalArgumentException(getContext().getString(R.string.error_insert_price));
        }
        // check the product's quantity is not null and >= 0
        if((productQuantity != null) && (productQuantity < 0)){
            throw new IllegalArgumentException(getContext().getString(R.string.error_insert_quantity));
        }
        //  check the supplier's name is not null
        if(supplierName == null){
            throw new IllegalArgumentException(getContext().getString(R.string.error_insert_supplier_name));
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);

        if(id == -1){
            return null;
        }

        //notify change on the database
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleteRow;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                // Delete all row
                deleteRow = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                deleteRow = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_delete_unknown_uri, uri));
        }

        if (deleteRow!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_unknown_uri, uri));
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // check if values is present
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)){
            // Check that the name is not null
            String productName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_name));
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)){
            Integer productPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            // check the product's price is not null and > 0
            if((productPrice != null) && (productPrice < 0)){
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_price));
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)){
            Integer productQuantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if((productQuantity != null) && (productQuantity < 0)){
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_quantity));
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)){
            String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if(supplierName == null){
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_supplier_name));
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)){
            String phoneNumberSupplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if(phoneNumberSupplier == null){
                throw new IllegalArgumentException(getContext().getString(R.string.error_insert_phone_number_supplier_name));
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Integer rowUpdate = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowUpdate!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdate;
    }
}
