package com.example.magic_code.ui.boardView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import android.app.Dialog;
import android.content.Context;
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
import com.example.magic_code.models.ShareToken;
import com.example.magic_code.ui.createNote.createNote;
import com.example.magic_code.ui.manageBoard.manageBoard;
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
    Board board;
    SwipeRefreshLayout refreshLayout;
    String board_id;
    private FragmentActivity activity;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!board.canEdit()){
            return;
        }
        inflater.inflate(R.menu.board_menu, menu);
        View menuItemView = menu.findItem(R.id.create_note_button_menu).getActionView();
        if (board.canEdit() && !board.canDelete()){
            menu.findItem(R.id.manageBoardOption).setVisible(false);
        }
        menuItemView.setOnClickListener(v -> new Thread(() -> {
            ArrayList<Category> categories = API.Categories.getCategories(board_id,authToken,activity);
            activity.runOnUiThread(() -> {
                createNote fragment = createNote.newInstance(categories);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_board_view_to_create_notes,fragment.getArguments());
            });
        }).start());
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
        if (item.getItemId() == R.id.shareOption){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_qr_generate);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            SwitchCompat readOnly = dialog.findViewById(R.id.switch_read_only);
            dialog.findViewById(R.id.button_generate).setOnClickListener(view -> {
                view.setEnabled(false);
                new Thread(() -> {
                    ShareToken newToken = API.Boards.generateToken(board_id,authToken,readOnly.isChecked(),activity);
                    if (newToken == null){
                        return;
                    }
                    activity.runOnUiThread(() -> {
                        view.setEnabled(true);
                        dialog.dismiss();
                        int size = 500;
                        BitMatrix bitMatrix = null;
                        try {
                            bitMatrix = new MultiFormatWriter().encode(newToken.getId(), BarcodeFormat.QR_CODE, size, size);
                        } catch (WriterException e) {
                            Toast.makeText(activity, "Unable to share: "+e, Toast.LENGTH_SHORT).show();
                        }
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        final Dialog dialog1 = new Dialog(activity);
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog1.setContentView(R.layout.dialog_qr_code);
                        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                        lp1.copyFrom(dialog1.getWindow().getAttributes());
                        lp1.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
                        lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        ImageView imageView = dialog1.findViewById(R.id.image_view_qr_code);
                        dialog1.getWindow().setAttributes(lp1);
                        dialog1.findViewById(R.id.button_close).setOnClickListener(v -> dialog1.dismiss());
                        dialog1.findViewById(R.id.button_save).setOnClickListener(view1 -> {
                            MediaStoreSupport.saveImageToGallery(bitmap,"note",activity);
                            dialog1.dismiss();
                        });
                        imageView.setImageBitmap(bitmap);
                        dialog1.show();
                    });
                }).start();
            });
            dialog.show();
            return true;
        }
        if (item.getItemId() == R.id.manageBoardOption){
            manageBoard fragment = manageBoard.newInstance(board_id);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_board_view_to_manageBoard,fragment.getArguments());
            return true;
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        board = (Board) getArguments().getSerializable("board");
        board_id = board.getId();
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        View root_view = inflater.inflate(R.layout.fragment_board_view, container, false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading board...");
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", activity.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        dialog.show();
        RecyclerView recyclerView = root_view.findViewById(R.id.category_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        new Thread(()->{
            board = API.Boards.getBoard(board_id,authToken,activity);
            activity.runOnUiThread(()->{
                ((MainActivity) activity).setActionBarTitle(board.getTitle());
                new Thread(() -> {
                    List<Category> categories = API.Categories.getCategories(board_id,authToken,activity);
                    activity.runOnUiThread(() -> {
                        adapter = new CategoryAdapter(categories,board,activity,authToken);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    });
                }).start();
                refreshLayout = root_view.findViewById(R.id.category_refresh);
                refreshLayout.setOnRefreshListener(() -> new Thread(() -> {
                    List<Category> categories = API.Categories.getCategories(board_id,authToken,activity);
                    activity.runOnUiThread(() -> {
                        adapter.updateData(categories);
                        refreshLayout.setRefreshing(false);
                    });
                }).start());
                if (board.canEdit()) {
                    root_view.findViewById(R.id.create_category_button).setVisibility(View.VISIBLE);
                }
                root_view.findViewById(R.id.create_category_button).setOnClickListener(view -> {
                    Dialog dialog = new Dialog(activity);
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
                    confirm_button.setOnClickListener(view12 -> new Thread(() -> {
                        API.Categories.createCategory(board_id,((EditText)dialog.findViewById(R.id.new_category_dialog_category_title)).getText().toString(),authToken,activity);
                        List<Category> categories = API.Categories.getCategories(board_id,authToken,activity);
                        activity.runOnUiThread(() -> {
                            adapter.updateData(categories);
                            dialog.dismiss();
                        });
                    }).start());
                    dialog.findViewById(R.id.new_category_dialog_cancel_button).setOnClickListener(view1 -> dialog.dismiss());
                    dialog.show();
                });
            });
        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardViewViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }
}