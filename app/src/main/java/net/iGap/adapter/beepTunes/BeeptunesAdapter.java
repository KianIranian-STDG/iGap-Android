package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.module.api.beepTunes.Album;

import java.util.ArrayList;
import java.util.List;

public class BeeptunesAdapter extends RecyclerView.Adapter<BeeptunesAdapter.RowViewHolder> {

    private List<Album> albums = new ArrayList<>();

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_row, viewGroup, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder rowViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView headerTv;
        private ItemAdapter adapter;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_rowItem);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter = new ItemAdapter();
            recyclerView.setAdapter(adapter);
        }

        void bindRow(Album album) {

        }
    }
}
