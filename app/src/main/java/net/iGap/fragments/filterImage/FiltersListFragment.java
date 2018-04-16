package net.iGap.fragments.filterImage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;


public class FiltersListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {

    private RecyclerView recyclerView;

    ThumbnailsAdapter mAdapter;

    List<ThumbnailItem> thumbnailItemList;

    FiltersListFragmentListener listener;

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        View view = inflater.inflate(R.layout.fragment_filters_list, container, false);
//
////        recyclerView = view.findViewById(R.id.recycler_view);
//
//
//        thumbnailItemList = new ArrayList<>();
//        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
//                getResources().getDisplayMetrics());
//        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
//        recyclerView.setAdapter(mAdapter);
//
//        prepareThumbnail(null);
//
//        return view;
//    }

//    /**
//     * Renders thumbnails in horizontal list
//     * loads default image from Assets if passed param is null
//     *
//     * @param bitmap
//     */
//    public void prepareThumbnail(final Bitmap bitmap) {
//        Runnable r = new Runnable() {
//            public void run() {
//                Bitmap thumbImage;
//
//                if (bitmap == null) {
//                    thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), FragmentFilterImage.IMAGE_NAME, 100, 100);
//                } else {
//                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//                }
//
//                if (thumbImage == null)
//                    return;
//
//                ThumbnailsManager.clearThumbs();
//                thumbnailItemList.clear();
//
//                // add normal bitmap first
//                ThumbnailItem thumbnailItem = new ThumbnailItem();
//                thumbnailItem.image = thumbImage;
//                thumbnailItem.filterName = getString(R.string.filter_normal);
//                ThumbnailsManager.addThumb(thumbnailItem);
//
//                List<Filter> filters = FilterPack.getFilterPack(getActivity());
//
//                for (Filter filter : filters) {
//                    ThumbnailItem tI = new ThumbnailItem();
//                    tI.image = thumbImage;
//                    tI.filter = filter;
//                    tI.filterName = filter.getName();
//                    ThumbnailsManager.addThumb(tI);
//                }
//
//                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        };
//
//        new Thread(r).start();
//    }

    @Override
    public void onFilterSelected(Filter filter) {
        if (listener != null)
            listener.onFilterSelected(filter);
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(Filter filter);
    }
}