package com.example.instagramclone.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Post;
import com.example.instagramclone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class addFragment extends Fragment implements View.OnClickListener{

    static final String TAG = "AddFragment";
    Bitmap file = null;

    ImageView imageView;
    EditText descriptionView;
    ConstraintLayout addLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        imageView = view.findViewById(R.id.imageView);
        descriptionView = view.findViewById(R.id.descriptionView);
        addLayout = view.findViewById(R.id.addLayout);

        addLayout.setOnClickListener(this::onClick);
        imageView.setOnClickListener(this::onClick);

        //check if permission is granted
        if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else{
            getPhoto();
        }

    }

    //get the photo
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }
    //intent data callBack launcher
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            Uri selectedImage = data.getData();
            try{
                file = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
                imageView.setImageBitmap(file);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    });

    //ask for permission launcher
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        getPhoto();
                    }
                }
            });

    //Define own menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.add_post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add:
                publicPost();
                break;
            case R.id.reselect:
                getPhoto();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //hide keyboard when not clicking the descriptionView
    @Override
    public void onClick(View view) {
        if(view != descriptionView){
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void publicPost(){
        if(file == null){
            Toast.makeText(requireActivity(), "Photo unselected", Toast.LENGTH_SHORT).show();
            return;
        }
        //turn bitmap to ParseFile
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ParseFile file = new ParseFile("image.png", byteArray);

        //create new post
        Post newPost = new Post();
        newPost.setDescription(descriptionView.getText().toString());
        newPost.setUser(ParseUser.getCurrentUser());
        newPost.setImage(file);

        //upload to server
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    ((BottomNavigationView)getActivity().findViewById(R.id.bottomNavigation)).setSelectedItemId(R.id.home);
                }
            }
        });


    }
}