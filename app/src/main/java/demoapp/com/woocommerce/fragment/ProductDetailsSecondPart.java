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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

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
import demoapp.com.woocommerce.activity.ProductDetails;
import demoapp.com.woocommerce.adapter.EachCategoryAdapter;
import demoapp.com.woocommerce.adapter.RelatedProductAdapter;
import demoapp.com.woocommerce.model.EachCategoryItem;
import demoapp.com.woocommerce.model.RelatedProduct;
import demoapp.com.woocommerce.utils.ItemClickSupport;

public class ProductDetailsSecondPart extends Fragment {
    //Defining Variables
    private static final String TAG = ProductDetailsSecondPart.class.getSimpleName();
    String mProductName;
    String mProductID;
    String mProductPrice;
    String mProductImageUrl;

    ArrayList<RelatedProduct> relatedProductItem;
    RecyclerView mRecyclerView;
    RelatedProductAdapter adapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    ProgressBar mProgress;
    RelativeLayout mLoadingProgress;
    ImageView mNoInternet;
    String imageID, ImageUrl;

    Button mMoreSameProductDetailsBtn;
    SmileRating mSmileRating;
    TextView mDescriptionTab, mConditionsTab, mReviewTab;
    View mDescriptionTabIndicator, mConditionsTabIndicator, mReviewTabIndicator;
    LinearLayout mDescriptionTabLayout, mConditionsTabLayout, mReviewTabLayout;

    public static ProductDetailsSecondPart newInstance() {
        ProductDetailsSecondPart fragment = new ProductDetailsSecondPart();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public ProductDetailsSecondPart() {
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
        return inflater.inflate(R.layout.product_details_second_part, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view initialize and functionality declare
        mMoreSameProductDetailsBtn = (Button) view.findViewById(R.id.more_same_product_details_btn);
        mMoreSameProductDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set action logic
                toast("Coming Soon...");
            }
        });

        relatedProductItem = new ArrayList<RelatedProduct>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.relatedProductsItemRecycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        //mLoadingProgress = (RelativeLayout) findViewById(R.id.each_category_loading_progress);
        //mProgress = (ProgressBar) findViewById(R.id.each_category_progress);

        mLoadingProgress = (RelativeLayout) view.findViewById(R.id.related_loading_progress);
        mProgress = (ProgressBar) view.findViewById(R.id.related_progress);
        mNoInternet = (ImageView) view.findViewById(R.id.related_no_internet_image);

        mSmileRating = (SmileRating) view.findViewById(R.id.product_review_smile_rating);
        mSmileRating.setSelectedSmile(BaseRating.OKAY, true);

        mDescriptionTab = (TextView) view.findViewById(R.id.product_description_tab);
        mConditionsTab = (TextView) view.findViewById(R.id.product_conditions_tab);
        mReviewTab= (TextView) view.findViewById(R.id.product_review_tab);
        mDescriptionTabIndicator = view.findViewById(R.id.product_description_tab_indicator);
        mConditionsTabIndicator = view.findViewById(R.id.product_conditions_tab_indicator);
        mReviewTabIndicator = view.findViewById(R.id.product_review_tab_indicator);
        mDescriptionTabLayout = (LinearLayout) view.findViewById(R.id.product_description_layout);
        mConditionsTabLayout = (LinearLayout) view.findViewById(R.id.product_conditions_layout);
        mReviewTabLayout = (LinearLayout) view.findViewById(R.id.product_review_layout);

        mDescriptionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionTabIndicator.setVisibility(View.VISIBLE);
                mConditionsTabIndicator.setVisibility(View.GONE);
                mReviewTabIndicator.setVisibility(View.GONE);
                mDescriptionTabLayout.setVisibility(View.VISIBLE);
                mConditionsTabLayout.setVisibility(View.GONE);
                mReviewTabLayout.setVisibility(View.GONE);
            }
        });

        mConditionsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionTabIndicator.setVisibility(View.GONE);
                mConditionsTabIndicator.setVisibility(View.VISIBLE);
                mReviewTabIndicator.setVisibility(View.GONE);
                mDescriptionTabLayout.setVisibility(View.GONE );
                mConditionsTabLayout.setVisibility(View.VISIBLE);
                mReviewTabLayout.setVisibility(View.GONE);
            }
        });

        mReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionTabIndicator.setVisibility(View.GONE);
                mConditionsTabIndicator.setVisibility(View.GONE);
                mReviewTabIndicator.setVisibility(View.VISIBLE);
                mDescriptionTabLayout.setVisibility(View.GONE);
                mConditionsTabLayout.setVisibility(View.GONE);
                mReviewTabLayout.setVisibility(View.VISIBLE);
            }
        });

        mSmileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley) {
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        break;
                }
            }
        });

        // Initializing Internet Check
        if (hasConnection(getActivity())) {
            new ProductDetailsSecondPart.CategoryAsyncTask().execute();
        } else {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mNoInternet.setVisibility(View.VISIBLE);
        }

        // Select item on listclick
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        RelatedProduct data = relatedProductItem.get(position);
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

    private void toast(String text) {
        Toast.makeText(getActivity(),text, Toast.LENGTH_SHORT).show();
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

    private class CategoryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String restURL = "http://www.reveriegroup.com/demo/wp-json/wc/v1/products?per_page=4";
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
            mRecyclerView.setVisibility(View.VISIBLE);
            adapter = new RelatedProductAdapter(getActivity(), relatedProductItem);
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
            RelatedProduct productItem = new RelatedProduct();
            productItem.setImageUrl(ImageUrl);
            productItem.setProductName(name);
            productItem.setProductId(id);
            productItem.setProductPrice(price);
            relatedProductItem.add(productItem);
        }
    }
}
