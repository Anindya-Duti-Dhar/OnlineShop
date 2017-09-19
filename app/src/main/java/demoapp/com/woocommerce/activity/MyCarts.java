package demoapp.com.woocommerce.activity;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.adapter.CartAdapter;
import demoapp.com.woocommerce.adapter.SearchAdapter;
import demoapp.com.woocommerce.database.DbManager;
import demoapp.com.woocommerce.model.CartList;
import demoapp.com.woocommerce.utils.ItemClickSupport;
import demoapp.com.woocommerce.utils.SharedPreference;
import demoapp.com.woocommerce.utils.Utils;

public class MyCarts extends AppCompatActivity {

    private static final String TAG = MyCarts.class.getSimpleName();
    String mProductName;
    String mProductID;
    String mProductPrice;
    String mProductImageUrl;

    RecyclerView mRecyclerView;
    CartAdapter adapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private BroadcastReceiver mBroadcastReceiver;

    List<CartList> dbList;
    DbManager helper;

    RelativeLayout mNoCartLayout;
    Button mContinueShoppingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.my_carts);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#de4256"));

        // Set up the toolbar.
        Toolbar cart_toolbar = (Toolbar) findViewById(R.id.cart_toolbar);
        setSupportActionBar(cart_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar cptb = getSupportActionBar();
        cptb.setDisplayHomeAsUpEnabled(true);
        cptb.setDisplayShowHomeEnabled(true);

        TextView mCartToolbarTitle = (TextView) findViewById(R.id.cart_toolbar_title);
        mCartToolbarTitle.setText("My Carts");

        mNoCartLayout = (RelativeLayout) findViewById(R.id.no_cart_layout);
        mContinueShoppingButton = (Button) findViewById(R.id.continue_shopping_btn);
        mContinueShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCarts.this, MainActivity.class);
                MyCarts.this.startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.cartItemRecycler);
        helper = DbManager.getInstance(MyCarts.this);
        dbList = new ArrayList<CartList>();
        dbList = helper.getDataFromDB();
        if (dbList.size() < 1) {
            mNoCartLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoCartLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            adapter = new CartAdapter(MyCarts.this, dbList);
            mRecyclerView.setAdapter(adapter);
            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals("deleteCartItem")) {
                    dbList = helper.getDataFromDB();
                    if (dbList.size() < 1) {
                        mNoCartLayout.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mNoCartLayout.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        adapter = new CartAdapter(MyCarts.this, dbList);
                        mRecyclerView.setAdapter(adapter);
                        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                    }
                }
            }
        };

        // Select item on listclick
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        CartList data = dbList.get(position);
                        mProductName = data.getProductName();
                        Log.d("Product Name: ", mProductName);
                        mProductID = data.getProductId();
                        Log.d("Product ID: ", mProductID);
                        mProductImageUrl = data.getImageUrl();
                        Log.d("Product Image: ", mProductImageUrl);
                        mProductPrice = data.getProductPrice();
                        Log.d("Product Image: ", mProductPrice);

                        Intent intent = new Intent(MyCarts.this, ProductDetails.class);
                        intent.putExtra("product_name", mProductName);
                        intent.putExtra("product_id", mProductID);
                        intent.putExtra("product_image", mProductImageUrl);
                        intent.putExtra("product_price", mProductPrice);
                        MyCarts.this.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.wishList) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.howToBuy) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.returnPolicy) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("deleteCartItem"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    // back arrow action
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // back button press method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
