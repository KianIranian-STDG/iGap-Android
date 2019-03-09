package net.iGap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iGap.R;
import net.iGap.module.dashboard.DashboardModel;

import java.util.ArrayList;

public class AdapterDashboard extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<DashboardModel> dashboardModels;

    public AdapterDashboard(Context context, ArrayList<DashboardModel> dashboardModels) {
        this.context = context;
        this.dashboardModels = dashboardModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context.getApplicationContext());
        switch (i) {
            case 1:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_1, viewGroup, false));
            case 2:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_2, viewGroup, false));
            case 3:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_3, viewGroup, false));
            case 4:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_4, viewGroup, false));
            case 5:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_5, viewGroup, false));
            case 6:
                return new ViewType1(layoutInflater.inflate(R.layout.item_dashboard_6, viewGroup, false));

        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ViewType1) {
    //        Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType1) viewHolder).imgDashboard);
        } else if (viewHolder instanceof ViewType2) {
           /* Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType2) viewHolder).imgDashboard);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType2) viewHolder).imgDashboard2);*/

        } else if (viewHolder instanceof ViewType3) {

           /* Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType3) viewHolder).imgDashboard);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType3) viewHolder).imgDashboard2);*/

        } else if (viewHolder instanceof ViewType4) {
           /* Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType4) viewHolder).imgDashboard);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType4) viewHolder).imgDashboard2);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(2)).into(((ViewType4) viewHolder).imgDashboard3);*/

        } else if (viewHolder instanceof ViewType5) {

          /*  Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType5) viewHolder).imgDashboard);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType5) viewHolder).imgDashboard2);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(2)).into(((ViewType5) viewHolder).imgDashboard3);*/

        } else if (viewHolder instanceof ViewType6) {
           /* Glide.with(context).load(dashboardModels.get(i).getImageList().get(0)).into(((ViewType6) viewHolder).imgDashboard);
            Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType6) viewHolder).imgDashboard2);*/
        }

    }

    @Override
    public int getItemCount() {
        return dashboardModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dashboardModels.get(position).getType();
    }

    public class ViewType1 extends RecyclerView.ViewHolder {
        ImageView imgDashboard;

        public ViewType1(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
        }
    }


    public class ViewType2 extends RecyclerView.ViewHolder {
        ImageView imgDashboard, imgDashboard2;

        public ViewType2(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
            imgDashboard2 = itemView.findViewById(R.id.imgDashboard2);

        }
    }


    public class ViewType3 extends RecyclerView.ViewHolder {
        ImageView imgDashboard, imgDashboard2;

        public ViewType3(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
            imgDashboard2 = itemView.findViewById(R.id.imgDashboard2);
        }
    }

    public class ViewType4 extends RecyclerView.ViewHolder {
        ImageView imgDashboard, imgDashboard2, imgDashboard3;

        public ViewType4(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
            imgDashboard2 = itemView.findViewById(R.id.imgDashboard2);
            imgDashboard3 = itemView.findViewById(R.id.imgDashboard3);
        }
    }

    public class ViewType5 extends RecyclerView.ViewHolder {
        ImageView imgDashboard, imgDashboard2, imgDashboard3;

        public ViewType5(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
            imgDashboard2 = itemView.findViewById(R.id.imgDashboard2);
            imgDashboard3 = itemView.findViewById(R.id.imgDashboard3);

        }
    }

    public class ViewType6 extends RecyclerView.ViewHolder {
        ImageView imgDashboard, imgDashboard2;

        public ViewType6(@NonNull View itemView) {
            super(itemView);
            imgDashboard = itemView.findViewById(R.id.imgDashboard);
            imgDashboard2 = itemView.findViewById(R.id.imgDashboard2);
        }
    }

}