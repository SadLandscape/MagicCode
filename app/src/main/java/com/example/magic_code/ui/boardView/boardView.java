package com.example.magic_code.ui.boardView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.CategoryAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.createNote.createNote;
import com.example.magic_code.ui.noteSettings.NoteSettings;
import com.example.magic_code.utils.MediaStoreSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

public class boardView extends Fragment {

    private BoardViewViewModel mViewModel;
    String authToken;
    SharedPreferences sharedPreferences;
    CategoryAdapter adapter;
    Dialog dialog;
    SwipeRefreshLayout refreshLayout;
    String board_id;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.board_menu, menu);
        View menuItemView = menu.findItem(R.id.create_note_button_menu).getActionView();
        menuItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Category> categories = API.Categories.getCategories(board_id,authToken,requireContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createNote fragment = createNote.newInstance(categories);
                                NavController navController = Navigation.findNavController(requireView());
                                navController.navigate(R.id.action_board_view_to_create_notes,fragment.getArguments());
                            }
                        });
                    }
                }).start();
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    public static boardView newInstance(Board board) {
        boardView fragment = new boardView();
        Bundle args = new Bundle();
        args.putSerializable("board", board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Navigation.findNavController(requireView()).navigateUp();
            return true;
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading board...");
        dialog.show();
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        Board board = ((Board)getArguments().getSerializable("board"));
        board_id = board.getId();
        View root_view = inflater.inflate(R.layout.fragment_board_view, container, false);
        ((MainActivity) requireActivity()).setActionBarTitle(board.getTitle());
        RecyclerView recyclerView = root_view.findViewById(R.id.category_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Category> categories = API.Categories.getCategories(board_id,authToken,requireContext());
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CategoryAdapter(categories,getContext(),authToken);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });
            }
        }).start();
        refreshLayout = root_view.findViewById(R.id.category_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Category> categories = API.Categories.getCategories(board_id,authToken,requireContext());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(categories);
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        root_view.findViewById(R.id.create_category_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.new_category_dialog);
                Button confirm_button = dialog.findViewById(R.id.new_category_dialog_create_button);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(lp);
                EditText title_edit =  ((EditText) dialog.findViewById(R.id.new_category_dialog_category_title));
                confirm_button.setEnabled(false);
               title_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (title_edit.getText().toString().length() >= 3 && !TextUtils.isEmpty(title_edit.getText().toString().trim())){
                            confirm_button.setEnabled(true);
                            title_edit.setError(null);
                        }
                        else {
                            confirm_button.setEnabled(false);
                            title_edit.setError("Title must be at least 3 letters long!");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                API.Categories.createCategory(board_id,((EditText)dialog.findViewById(R.id.new_category_dialog_category_title)).getText().toString(),authToken,requireContext());
                                List<Category> categories = API.Categories.getCategories(board_id,authToken,requireContext());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.updateData(categories);
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                dialog.findViewById(R.id.new_category_dialog_cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardViewViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }
}