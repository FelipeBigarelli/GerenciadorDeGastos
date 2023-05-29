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

import com.example.gerenciadordegastos.modelo.Meta;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;
import com.example.gerenciadordegastos.utils.UtilsGUI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MetasActivity extends AppCompatActivity {
    private static final int REQUEST_NOVA_META    = 1;
    private static final int REQUEST_ALTERAR_META = 2;

    private ListView listViewMetas;
    private ArrayAdapter<Meta> listaAdapter;
    private List<Meta> lista;

    public static void metas(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MetasActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_metas);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewMetas = findViewById(R.id.listViewListaMetas);

        listViewMetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Meta meta = (Meta) parent.getItemAtPosition(position);

                MetaActivity.alterar(MetasActivity.this,
                        REQUEST_ALTERAR_META,
                        meta);
            }
        });

        carregaMetas();

        registerForContextMenu(listViewMetas);

        setTitle(R.string.meta);
    }

    private void carregaMetas(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(MetasActivity.this);

                lista = database.metaDAO().queryAll();


                MetasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(MetasActivity.this,
                                android.R.layout.simple_list_item_1,
                                lista);

                        listViewMetas.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void excluirMeta(final Meta meta){

        String mensagem = getString(R.string.deseja_realmente_apagar_meta);

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
                                                GastosDatabase.getDatabase(MetasActivity.this);

                                        database.metaDAO().delete(meta);

                                        MetasActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(meta);
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
        if ((requestCode == REQUEST_NOVA_META || requestCode == REQUEST_ALTERAR_META)
                && resultCode == Activity.RESULT_OK) {

            carregaMetas();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
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

        getMenuInflater().inflate(R.menu.item_selecionado_metas, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Meta meta = (Meta) listViewMetas.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                MetaActivity.alterar(this,
                        REQUEST_ALTERAR_META,
                        meta);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
