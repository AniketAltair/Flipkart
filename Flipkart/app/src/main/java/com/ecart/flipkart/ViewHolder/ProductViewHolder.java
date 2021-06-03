package com.ecart.flipkart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecart.flipkart.Interface.ItemClickListener;
import com.ecart.flipkart.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtproname,txtprodescrip,txtproprice;
    public ImageView imageView;

    public ItemClickListener listner;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        imageView= (ImageView)itemView.findViewById(R.id.product_image);
        txtproname= (TextView)itemView.findViewById(R.id.product_Name);
        txtprodescrip= (TextView)itemView.findViewById(R.id.product_Description);
        txtproprice= (TextView)itemView.findViewById(R.id.product_Price);


    }

    public void setItemClickListner(ItemClickListener listner){

        this.listner=listner;

    }

    @Override
    public void onClick(View view) {
        listner.onClick(view,getAdapterPosition(),false);

    }
}
