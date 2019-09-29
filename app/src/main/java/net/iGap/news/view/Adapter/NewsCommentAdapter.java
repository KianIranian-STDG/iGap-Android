package net.iGap.news.view.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsGroup;

public class NewsCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NewsComment mData;

    public NewsCommentAdapter(NewsComment mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_comment_item, parent, false);
        return new GroupViewHolder(singleVH);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupViewHolder)holder).initView(position);
    }

    @Override
    public int getItemCount() {
        if (mData.getComments().size() > 5)
            return 5;
        return mData.getComments().size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView author, comment;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            comment = itemView.findViewById(R.id.comment);
        }

        void initView(int position) {
            Log.d("amini", "initView: ");
            author.setText(mData.getComments().get(position).getAuthor());
            comment.setText(mData.getComments().get(position).getBody());
        }
    }

}
