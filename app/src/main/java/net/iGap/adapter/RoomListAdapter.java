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
import net.iGap.messenger.theme.Theme;
import net.iGap.network.RequestManager;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.Room;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class RoomListAdapter extends MyRealmRecyclerViewAdapter<RealmRoom, RoomListAdapter.ViewHolder> {

    private final AvatarHandler avatarHandler;
    private final List<Long> selectedRoom;
    private OnMainFragmentCallBack callBack;
    private boolean isChatMultiSelectEnable;

    public RoomListAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, AvatarHandler avatarHandler, List<Long> selectedRoom) {
        super(data, true);
        this.avatarHandler = avatarHandler;
        this.selectedRoom = selectedRoom;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RoomListCell roomListCell = new RoomListCell(parent.getContext());
        roomListCell.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 72));
        roomListCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return new ViewHolder(roomListCell);
    }

    @Override
    protected OrderedRealmCollectionChangeListener<RealmResults<RealmRoom>> createListener() {
        return (OrderedRealmCollectionChangeListener<RealmResults<RealmRoom>>) (collection, changeSet) -> {
            if (G.onUnreadChange != null) {
                int unreadCount = 0;
                for (RealmRoom room : collection) {
                    if (!room.getMute() && !room.isDeleted() && room.getUnreadCount() > 0) {
                        unreadCount += room.getUnreadCount();
                    }
                }
                G.onUnreadChange.onChange(unreadCount,false);
            }

            callBack.needShowEmptyView(getData() == null || getData().size() <= 0);

            if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                callBack.needShowLoadProgress(false);
                notifyDataSetChanged();
                return;
            }

            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                notifyItemRangeRemoved(range.startIndex, range.length);
            }
            if (deletions.length > 0 && selectedRoom.size() > 0) {
                selectedRoom.clear();
                callBack.needCheckMultiSelect();
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
        holder.setSelectedRoom();
        if (!mInfo.isValid() || mInfo == null) {
            return;
        }
        holder.getRootView().setData(mInfo, avatarHandler, isChatMultiSelectEnable);
        holder.getRootView().setCheck(selectedRoom.contains(holder.realmRoom.id));
        holder.getRootView().setBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    public void setCallBack(OnMainFragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public void setMultiSelect(boolean isChatMultiSelectEnable) {
        this.isChatMultiSelectEnable = isChatMultiSelectEnable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RealmRoom realmRoom;
        private final RoomListCell rootView;
        private Room selectedModel;

        public ViewHolder(View view) {
            super(view);

            rootView = (RoomListCell) itemView;
            itemView.setOnClickListener(v -> callBack.onClick(rootView, realmRoom, getAdapterPosition()));
            itemView.setOnLongClickListener(v -> callBack.onLongClick(rootView, realmRoom, getAdapterPosition()));
        }

        public RoomListCell getRootView() {
            return rootView;
        }

        public void setSelectedRoom() {
            selectedModel = new Room(realmRoom.getId(), realmRoom.getType().name(), realmRoom.getTitle(), "", "");
            if (realmRoom.getType() == GROUP) {
                selectedModel.setGroupRole(realmRoom.getGroupRoom().getRole().toString());
            } else if (realmRoom.getType() == CHANNEL) {
                selectedModel.setChannelRole(realmRoom.getChannelRoom().getRole().toString());
            }
        }
    }

    public interface OnMainFragmentCallBack {
        void onClick(RoomListCell roomListCell, RealmRoom realmRoom, int adapterPosition);

        boolean onLongClick(RoomListCell roomListCell, RealmRoom realmRoom, int position);

        void needShowLoadProgress(boolean show);

        void needShowEmptyView(boolean show);

        void needCheckMultiSelect();
    }
}
