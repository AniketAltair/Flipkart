package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainActivity extends AppCompatActivity {


    private Button applychangesbtn,deletebtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productsref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        productID=getIntent().getStringExtra("pid");
        productsref= FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applychangesbtn=findViewById(R.id.product_maintain_btn);
        name=findViewById(R.id.product_name_maintain);
        price=findViewById(R.id.product_Price_maintain);
        description=findViewById(R.id.product_Description_maintain);
        imageView=findViewById(R.id.product_image_maintain);
        deletebtn=findViewById(R.id.delete_maintain_btn);

        displayspecificproductInfo();

        applychangesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                applychanges();

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletethisproduct();

            }
        });

    }

    private void deletethisproduct() {

        productsref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent1 = new Intent(AdminMaintainActivity.this, AdminCategoryActivity.class);
                startActivity(intent1);
                finish();

                Toast.makeText(AdminMaintainActivity.this,"Product Deleted successfully",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void applychanges() {

        String pName =name.getText().toString();
        String pPrice =price.getText().toString();
        String pDescription =description.getText().toString();


        if(pName.equals("")){
            Toast.makeText(this,"Write down product Name",Toast.LENGTH_SHORT).show();
        }
        else if(pPrice.equals("")){
            Toast.makeText(this,"Write down product Price",Toast.LENGTH_SHORT).show();
        }
        else if(pDescription.equals("")){
            Toast.makeText(this,"Write down product Description",Toast.LENGTH_SHORT).show();
        }
        else{

            HashMap<String,Object> productMap = new HashMap<>();

            productMap.put("pid",productID);
            productMap.put("description",pDescription);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);

            productsref.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainActivity.this,"Changes Applied Successfully",Toast.LENGTH_SHORT).show();


                        Intent intent1 = new Intent(AdminMaintainActivity.this, AdminCategoryActivity.class);
                        startActivity(intent1);
                        finish();

                    }

                }
            });

        }

    }

    private void displayspecificproductInfo() {

        productsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String pname= snapshot.child("pname").getValue().toString();
                    String pprice= snapshot.child("price").getValue().toString();
                    String pdescription= snapshot.child("description").getValue().toString();
                    String pimage= snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);

                    Picasso.get().load(pimage).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}