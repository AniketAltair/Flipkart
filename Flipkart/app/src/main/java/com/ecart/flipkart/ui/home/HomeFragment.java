package com.ecart.flipkart.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecart.flipkart.Model.Products;
import com.ecart.flipkart.ProductsDetailScreen_2;
import com.ecart.flipkart.R;
import com.ecart.flipkart.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {


    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private String type="";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        //NavigationView navigationView = root.findViewById(R.id.nav_view);

        //View headerview = navigationView.getHeaderView(0);
        TextView usernameTextview = (TextView) root.findViewById(R.id.user_profile_name);// not important line, taken care in newhomeactivity
        //CircleImageView profileImageView = headerview.findViewById(R.id.user_profile_image);

        //usernameTextview.setText(Prevalent.currentOnlineUser.getName());

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_menu1);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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


                        Intent intent = new Intent(getActivity(), ProductsDetailScreen_2.class);
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


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


}