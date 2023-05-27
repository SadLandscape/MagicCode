package com.example.magic_code.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.ShareToken;
import com.example.magic_code.utils.MediaStoreSupport;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.TokenViewHolder> {

    private List<ShareToken> tokenList;
    private Context ctx;
    private String authToken;

    public TokenAdapter(List<ShareToken> tokens, Context ctx,String authToken) {
        tokenList = tokens;
        this.ctx = ctx;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.qr_item_view, parent, false);
        return new TokenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenViewHolder holder, int position) {
        ShareToken token = tokenList.get(position);
        holder.qrIdView.setText(token.getId());
        holder.qrUsesView.setText("Uses: "+token.getUses());
        holder.qrPermSwitch.setChecked(!token.getCanWrite());
        holder.qrDeleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Invalidate QR?");
            builder.setMessage("Are you sure you want to invalidate this QR?");
            builder.setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                boolean status = API.Boards.deleteToken(token.getId(),authToken,ctx);
                if (status) {
                    tokenList.remove(token);
                }
                ((Activity) ctx).runOnUiThread(this::notifyDataSetChanged);
            }).start());
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        holder.viewHolder.setOnClickListener(view ->{
            int size = 500;
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = new MultiFormatWriter().encode(token.getId(), BarcodeFormat.QR_CODE, size, size);
            } catch (WriterException e) {
                Toast.makeText(ctx, "Unable to share: "+e, Toast.LENGTH_SHORT).show();
            }
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            final Dialog dialog1 = new Dialog(ctx);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.dialog_qr_code);
            WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
            lp1.copyFrom(dialog1.getWindow().getAttributes());
            lp1.width = (int) (ctx.getResources().getDisplayMetrics().widthPixels * 0.5);
            lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;
            ImageView imageView = dialog1.findViewById(R.id.image_view_qr_code);
            dialog1.getWindow().setAttributes(lp1);
            dialog1.findViewById(R.id.button_close).setOnClickListener(v -> dialog1.dismiss());
            dialog1.findViewById(R.id.button_save).setOnClickListener(view1 -> {
                MediaStoreSupport.saveImageToGallery(bitmap,"note",ctx);
                dialog1.dismiss();
            });
            imageView.setImageBitmap(bitmap);
            dialog1.show();
        });

    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public class TokenViewHolder extends RecyclerView.ViewHolder {
        public TextView qrIdView;
        public TextView qrUsesView;
        public ImageButton qrDeleteButton;
        public SwitchCompat qrPermSwitch;
        public CardView viewHolder;

        public TokenViewHolder(@NonNull View itemView) {
            super(itemView);
            qrIdView = itemView.findViewById(R.id.qr_id_view);
            qrUsesView = itemView.findViewById(R.id.qr_uses_view);
            qrDeleteButton = itemView.findViewById(R.id.remove_qr_button);
            qrPermSwitch = itemView.findViewById(R.id.qrPermSwitch);
            viewHolder = itemView.findViewById(R.id.tokenHolder);
        }
    }
    public void updateData(List<ShareToken> tokens){
        tokenList.clear();
        tokenList.addAll(tokens);
        notifyDataSetChanged();
    }
}
