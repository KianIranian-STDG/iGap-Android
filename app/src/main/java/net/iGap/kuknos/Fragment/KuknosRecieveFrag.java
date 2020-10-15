package net.iGap.kuknos.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosRecieveBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosRecieveVM;

public class KuknosRecieveFrag extends BaseFragment {

    private FragmentKuknosRecieveBinding binding;
    private KuknosRecieveVM kuknosRecieveVM;
    private final static int QRcodeWidth = 500;

    public static KuknosRecieveFrag newInstance() {
        return new KuknosRecieveFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosRecieveVM = ViewModelProviders.of(this).get(KuknosRecieveVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_recieve, container, false);
        binding.setViewmodel(kuknosRecieveVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosRcToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        Button copyBtn = binding.fragKuknosRcCopy;
        copyBtn.setOnClickListener(v -> copyWalletID());

        loadImage loadImage = new loadImage();
        loadImage.execute();

    }

    private void loadQrCode(Bitmap b) {
        Glide.with(getContext())
                .load(b)
                .into(binding.fragKuknosRcQrCode);
    }

    private void copyWalletID() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("My Kuknos wallet address", getString(R.string.kuknos_recieve_walletCopy) + kuknosRecieveVM.getClientKey().get());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), R.string.kuknos_recieve_copyToast, Toast.LENGTH_SHORT).show();
    }

    private Bitmap TextToImageEncode(String Value) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {
            Toast.makeText(getContext(), getString(R.string.kuknos_recieve_error), Toast.LENGTH_SHORT).show();
            return null;
        } catch (WriterException e) {
            Toast.makeText(getContext(), getString(R.string.kuknos_recieve_error), Toast.LENGTH_SHORT).show();
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class loadImage extends AsyncTask<String, Boolean, Bitmap> {

        @Override
        protected void onPreExecute() {
            onProgressUpdate(true);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                bitmap = TextToImageEncode(kuknosRecieveVM.getClientKey().get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            if (s == null)
                return;
            loadQrCode(s);
            onProgressUpdate(false);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0])
                binding.fragKuknosPProgressV.setVisibility(View.VISIBLE);
            else
                binding.fragKuknosPProgressV.setVisibility(View.GONE);
            super.onProgressUpdate(values);
        }
    }

}
