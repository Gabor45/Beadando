package com.example.butorshop;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoplistAdapter extends RecyclerView.Adapter<ShoplistAdapter.ViewHolder> implements Filterable {

    private ArrayList<ShopList>mShopListData;
    private ArrayList<ShopList>mShopListDataAll;
    private Context mcontext;
    private int lastPosition=-1;

    ShoplistAdapter(Context context, ArrayList<ShopList> listdata){
        this.mShopListData=listdata;
        this.mShopListDataAll=listdata;
        this.mcontext=context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.list_items,parent,false));
    }

    @Override
    public void onBindViewHolder(ShoplistAdapter.ViewHolder holder, int position) {
        ShopList currentItem=mShopListData.get(position);

        holder.bindto(currentItem);

        if(holder.getAdapterPosition() >lastPosition){
            Animation animation= AnimationUtils.loadAnimation(mcontext,R.anim.slide_inrow);
            holder.itemView.startAnimation(animation);
            lastPosition=holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShopListData.size();
    }

    private Filter ShoppingFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShopList> filteredList=new ArrayList<>();
            FilterResults result=new FilterResults();

            if(constraint==null || constraint.length()==0)
            {
                result.count=mShopListDataAll.size();
                result.values=mShopListDataAll;
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for (ShopList item:mShopListDataAll)
                {
                    if(item.getName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }

                }
                result.count=filteredList.size();
                result.values=filteredList;
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShopListData=(ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return ShoppingFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView info;
        private TextView ar;
        private ImageView butor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.itemnev);
            info=itemView.findViewById(R.id.leiras);
            ar=itemView.findViewById(R.id.ar);
            butor=itemView.findViewById(R.id.imagebutor);

            itemView.findViewById(R.id.kosarba).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((ShopActivity)mcontext).updateAlertIcon();
                }
            });
        }

        public void bindto(ShopList currentItem) {
            title.setText(currentItem.getName());
            info.setText(currentItem.getInfo());
            ar.setText(currentItem.getPrice());

            Glide.with(mcontext).load(currentItem.getImgres()).into(butor);
        }
    };

}