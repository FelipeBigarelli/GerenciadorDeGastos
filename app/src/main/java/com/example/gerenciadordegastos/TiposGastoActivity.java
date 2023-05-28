package com.example.gerenciadordegastos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordegastos.modelo.Gasto;
import com.example.gerenciadordegastos.modelo.TiposGasto;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;
import com.example.gerenciadordegastos.utils.UtilsGUI;

import java.util.List;

public class TiposGastoActivity extends AppCompatActivity {
    private static final int REQUEST_NOVO_TIPO    = 1;
    private static final int REQUEST_ALTERAR_TIPO = 2;

    private ListView listViewTipos;
    private ArrayAdapter<TiposGasto> listaAdapter;
    private List<TiposGasto> lista;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, TiposGastoActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tipo_gasto);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewTipos = findViewById(R.id.listViewItensTipoGasto);

        listViewTipos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TiposGasto tipo = (TiposGasto) parent.getItemAtPosition(position);

                TipoGastoActivity.alterar(TiposGastoActivity.this,
                        REQUEST_ALTERAR_TIPO,
                        tipo);
            }
        });

        carregaTipos();

        registerForContextMenu(listViewTipos);

        setTitle(R.string.types);
    }

    private void carregaTipos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                GastosDatabase database = GastosDatabase.getDatabase(TiposGastoActivity.this);

                lista = database.tiposGastoDAO().queryAll();

                TiposGastoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(TiposGastoActivity.this,
                                android.R.layout.simple_list_item_1,
                                lista);

                        listViewTipos.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void verificaUsoTipo(final TiposGasto tipo){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(TiposGastoActivity.this);

                List<Gasto> lista = database.gastoDAO().queryForTipoId(tipo.getId());

                if (lista != null && lista.size() > 0){

                    TiposGastoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.avisoErro(TiposGastoActivity.this, R.string.tipo_usado);
                        }
                    });

                    return;
                }

                TiposGastoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excluirTipo(tipo);
                    }
                });
            }
        });
    }

    private void excluirTipo(final TiposGasto tipo){

        String mensagem = getString(R.string.deseja_realmente_apagar) + "\n" + tipo.getDescricao();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        GastosDatabase database =
                                                GastosDatabase.getDatabase(TiposGastoActivity.this);

                                        database.tiposGastoDAO().delete(tipo);

                                        TiposGastoActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(tipo);
                                            }
                                        });
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_NOVO_TIPO || requestCode == REQUEST_ALTERAR_TIPO)
                && resultCode == Activity.RESULT_OK) {

            carregaTipos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_tipos, menu);

        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovoTipo:
                TipoGastoActivity.novo(this, REQUEST_NOVO_TIPO);
                return true;

            case android.R.id.home:
                cancelar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final TiposGasto tipo = (TiposGasto) listViewTipos.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                TipoGastoActivity.alterar(this,
                        REQUEST_ALTERAR_TIPO,
                        tipo);
                return true;

            case R.id.menuItemApagar:
                verificaUsoTipo(tipo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
