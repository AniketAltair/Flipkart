package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecart.flipkart.Model.Products;
import com.ecart.flipkart.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminProductsLayout extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private String type="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products_layout);

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        //NavigationView navigationView = root.findViewById(R.id.nav_view);

        //View headerview = navigationView.getHeaderView(0);
        //TextView usernameTextview = (TextView) findViewById(R.id.user_profile_name);// not important line, taken care in newhomeactivity
        //CircleImageView profileImageView = headerview.findViewById(R.id.user_profile_image);

        //usernameTextview.setText(Prevalent.currentOnlineUser.getName());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu2);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Products> options= new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef,Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Products model)
            {
                holder.txtproname.setText(model.getPname());
                holder.txtprodescrip.setText(model.getDescription());
                holder.txtproprice.setText("Price = ₹ "+model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.imageView);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(AdminProductsLayout.this, AdminMaintainActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);


                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}