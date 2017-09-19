package demoapp.com.woocommerce.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import demoapp.com.woocommerce.R;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

public class ProductDetailsFirstPart extends Fragment {
    //Defining Variables
    private static final String TAG = ProductDetailsFirstPart.class.getSimpleName();
    String mParsedProductName;
    String mParsedProductID;
    String mParsedProductImageUrl;
    String mParsedProductPrice;

    ImageView mAddToWishList, mProductDetailsImage;
    TextView mProductDetailsName, mProductDetailsPrice;
    public SmallBang mSmallBang;

    public static ProductDetailsFirstPart newInstance() {
        ProductDetailsFirstPart fragment = new ProductDetailsFirstPart();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public ProductDetailsFirstPart() {
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
        return inflater.inflate(R.layout.product_details_first_part, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view initialize and functionality declare

        Bundle bundle = getActivity().getIntent().getExtras();
        mParsedProductName = bundle.getString("product_name");
        mParsedProductID = bundle.getString("product_id");
        mParsedProductImageUrl = bundle.getString("product_image");
        mParsedProductPrice = bundle.getString("product_price");

        mProductDetailsName = (TextView) view.findViewById(R.id.product_details_name);
        mProductDetailsName.setText(mParsedProductName);

        mProductDetailsPrice = (TextView) view.findViewById(R.id.product_details_price);
        mProductDetailsPrice.setText(mParsedProductPrice+" Taka");

        mProductDetailsImage = (ImageView) view.findViewById(R.id.product_details_image);
        Picasso.with(getActivity())
                .load(mParsedProductImageUrl).noFade().into(mProductDetailsImage);

        mSmallBang = SmallBang.attach2Window(getActivity());
        mAddToWishList = (ImageView) view.findViewById(R.id.product_details_fav);
        mAddToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddToWishList.setImageResource(R.drawable.heart_red);
                mSmallBang.bang(view);
                mSmallBang.setmListener(new SmallBangListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        toast("Add to Wish List");
                    }
                });
            }
        });

    }

    private void toast(String text) {
        Toast.makeText(getActivity(),text, Toast.LENGTH_SHORT).show();
    }
}
