package com.example.magic_code.ui.noteView;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Note;
import com.example.magic_code.utils.TextFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.notemenu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareOption:
                int size = 500;
                BitMatrix bitMatrix = null;
                try {
                    bitMatrix = new MultiFormatWriter().encode(note.getShareToken(), BarcodeFormat.QR_CODE, size, size);
                } catch (WriterException e) {
                    Toast.makeText(getActivity(), "Unable to share: "+e, Toast.LENGTH_SHORT).show();
                }
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.qr_code_dialog);
                ImageView imageView = dialog.findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
                dialog.show();
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
