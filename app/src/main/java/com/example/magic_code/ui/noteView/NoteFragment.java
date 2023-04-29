package com.example.magic_code.ui.noteView;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Note;
import com.example.magic_code.utils.QrCodeUtils;
import com.example.magic_code.utils.TextFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.OutputStream;

public class NoteFragment extends Fragment {

    private String note_id;

    public static NoteFragment newInstance(String note_id) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString("note_id", note_id);
        fragment.setArguments(args);
        Log.d("MAGIC CODE", "newInstance: "+fragment.getArguments());
        return fragment;
    }

    private NoteViewModel viewModel;
    private EditText noteDescription;
    private String noteText;
    private Note note;
    private InputMethodManager imm;
    private HtmlRenderer renderer;
    private Parser parser;

    private Boolean isEditing = false;
    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outputStream;
        try {
            outputStream = getActivity().getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Toast.makeText(getContext(), "Image successfully saved to gallery!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to save image to gallery", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.notemenu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareOption:
                Bitmap qrBitmap = QrCodeUtils.generateQRCodeBitmap("my_qr_code_data",500);
                Dialog qrDialog = new Dialog(getContext());
                qrDialog.setContentView(R.layout.dialog_qr_code);
                ImageView qrImageView = qrDialog.findViewById(R.id.image_view_qr_code);
                qrImageView.setImageBitmap(qrBitmap);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(qrDialog.getWindow().getAttributes());
                lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
                lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
                qrDialog.getWindow().setAttributes(lp);
                qrDialog.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        qrDialog.dismiss();
                    }
                });
                qrDialog.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 121);
                        } else {
                            saveImageToGallery(qrBitmap);
                        }
                    }
                });
                qrDialog.show();
                return true;
            case R.id.manageOption:
                // TODO Handle manage option click
                return true;
            case R.id.deleteOption:
                // TODO Handle delete option click
                return true;
            case R.id.editOption:
                isEditing = !isEditing;
                if (isEditing) {
                    item.setIcon(R.drawable.baseline_save_24);
                    noteDescription.setText(noteText);
                    noteDescription.setFocusable(true);
                    noteDescription.setFocusableInTouchMode(true);
                    noteDescription.setClickable(true);
                    imm.showSoftInput(noteDescription,0);
                } else {
                    item.setIcon(R.drawable.baseline_edit_24);
                    noteText = noteDescription.getText().toString();
                    noteDescription.setText(Html.fromHtml(renderer.render(parser.parse(noteText))));
                    noteDescription.setFocusable(false);
                    noteDescription.setFocusableInTouchMode(false);
                    noteDescription.setClickable(false);
                    imm.hideSoftInputFromWindow(noteDescription.getWindowToken(),0);
                }
                    return true;
            case android.R.id.home:
                Navigation.findNavController(requireView()).navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        imm = ContextCompat.getSystemService(requireContext(), InputMethodManager.class);
        renderer = HtmlRenderer.builder().build();
        parser = Parser.builder().build();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        note_id = getArguments().getString("note_id");
        View root_view = inflater.inflate(R.layout.noteview, container, false);
        noteDescription = (EditText) root_view.findViewById(R.id.note_text);
        note = API.Notes.getNote(note_id);
        if (noteText == null) {
            noteText = note.getText();
        }
        noteDescription.setText(Html.fromHtml(renderer.render(parser.parse(noteText))));
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.notes);
        menuItem.setChecked(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
