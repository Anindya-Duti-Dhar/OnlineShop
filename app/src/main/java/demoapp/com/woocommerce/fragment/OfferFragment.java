package demoapp.com.woocommerce.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.WooCommerceApi;
import demoapp.com.woocommerce.activity.EachCategory;
import demoapp.com.woocommerce.activity.ProductDetails;
import demoapp.com.woocommerce.adapter.CommonCategoryAdapter;
import demoapp.com.woocommerce.adapter.EachCategoryAdapter;
import demoapp.com.woocommerce.model.CommonCategory;
import demoapp.com.woocommerce.model.EachCategoryItem;
import demoapp.com.woocommerce.utils.ItemClickSupport;

public class OfferFragment extends Fragment {
    //Defining Variables
    private static final String TAG = OfferFragment.class.getSimpleName();
    String mProductName;
    String mProductID;
    String mProductPrice;
    String mProductImageUrl;
    ArrayList<EachCategoryItem> eachCategoryItem;
    RecyclerView mRecyclerView, mRecyclerView2;
    EachCategoryAdapter adapter;
    ProgressBar mProgress;
    LinearLayout mMainContent;
    RelativeLayout mLoadingProgress;
    ImageView mNoInternet;

    StaggeredGridLayoutManager mStaggeredGridLayoutManager, mStaggeredGridLayoutManager2;
    String imageID, ImageUrl;

    public static OfferFragment newInstance() {
        OfferFragment fragment = new OfferFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public OfferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.offer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view initialize and functionality declare

        eachCategoryItem = new ArrayList<EachCategoryItem>();

        mMainContent = (LinearLayout) view.findViewById(R.id.offer_main_content);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.latestItemRecycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mRecyclerView2 = (RecyclerView) view.findViewById(R.id.offerItemRecycler);
        mRecyclerView2.setNestedScrollingEnabled(false);
        mStaggeredGridLayoutManager2 = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mRecyclerView2.setLayoutManager(mStaggeredGridLayoutManager2);
        mLoadingProgress = (RelativeLayout) view.findViewById(R.id.offer_loading_progress);
        mProgress = (ProgressBar) view.findViewById(R.id.offer_progress);
        mNoInternet = (ImageView) view.findViewById(R.id.offer_no_internet_image);

        // Initializing Internet Check
        if (hasConnection(getActivity())) {
            new OfferFragment.CategoryAsyncTask().execute();
        } else {
            mMainContent.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.VISIBLE);
            mNoInternet.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "Check Your Internet Connection! ", Toast.LENGTH_LONG).show();
        }

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

                        Intent intent = new Intent(getActivity(), ProductDetails.class);
                        intent.putExtra("product_name", mProductName);
                        intent.putExtra("product_id", mProductID);
                        intent.putExtra("product_image", mProductImageUrl);
                        intent.putExtra("product_price", mProductPrice);
                        getActivity().startActivity(intent);
                    }
                }
        );

        // Select item on listclick
        ItemClickSupport.addTo(mRecyclerView2).setOnItemClickListener(
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

                        Intent intent = new Intent(getActivity(), ProductDetails.class);
                        intent.putExtra("product_name", mProductName);
                        intent.putExtra("product_id", mProductID);
                        intent.putExtra("product_image", mProductImageUrl);
                        intent.putExtra("product_price", mProductPrice);
                        getActivity().startActivity(intent);
                    }
                }
        );

    }

    // Internet check method
    public boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    private void toast(String text) {
        Toast.makeText(getActivity(),text, Toast.LENGTH_SHORT).show();
    }

    private class CategoryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String restURL = "http://woocommerce.cloudaccess.host/wp-json/wc/v1/products?";//http://www.reveriegroup.com/demo/wp-json/wc/v1/products?per_page=4";
            OAuthService service = new ServiceBuilder()
                    .provider(WooCommerceApi.class)
                    .apiKey("ck_4e14689b6cb44beec1f2b5fa307c7c131ab54f57")//("ck_8e344e1ef42a00f4d4c3bb1ede1557d48790c322") //Your Consumer key
                    .apiSecret("cs_32d6111d7da5082ad5dbb384478d38d2c4f2ea56")//("cs_09de5a54244811bc1291ebb4a4d4b7b6725c4cec") //Your Consumer secret
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
            adapter = new EachCategoryAdapter(getActivity(), eachCategoryItem);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView2.setAdapter(adapter);

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

}
