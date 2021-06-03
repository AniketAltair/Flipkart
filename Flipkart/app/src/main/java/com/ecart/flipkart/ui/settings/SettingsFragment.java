package com.ecart.flipkart.ui.settings;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.ecart.flipkart.HomeActivity;
import com.ecart.flipkart.Prevalent.Prevalent;
import com.ecart.flipkart.R;
import com.ecart.flipkart.ResetPasswordActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private CircleImageView profileImageView;
    private EditText fullnameEditText,phoneNumberEditText,addressEditText;
    private TextView profileChangebtn,savebtn,closebtn;
    private Button securityquesbtn;


    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    Activity mActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_setting, container, false);

        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView=(CircleImageView) root.findViewById(R.id.settings_profile_image);
        fullnameEditText=(EditText)root.findViewById(R.id.settings_full_name);
        phoneNumberEditText=(EditText)root.findViewById(R.id.settings_phone_number);
        addressEditText=(EditText)root.findViewById(R.id.settings_address);
        profileChangebtn=(TextView)root.findViewById(R.id.profile_image_change_btn);
        closebtn=(TextView)root.findViewById(R.id.close_settings_btn);
        savebtn=(TextView)root.findViewById(R.id.update_settings_btn);
        securityquesbtn=(Button)root.findViewById(R.id.security_questions_btn);

        userInfoDisplay(profileImageView,fullnameEditText,phoneNumberEditText,addressEditText);

        securityquesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=  new Intent(getActivity(), ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }




        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){

                    userInfoSaved();

                }
                else{

                    updateOnlyUserInfo();

                }
            }
        });

        profileChangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";

                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    startGallery();
                }




               /* CropImage.activity(imageUri).setAspectRatio(1,1)
                        .start(mActivity);// possiblity of error

                
                Toast.makeText(getActivity(),"ImageUri:"+imageUri,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_SEARCH);

                startActivityForResult(intent,1888);*/

            }
        });


        return root;
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullnameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",phoneNumberEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(getActivity(), HomeActivity.class));
        Toast.makeText(getActivity(),"Profile Info Updated Successfully",Toast.LENGTH_LONG).show();
        getActivity().finish();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(resultCode == RESULT_OK) {
            Toast.makeText(mActivity,"AAAA",Toast.LENGTH_LONG).show();

            if(requestCode == 1000){
                Toast.makeText(mActivity,"BBBB",Toast.LENGTH_LONG).show();

                imageUri = data.getData();

                profileImageView.setImageURI(imageUri);
                Toast.makeText(mActivity,"ImageUri1:"+imageUri,Toast.LENGTH_LONG).show();

            }
        }


       /*if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null ){// possibility of error
            Toast.makeText(mActivity,"ImageUri2:"+imageUri,Toast.LENGTH_LONG).show();


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();



            profileImageView.setImageURI(imageUri);

        }else{
            Toast.makeText(mActivity,"ImageUri3:"+imageUri,Toast.LENGTH_LONG).show();


            Toast.makeText(getActivity(),"Error, Try Again",Toast.LENGTH_LONG).show();

            startActivity(new Intent(getActivity(),SettingsFragment.class));
            getActivity().finish();

        }*/

    }

    private void userInfoSaved() {

        if(TextUtils.isEmpty(fullnameEditText.getText().toString())){

            Toast.makeText(getActivity(),"Name is mandatory",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(getActivity(),"Address is mandatory",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(phoneNumberEditText.getText().toString())){
            Toast.makeText(getActivity(),"PhoneNumber is mandatory",Toast.LENGTH_SHORT).show();

        }
        else if(checker=="clicked"){
            uploadImage();
        }

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null){

            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone()+".jpg");
            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {

                        throw task.getException();

                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",fullnameEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("phoneOrder",phoneNumberEditText.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                        Toast.makeText(getActivity(),"Profile Info Updated Successfully",Toast.LENGTH_SHORT).show();
                        getActivity().finish();

                    }
                    else{

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();

                    }


                }
            });
        }
        else{

            Toast.makeText(getActivity(),"Image is not selected",Toast.LENGTH_SHORT).show();

        }

    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullnameEditText, final EditText phoneNumberEditText, final EditText addressEditText) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());// possiblity of error

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    if(snapshot.child("image").exists()){

                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEditText.setText(name);
                        phoneNumberEditText.setText(phone);
                        addressEditText.setText(address);


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




