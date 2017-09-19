package demoapp.com.woocommerce.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import demoapp.com.woocommerce.model.CartList;

import static demoapp.com.woocommerce.database.DbHelper.TABLE_NAME;


public class DbManager {
    private static String TAG = DbManager.class.getSimpleName();

    private static DbManager instance;
    private Context mContext;

    private DbManager(Context context) {
        this.mContext = context;
    }

    public static DbManager getInstance(Context context) {
        if (instance == null) {
            instance = new DbManager(context);
        }

        return instance;
    }


    public void clearDB() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        dbHelper.close();
    }

    /* get  data from cart table */
    public List<CartList> getDataFromDB() {
        List<CartList> modelList = new ArrayList<CartList>();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                CartList model = new CartList();
                model.setProductId(cursor.getString(0));
                model.setProductName(cursor.getString(1));
                model.setProductPrice(cursor.getString(2));
                model.setImageUrl(cursor.getString(3));
                Log.d("DemoCartData ID: ", cursor.getString(0));
                Log.d("DemoCartData Name:", cursor.getString(1));
                modelList.add(model);

            } while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();

        Log.d("cart data", modelList.toString());

        return modelList;
    }

    /* Insert into cart table*/
    public void insertIntoDB(String productID,String productName,String productPrice,String productImage){
        Log.d("insert", "before insert");

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        // 1. get reference to writable DB
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        values.put(DbHelper.PRODUCT_ID, productID);
        values.put(DbHelper.PRODUCT_NAME, productName);
        values.put(DbHelper.PRODUCT_PRICE, productPrice);
        values.put(DbHelper.PRODUCT_IMAGE, productImage);

        // 3. insert
        db.insert(TABLE_NAME, null, values);
        // 4. close
        dbHelper.close();
        Log.i("insert into DB", "After insert");
    }

    //delete a row from cart table
    public void deleteARow(String productID){
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, "productID" + " = ?", new String[] { productID });
        db.close();
    }
}

