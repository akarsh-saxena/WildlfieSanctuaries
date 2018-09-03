package com.application.wildlife.wildlifesanctuaries;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import static android.view.View.GONE;

public class FeedbacksAdapter extends RecyclerView.Adapter<FeedbacksAdapter.Items> {

    Context context;
    List<FeedbackModel> feedbackModels;

    public FeedbacksAdapter(Context context, List<FeedbackModel> feedbackModels) {

        this.context = context;
        this.feedbackModels = feedbackModels;

    }

    @Override
    public Items onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_feedback, parent, false);
        return new Items(view);
    }

    @Override
    public void onBindViewHolder(Items holder, int position) {
        holder.tvName.setText(feedbackModels.get(position).getName());
        holder.tvFeedback.setText(feedbackModels.get(position).getFeedback());
        holder.tvName.setText(feedbackModels.get(position).getName());
        holder.tvEmail.setText(feedbackModels.get(position).getEmail());
        holder.rbFeedback.setRating(feedbackModels.get(position).getRating());
        if(feedbackModels.get(position).getImagePath()!=null)
            holder.imageView.setImageURI(Uri.parse(feedbackModels.get(position).getImagePath()));
        else
            holder.imageView.setVisibility(GONE);
    }

    @Override
    public int getItemCount() {
        return feedbackModels.size();
    }

    public class Items extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvName, tvFeedback, tvEmail;
        RatingBar rbFeedback;

        public Items(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tvName);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            rbFeedback = itemView.findViewById(R.id.rbFeedback);
        }
    }
}
