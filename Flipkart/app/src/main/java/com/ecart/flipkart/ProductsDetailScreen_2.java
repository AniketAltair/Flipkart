package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailScreen_2 extends AppCompatActivity {

    private TextView productPrice;
    private Button addToCartButton;
    private TextView productDescription;
    private TextView productName;
    private ImageView img;
    private ElegantNumberButton numberButton;
    private String productID="",state="Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_detail_screen_2);


        productID=getIntent().getStringExtra("pid");
        //productsref= FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        numberButton=findViewById(R.id.nnn111);
        productName=findViewById(R.id.product_name_maintain111);
        productPrice=findViewById(R.id.product_Price_maintain111);
        productDescription=findViewById(R.id.product_Description_maintain111);
        img=findViewById(R.id.product_image_maintain111);
        addToCartButton=findViewById(R.id.delete_maintain_btn111);

        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductsDetailScreen_2.this,"You can place order once your previous order is shipped or confirmed!!!",Toast.LENGTH_SHORT).show();

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
                                                Toast.makeText(ProductsDetailScreen_2.this, "Added to Cart List", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductsDetailScreen_2.this,NewHomeActivity.class);
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
                    Picasso.get().load(products.getImage()).into(img);

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