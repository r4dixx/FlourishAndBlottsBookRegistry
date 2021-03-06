package com.r4dixx.fbbookregistry;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.r4dixx.fbbookregistry.database.BookContract.BookEntry;

import static com.r4dixx.fbbookregistry.database.BookContract.BookEntry.QUANTITY_DEFAULT;
import static com.r4dixx.fbbookregistry.database.BookContract.BookEntry.SUBJECT_UNKNOWN;

public class NewEntryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    private Uri mCurrentBookUri;

    private EditText mTitleET;
    private EditText mAuthorET;
    private EditText mPublisherET;
    private EditText mYearET;
    private EditText mPriceET;
    private EditText mQuantityET;
    private EditText mSupplierET;
    private EditText mSupplierPhoneET;

    private Spinner mSubjectSpin;
    private int mSubject = SUBJECT_UNKNOWN;
    public int mQuantity = QUANTITY_DEFAULT;
    private boolean mBookChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Toolbar toolbar = findViewById(R.id.toolbar_new_entry);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri != null) {
            setTitle(getString(R.string.new_entry_activity_edit));
            getLoaderManager().initLoader(LOADER, null, this);
        } else {
            setTitle(getString(R.string.new_entry_activity_add));
            invalidateOptionsMenu();
        }

        mTitleET = findViewById(R.id.edit_book_title);
        mAuthorET = findViewById(R.id.edit_book_author);
        mPublisherET = findViewById(R.id.edit_book_publisher);
        mYearET = findViewById(R.id.edit_book_year);
        mPriceET = findViewById(R.id.edit_book_price);
        mQuantityET = findViewById(R.id.edit_book_quantity);
        mSupplierET = findViewById(R.id.edit_book_supplier);
        mSupplierPhoneET = findViewById(R.id.edit_book_phone);
        mSubjectSpin = findViewById(R.id.spinner_subject);

        mTitleET.setOnTouchListener(mTouchListener);
        mAuthorET.setOnTouchListener(mTouchListener);
        mPublisherET.setOnTouchListener(mTouchListener);
        mYearET.setOnTouchListener(mTouchListener);
        mPriceET.setOnTouchListener(mTouchListener);
        mQuantityET.setOnTouchListener(mTouchListener);
        mSupplierET.setOnTouchListener(mTouchListener);
        mSupplierPhoneET.setOnTouchListener(mTouchListener);
        mSubjectSpin.setOnTouchListener(mTouchListener);

        setSpinner();

        ImageView phoneIV = findViewById(R.id.edit_book_phone_intent);
        phoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mSupplierPhoneET.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(NewEntryActivity.this, getString(R.string.toast_no_phone), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent in = new Intent(Intent.ACTION_DIAL);
                in.setData(Uri.parse("tel:" + number));
                if (in.resolveActivity(getPackageManager()) != null) {
                    startActivity(in);
                }
            }
        });
    }

    public void increment(View v) {
        if (!TextUtils.isEmpty(mQuantityET.getText())) {
            mQuantity = Integer.parseInt(mQuantityET.getText().toString().trim());
        }
        mQuantityET.setText(String.valueOf(mQuantity + 1));
        mBookChanged = true;
    }

    public void decrement(View v) {
        if (!TextUtils.isEmpty(mQuantityET.getText())) {
            mQuantity = Integer.parseInt(mQuantityET.getText().toString().trim());
        }
        if ((mQuantity - 1) >= 0) {
            mQuantity--;
        }
        if (mQuantity == 0) {
            Toast.makeText(NewEntryActivity.this, getString(R.string.toast_decrement_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantityET.setText(String.valueOf(mQuantity));
        mBookChanged = true;
    }

    private void setSpinner() {

        ArrayAdapter subjectSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_subjects, android.R.layout.simple_spinner_item);
        subjectSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSubjectSpin.setAdapter(subjectSpinnerAdapter);

        mSubjectSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> par, View v, int pos, long id) {
                String selected = (String) par.getItemAtPosition(pos);

                // Padding similar to EditTexts
                v.setPadding(8, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());

                // TODO this could be a looot cleaner. With a switch maybe or even better a loop
                if (!TextUtils.isEmpty(selected)) {
                    if (selected.equals(getString(R.string.subject_alchemy))) {
                        mSubject = BookEntry.SUBJECT_ALCHEMY;
                    } else if (selected.equals(getString(R.string.subject_ancient_runes))) {
                        mSubject = BookEntry.SUBJECT_ANCIENT_RUNES;
                    } else if (selected.equals(getString(R.string.subject_ancient_studies))) {
                        mSubject = BookEntry.SUBJECT_ANCIENT_STUDIES;
                    } else if (selected.equals(getString(R.string.subject_apparition))) {
                        mSubject = BookEntry.SUBJECT_APPARITION;
                    } else if (selected.equals(getString(R.string.subject_arithmancy))) {
                        mSubject = BookEntry.SUBJECT_ARITHMANCY;
                    } else if (selected.equals(getString(R.string.subject_art))) {
                        mSubject = BookEntry.SUBJECT_ART;
                    } else if (selected.equals(getString(R.string.subject_astronomy))) {
                        mSubject = BookEntry.SUBJECT_ASTRONOMY;
                    } else if (selected.equals(getString(R.string.subject_charms))) {
                        mSubject = BookEntry.SUBJECT_CHARMS;
                    } else if (selected.equals(getString(R.string.subject_creatures))) {
                        mSubject = BookEntry.SUBJECT_CREATURES;
                    } else if (selected.equals(getString(R.string.subject_dark_arts))) {
                        mSubject = BookEntry.SUBJECT_DARK_ARTS;
                    } else if (selected.equals(getString(R.string.subject_divination))) {
                        mSubject = BookEntry.SUBJECT_DIVINATION;
                    } else if (selected.equals(getString(R.string.subject_flying))) {
                        mSubject = BookEntry.SUBJECT_FLYING;
                    } else if (selected.equals(getString(R.string.subject_herbology))) {
                        mSubject = BookEntry.SUBJECT_HERBOLOGY;
                    } else if (selected.equals(getString(R.string.subject_magic_history))) {
                        mSubject = BookEntry.SUBJECT_MAGIC_HISTORY;
                    } else if (selected.equals(getString(R.string.subject_magical_theory))) {
                        mSubject = BookEntry.SUBJECT_MAGICAL_THEORY;
                    } else if (selected.equals(getString(R.string.subject_muggles_studies))) {
                        mSubject = BookEntry.SUBJECT_MUGGLES_STUDY;
                    } else if (selected.equals(getString(R.string.subject_muggle_art))) {
                        mSubject = BookEntry.SUBJECT_MUGGLE_ART;
                    } else if (selected.equals(getString(R.string.subject_music))) {
                        mSubject = BookEntry.SUBJECT_MUSIC;
                    } else if (selected.equals(getString(R.string.subject_potions))) {
                        mSubject = BookEntry.SUBJECT_POTIONS;
                    } else if (selected.equals(getString(R.string.subject_transfiguration))) {
                        mSubject = BookEntry.SUBJECT_TRANFIGURATION;
                    } else if (selected.equals(getString(R.string.subject_xylomancy))) {
                        mSubject = BookEntry.SUBJECT_XYLOMANCY;
                    } else {
                        mSubject = SUBJECT_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSubject = SUBJECT_UNKNOWN;
            }
        });
    }

    private void newEntry() {
        String title = mTitleET.getText().toString().trim();
        String author = mAuthorET.getText().toString().trim();
        String publisher = mPublisherET.getText().toString().trim();
        String yearString = mYearET.getText().toString().trim();
        int year = Integer.parseInt(yearString);
        String priceString = mPriceET.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String quantityString = mQuantityET.getText().toString().trim();
        mQuantity = Integer.parseInt(quantityString);
        String supplier = mSupplierET.getText().toString().trim();
        String phone = mSupplierPhoneET.getText().toString().trim();

        // If no field was modified, do nothing
        if (mCurrentBookUri == null && TextUtils.isEmpty(title) && TextUtils.isEmpty(author) && TextUtils.isEmpty(publisher) && TextUtils.isEmpty(yearString) && TextUtils.isEmpty(priceString) && mSubject == SUBJECT_UNKNOWN) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_TITLE, title);
        values.put(BookEntry.COLUMN_AUTHOR, author);
        values.put(BookEntry.COLUMN_PUBLISHER, publisher);
        values.put(BookEntry.COLUMN_YEAR, year);
        values.put(BookEntry.COLUMN_SUBJECT, mSubject);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, mQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER, supplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, phone);

        // Determine if this is a new or existing book
        if (mCurrentBookUri != null) {
            int rowsChanged = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsChanged > 0) {
                Toast.makeText(this, getString(R.string.toast_edit_successful), Toast.LENGTH_SHORT).show();
            } else {
                // no rows affected means an error happened during the update
                Toast.makeText(this, getString(R.string.toast_edit_failed), Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri newUri = getContentResolver().insert(BookEntry.URI_FINAL, values);
            if (newUri != null) {
                Toast.makeText(this, getString(R.string.toast_new_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_new_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mBookChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        exitEditionDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // If these fields are empty, stay here
                if (TextUtils.isEmpty(mTitleET.getText()) || TextUtils.isEmpty(mAuthorET.getText()) || TextUtils.isEmpty(mYearET.getText())
                        || TextUtils.isEmpty(mPriceET.getText()) || (mPriceET.getText().toString().matches("0")) || TextUtils.isEmpty(mQuantityET.getText())
                        || (mQuantityET.getText().toString().matches("0"))) {
                    // Hides keyboard if open...
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive(view)) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    // ... to properly show the toast message
                    Toast.makeText(this, getString(R.string.toast_edit_failed), Toast.LENGTH_LONG).show();
                    TextView tvTitle = findViewById(R.id.edit_book_title_title);
                    TextView tvAuthor = findViewById(R.id.edit_book_title_author);
                    TextView tvYear = findViewById(R.id.edit_book_title_year);
                    TextView tvPrice = findViewById(R.id.edit_book_title_price);
                    TextView tvQuantity = findViewById(R.id.edit_book_title_quantity);
                    LinearLayout quantityBlock = findViewById(R.id.quantity_block);

                    // Validation per field
                    TextInputLayout tilTitle = findViewById(R.id.til_title);
                    if (TextUtils.isEmpty(mTitleET.getText())) {
                        tilTitle.setError(getString(R.string.edit_book_validation));
                        tvTitle.setTextColor(this.getResources().getColor(R.color.colorError));
                        tilTitle.setPadding(0, 0, 0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra));
                    } else {
                        tilTitle.setError(null);
                        tvTitle.setTextColor(this.getResources().getColor(R.color.colorAccent));
                        tilTitle.setPadding(0, 0, 0, 0);
                    }
                    TextInputLayout tilAuthor = findViewById(R.id.til_author);
                    if (TextUtils.isEmpty(mAuthorET.getText())) {
                        tilAuthor.setError(getString(R.string.edit_book_validation));
                        tvAuthor.setTextColor(this.getResources().getColor(R.color.colorError));
                        tilAuthor.setPadding(0, 0, 0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra));
                    } else {
                        tilAuthor.setError(null);
                        tvAuthor.setTextColor(this.getResources().getColor(R.color.colorAccent));
                        tilAuthor.setPadding(0, 0, 0, 0);
                    }
                    TextInputLayout tilYear = findViewById(R.id.til_year);
                    if (TextUtils.isEmpty(mYearET.getText())) {
                        tilYear.setError(getString(R.string.edit_book_validation));
                        tvYear.setTextColor(this.getResources().getColor(R.color.colorError));
                        tilYear.setPadding(0, 0, 0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra));
                    } else {
                        tilYear.setError(null);
                        tvYear.setTextColor(this.getResources().getColor(R.color.colorAccent));
                        tilYear.setPadding(0, 0, 0, 0);
                    }
                    TextInputLayout tilQuantity = findViewById(R.id.til_quantity);
                    if (TextUtils.isEmpty(mQuantityET.getText())) {
                        tilQuantity.setError(getString(R.string.edit_book_validation));
                        tvQuantity.setTextColor(this.getResources().getColor(R.color.colorError));
                        quantityBlock.setPadding(0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra), 0, 0);
                    } else if ((mQuantityET.getText().toString().matches("0"))) {
                        tilQuantity.setError(getString(R.string.edit_book_validation_quantity_zero));
                        tvQuantity.setTextColor(this.getResources().getColor(R.color.colorError));
                        quantityBlock.setPadding(0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra), 0, 0);
                    } else {
                        tilQuantity.setError(null);
                        tvQuantity.setTextColor(this.getResources().getColor(R.color.colorAccent));
                        quantityBlock.setPadding(0, 0, 0, 0);
                    }
                    TextInputLayout tilPrice = findViewById(R.id.til_price);
                    if (TextUtils.isEmpty(mPriceET.getText())) {
                        tilPrice.setError(getString(R.string.edit_book_validation));
                        tvPrice.setTextColor(this.getResources().getColor(R.color.colorError));
                        tilPrice.setPadding(0, 0, 0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra));
                    } else if (mPriceET.getText().toString().matches("0")) {
                        tilPrice.setError(getString(R.string.edit_book_validation_price_zero));
                        tvPrice.setTextColor(this.getResources().getColor(R.color.colorError));
                        tilPrice.setPadding(0, this.getResources().getDimensionPixelSize(R.dimen.new_entry_padding_extra), 0, 0);
                    } else {
                        tilPrice.setError(null);
                        tvPrice.setTextColor(this.getResources().getColor(R.color.colorAccent));
                        tilPrice.setPadding(0, 0, 0, 0);
                    }
                    // do not save
                    return false;
                } else if (!mBookChanged) {
                    Toast.makeText(this, getString(R.string.toast_edit_no_changes), Toast.LENGTH_SHORT).show();
                } else {
                    newEntry();
                    finish();
                }
                return true;
            case R.id.action_delete:
                confirmDeletionDialog();
                return true;
            case android.R.id.home:
                if (!mBookChanged) {
                    NavUtils.navigateUpFromSameTask(NewEntryActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(NewEntryActivity.this);
                        finish();
                    }
                };
                exitEditionDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //DIALOGS
    private void exitEditionDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(R.string.dialog_unsaved);
        bldr.setPositiveButton(R.string.dialog_unsaved_pos, discardButtonClickListener);
        bldr.setNegativeButton(R.string.dialog_unsaved_neg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = bldr.create();
        alertDialog.show();
    }

    private void confirmDeletionDialog() {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(R.string.dialog_delete);
        bldr.setPositiveButton(R.string.dialog_delete_pos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteEntry();
            }
        });
        bldr.setNegativeButton(R.string.dialog_delete_neg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = bldr.create();
        dialog.show();
    }

    private void deleteEntry() {
        if (mCurrentBookUri != null) {
            int rowsRemoved = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsRemoved != 0) {
                Toast.makeText(this, getString(R.string.toast_delete_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_delete_failed), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry.COLUMN_TITLE,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_PUBLISHER,
                BookEntry.COLUMN_YEAR,
                BookEntry.COLUMN_SUBJECT,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor curs) {
        if (curs == null || curs.getCount() < 1) {
            return;
        }

        if (curs.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColIndex = curs.getColumnIndex(BookEntry.COLUMN_TITLE);
            int authorColIndex = curs.getColumnIndex(BookEntry.COLUMN_AUTHOR);
            int publisherColIndex = curs.getColumnIndex(BookEntry.COLUMN_PUBLISHER);
            int yearColIndex = curs.getColumnIndex(BookEntry.COLUMN_YEAR);
            int subjectColIndex = curs.getColumnIndex(BookEntry.COLUMN_SUBJECT);
            int priceColIndex = curs.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColIndex = curs.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColIndex = curs.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int phoneColIndex = curs.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            String title = curs.getString(titleColIndex);
            String author = curs.getString(authorColIndex);
            String publisher = curs.getString(publisherColIndex);
            int year = curs.getInt(yearColIndex);
            int subject = curs.getInt(subjectColIndex);
            int price = curs.getInt(priceColIndex);
            mQuantity = curs.getInt(quantityColIndex);
            String supplier = curs.getString(supplierColIndex);
            String phone = curs.getString(phoneColIndex);

            mTitleET.setText(title);
            mAuthorET.setText(author);
            mPublisherET.setText(publisher);
            mYearET.setText(String.valueOf(year));
            mPriceET.setText(String.valueOf(price));
            mQuantityET.setText(String.valueOf(mQuantity));
            mSupplierET.setText(supplier);
            mSupplierPhoneET.setText(phone);

            switch (subject) {
                case BookEntry.SUBJECT_ALCHEMY:
                    mSubjectSpin.setSelection(1);
                    break;
                case BookEntry.SUBJECT_ANCIENT_STUDIES:
                    mSubjectSpin.setSelection(2);
                    break;
                case BookEntry.SUBJECT_ANCIENT_RUNES:
                    mSubjectSpin.setSelection(3);
                    break;
                case BookEntry.SUBJECT_APPARITION:
                    mSubjectSpin.setSelection(4);
                    break;
                case BookEntry.SUBJECT_ARITHMANCY:
                    mSubjectSpin.setSelection(5);
                    break;
                case BookEntry.SUBJECT_ART:
                    mSubjectSpin.setSelection(6);
                    break;
                case BookEntry.SUBJECT_ASTRONOMY:
                    mSubjectSpin.setSelection(7);
                    break;
                case BookEntry.SUBJECT_CHARMS:
                    mSubjectSpin.setSelection(8);
                    break;
                case BookEntry.SUBJECT_CREATURES:
                    mSubjectSpin.setSelection(9);
                    break;
                case BookEntry.SUBJECT_DARK_ARTS:
                    mSubjectSpin.setSelection(10);
                    break;
                case BookEntry.SUBJECT_DIVINATION:
                    mSubjectSpin.setSelection(11);
                    break;
                case BookEntry.SUBJECT_FLYING:
                    mSubjectSpin.setSelection(12);
                    break;
                case BookEntry.SUBJECT_HERBOLOGY:
                    mSubjectSpin.setSelection(13);
                    break;
                case BookEntry.SUBJECT_MAGIC_HISTORY:
                    mSubjectSpin.setSelection(14);
                    break;
                case BookEntry.SUBJECT_MAGICAL_THEORY:
                    mSubjectSpin.setSelection(15);
                    break;
                case BookEntry.SUBJECT_MUGGLES_STUDY:
                    mSubjectSpin.setSelection(16);
                    break;
                case BookEntry.SUBJECT_MUGGLE_ART:
                    mSubjectSpin.setSelection(17);
                    break;
                case BookEntry.SUBJECT_MUSIC:
                    mSubjectSpin.setSelection(18);
                    break;
                case BookEntry.SUBJECT_POTIONS:
                    mSubjectSpin.setSelection(19);
                    break;
                case BookEntry.SUBJECT_TRANFIGURATION:
                    mSubjectSpin.setSelection(20);
                    break;
                case BookEntry.SUBJECT_XYLOMANCY:
                    mSubjectSpin.setSelection(21);
                    break;
                default:
                    mSubjectSpin.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleET.getText().clear();
        mAuthorET.getText().clear();
        mPublisherET.getText().clear();
        mYearET.getText().clear();
        mPriceET.getText().clear();
        mQuantityET.setText("1");
        mSupplierET.getText().clear();
        mSupplierPhoneET.getText().clear();
        mSubjectSpin.setSelection(0);
    }
}
