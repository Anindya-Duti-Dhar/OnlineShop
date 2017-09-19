package demoapp.com.woocommerce.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.model.CommonCategory;
import demoapp.com.woocommerce.model.EachCategoryItem;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

public class EachCategoryAdapter extends RecyclerView.Adapter<EachCategoryAdapter.ViewHolder> {

    public SmallBang mSmallBang;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_item_image, product_item_fav;
        public TextView product_item_title, product_item_price;

        public ViewHolder(View itemView) {
            super(itemView);
            product_item_title = (TextView) itemView.findViewById(R.id.product_item_title);
            product_item_price = (TextView) itemView.findViewById(R.id.product_item_price);
            product_item_image = (ImageView) itemView.findViewById(R.id.product_item_image);
            product_item_fav = (ImageView) itemView.findViewById(R.id.product_item_fav);
        }
    }

    private ArrayList<EachCategoryItem> _data;
    private Context mContext;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public EachCategoryAdapter(Context context, ArrayList<EachCategoryItem> _data) {
        this._data = _data;
        mContext = context;
    }

    @Override
    public EachCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View categoryView = inflater.inflate(R.layout.product_item, parent, false);
        EachCategoryAdapter.ViewHolder viewHolder = new EachCategoryAdapter.ViewHolder(categoryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final EachCategoryAdapter.ViewHolder viewHolder, int position) {
        mSmallBang = SmallBang.attach2Window((Activity) mContext);
        // Get the data model based on position
        final EachCategoryItem data = _data.get(position);
        TextView ListTitle = viewHolder.product_item_title;
        ListTitle.setText(data.getProductName());

        TextView ListPrice = viewHolder.product_item_price;
        ListPrice.setText(data.getProductPrice()+" Taka");

        final ImageView ListFavourite = viewHolder.product_item_fav;
        ListFavourite.setImageResource(R.drawable.heart);

        ImageView ListImage = viewHolder.product_item_image;
        //ListImage.setImageResource(R.drawable.demo_shirt);
        Picasso.with(getContext())
                .load(data.getImageUrl()).noFade().into(ListImage);

        // Set ImageView listener android
        ListFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListFavourite.setImageResource(R.drawable.heart_red);
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
        Toast.makeText(getContext(),text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

}