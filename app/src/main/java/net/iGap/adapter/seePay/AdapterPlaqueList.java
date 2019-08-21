package net.iGap.adapter.seePay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.libs.PlaqueView;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlaqueList extends RecyclerView.Adapter<AdapterPlaqueList.PlaqueViewHolder> {

    private List<String> plaqueList = new ArrayList<>();
    private List<String> selectedPlaqueList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public AdapterPlaqueList(Context context) {
        this.mContext = context;
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
    public void onBindViewHolder(@NonNull PlaqueViewHolder plaqueViewHolder, int i) {
        plaqueViewHolder.bind(plaqueList.get(i));
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
        private View view ;

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

            String[] plaqueValue = getPlaque(plaque);

            plaqueView.setPlaque1(plaqueValue[0]);
            plaqueView.setPlaqueAlphabet(getPlaqueAlphabet(plaqueValue[1]));
            plaqueView.setPlaque2(plaqueValue[2]);
            plaqueView.setPlaqueCity(plaqueValue[3]);

            root.setOnClickListener(v -> {

                if (selectedPlaqueList.contains(plaque)) {
                    selectedPlaqueList.remove(plaque);
                    checkBox.setChecked(false);
                } else {
                    selectedPlaqueList.add(plaque);
                    checkBox.setChecked(true);
                }

            });

            view.setOnClickListener(v -> root.performClick());

            edit.setOnClickListener(v -> {

            });
        }

        private String getPlaqueAlphabet(String s) {
            return "غ";
        }

        public String[] getPlaque(String plaque) {
            String[] result = {"", "", "", ""};

            result[0] = plaque.substring(0, 2);
            result[1] = plaque.substring(2, 4);
            result[2] = plaque.substring(4, 7);
            result[3] = plaque.substring(7, 9);

            return result;
        }
    }
}
