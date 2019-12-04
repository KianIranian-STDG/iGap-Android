package net.iGap.news.view.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.news.repository.model.NewsComment;

import java.util.List;

public class NewsCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsComment> mData;

    public NewsCommentAdapter(List<NewsComment> mData) {
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
        ((GroupViewHolder) holder).initView(position);
    }

    @Override
    public int getItemCount() {
        if (mData.size() > 5)
            return 5;
        return mData.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView author, comment;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            comment = itemView.findViewById(R.id.comment);
        }

        void initView(int position) {
            author.setText(mData.get(position).getUsername());
            comment.setText(mData.get(position).getComment());
        }
    }

}
