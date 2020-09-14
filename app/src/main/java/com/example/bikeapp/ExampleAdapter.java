package com.example.bikeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.mViewHolder> {

    private List<HomeModel> homeModel;

    public ExampleAdapter(List<HomeModel> homeModel) {

        this.homeModel = homeModel;

    }



    @NonNull
    @Override
    public ExampleAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exlistcard, parent, false);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new ExampleAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleAdapter.mViewHolder holder, int position) {
        Integer image=homeModel.get(position).getImage();
        holder.setImageView(image);
    }

    @Override
    public int getItemCount() {
        return homeModel.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private View view;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void setImageView(Integer imageview){
            imageView=view.findViewById(R.id.exlistImage);
            imageView.setImageResource(imageview);
        }
    }
}
