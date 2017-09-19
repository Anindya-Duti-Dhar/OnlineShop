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
import demoapp.com.woocommerce.adapter.CommonCategoryAdapter;
import demoapp.com.woocommerce.model.CommonCategory;
import demoapp.com.woocommerce.utils.ItemClickSupport;


public class KidsFragment extends Fragment {
    //Defining Variables
    ArrayList<CommonCategory> commonCategory;
    RecyclerView mRecyclerView;
    CommonCategoryAdapter adapter;
    String mCategoryName;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    ProgressBar mProgress;
    RelativeLayout mLoadingProgress;
    ImageView mNoInternet;



    String imageID, ImageUrl;

    public static KidsFragment newInstance() {
        KidsFragment fragment = new KidsFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public KidsFragment() {
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
        return inflater.inflate(R.layout.common_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view initialize and functionality declare

        commonCategory = new ArrayList<CommonCategory>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.categoryItemRecycler);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mLoadingProgress = (RelativeLayout) view.findViewById(R.id.category_loading_progress);
        mProgress = (ProgressBar) view.findViewById(R.id.category_progress);
        mNoInternet = (ImageView) view.findViewById(R.id.category_no_internet_image);

        // Initializing Internet Check
        if (hasConnection(getActivity())) {
            new KidsFragment.CategoryAsyncTask().execute();
        } else {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mNoInternet.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "Check Your Internet Connection! ", Toast.LENGTH_LONG).show();
        }

        // Select item on listclick
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        CommonCategory data = commonCategory.get(position);
                        mCategoryName = data.getCategoryName();
                        Log.d("Category Name: ", mCategoryName);

                        Intent intent = new Intent(getActivity(), EachCategory.class);
                        intent.putExtra("category_name", mCategoryName);
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
            adapter = new CommonCategoryAdapter(getActivity(), commonCategory);
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
            CommonCategory categoryItem = new CommonCategory();
            categoryItem.setImageUrl(ImageUrl);
            categoryItem.setCategoryName(name);
            commonCategory.add(categoryItem);
        }
    }


}
