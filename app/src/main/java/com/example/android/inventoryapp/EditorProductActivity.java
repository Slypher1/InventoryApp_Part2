package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.databinding.ActivityEditorProductBinding;

import static java.lang.Integer.parseInt;

public class EditorProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <Cursor> {

    private static final int PRODUCT_LOADER = 1;
    ActivityEditorProductBinding binding;
    Uri currentUriProduct;
    private boolean productHasChanged = false;
    int quantity;

    // Noted modify the data
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor_product);

        currentUriProduct = getIntent().getData();
        // if currentUriProduct is null, i set the activity for the new entry otherwise i set for
        // the edit product selected
        if (currentUriProduct == null){
            setTitle(R.string.editor_title_new_product);
            quantity = 0;
            binding.quantityEditText.setText(String.valueOf(quantity));
            binding.contactSupplierButton.setVisibility(View.GONE);
        }
        else {
            setTitle(R.string.editor_title_edit_product);
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        binding.productNameEditText.setOnTouchListener(touchListener);
        binding.priceEditText.setOnTouchListener(touchListener);
        binding.quantityEditText.setOnTouchListener(touchListener);
        binding.supplierNameEditText.setOnTouchListener(touchListener);
        binding.supplierPhoneNumberEditText.setOnTouchListener(touchListener);

        binding.productNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tilProductName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tilPrice.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tilQuantity.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.supplierNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tilSupplierName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.supplierPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tilSupplierPhone.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(binding.quantityEditText.getText().toString());
                if(quantity > 0){
                    quantity--;
                    binding.quantityEditText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.error_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(binding.quantityEditText.getText().toString());
                quantity++;
                binding.quantityEditText.setText(String.valueOf(quantity));
            }
        });

        binding.contactSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + binding.supplierPhoneNumberEditText.getText().toString()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor_product, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this is a new product, hide the "Delete" menu item.
        if (currentUriProduct == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                if(saveProduct()){
                    // Exit activity
                    finish();
                }

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                if(!productHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorProductActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUriProduct != null){
            int deleteRow = getContentResolver().delete(currentUriProduct, null, null);
            if (deleteRow == 0){
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean saveProduct() {

        boolean dataIsReady = true;
        binding.tilProductName.setError(null);
        binding.tilPrice.setError(null);
        binding.tilQuantity.setError(null);
        binding.tilSupplierName.setError(null);
        // read from input fields
        String productNameString = binding.productNameEditText.getText().toString().trim();
        String priceString = binding.priceEditText.getText().toString().trim();
        String quantityString = binding.quantityEditText.getText().toString().trim();
        String supplierNameString = binding.supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = binding.supplierPhoneNumberEditText.getText().toString().trim();

        if(TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString) && (quantityString.equals("0")) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString)){
            binding.tilProductName.setError(getString(R.string.camp_required));
            binding.tilPrice.setError(getString(R.string.camp_required));
            binding.tilSupplierName.setError(getString(R.string.camp_required));
            binding.tilSupplierPhone.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }

        if(TextUtils.isEmpty(productNameString)){
            binding.tilProductName.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }
        // price data is integer, i trasform the data in this field in cents
        int priceCentsInt = 0;
        if (!TextUtils.isEmpty(priceString)){
            Double priceCents = Double.parseDouble(priceString) * 100.00;
            priceCentsInt = priceCents.intValue();
        } else {
            binding.tilPrice.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }

        // quantity data is integer
        int quantityInt = 0;
        if (!TextUtils.isEmpty(quantityString)){
            quantityInt = parseInt(quantityString);
        } else {
            binding.tilQuantity.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }

        if (TextUtils.isEmpty(supplierNameString)){
            binding.tilSupplierName.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }

        if (TextUtils.isEmpty(supplierPhoneNumberString)){
            binding.tilSupplierPhone.setError(getString(R.string.camp_required));
            dataIsReady = false;
        }

        if(dataIsReady) {
            // Prepare values for store
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceCentsInt);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

            // Insert the new product
            if (currentUriProduct == null) {

                Uri newRowId = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                if (newRowId == null) {
                    Toast.makeText(this, getString(R.string.error_save), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.ok_save), Toast.LENGTH_SHORT).show();
                }
            } else {

                int updateRowId = getContentResolver().update(currentUriProduct, values, null, null);

                if (updateRowId == 0) {
                    Toast.makeText(this, getString(R.string.error_save), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.ok_update), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_save_data), Toast.LENGTH_SHORT).show();
        }
        return dataIsReady;
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER };
        return new CursorLoader(this, currentUriProduct, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToNext()){
            int priceCents = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            // trasform price to euro
            double price = priceCents / 100.00;
            String priceString = correctPriceString(String.valueOf(price));

            binding.productNameEditText.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME)));
            binding.priceEditText.setText(priceString);
            quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            binding.quantityEditText.setText(String.valueOf(quantity));
            binding.supplierNameEditText.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)));
            binding.supplierPhoneNumberEditText.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.productNameEditText.setText("");
        binding.priceEditText.setText("");
        binding.quantityEditText.setText("");
        binding.supplierNameEditText.setText("");
        binding.supplierPhoneNumberEditText.setText("");
    }

    public String correctPriceString(String price){
        int delta = price.length() - price.indexOf(".");
        if (delta == 2){
            price = price + "0";
        }
        return price;
    }
}
