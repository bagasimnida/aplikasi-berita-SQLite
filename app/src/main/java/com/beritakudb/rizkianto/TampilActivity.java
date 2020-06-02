package com.beritakudb.rizkianto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

public class TampilActivity extends AppCompatActivity {

    private ImageView imgBerita;
    private TextView tvJudul, tvTanggal, tvCaption, tvPenulis, tvIsiBerita;
    private String linkBerita;
    private SimpleDateFormat sdFormar = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil);

        imgBerita = findViewById(R.id.iv_berita);
        tvJudul = findViewById(R.id.tv_judul);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvCaption = findViewById(R.id.tv_caption);
        tvPenulis = findViewById(R.id.tv_penulis);
        tvIsiBerita = findViewById(R.id.tv_isi_berita);

        Intent terimaData = getIntent();
        tvJudul.setText(terimaData.getStringExtra("JUDUL"));
        tvTanggal.setText(terimaData.getStringExtra("TANGGAL"));
        tvCaption.setText(terimaData.getStringExtra("CAPTION"));
        tvPenulis.setText(terimaData.getStringExtra("PENULIS"));
        tvIsiBerita.setText(terimaData.getStringExtra("ISI_BERITA"));
        String imgLocation = terimaData.getStringExtra("GAMBAR");

        try {
            File file = new File(imgLocation);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imgBerita.setImageBitmap(bitmap);
            imgBerita.setContentDescription(imgLocation);
        }catch (FileNotFoundException er){
            er.printStackTrace();
            Toast.makeText(this, "Gagal Mengambil Gambar dari Media Penyimpanan", Toast.LENGTH_SHORT).show();
        }

        linkBerita = terimaData.getStringExtra("LINK");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tampil_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_bagikan){
            Intent bagikanBerita = new Intent(Intent.ACTION_SEND);
            bagikanBerita.putExtra(Intent.EXTRA_SUBJECT, tvJudul.getText().toString());
            bagikanBerita.putExtra(Intent.EXTRA_TEXT, linkBerita);
            bagikanBerita.setType("text/plain");
            startActivity(Intent.createChooser(bagikanBerita, "Bagikan Berita"));
        }
        return super.onOptionsItemSelected(item);
    }
}
