package net.iGap.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.adapter.items.cells.RoomListCell;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.libs.MyRealmRecyclerViewAdapter;
import net.iGap.realm.RealmRoom;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

public class RoomListAdapter extends MyRealmRecyclerViewAdapter<RealmRoom, RoomListAdapter.ViewHolder> {

    private View emptyView;
    private View loadingView;
    private AvatarHandler avatarHandler;
    private List<RealmRoom> mSelectedRoomList;
    private OnMainFragmentCallBack callBack;
    private boolean isChatMultiSelectEnable;

    public RoomListAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, View emptyView, View loadingView, AvatarHandler avatarHandler, List<RealmRoom> mSelectedRoomList) {
        super(data, true);
        this.emptyView = emptyView;
        this.loadingView = loadingView;
        this.avatarHandler = avatarHandler;
        this.mSelectedRoomList = mSelectedRoomList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RoomListCell roomListCell = new RoomListCell(parent.getContext());
        roomListCell.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 72));
        return new ViewHolder(roomListCell);
    }

    @Override
    protected OrderedRealmCollectionChangeListener createListener() {
        return (OrderedRealmCollectionChangeListener<RealmResults<RealmRoom>>) (collection, changeSet) -> {
            if (G.onUnreadChange != null) {
                int unreadCount = 0;
                for (RealmRoom room : collection) {
                    if (!room.getMute() && !room.isDeleted() && room.getUnreadCount() > 0) {
                        unreadCount += room.getUnreadCount();
                    }
                }
                G.onUnreadChange.onChange(unreadCount);
            }

            if (getData() != null && getData().size() > 0) {
                emptyView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
            if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                loadingView.setVisibility(View.GONE);
                notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                notifyItemRangeInserted(range.startIndex, range.length);
            }

            if (!updateOnModification) {
                return;
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                notifyItemRangeChanged(range.startIndex, range.length);
            }
        };
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int i) {
        final RealmRoom mInfo = holder.realmRoom = getItem(i);
        if (mInfo == null) {
            return;
        }
        holder.getRootView().setData(mInfo, avatarHandler, isChatMultiSelectEnable);
        holder.getRootView().setCheck(mSelectedRoomList.contains(mInfo));
    }

    public void setCallBack(OnMainFragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public void setMultiSelect(boolean isChatMultiSelectEnable) {
        this.isChatMultiSelectEnable = isChatMultiSelectEnable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RealmRoom realmRoom;
        private RoomListCell rootView;

        public ViewHolder(View view) {
            super(view);

            rootView = (RoomListCell) itemView;
            itemView.setOnClickListener(v -> callBack.onClick(rootView, realmRoom, getAdapterPosition()));
            itemView.setOnLongClickListener(v -> callBack.onLongClick(rootView, realmRoom,getAdapterPosition()));
        }

        public RoomListCell getRootView() {
            return rootView;
        }
    }

    public interface OnMainFragmentCallBack {
        void onClick(RoomListCell roomListCell, RealmRoom realmRoom, int adapterPosition);

        boolean onLongClick(RoomListCell roomListCell, RealmRoom realmRoom,int position);
    }
}
