package com.example.magic_code.ui.noteView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.noteSettings.NoteSettings;
import com.example.magic_code.utils.MediaStoreSupport;
import com.example.magic_code.utils.QrCodeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class NoteFragment extends Fragment {

    private String note_id;
    private FragmentActivity activity;

    public static NoteFragment newInstance(String note_id, Boolean canEdit, Board board) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString("note_id", note_id);
        args.putBoolean("canEdit",canEdit);
        args.putSerializable("board",board);
        fragment.setArguments(args);
        Log.d("MAGIC CODE", "newInstance: "+fragment.getArguments());
        return fragment;
    }

    private NoteViewModel viewModel;
    private EditText noteDescription;
    private String noteText;
    private Note note;
    private InputMethodManager imm;
    private Board board;
    private HtmlRenderer renderer;
    private Parser parser;
    private SharedPreferences sharedPreferences;
    private Boolean canEdit;
    private String authToken;
    private ProgressBar progressBar;

    private Boolean isEditing = false;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!canEdit){return;}
        inflater.inflate(R.menu.notemenu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manageOption:
                NoteSettings fragment = NoteSettings.newInstance(note_id,board);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_detailed_note_view_to_fragment_note_settings,fragment.getArguments());
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
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            API.Notes.setBody(note_id,noteText,authToken,activity);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();
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
        canEdit = getArguments().getBoolean("canEdit");
        board = (Board) getArguments().getSerializable("board");
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", activity.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        imm = ContextCompat.getSystemService(activity, InputMethodManager.class);
        renderer = HtmlRenderer.builder().build();
        parser = Parser.builder().build();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        note_id = getArguments().getString("note_id");
        View root_view = inflater.inflate(R.layout.noteview, container, false);
        noteDescription = (EditText) root_view.findViewById(R.id.note_text);
        noteDescription.setMovementMethod(LinkMovementMethod.getInstance());
        progressBar = root_view.findViewById(R.id.progressBar2);
        new Thread(() -> {
            note = API.Notes.getNote(note_id,authToken,activity);
            activity.runOnUiThread(() -> {
                if (note == null){
                    Navigation.findNavController(requireView()).navigateUp();
                    return;
                }
                ((MainActivity) activity).setActionBarTitle(note.getTitle());
                progressBar.setVisibility(View.GONE);
                if (noteText == null && note!=null) {
                    noteText = note.getText();
                }
                noteDescription.setText(Html.fromHtml(renderer.render(parser.parse(noteText))));
                noteDescription.setFocusable(false);
                noteDescription.setFocusableInTouchMode(false);
                noteDescription.setClickable(false);
            });
        }).start();
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }
}
