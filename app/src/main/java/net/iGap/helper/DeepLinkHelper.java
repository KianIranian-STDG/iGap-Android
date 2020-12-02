package net.iGap.helper;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.util.Log;
import android.view.Window;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.network.IG_RPC;

import java.util.ArrayList;

/**
 * This class handles deep link progress to open target page.
 */
public class DeepLinkHelper {
    private static DiscoveryFragment.CrawlerStruct struct;
    private static final ArrayList<Integer> pages = new ArrayList<>();
    private static ArrayList<DiscoveryItem> discoveryArrayList;

    public static void HandleDiscoveryDeepLink(BottomNavigationFragment fragment, String uri) {

        pages.add(Integer.valueOf(uri));

        struct = new DiscoveryFragment.CrawlerStruct(0, pages);
        if (fragment != null) {
            changeFragment(fragment);
        }
    }

    private static void changeFragment(BottomNavigationFragment fragment) {

        ArrayList<Integer> deepLinkPages = (ArrayList<Integer>) struct.getPages();

        IG_RPC.Client_Get_Discovery req = new IG_RPC.Client_Get_Discovery();
        req.itemId = deepLinkPages.get(deepLinkPages.size() - 1);

        Rect displayRectangle = new Rect();
        Window window = fragment.getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext())
                .setMessage(R.string.please_wait);

        AlertDialog dialog = builder.create();
        dialog.show();

        fragment.getRequestManager().sendRequest(req, (response, error) -> {
            G.runOnUiThread(() -> {
                if (response != null) {
                    dialog.dismiss();
                    IG_RPC.Res_Client_Get_Discovery res = (IG_RPC.Res_Client_Get_Discovery) response;
                    discoveryArrayList = res.items;

                    try {
                        for (int i = 0; i < discoveryArrayList.size(); i++) {
                            ArrayList<DiscoveryItemField> discoveryFields = discoveryArrayList.get(i).discoveryFields;
                            for (int j = 0; j < discoveryFields.size(); j++) {
                                if (discoveryFields.get(j).id == struct.getPages().get(deepLinkPages.size() - 1)) {
                                    BaseViewHolder.handleDiscoveryFieldsClickStatic(discoveryFields.get(j), fragment.getActivity(), false);
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        HelperError.showSnackMessage(fragment.getResources().getString(R.string.link_not_valid), false);
                    }

                } else {
                    dialog.dismiss();
                    IG_RPC.Error err = (IG_RPC.Error) error;
                    Log.e("TAG", "getDiscoveryItems: " + err.major + " - " + err.minor);
                }
            });
        });
    }
}
