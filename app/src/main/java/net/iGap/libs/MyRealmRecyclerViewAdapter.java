package net.iGap.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

public abstract class MyRealmRecyclerViewAdapter<T extends RealmModel, S extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<S> {

    protected boolean hasAutoUpdates;
    protected boolean updateOnModification;
    protected OrderedRealmCollectionChangeListener listener;

    @Nullable
    protected OrderedRealmCollection<T> adapterData;

    protected OrderedRealmCollectionChangeListener createListener() {
        return new OrderedRealmCollectionChangeListener() {
            @Override
            public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
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
            }
        };
    }

    public MyRealmRecyclerViewAdapter(@Nullable OrderedRealmCollection<T> data, boolean autoUpdate) {
        this(data, autoUpdate, true);
    }

    public MyRealmRecyclerViewAdapter(@Nullable OrderedRealmCollection<T> data, boolean autoUpdate,
                                    boolean updateOnModification) {
        if (data != null && !data.isManaged())
            throw new IllegalStateException("Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseRecyclerViewAdapter");
        this.adapterData = data;
        this.hasAutoUpdates = autoUpdate;
        this.listener = hasAutoUpdates ? createListener() : null;
        this.updateOnModification = updateOnModification;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
    }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Only indexes >= 0 are allowed. Input was: " + index);
        }

        // To avoid exception, return null if there are some extra positions that the
        // child adapter is adding in getItemCount (e.g: to display footer view in recycler view)
        if(adapterData != null && index >= adapterData.size()) return null;
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    @Nullable
    public OrderedRealmCollection<T> getData() {
        return adapterData;
    }

    @SuppressWarnings("WeakerAccess")
    public void updateData(@Nullable OrderedRealmCollection<T> data) {
        if (hasAutoUpdates) {
            if (isDataValid()) {
                //noinspection ConstantConditions
                removeListener(adapterData);
            }
            if (data != null) {
                addListener(data);
            }
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    protected void addListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.addChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.addChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    protected void removeListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.removeChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.removeChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    protected boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }
}
