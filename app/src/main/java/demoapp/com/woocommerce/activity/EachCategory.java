package demoapp.com.woocommerce.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Arrays;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.WooCommerceApi;
import demoapp.com.woocommerce.adapter.EachCategoryAdapter;
import demoapp.com.woocommerce.adapter.SearchAdapter;
import demoapp.com.woocommerce.model.EachCategoryItem;
import demoapp.com.woocommerce.utils.ItemClickSupport;
import demoapp.com.woocommerce.utils.SharedPreference;
import demoapp.com.woocommerce.utils.Utils;

public class EachCategory extends AppCompatActivity {

    private static final String TAG = EachCategory.class.getSimpleName();
    String mCategoryName;
    String mProductName;
    String mProductID;
    String mProductPrice;
    String mProductImageUrl;

    ArrayList<EachCategoryItem> eachCategoryItem;
    RecyclerView mRecyclerView;
    EachCategoryAdapter adapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    ProgressBar mProgress;
    RelativeLayout mLoadingProgress;
    String imageID, ImageUrl;

    private ArrayList<String> mCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.each_category);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#de4256"));

        // Set up the toolbar.
        Toolbar each_category_toolbar = (Toolbar) findViewById(R.id.each_category_toolbar);
        setSupportActionBar(each_category_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar cptb = getSupportActionBar();
        cptb.setDisplayHomeAsUpEnabled(true);
        cptb.setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mCategoryName = bundle.getString("category_name");

        TextView mContactProfileToolbarTitle = (TextView) findViewById(R.id.each_category_toolbar_title);
        mContactProfileToolbarTitle.setText(mCategoryName);

        eachCategoryItem = new ArrayList<EachCategoryItem>();

        mRecyclerView = (RecyclerView) findViewById(R.id.eachCategoryItemRecycler);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mLoadingProgress = (RelativeLayout) findViewById(R.id.each_category_loading_progress);
        mProgress = (ProgressBar) findViewById(R.id.each_category_progress);

        new CategoryAsyncTask().execute();

        // Select item on listclick
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        EachCategoryItem data = eachCategoryItem.get(position);
                        mProductName = data.getProductName();
                        Log.d("Product Name: ", mProductName);
                        mProductID = data.getProductId();
                        Log.d("Product ID: ", mProductID);
                        mProductImageUrl = data.getImageUrl();
                        Log.d("Product Image: ", mProductImageUrl);
                        mProductPrice = data.getProductPrice();
                        Log.d("Product Image: ", mProductPrice);

                        Intent intent = new Intent(EachCategory.this, ProductDetails.class);
                        intent.putExtra("product_name", mProductName);
                        intent.putExtra("product_id", mProductID);
                        intent.putExtra("product_image", mProductImageUrl);
                        intent.putExtra("product_price", mProductPrice);
                        EachCategory.this.startActivity(intent);
                    }
                }
        );
    }

    private class CategoryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String restURL = "http://www.reveriegroup.com/demo/wp-json/wc/v1/products?per_page=12";
            OAuthService service = new ServiceBuilder()
                    .provider(WooCommerceApi.class)
                    .apiKey("ck_8e344e1ef42a00f4d4c3bb1ede1557d48790c322") //Your Consumer key
                    .apiSecret("cs_09de5a54244811bc1291ebb4a4d4b7b6725c4cec") //Your Consumer secret
                    .scope("API.Public") //fixed
                    .signatureType(SignatureType.QueryString)
                    .build();
            OAuthRequest request = new OAuthRequest(Verb.GET, restURL);
            Token accessToken = new Token("", ""); //not required for context.io
            service.signRequest(accessToken, request);
            Response response = request.send();
            String data = response.getBody();
            Log.d("OAuthTask", response.getBody());
            try {
                parseJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //update your listView adapter here
            //Dismiss your dialog
            mLoadingProgress.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            adapter = new EachCategoryAdapter(EachCategory.this, eachCategoryItem);
            mRecyclerView.setAdapter(adapter);

        }
    }

    public void parseJSON(String data) throws JSONException {
        JSONArray jsonArr = new JSONArray(data);
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            String id = jsonObj.getString("id").toString();
            String name = jsonObj.getString("name");
            String price = jsonObj.getString("price");
            String subdata = jsonObj.getString("images");
            JSONArray json_data1 = new JSONArray(subdata);
            for (int j = 0; j < json_data1.length(); j++) {
                jsonObj = json_data1.getJSONObject(j);
                imageID = jsonObj.getString("id");
                ImageUrl = jsonObj.getString("src");
            }
            Log.d("Products Details: ", "ID:: " + id + " Name:: " + name + " Price:: " + price + " ImageID:: " + imageID + " ImageUrl:: " + ImageUrl);
            EachCategoryItem productItem = new EachCategoryItem();
            productItem.setImageUrl(ImageUrl);
            productItem.setProductName(name);
            productItem.setProductId(id);
            productItem.setProductPrice(price);
            eachCategoryItem.add(productItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            loadToolBarSearch();
            return true;
        } else if (id == R.id.cart) {
            Intent intent = new Intent(EachCategory.this, MyCarts.class);
            EachCategory.this.startActivity(intent);
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

        else if (id == R.id.loginLogout) {
            Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadToolBarSearch() {


        ArrayList<String> countryStored = SharedPreference.loadList(EachCategory.this, Utils.PREFS_NAME, Utils.KEY_COUNTRIES);

        View view = EachCategory.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

        Utils.setListViewHeightBasedOnChildren(listSearch);

        edtToolSearch.setHint("Search your Product");

        final Dialog toolbarSearchDialog = new Dialog(EachCategory.this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        countryStored = (countryStored != null && countryStored.size() > 0) ? countryStored : new ArrayList<String>();
        final SearchAdapter searchAdapter = new SearchAdapter(EachCategory.this, countryStored, false);

        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);


        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String country = String.valueOf(adapterView.getItemAtPosition(position));
                SharedPreference.addList(EachCategory.this, Utils.PREFS_NAME, Utils.KEY_COUNTRIES, country);
                edtToolSearch.setText(country);
                listSearch.setVisibility(View.GONE);


            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String[] country = EachCategory.this.getResources().getStringArray(R.array.products_array);
                mCountries = new ArrayList<String>(Arrays.asList(country));
                listSearch.setVisibility(View.VISIBLE);
                searchAdapter.updateList(mCountries, true);


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> filterList = new ArrayList<String>();
                boolean isNodata = false;
                if (s.length() > 0) {
                    for (int i = 0; i < mCountries.size(); i++) {


                        if (mCountries.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {

                            filterList.add(mCountries.get(i));

                            listSearch.setVisibility(View.VISIBLE);
                            searchAdapter.updateList(filterList, true);
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });

        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtToolSearch.setText("");

            }
        });


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
