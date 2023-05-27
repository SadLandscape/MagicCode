package com.example.magic_code.ui.scan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.magic_code.api.API;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.example.magic_code.R;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.List;

public class ScanPage extends Fragment implements DecoratedBarcodeView.TorchListener {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String authToken;
    SharedPreferences sharedPreferences;
    private FragmentActivity activity;

    public static ScanPage newInstance() {
        return new ScanPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View view = inflater.inflate(R.layout.scan, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);
        beepManager = new BeepManager(activity);

        barcodeView.setTorchListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barcodeView.decodeSingle(callback);
        barcodeView.setStatusText("Please scan QR");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 66){
            if (data == null){
                return;
            }
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                // Decode the image
                MultiFormatReader reader = new MultiFormatReader();
                try {
                    Result result = reader.decode(binaryBitmap);
                    String contents = result.getText();
                    Toast.makeText(activity, "Checking QR...", Toast.LENGTH_SHORT).show();
                    new Thread(() -> API.Boards.joinBoard(contents,authToken,activity)).start();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.openQr){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 66);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.scan_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            beepManager.playBeepSoundAndVibrate();
            Toast.makeText(activity, "Checking QR...", Toast.LENGTH_SHORT).show();
            new Thread(() -> API.Boards.joinBoard(result.getText(),authToken,activity)).start();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onTorchOn() {
    }

    @Override
    public void onTorchOff() {
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }
}