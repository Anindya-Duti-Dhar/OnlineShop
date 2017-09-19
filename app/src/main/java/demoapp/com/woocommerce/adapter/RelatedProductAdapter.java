package demoapp.com.woocommerce.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.model.RelatedProduct;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.ViewHolder> {

    public SmallBang mSmallBang;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView related_product_item_image, related_product_item_fav;
        public TextView related_product_item_title, related_product_item_price;

        public ViewHolder(View itemView) {
            super(itemView);
            related_product_item_title = (TextView) itemView.findViewById(R.id.related_product_item_title);
            related_product_item_price = (TextView) itemView.findViewById(R.id.related_product_item_price);
            related_product_item_image = (ImageView) itemView.findViewById(R.id.related_product_item_image);
            related_product_item_fav = (ImageView) itemView.findViewById(R.id.related_product_item_fav);
        }
    }

    private ArrayList<RelatedProduct> _data;
    private Context mContext;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public RelatedProductAdapter(Context context, ArrayList<RelatedProduct> _data) {
        this._data = _data;
        mContext = context;
    }

    @Override
    public RelatedProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View relatedProductView = inflater.inflate(R.layout.related_product_item, parent, false);
        RelatedProductAdapter.ViewHolder viewHolder = new RelatedProductAdapter.ViewHolder(relatedProductView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RelatedProductAdapter.ViewHolder viewHolder, int position) {
        mSmallBang = SmallBang.attach2Window((Activity) mContext);
        // Get the data model based on position
        final RelatedProduct data = _data.get(position);
        TextView ListTitle = viewHolder.related_product_item_title;
        ListTitle.setText(data.getProductName());

        TextView ListPrice = viewHolder.related_product_item_price;
        ListPrice.setText(data.getProductPrice()+" Taka");

        final ImageView ListFavourite = viewHolder.related_product_item_fav;
        ListFavourite.setImageResource(R.drawable.heart);

        ImageView ListImage = viewHolder.related_product_item_image;
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