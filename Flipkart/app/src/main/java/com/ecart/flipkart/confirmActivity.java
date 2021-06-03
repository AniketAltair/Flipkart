package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ecart.flipkart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class confirmActivity extends AppCompatActivity {

    private EditText nameedittxt,phoneedittxt,addressedittxt,cityedittxt;
    private Button confirmorderbtn;
    private String totalAmount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);


        totalAmount=getIntent().getStringExtra("Total Price") ;
        Toast.makeText(this,"Total Price = "+totalAmount,Toast.LENGTH_SHORT).show();

        confirmorderbtn=(Button)findViewById(R.id.confirm_final_order_btn);

        nameedittxt=(EditText)findViewById(R.id.shippment_name);
        phoneedittxt=(EditText) findViewById(R.id.shippment_phone_number);
        addressedittxt=(EditText) findViewById(R.id.shippment_address);
        cityedittxt=(EditText) findViewById(R.id.shippment_city);

        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                Check();
                
            }


        });

    }

    private void Check() {

        if(TextUtils.isEmpty(nameedittxt.getText().toString())){
            Toast.makeText(this,"Please Provide your Full Name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneedittxt.getText().toString())){
            Toast.makeText(this,"Please Provide your PhoneNumber",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressedittxt.getText().toString())){
            Toast.makeText(this,"Please Provide your Address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityedittxt.getText().toString())){
            Toast.makeText(this,"Please Provide your City",Toast.LENGTH_SHORT).show();
        }
        else{
            ConfirmOrder();
        }

    }

    private void ConfirmOrder() {

        final String saveCurrentDate,saveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(callForDate.getTime());

        final DatabaseReference ordersref = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> ordersmap = new HashMap<>();

        ordersmap.put("totalAmount",totalAmount);
        ordersmap.put("name",nameedittxt.getText().toString());
        ordersmap.put("phone",phoneedittxt.getText().toString());
        ordersmap.put("date",saveCurrentDate);
        ordersmap.put("time",saveCurrentTime);
        ordersmap.put("address",addressedittxt.getText().toString());
        ordersmap.put("city",cityedittxt.getText().toString());
        ordersmap.put("state","not shipped");

        ordersref.updateChildren(ordersmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(confirmActivity.this,"Your order has been placed",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(confirmActivity.this,NewHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                }

            }
        });

    }
}