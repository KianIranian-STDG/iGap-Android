/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterExplorer;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.interfaces.IPickFile;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.FileUtils;
import net.iGap.module.structs.StructExplorerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentExplorer extends BaseFragment {

    String nextnode;
    int rootcount = 0;
    ArrayList<StructExplorerItem> item;
    ArrayList<String> node;          //path of the hierychical directory
    ArrayList<Integer> mscroll;
    StructExplorerItem x;
    RecyclerView recyclerView;
    String mode = "normal";
    boolean first = true;
    private boolean finish = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private IPickFile pickFile = null;
    List<String> storageList = null;

    private HelperToolbar mHelperToolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_explorer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mode = getArguments().getString("Mode");

            pickFile = (IPickFile) getArguments().getSerializable("Listener");
        }
        if (mode == null) {
            mode = "normal";
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle("root")
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        G.fragmentActivity.onBackPressed();
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.ae_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        recyclerView = view.findViewById(R.id.ae_recycler_view_explorer);
        recyclerView.setItemViewCacheSize(100);
        mLayoutManager = new LinearLayoutManager(G.fragmentActivity);
        recyclerView.setLayoutManager(mLayoutManager);

        item = new ArrayList<StructExplorerItem>();
        node = new ArrayList<String>();
        mscroll = new ArrayList<Integer>();

        firstfill();
    }

    @Override
    public void onResume() {
        super.onResume();

        G.onBackPressedExplorer = new IOnBackPressed() {
            @Override
            public boolean onBack() {
                return onBackPressedExplorer();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        G.onBackPressedExplorer = null;
    }

    private void onItemClickInernal(int position) {

        if (node.size() == rootcount) {
            nextnode = node.get(position);
        } else {
            nextnode = node.get(node.size() - 1) + "/" + item.get(position).name;
        }

        fill(nextnode, position);
    }

    private boolean onBackPressedExplorer() {

        if (finish) {
            return false;
        }

        boolean stopSuperPress = true;

        int size = node.size();
        if (size == rootcount) {
            return false;
        } else if (size == rootcount + 1) {
            firstfill();
        } else {
            node.remove(node.size() - 1);
            fill(node.get(node.size() - 1), 0);
            node.remove(node.size() - 1);
            mscroll.remove(mscroll.size() - 1);
            recyclerView.scrollToPosition(mscroll.remove(mscroll.size() - 1));
        }

        return stopSuperPress;
    }

    void firstfill() {

        item.clear();
        node.clear();
        rootcount = 0;
        mscroll.clear();
        mscroll.add(0);

        if (storageList == null) {
            storageList = FileUtils.getSdCardPathList();
        }

        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) {
            x = new StructExplorerItem();
            x.name = "Device";
            x.image = R.mipmap.j_device;
            x.path = Environment.getExternalStorageDirectory().getAbsolutePath();
            item.add(x);
            node.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            rootcount++;
        }

        for (String sdPath : storageList) {
            if (new File(sdPath).exists()) {
                x = new StructExplorerItem();
                x.name = "SdCard";
                x.image = R.mipmap.j_sdcard;
                x.path = sdPath + "/";
                item.add(x);
                node.add(sdPath + "/");
                rootcount++;
            }
        }

        if (!G.DIR_SDCARD_EXTERNAL.equals("")) {
            if (new File(G.DIR_SDCARD_EXTERNAL).exists()) {
                x = new StructExplorerItem();
                x.name = "iGap SdCard";
                x.image = R.mipmap.actionbar_icon_myfiles;
                x.path = G.DIR_SDCARD_EXTERNAL + "/";
                item.add(x);
                node.add(G.DIR_SDCARD_EXTERNAL + "/");
                rootcount++;
            }
        }

        if (new File(G.DIR_APP).exists()) {
            x = new StructExplorerItem();
            x.name = "iGap";
            x.image = R.mipmap.actionbar_icon_myfiles;
            x.path = G.DIR_APP + "/";
            item.add(x);
            node.add(G.DIR_APP + "/");
            rootcount++;
        }

        mHelperToolbar.setDefaultTitle("root");
        recyclerView.setAdapter(new AdapterExplorer(item, new AdapterExplorer.OnItemClickListenerExplorer() {

            @Override
            public void onItemClick(View view, int position) {

                onItemClickInernal(position);
            }
        }));

        if (first) {

            if (mode.equals("documnet")) {

                File file = new File(Environment.getExternalStorageDirectory().toString(), "Documents");

                if (!file.exists()) {
                    file = new File(Environment.getExternalStorageDirectory().toString(), "My Documents");
                }

                if (file.exists()) {
                    onItemClickInernal(0);

                    int po = getItemId(file.getPath());

                    if (po >= 0) onItemClickInernal(po);
                }
            }
            first = false;
        }
    }

    private int getItemId(String path) {

        int setlectedItem = -1;

        for (int i = 0; i < item.size(); i++) {
            if (item.get(i).path.equals(path)) {
                setlectedItem = i;
                break;
            }
        }

        return setlectedItem;
    }

    void fill(String nextnod, int position) {

        try {
            File fileDir = new File(nextnod);

            if (fileDir.isDirectory()) {
                mscroll.add(position);

                String[] tmpname = fileDir.list();

                if (tmpname == null) {
                    return;
                }

                item.clear();
                for (int i = 0; i < tmpname.length; i++) {
                    if (tmpname[i].startsWith(".")) {
                        continue;
                    } else {
                        File tmp = new File(fileDir.getAbsolutePath() + "/" + tmpname[i]);
                        if (tmp.canRead()) {

                            if (canAddFile(tmpname[i]) || tmp.isDirectory()) {

                                x = new StructExplorerItem();
                                x.name = tmpname[i];

                                if (tmp.isDirectory()) {
                                    x.image = R.mipmap.actionbar_icon_myfiles;
                                } else {
                                    x.image = HelperMimeType.getMimeResource(tmpname[i]);
                                }
                                x.path = tmp.getAbsolutePath();
                                item.add(x);
                            }
                        }
                    }
                }

                recyclerView.setAdapter(new AdapterExplorer(item, new AdapterExplorer.OnItemClickListenerExplorer() {

                    @Override
                    public void onItemClick(View view, int position) {

                        onItemClickInernal(position);
                    }
                }));
                mHelperToolbar.setDefaultTitle(nextnod);

                node.add(nextnod);
            } else if (fileDir.isFile()) {
                //Intent data = new Intent();
                //data.setData(Uri.parse(fileDir.getAbsolutePath()));

                // G.fragmentActivity.setResult(Activity.RESULT_OK, data);

                if (pickFile != null) {

                    ArrayList<String> list = new ArrayList<>();
                    list.add(fileDir.getAbsolutePath());
                    pickFile.onPick(list);
                }

                finish();
            }
        } catch (Exception e) {
            Toast.makeText(G.fragmentActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean canAddFile(String mime) {
        boolean result = false;

        if (mode.equals("documnet")) {

            if (mime == null) return false;

            if (mime.length() < 1) return false;

            mime = mime.toLowerCase();

            if (mime.endsWith(".txt")
                    || mime.endsWith(".pdf")
                    || mime.endsWith(".doc")
                    || mime.endsWith(".xls")
                    || mime.endsWith(".snb")
                    || mime.endsWith(".ppt")
                    || mime.endsWith(".html")
                    || mime.endsWith(".htm")
                    || mime.endsWith(".docx")
                    || mime.endsWith(".xml")) {
                result = true;
            }
        } else {
            return true;
        }

        return result;
    }

    private void finish() {
        finish = true;
        G.fragmentActivity.onBackPressed();
    }
}
