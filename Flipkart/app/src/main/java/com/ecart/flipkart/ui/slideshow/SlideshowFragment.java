package com.ecart.flipkart.ui.slideshow;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecart.flipkart.Model.Cart;
import com.ecart.flipkart.NewHomeActivity;
import com.ecart.flipkart.Prevalent.Prevalent;
import com.ecart.flipkart.ProductsDetailActivity;
import com.ecart.flipkart.R;
import com.ecart.flipkart.ViewHolder.CartViewHolder;
import com.ecart.flipkart.confirmActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SlideshowFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button NextProcessBtn;
    private TextView txtTotalAmount,msg1;

    private int overTotalPrice = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        CheckOrderState();

        recyclerView=(RecyclerView)root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        NextProcessBtn=(Button)root.findViewById(R.id.next_process_btn);
        txtTotalAmount=(TextView)root.findViewById(R.id.total_price);
        msg1=(TextView)root.findViewById(R.id.msg1);

        txtTotalAmount.setText("Total Price = "+String.valueOf(overTotalPrice));


        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTotalAmount.setText("Total Price = "+String.valueOf(overTotalPrice));

                Intent intent = new Intent(getActivity(), confirmActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                getActivity().finish();
            }
        });

        final DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartlistref.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart,CartViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductPrice.setText("Price = ₹ "+model.getPrice());
                holder.txtProductName.setText(model.getPname());

                int oneTypeProductPrice = ((Integer.valueOf(model.getPrice())))*((Integer.valueOf(model.getQuantity())));
                overTotalPrice=overTotalPrice+oneTypeProductPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[]=new CharSequence[]{
                                "Edit","Remove"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i==0){
                                    Intent intent = new Intent(getActivity(), ProductsDetailActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if(i==1){
                                    cartlistref.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(getActivity(),"Item removed Successfully",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getActivity(), NewHomeActivity.class);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                                }

                            }
                        });
                        builder.show();

                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


        return root;
    }

    private void  CheckOrderState(){

        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());


        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();
                    String username = snapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped")){

                        txtTotalAmount.setText("Your Order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        msg1.setText("Your Order is shipped successfully");
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"You can order once Your previous order has been received",Toast.LENGTH_SHORT).show();

                    }else if(shippingState.equals("not shipped")){

                        txtTotalAmount.setText("Not shipped");
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"You can order once Your previous order has been received",Toast.LENGTH_SHORT).show();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}