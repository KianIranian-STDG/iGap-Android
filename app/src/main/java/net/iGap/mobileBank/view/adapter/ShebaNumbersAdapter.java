package net.iGap.mobileBank.view.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ShebaNumbersAdapter extends RecyclerView.Adapter<ShebaNumbersAdapter.ViewHolderSheba> {

    private List<String> items;

    public ShebaNumbersAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolderSheba onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSheba(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sheba_numbers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSheba holder, int position) {
        holder.tvSheba.setText(items.get(position));
        holder.tvCopy.setOnClickListener(v -> copy(items.get(holder.getAdapterPosition()), holder.tvCopy.getContext()));
    }

    private void copy(String sheba, Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Sheba", sheba);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderSheba extends RecyclerView.ViewHolder {

        private TextView tvSheba, tvCopy;

        public ViewHolderSheba(@NonNull View itemView) {
            super(itemView);
            tvSheba = itemView.findViewById(R.id.tvTitle);
            tvCopy = itemView.findViewById(R.id.tvCopy);
        }
    }
}
