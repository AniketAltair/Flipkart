package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.ecart.flipkart.Model.Products;
import com.ecart.flipkart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailActivity extends AppCompatActivity {
    private Button productPrice;
    private Button addToCartButton;
    private Button productDescription;
    private Button productName;
    private Button img;
    //private ImageView newimg;
    private ElegantNumberButton numberButton;
    private String productID="",state="Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_detail);


        productID=getIntent().getStringExtra("pid");
        Toast.makeText(this, productID, Toast.LENGTH_SHORT).show();



        numberButton=(ElegantNumberButton)findViewById(R.id.nnn);
        addToCartButton=(Button)findViewById(R.id.pd_add_to_cart_button);


        productPrice= (Button) findViewById(R.id.products_detail_price);
        //newimg=(ImageView)findViewById(R.id.pro_img);
        img=(Button)findViewById(R.id.products_detail_image);
        productDescription=(Button) findViewById(R.id.products_detail_description);
        productName=(Button) findViewById(R.id.products_detail_name);


        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductsDetailActivity.this,"You can place order once your previous order is shipped or confirmed!!!",Toast.LENGTH_SHORT).show();

                }else{
                    addingToCartList();
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

    }

    private void addingToCartList() {
        String saveCurrentTime,saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(ProductsDetailActivity.this, "Added to Cart List", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductsDetailActivity.this,NewHomeActivity.class);
                                                startActivity(intent);
                                            }

                                        }
                                    });


                        }

                    }
                });
    }

    private void getProductDetails(String productID) {

            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
            productsRef.child(productID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        Products products = snapshot.getValue(Products.class);

                        productName.setText(products.getPname());
                        productPrice.setText(products.getPrice());
                        productDescription.setText(products.getDescription());
                        //Picasso.get().load(products.getImage()).into(productImage);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    private void  CheckOrderState(){

        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());


        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped")){

                        state = "Order Shipped";

                    }else if(shippingState.equals("not shipped")){

                        state = "Order Placed";

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    }