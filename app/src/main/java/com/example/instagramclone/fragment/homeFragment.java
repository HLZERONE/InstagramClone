package com.example.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.EndlessRecyclerViewScrollListener;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {

    RecyclerView recyclerView;
    PostAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;


    List<Post> posts = new ArrayList<>();
    static final String TAG = "homeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set up recycle view
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresher);

        adapter = new PostAdapter(requireContext(), posts);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        queryPost();

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPost();
            }
        });

        //Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Build scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    //query the posts
    public void queryPost(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include("user");
        query.addDescendingOrder("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> newPosts, ParseException e) {
                if(e != null){
                    Log.i(TAG, "Fail to grab posts");
                }
                else{
                    adapter.clean();
                    adapter.addAll(newPosts);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    //query the next posts
    public void loadNextData(int offset){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include("user");
        query.addDescendingOrder("createdAt");
        query.whereLessThan("createdAt", posts.get(posts.size()-1).getCreatedAt());
        query.setLimit(20);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> newPosts, ParseException e) {
                if(e != null){
                    Log.i(TAG, "Fail to grab posts");
                }
                else{
                    if(posts != null){
                        posts.addAll(newPosts);
                        adapter.notifyItemRangeInserted(20 * offset, 20);
                    }
                }
            }
        });
    }
}