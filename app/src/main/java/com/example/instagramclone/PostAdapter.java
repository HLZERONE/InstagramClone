package com.example.instagramclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    //initialize
    List<Post> posts;
    Context context;
    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postAvatar;
        TextView postUsername;
        ImageView postView;
        TextView postDescriptionName;
        TextView postDescription;
        TextView timeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postAvatar = itemView.findViewById(R.id.postAvatar);
            postUsername = itemView.findViewById(R.id.postUsername);
            postView = itemView.findViewById(R.id.postView);
            postDescriptionName = itemView.findViewById(R.id.postDescriptionName);
            postDescription = itemView.findViewById(R.id.postDescription);
            timeStamp = itemView.findViewById(R.id.timestamp);
        }

        public void bind(Post post){
            postUsername.setText(post.getUser().getUsername());
            postDescription.setText(post.getDescription());
            postDescriptionName.setText(post.getUser().getUsername());
            timeStamp.setText(post.getFormattedTimestamp() + " ago");

            Glide.with(context).load(post.getImage().getUrl()).into(postView);
            Glide.with(context).load(post.getUser().getParseFile("avatar").getUrl()).into(postAvatar);
        }
    }

    public void clean(){
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> newPosts){
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }
}
