package demoapp.com.woocommerce.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private String TAG = DbHelper.class.getSimpleName();

    private static final String DB_NAME = "reverieshop";
    // Cart Information
    public static final String PRODUCT_ID = "productID" ;
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_PRICE = "productPrice";
    public static final String PRODUCT_IMAGE = "productImage";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cart";
    public static final String TABLE_STATEMENT = "create table "+TABLE_NAME +"(productID TEXT,productName TEXT,productPrice TEXT,productImage TEXT)";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "--- onCreate database ---");
        // statement for cart table
        db.execSQL(TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
