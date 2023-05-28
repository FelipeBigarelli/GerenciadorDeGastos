package com.example.gerenciadordegastos;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordegastos.modelo.TiposGasto;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;
import com.example.gerenciadordegastos.utils.UtilsGUI;

import java.util.List;

public class TipoGastoActivity extends AppCompatActivity {
    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTextDescricao;

    private int  modo;
    private TiposGasto tipo;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, TipoGastoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, TiposGasto tipo){

        Intent intent = new Intent(activity, TipoGastoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, tipo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_gasto);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextDescricao = findViewById(R.id.editTextDescricaoTipoGasto);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
        }else{
            modo = NOVO;
        }

        if (modo == ALTERAR){

            setTitle(getString(R.string.mudarTipoGasto));

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    int id = bundle.getInt(ID);

                    GastosDatabase database = GastosDatabase.getDatabase(TipoGastoActivity.this);

                    tipo = database.tiposGastoDAO().queryForId(id);

                    TipoGastoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextDescricao.setText(tipo.getDescricao());
                        }
                    });
                }
            });

        }else{

            setTitle(getString(R.string.novoTipoGasto));

            tipo = new TiposGasto("");
        }
    }

    public void limparCampos() {
        editTextDescricao.setText(null);

        editTextDescricao.requestFocus();

        Toast.makeText(this, R.string.limparCamposNovoGasto, Toast.LENGTH_SHORT).show();
    }

    private void salvar(){

        final String descricao  = UtilsGUI.validaCampoTexto(
                this,
                editTextDescricao,
                R.string.descricaoVazia
        );

        if (descricao == null){
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                GastosDatabase database = GastosDatabase.getDatabase(TipoGastoActivity.this);

                List<TiposGasto> lista = database.tiposGastoDAO().queryForDescricao(descricao);

                if (modo == NOVO) {

                    if (lista.size() > 0){

                        TipoGastoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilsGUI.avisoErro(TipoGastoActivity.this, R.string.descricao_usada);
                            }
                        });

                        return;
                    }

                    tipo.setDescricao(descricao);

                    database.tiposGastoDAO().insert(tipo);

                } else {

                    if (!descricao.equals(tipo.getDescricao())){

                        if (lista.size() >= 1){

                            TipoGastoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsGUI.avisoErro(TipoGastoActivity.this, R.string.descricao_usada);
                                }
                            });

                            return;
                        }

                        tipo.setDescricao(descricao);

                        database.tiposGastoDAO().update(tipo);
                    }
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
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
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemLimparCampoTipo:
                limparCampos();
                return true;

            case android.R.id.home:
                cancelar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
