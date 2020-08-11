package net.iGap.adapter.cPay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCPay;
import net.iGap.libs.PlaqueView;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlaqueList extends RecyclerView.Adapter<AdapterPlaqueList.PlaqueViewHolder> {

    public MutableLiveData<String> onEditClickListener = new MutableLiveData<>();
    private List<String> plaqueList = new ArrayList<>();
    private List<String> selectedPlaqueList = new ArrayList<>();
    private LayoutInflater mInflater;

    public AdapterPlaqueList(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setPlaqueList(List<String> plaqueList) {
        this.plaqueList = plaqueList;
    }

    @NonNull
    @Override
    public PlaqueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.row_plaque_list, viewGroup, false);
        return new PlaqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaqueViewHolder plaqueViewHolder, int position) {
        plaqueViewHolder.bind(plaqueList.get(position));
    }

    @Override
    public int getItemCount() {
        return plaqueList.size();
    }

    public List<String> getSelectedPlaqueList() {
        return selectedPlaqueList;
    }

    public class PlaqueViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private PlaqueView plaqueView;
        private TextView edit;
        private ViewGroup root;
        private View view;

        public PlaqueViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.row_plaque_check_box);
            plaqueView = itemView.findViewById(R.id.row_plaque_plaqueView);
            edit = itemView.findViewById(R.id.row_plaque_edit);
            root = itemView.findViewById(R.id.row_plaque_root);
            view = itemView.findViewById(R.id.row_plaque_view);

        }

        public void bind(String plaque) {

            checkBox.setChecked(selectedPlaqueList.contains(plaque));

            String[] plaqueValue = HelperCPay.getPlaque(plaque);

            plaqueView.setPlaque1(plaqueValue[0]);
            plaqueView.setPlaqueAlphabet(HelperCPay.getPlaqueAlphabet(Integer.valueOf(plaqueValue[1])));
            plaqueView.setPlaque2(plaqueValue[2]);
            plaqueView.setPlaqueCity(plaqueValue[3]);

            root.setOnClickListener(v -> {

                if (selectedPlaqueList.contains(plaque)) {
                    selectedPlaqueList.clear();
                    checkBox.setChecked(false);
                } else {
                    selectedPlaqueList.clear();
                    selectedPlaqueList.add(plaque);
                    checkBox.setChecked(true);
                }

                notifyDataSetChanged();

            });

            view.setOnClickListener(v -> root.performClick());

            edit.setOnClickListener(v -> {
                onEditClickListener.postValue(plaque);
            });
        }

    }
}
