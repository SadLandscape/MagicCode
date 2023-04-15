package com.example.magic_code.ui.scan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.example.magic_code.R;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class ScanPage extends Fragment implements DecoratedBarcodeView.TorchListener {

    private static final String TAG = ScanPage.class.getSimpleName();

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    public static ScanPage newInstance() {
        return new ScanPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);
        beepManager = new BeepManager(getActivity());

        barcodeView.setTorchListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barcodeView.decodeSingle(callback);
        barcodeView.setStatusText("Please scan QR");
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
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
            Log.d(TAG, "QR code: " + result.getText());
            Toast.makeText(getActivity(), "QR CODE: "+result.getText(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onTorchOn() {
        // Do something when the flashlight is turned on
    }

    @Override
    public void onTorchOff() {
        // Do something when the flashlight is turned off
    }
}