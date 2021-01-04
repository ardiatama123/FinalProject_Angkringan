package com.kunti.kasir.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.azhar.kasir.R;
import com.kunti.kasir.database.DatabaseHelper;
import com.kunti.kasir.session.SessionManager;

import java.util.Calendar;
import java.util.HashMap;

public class BookMakanan extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinJenis, spinMenu, spinJumlah, spinNote;
    SessionManager session;
    String email;
    int id_book;
    public String sJenis, sMenu, sTanggal, sJumlah, sNote;
    int Jumlah, Note;
    int harga1, harga2;
    int hargatotal1, hargatotal2, hargaTotal;
    private EditText etTanggal;
    private DatePickerDialog dpTanggal;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan_makan);

        dbHelper = new DatabaseHelper(com.kunti.kasir.activity.BookMakanan.this);
        db = dbHelper.getReadableDatabase();

        final String[] jenis = {" ","Makan di Tempat", "Bawa Pulang"};
        final String[] menu = {" ","Paket 1", "Paket 2", "Paket 3", "Paket Lapar", "Paket Jumbo"};
        final String[] jumlah = {" ","1", "2", "3", "4", "5","6","7","8","9","10","> 10 OFFER"};
        final String[] note = {" ","1", "2", "3"};

        spinJenis = findViewById(R.id.Jenis);
        spinMenu = findViewById(R.id.Menu);
        spinJumlah = findViewById(R.id.Jumlah);
        spinNote = findViewById(R.id.Note);

        ArrayAdapter<CharSequence> adapterAsal = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, jenis);
        adapterAsal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinJenis.setAdapter(adapterAsal);

        ArrayAdapter<CharSequence> adapterTujuan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, menu);
        adapterTujuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMenu.setAdapter(adapterTujuan);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, jumlah);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinJumlah.setAdapter(adapterDewasa);

        ArrayAdapter<CharSequence> adapterAnak = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, note);
        adapterAnak.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNote.setAdapter(adapterAnak);

        spinJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sJenis = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sMenu = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinJumlah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sJumlah = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sNote = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnBook = findViewById(R.id.book);

        etTanggal = findViewById(R.id.tanggal_berangkat);
        etTanggal.setInputType(InputType.TYPE_NULL);
        etTanggal.requestFocus();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        setDateTimeField();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perhitunganHarga();
                if (sJenis != null && sMenu != null && sTanggal != null && sJumlah != null) {
                    if ((sJenis.equalsIgnoreCase("1") && sMenu.equalsIgnoreCase("1"))
                            || (sJenis.equalsIgnoreCase("2") && sMenu.equalsIgnoreCase("2"))
                            || (sJenis.equalsIgnoreCase("3") && sMenu.equalsIgnoreCase("3"))
                            || (sJenis.equalsIgnoreCase("4") && sMenu.equalsIgnoreCase("4"))
                            || (sJenis.equalsIgnoreCase("5") && sMenu.equalsIgnoreCase("5"))) {
                        Toast.makeText(com.kunti.kasir.activity.BookMakanan.this, "OOPS !", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(com.kunti.kasir.activity.BookMakanan.this)
                                .setTitle("Pesan Sekarang?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal, dewasa, anak) VALUES ('" +
                                                    sJenis + "','" +
                                                    sMenu + "','" +
                                                    sTanggal + "','" +
                                                    sJumlah + "','" +
                                                    sNote + "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_BOOK ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_HARGA (username, id_book, harga_dewasa, harga_anak, harga_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    hargatotal1 + "','" +
                                                    hargatotal2 + "','" +
                                                    hargaTotal + "');");
                                            Toast.makeText(com.kunti.kasir.activity.BookMakanan.this, "Booking berhasil", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(com.kunti.kasir.activity.BookMakanan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(com.kunti.kasir.activity.BookMakanan.this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sJenis.equalsIgnoreCase("Bawa Pulang") && sMenu.equalsIgnoreCase("Paket 1")) {
            harga1 = 10000;
            harga2 = 20000;
        } else if (sJenis.equalsIgnoreCase("Bawa Pulang") && sMenu.equalsIgnoreCase("Paket 2")) {
            harga1 = 20000;
            harga2 = 30000;
        } else if (sJenis.equalsIgnoreCase("Bawa Pulang") && sMenu.equalsIgnoreCase("Paket 3")) {
            harga1 = 30000;
            harga2 = 40000;
        } else if (sJenis.equalsIgnoreCase("Bawa Pulang") && sMenu.equalsIgnoreCase("Paket Lapar")) {
            harga1 = 40000;
            harga2 = 50000;
        } else if (sJenis.equalsIgnoreCase("Bawa Pulang") && sMenu.equalsIgnoreCase("Paket Jumbo")) {
            harga1 = 40000;
            harga2 = 45000;
        } else if (sJenis.equalsIgnoreCase("Makan di Tempat") && sMenu.equalsIgnoreCase("Paket 1")) {
            harga1 = 9000;
            harga2 = 19000;
        } else if (sJenis.equalsIgnoreCase("Makan di Tempat") && sMenu.equalsIgnoreCase("Paket 2")) {
            harga1 = 19000;
            harga2 = 29000;
        } else if (sJenis.equalsIgnoreCase("Makan di Tempat") && sMenu.equalsIgnoreCase("Paket 3")) {
            harga1 = 39000;
            harga2 = 49000;
        } else if (sJenis.equalsIgnoreCase("Makan di Tempat") && sMenu.equalsIgnoreCase("Paket Lapar")) {
            harga1 = 49000;
            harga2 = 59000;
        } else if (sJenis.equalsIgnoreCase("Makan di Tempat") && sMenu.equalsIgnoreCase("Paket Jumbo")) {
            harga1 = 39000;
            harga2 = 44000;
        }

        Jumlah = Integer.parseInt(sJumlah);
        Note = Integer.parseInt(sNote);

        hargatotal1 = Jumlah * harga1;
        hargatotal2 = Note * harga2;
        hargaTotal = hargatotal1 + hargatotal2;
    }

    private void setDateTimeField() {
        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpTanggal.show();
            }
        });

        dpTanggal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei",
                        "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                sTanggal = dayOfMonth + " " + bulan[monthOfYear] + " " + year;
                etTanggal.setText(sTanggal);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}