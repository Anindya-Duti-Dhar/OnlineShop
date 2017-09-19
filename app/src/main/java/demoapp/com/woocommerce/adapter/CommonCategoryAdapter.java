package demoapp.com.woocommerce.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.model.Item;
import demoapp.com.woocommerce.model.CommonCategory;

public class CommonCategoryAdapter extends RecyclerView.Adapter<CommonCategoryAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView category_item_image;
        public TextView category_item_title;

        public ViewHolder(View itemView) {
            super(itemView);
            category_item_title = (TextView) itemView.findViewById(R.id.category_item_title);
            category_item_image = (ImageView) itemView.findViewById(R.id.category_item_image);
        }
    }

    private ArrayList<CommonCategory> _data;
    private Context mContext;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public CommonCategoryAdapter(Context context, ArrayList<CommonCategory> _data) {
        this._data = _data;
        mContext = context;
    }

    @Override
    public CommonCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View categoryView = inflater.inflate(R.layout.common_category_item, parent, false);
        CommonCategoryAdapter.ViewHolder viewHolder = new CommonCategoryAdapter.ViewHolder(categoryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommonCategoryAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final CommonCategory data = _data.get(position);
        TextView ListTitle = viewHolder.category_item_title;
        ListTitle.setText("T-Shirt"); /*data.getCategoryName()*/

        ImageView ListImage = viewHolder.category_item_image;
        ListImage.setImageResource(R.drawable.t_shirts_5);
       // Picasso.with(getContext())
               // .load(data.getImageUrl()).noFade().into(ListImage);
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

}