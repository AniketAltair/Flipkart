package com.ecart.flipkart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecart.flipkart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check="";
    private TextView pagetitle,titlequestions;
    private EditText phoneNumber,question1,question2;
    private Button verifybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check=getIntent().getStringExtra("check");

        pagetitle=findViewById(R.id.page_title);
        titlequestions=findViewById(R.id.title_questions);
        phoneNumber=findViewById(R.id.find_phone_number);
        question1=findViewById(R.id.question1);
        question2=findViewById(R.id.question2);
        verifybtn=findViewById(R.id.verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);



        if(check.equals("settings")){
            Toast.makeText(ResetPasswordActivity.this,"setting",Toast.LENGTH_SHORT).show();
            pagetitle.setText("Set Questions");
            titlequestions.setText("Please set questions for security questions");
            verifybtn.setText("Set");

            displaypreviousAnswers();

            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();
                }
            });

        }
        else if(check.equals("login")){
            Toast.makeText(ResetPasswordActivity.this,"login",Toast.LENGTH_SHORT).show();

            phoneNumber.setVisibility(View.VISIBLE);

            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    verifyuser();

                }
            });

        }

    }


    private void setAnswers(){

        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();

        if(question1.equals("") && question2.equals("")){

            Toast.makeText(ResetPasswordActivity.this,"Please answer both the questions",Toast.LENGTH_SHORT).show();


        }else{

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String,Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1",answer1);
            userdataMap.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(ResetPasswordActivity.this,"Security Questions set successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, NewHomeActivity.class);
                        startActivity(intent);
                    }

                }
            });

        }

    }

    private void displaypreviousAnswers(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String ans1=snapshot.child("answer1").getValue().toString();
                    String ans2=snapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void verifyuser(){

        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){

                        String mphone=snapshot.child("phone").getValue().toString();

                        if(snapshot.hasChild("Security Questions")){

                            String ans1=snapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2=snapshot.child("Security Questions").child("answer2").getValue().toString();

                            if(!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this,"Your 1st answer is wrong",Toast.LENGTH_SHORT).show();

                            }else if(!ans2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this,"Your 2nd answer is wrong",Toast.LENGTH_SHORT).show();

                            }else{

                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("New Password...");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(!newPassword.getText().toString().equals("")){

                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){

                                                                Toast.makeText(ResetPasswordActivity.this,"Password changed successfully",Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);

                                                            }

                                                        }
                                                    });

                                        }

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();

                                    }
                                });

                                builder.show();
                            }
                        }
                        else{

                            Toast.makeText(ResetPasswordActivity.this,"you have not set security questions",Toast.LENGTH_SHORT).show();

                        }

                    }
                    else{
                        Toast.makeText(ResetPasswordActivity.this,"This phone number does not exist",Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        else{

            Toast.makeText(ResetPasswordActivity.this,"Please fill all details !!!",Toast.LENGTH_SHORT).show();

        }


    }


}