package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ecart.flipkart.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminsNewActivity extends AppCompatActivity {

    private RecyclerView orederslist;
    private DatabaseReference ordersref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_new);

        ordersref= FirebaseDatabase.getInstance().getReference().child("Orders");
        orederslist=(RecyclerView)findViewById(R.id.orders_list);

        orederslist.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersref,AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int i, @NonNull final AdminOrders model) {

                holder.username.setText("Name: "+model.getName());
                holder.userphoneNumber.setText("Phone: "+model.getPhone());
                holder.userTotalprice.setText("Total Amount : $"+model.getTotalAmount());
                holder.userDateTime.setText("Order At: "+model.getDate()+" "+model.getTime());
                holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());

                holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String uID=getRef(i).getKey();

                        Intent intent=new Intent(AdminsNewActivity.this,AdminUserProductsActivity.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{
                                "Yes","No"
                        };
                        AlertDialog.Builder build = new AlertDialog.Builder(AdminsNewActivity.this);
                        build.setTitle("Have U shipped this Products ?");
                        build.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {

                                if(j==0){

                                    String uid=getRef(i).getKey();
                                    RemoveOrder(uid);

                                }else{
                                    finish();
                                }

                            }
                        });
                        build.show();
                    }
                });

            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                return new AdminOrdersViewHolder(view);

            }
        };

        orederslist.setAdapter(adapter);
        adapter.startListening();

    }

    private void RemoveOrder(String uid) {

        ordersref.child(uid).removeValue();

    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView username,userphoneNumber,userTotalprice,userDateTime,userShippingAddress;
        public Button showOrderBtn;


        public AdminOrdersViewHolder(View itemView){

            super(itemView);


            username=itemView.findViewById(R.id.order_username);
            userphoneNumber=itemView.findViewById(R.id.order_phone_number);
            userTotalprice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            showOrderBtn=itemView.findViewById(R.id.show_order_all_btn);


        }

    }

}