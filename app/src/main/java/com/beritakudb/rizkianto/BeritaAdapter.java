package com.beritakudb.rizkianto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder> {
    private Context context;
    private ArrayList<Berita> dataBerita;
    private SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public BeritaAdapter(Context context, ArrayList<Berita> dataBerita) {
        this.context = context;
        this.dataBerita = dataBerita;
    }

    @NonNull
    @Override
    public BeritaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_berita, parent, false);
        return new BeritaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaViewHolder holder, int position) {
        Berita tempBerita = dataBerita.get(position);
        holder.idBerita = tempBerita.getIdBerita();
        holder.tvJudul.setText(tempBerita.getJudul());
        holder.tvHeadline.setText(tempBerita.getIsiBerita());
        holder.tanggal = sdFormat.format(tempBerita.getTanggal());
        holder.gambar = tempBerita.getGambar();
        holder.caption = tempBerita.getCaption();
        holder.penulis = tempBerita.getPenulis();
        holder.link = tempBerita.getLink();
        try {
            File file = new File(holder.gambar);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            holder.imgBerita.setImageBitmap(bitmap);
            holder.imgBerita.setContentDescription(holder.gambar);
        }catch (FileNotFoundException er){
            er.printStackTrace();
            Toast.makeText(context, "Gagal Mengambil Gambar dari Media Penyimpanan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return dataBerita.size();
    }

    public class BeritaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView imgBerita;
        private TextView tvJudul, tvHeadline;
        private int idBerita;
        private String tanggal, gambar, caption, penulis, link;

        public BeritaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBerita = itemView.findViewById(R.id.iv_berita);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvHeadline = itemView.findViewById(R.id.tv_headline);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent bukaBerita = new Intent(context, TampilActivity.class);
            bukaBerita.putExtra("ID", idBerita);
            bukaBerita.putExtra("JUDUL", tvJudul.getText());
            bukaBerita.putExtra("TANGGAL", tanggal);
            bukaBerita.putExtra("GAMBAR", gambar);
            bukaBerita.putExtra("CAPTION", caption);
            bukaBerita.putExtra("PENULIS", penulis);
            bukaBerita.putExtra("ISI_BERITA", tvHeadline.getText());
            bukaBerita.putExtra("LINK", link);
            context.startActivity(bukaBerita);
        }

        @Override
        public boolean onLongClick(View v) {
            Intent bukaInput = new Intent(context, InputActivity.class);
            bukaInput.putExtra("OPERASI", "update");
            bukaInput.putExtra("ID", idBerita);
            bukaInput.putExtra("JUDUL", tvJudul.getText());
            bukaInput.putExtra("TANGGAL", tanggal);
            bukaInput.putExtra("GAMBAR", gambar);
            bukaInput.putExtra("CAPTION", caption);
            bukaInput.putExtra("PENULIS", penulis);
            bukaInput.putExtra("ISI_BERITA", tvHeadline.getText());
            bukaInput.putExtra("LINK", link);
            context.startActivity(bukaInput);

            return true;
        }
    }
}
