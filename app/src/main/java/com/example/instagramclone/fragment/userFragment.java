package com.example.instagramclone.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.R;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class userFragment extends Fragment {

    ImageView userAvatar;
    TextView userUsername;
    Button logOutButton;
    ImageView changeAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAvatar = view.findViewById(R.id.userAvatar);
        userUsername = view.findViewById(R.id.userUsername);
        logOutButton = view.findViewById(R.id.logOutButton);
        changeAvatar = view.findViewById(R.id.changeAvatar);

        //Bind username
        userUsername.setText(ParseUser.getCurrentUser().getUsername());
        //Bind avatar
        Glide.with(requireContext()).load(ParseUser.getCurrentUser().getParseFile("avatar").getUrl()).into(userAvatar);

        //Log Out, OnclickListener
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                    }
                });
            }
        });

        //Update Avatar, OnClickListener
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the photo
                if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    getPhoto();
                }
                else{
                    requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });
    }

    //getting photo
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoResult.launch(intent);
    }
    ActivityResultLauncher<Intent> photoResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri selectedPhoto = result.getData().getData();
                    try{
                        Bitmap newAvatar = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedPhoto);
                        //Update avatar
                        userAvatar.setImageBitmap(newAvatar);

                        uploadAvatar(newAvatar);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

    //ask for permission
    ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        getPhoto();
                    }
                }
            });

    //Upload avatar
    public void uploadAvatar(Bitmap file){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        ParseFile avatarFile = new ParseFile("avatar.png", array);

        ParseUser.getCurrentUser().put("avatar", avatarFile);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    e.printStackTrace();
                }
            }
        });
    }
}