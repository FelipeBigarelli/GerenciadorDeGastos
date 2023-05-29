package com.example.gerenciadordegastos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordegastos.modelo.Meta;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;
import com.example.gerenciadordegastos.utils.UtilsDate;
import com.example.gerenciadordegastos.utils.UtilsGUI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MetaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTextValorNovaMeta;
    private EditText   editTextDataNovaMeta;
    private List<Meta> listaMetas;

    private ListView listViewContatos;
    private ArrayAdapter<Meta> listaAdapter;

    private TextView textViewDataNovaMeta;

    private int modo;
    private Meta meta;
    private Calendar calendarDataNovaMeta;

    public MetaActivity() {
    }

    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, MetaActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Meta meta){

        Intent intent = new Intent(activity, MetaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, meta.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meta);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextValorNovaMeta = findViewById(R.id.editTextValorNovaMeta);
        editTextDataNovaMeta = findViewById(R.id.editTextDataNovaMeta);
        textViewDataNovaMeta = findViewById(R.id.textViewDataNovaMeta);

        editTextDataNovaMeta.setEnabled(true);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, NOVO);

        calendarDataNovaMeta = Calendar.getInstance();
        calendarDataNovaMeta.add(Calendar.YEAR, -
                getResources().getInteger(R.integer.anos_para_tras));

        editTextDataNovaMeta.setFocusable(false);
        editTextDataNovaMeta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog picker = new DatePickerDialog(MetaActivity.this,
                        R.style.CustomDatePickerDialogTheme,
                        MetaActivity.this,
                        calendarDataNovaMeta.get(Calendar.YEAR),
                        calendarDataNovaMeta.get(Calendar.MONTH),
                        calendarDataNovaMeta.get(Calendar.DAY_OF_MONTH));

                picker.show();
            }
        });

        if (modo == ALTERAR){

            setTitle(getString(R.string.alterar_meta));

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    GastosDatabase database = GastosDatabase.getDatabase(MetaActivity.this);

                    meta = database.metaDAO().queryForId(id);

                    MetaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            editTextValorNovaMeta.setText(meta.getValorMeta());

                            calendarDataNovaMeta.setTime(meta.getDataMeta());

                            String textoData = UtilsDate.formatDate(MetaActivity.this,
                                    meta.getDataMeta());

                            editTextDataNovaMeta.setText(textoData);
                        }
                    });
                }
            });

        }else{

            setTitle(R.string.nova_meta);

            meta = new Meta("");

            textViewDataNovaMeta.setVisibility(View.INVISIBLE);
            editTextDataNovaMeta.setVisibility(View.INVISIBLE);
        }
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes_meta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case android.R.id.home:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendarDataNovaMeta.set(year, month, dayOfMonth);

        String textoData = UtilsDate.formatDate(this, calendarDataNovaMeta.getTime());

        editTextDataNovaMeta.setText(textoData);
    }

    private void salvar(){
        String valorMeta  = UtilsGUI.validaCampoTexto(this,
                editTextValorNovaMeta,
                R.string.meta_vazia);
        if (valorMeta == null){
            return;
        }

        String txtDataMeta = UtilsGUI.validaCampoTexto(this,
                editTextDataNovaMeta,
                R.string.data_meta_vazia);
        if (txtDataMeta == null){
            return;
        }

        meta.setValorMeta(valorMeta);
        meta.setDataMeta(calendarDataNovaMeta.getTime());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(MetaActivity.this);

                if (modo == NOVO) {

                    int novoId = (int) database.metaDAO().insert(meta);

                    meta.setId(novoId);

                } else {

                    database.metaDAO().update(meta);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
