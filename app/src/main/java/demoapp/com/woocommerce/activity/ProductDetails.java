package demoapp.com.woocommerce.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.adapter.SearchAdapter;
import demoapp.com.woocommerce.database.DbManager;
import demoapp.com.woocommerce.fragment.ProductDetailsFirstPart;
import demoapp.com.woocommerce.fragment.ProductDetailsSecondPart;
import demoapp.com.woocommerce.model.CartList;
import demoapp.com.woocommerce.utils.SharedPreference;
import demoapp.com.woocommerce.utils.Utils;

public class ProductDetails extends AppCompatActivity {

    private static final String TAG = ProductDetails.class.getSimpleName();
    String mParsedProductName;
    Button mAddToCartButton, mBuyButton, mCartSubmitButton, mItemIncreaseButton, mItemDecreaseButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String CurntStatus;

    String mParsedProductID;
    String mParsedProductImageUrl;
    String mParsedProductPrice;

    List<CartList> dbList;
    DbManager helper;

    private Dialog dialog;
    MaterialSpinner spinner1;
    ImageButton cancelButton;
    TextView mCartCount;
    int mInteger = 1;

    private static final String[] SIZE = {
            "L", "M", "XL"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.product_details_parent);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#de4256"));

        // Set up the toolbar.
        Toolbar each_category_toolbar = (Toolbar) findViewById(R.id.product_details_toolbar);
        setSupportActionBar(each_category_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar cptb = getSupportActionBar();
        cptb.setDisplayHomeAsUpEnabled(true);
        cptb.setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mParsedProductName = bundle.getString("product_name");
        mParsedProductID = bundle.getString("product_id");
        mParsedProductImageUrl = bundle.getString("product_image");
        mParsedProductPrice = bundle.getString("product_price");

        TextView mProductDetailsToolbarTitle = (TextView) findViewById(R.id.product_details_toolbar_title);
        mProductDetailsToolbarTitle.setText(mParsedProductName);

        // initialize tab layout with tab icon
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("At a Glance"));
        tabLayout.addTab(tabLayout.newTab().setText("Description"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // initialize view pager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);         // for smooth transition between tabs
        // initialize view pager adapter and setting that adapter
        final PagerAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // view pager listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tab listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // get the position which tab is selected
                viewPager.setCurrentItem(tab.getPosition());
                int Status = tab.getPosition();
                CurntStatus = String.valueOf(Status);
                Log.d(TAG, "Tab Position: "+CurntStatus);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // get the position which tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // get the position which tab is reselected
            }
        });

        mAddToCartButton = (Button) findViewById(R.id.product_cart_btn);
        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDemand();
            }
        });
        mBuyButton = (Button) findViewById(R.id.product_buy_btn);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast("Coming Soon...");
                getCartList();
            }
        });

    }

    private void toast(String text) {
        Toast.makeText(ProductDetails.this,text, Toast.LENGTH_SHORT).show();
    }

    public void addDemand(){
        // custom dialog
        dialog = new Dialog(ProductDetails.this);  // always give context of activity.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.add_to_cart_dialog);

        dialog.show();

        mCartCount = (TextView) dialog.findViewById(R.id.cart_item_count);

        mItemDecreaseButton = (Button) dialog.findViewById(R.id.item_decrease);
        mItemDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInteger = mInteger - 1;
                display(mInteger);
            }
        });

        mItemIncreaseButton = (Button) dialog.findViewById(R.id.item_increase);
        mItemIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInteger = mInteger + 1;
                display(mInteger);
            }
        });

        mCartSubmitButton = (Button) dialog.findViewById(R.id.cart_submit_button);
        mCartSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertCartList();
                dialog.dismiss();
                toast("Added to Cart List...");
            }
        });

        cancelButton = (ImageButton) dialog.findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        // Size Spinner
        spinner1 = (MaterialSpinner) dialog.findViewById(R.id.size_unit_spinner);
        spinner1.setItems(SIZE);
        spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if (item.equals("L")) {

                } else if (item.equals("M")) {

                } else if (item.equals("XL")) {

                } else {

                }
            }
        });
        spinner1.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });
    }

    private void display(int number) {
        mCartCount.setText("" + number);
    }

    // view pager adapter class
    class PageAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PageAdapter(FragmentManager fm, int numTabs) {
            super(fm);
            this.mNumOfTabs = numTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ProductDetailsFirstPart ProductDetailsFirstPart = new ProductDetailsFirstPart();
                    return ProductDetailsFirstPart;
                case 1:
                    ProductDetailsSecondPart ProductDetailsSecondPart = new ProductDetailsSecondPart();
                    return ProductDetailsSecondPart;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            Intent intent = new Intent(ProductDetails.this, MyCarts.class);
            ProductDetails.this.startActivity(intent);
            return true;
        } else if (id == R.id.wishList) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.howToBuy) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }

        else if (id == R.id.returnPolicy) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void InsertCartList(){
        // insert into cart list
        dbList = new ArrayList<>();
        helper = DbManager.getInstance(ProductDetails.this);
        helper.insertIntoDB(mParsedProductID, mParsedProductName, mParsedProductPrice, mParsedProductImageUrl);
    }

    public void getCartList(){
        helper = DbManager.getInstance(ProductDetails.this);
        dbList= new ArrayList<CartList>();
        dbList = helper.getDataFromDB();
        Log.i("DemoCart", "DemoCartList: " + dbList.toString());
    }

    public void DeleteFromCartList(){
        dbList= new ArrayList<CartList>();
        helper = DbManager.getInstance(ProductDetails.this);
        helper.deleteARow(mParsedProductID);
    }
}
