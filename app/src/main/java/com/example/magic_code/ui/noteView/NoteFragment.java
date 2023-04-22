package com.example.magic_code.ui.noteView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Note;
import com.example.magic_code.utils.HtmlUtils;
import com.example.magic_code.utils.TextFormatter;

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
    private WebView webView;
    private String noteText;
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
                // TODO Handle share option click
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
                    webView.loadDataWithBaseURL(null, TextFormatter.convertToRawTextWithHtml(noteText,isEditing), "text/html", "UTF-8", null);
                } else {
                    item.setIcon(R.drawable.baseline_edit_24);
                    webView.evaluateJavascript("(function() { return document.body.innerHTML; })();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String html) {
                            html = html.replaceAll("\\\\\\\\", "\\\\");
                            Log.d("DECODER", "onReceiveValue: "+html);
                            String body = TextFormatter.convertToRawText(HtmlUtils.decodeHtml(html));
                            Log.d("HTML CONTENT", "onReceiveValue: " + body);
                            noteText = body;
                            Log.d("HTML CONTENT", "onReceiveValue(1): "+ TextFormatter.formatTextWithHtml(body,isEditing));
                            webView.loadDataWithBaseURL(null, TextFormatter.formatTextWithHtml(body,isEditing), "text/html", "UTF-8", null);
                        }
                    });
                }
                webView.loadUrl("javascript:document.body.contentEditable='" + isEditing + "';document.designMode='" + (isEditing ? "on" : "off") + "';void 0;");
                webView.requestFocus();
                Log.d("HTML", "onOptionsItemSelected: " + isEditing);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        note_id = getArguments().getString("note_id");
        View root_view = inflater.inflate(R.layout.noteview, container, false);
        webView = (WebView) root_view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        Note note = API.Notes.getNote(note_id);
        if (noteText == null) {
            noteText = note.getText();
        }
        webView.loadDataWithBaseURL(null, TextFormatter.formatTextWithHtml(noteText,isEditing),"text/html","UTF-8",null);
        return root_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
