package com.beritakudb.rizkianto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class InputActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editJudul, editTanggal, editCaption, editPenulis, editIsiBerita, editLink;
    private ImageView ivBerita;
    private DatabaseHandler dbHandler;
    private SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private boolean updateData = false;
    private int idBerita = 0;
    private Button btnSimpan, btnPilihTanggal;
    private String tanggalBerita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        editJudul = findViewById(R.id.edit_judul);
        editTanggal = findViewById(R.id.edit_tanggal);
        editCaption = findViewById(R.id.edit_caption);
        editPenulis = findViewById(R.id.edit_penulis);
        editIsiBerita = findViewById(R.id.edit_isi_berita);
        editLink = findViewById(R.id.edit_link);
        ivBerita = findViewById(R.id.iv_berita);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnPilihTanggal = findViewById(R.id.btn_pilih_tanggal);

        dbHandler = new DatabaseHandler(this);

        Intent terimaIntent = getIntent();
        Bundle data = terimaIntent.getExtras();
        if (data.getString("OPERASI").equals("insert")){
            updateData = false;
        }else{
            updateData = true;
            idBerita = data.getInt("ID");
            editJudul.setText(data.getString("JUDUL"));
            editTanggal.setText(data.getString("TANGGAL"));
            editCaption.setText(data.getString("CAPTION"));
            editPenulis.setText(data.getString("PENULIS"));
            editIsiBerita.setText(data.getString("ISI_BERITA"));
            editLink.setText(data.getString("LINK"));
            loadImageFromInternalStorage(data.getString("GAMBAR"));
        }

        ivBerita.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        btnPilihTanggal.setOnClickListener(this);
    }

    private void pickImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4,3)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                try{
                    Uri imageUri = result.getUri();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String location = saveImageToInternalStorage(selectedImage, getApplicationContext());
                    loadImageFromInternalStorage(location);
                }catch(FileNotFoundException er){
                    er.printStackTrace();
                    Toast.makeText(this, "Ada Kesalahan dalam Pemilihan Gambar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static String saveImageToInternalStorage(Bitmap bitmap, Context ctx){
        ContextWrapper ctxWrapper = new ContextWrapper(ctx);
        File file = ctxWrapper.getDir("images", MODE_PRIVATE);
        String uniqueID = UUID.randomUUID().toString();
        file = new File(file, "berita-" + uniqueID + ".jpg");
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        }catch(IOException er){
            er.printStackTrace();
        }

        Uri savedImage = Uri.parse(file.getAbsolutePath());
        return savedImage.toString();
    }

    private void loadImageFromInternalStorage(String imageLocation){
        try {
            File file = new File(imageLocation);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivBerita.setImageBitmap(bitmap);
            ivBerita.setContentDescription(imageLocation);
        }catch (FileNotFoundException er){
            er.printStackTrace();
            Toast.makeText(this, "Gagal Mengambil Gambar dari Media Penyimpanan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_menu_hapus);
        if(updateData==true){
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        }else{
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.item_menu_hapus){
            hapusData();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void simpanData(){
        String judul, gambar, caption, penulis, isiBerita, link;
        Date tanggal = new Date();
        judul = editJudul.getText().toString();
        gambar = ivBerita.getContentDescription().toString();
        caption = editCaption.getText().toString();
        penulis = editPenulis.getText().toString();
        isiBerita = editIsiBerita.getText().toString();
        link = editLink.getText().toString();

        try {
            tanggal = sdFormat.parse(editTanggal.getText().toString());
        }catch (ParseException er){
            er.printStackTrace();
        }

        Berita tempBerita = new Berita(
                idBerita, judul, tanggal, gambar, caption, penulis, isiBerita, link
        );

        if(updateData==true){
            dbHandler.editBerita(tempBerita);
            Toast.makeText(this, "Data Berita Telah Diperbaharui", Toast.LENGTH_SHORT).show();
        }else{
            dbHandler.tambahBerita(tempBerita);
            Toast.makeText(this, "Data Berita Telah Ditambahkan", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void hapusData(){
        dbHandler.hapusBerita(idBerita);
        Toast.makeText(this, "Data Berita Telah Dihapus", Toast.LENGTH_SHORT).show();
    }

    private void pilihTanggal(){
        //Mengambil Tanggal Aktual
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tanggalBerita = dayOfMonth + "/" + month + "/" + year;

                //Memilih Waktu (Jam)
                pilihWaktu();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void pilihWaktu(){
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog pickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tanggalBerita = tanggalBerita + " " + hourOfDay + ":" + minute;
                editTanggal.setText(tanggalBerita);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        pickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();

        if(idView==R.id.btn_simpan){
            simpanData();
        }else if(idView==R.id.iv_berita){
            pickImage();
        }else if(idView==R.id.btn_pilih_tanggal ){
            pilihTanggal();
        }
    }
}
