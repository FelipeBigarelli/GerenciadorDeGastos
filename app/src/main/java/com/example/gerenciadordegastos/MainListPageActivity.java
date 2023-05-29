package com.example.gerenciadordegastos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gerenciadordegastos.modelo.Gasto;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;
import com.example.gerenciadordegastos.utils.UtilsGUI;

import java.util.ArrayList;
import java.util.List;

public class MainListPageActivity extends AppCompatActivity {

    private static final String ARQUIVO = "com.example.gerenciadordegastos.PREFERENCIAS_TEMAS";
    private static final String TEMA = "TEMA";
    private ConstraintLayout layout;
    private ListView listViewGastos;
    private GastoAdapter listaGastoAdapter;
    private ArrayList<Gasto> gastos;
    private List<Gasto>         lista;


    private ActionMode actionMode;
    private int posicaoSelecionada = -1;
    private View viewSelecionada;
    private boolean themeChecked = false;

    private static final int REQUEST_NOVO_GASTO = 1;
    private static final int REQUEST_ALTERAR_GASTO = 2;

   /* private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.principal_gasto_selecionado, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.menuItemEditar:
                    //alterarGasto();
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    //excluirGasto();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(viewSelecionada != null) {
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelecionada = null;

            listViewGastos.setEnabled(true);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_page);

        layout = findViewById(R.id.layoutPrincipal);

        lerPreferenciaTema();

        listViewGastos = findViewById(R.id.listViewGastos);

        listViewGastos.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Gasto gasto = (Gasto) parent.getItemAtPosition(position);

                        /*posicaoSelecionada = position;
                        alterarGasto();*/

                        NewGastoActivity.alterarGasto(MainListPageActivity.this, REQUEST_ALTERAR_GASTO, gasto);
                    }
                }
        );

        listViewGastos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        /*listViewGastos.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                        if (actionMode != null){
                            return false;
                        }

                        posicaoSelecionada = position;

                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listViewGastos.setEnabled(false);

                        actionMode = startActionMode(mActionModeCallback);

                        return true;
                    }
                });*/

        popularLista();

        registerForContextMenu(listViewGastos);
    }

    private void lerPreferenciaTema() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        themeChecked = shared.getBoolean(TEMA, themeChecked);

        mudarTema();
    }

    private void mudarTema() {
        if(themeChecked == false) {
            layout.setBackgroundColor(Color.GRAY);
        }

        if(themeChecked == true) {
            layout.setBackgroundColor(Color.WHITE);
        }

        themeChecked = !themeChecked;
    }

    private void salvarPreferenciaTema(boolean novoValor) {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(TEMA, novoValor);

        editor.commit();

        themeChecked = novoValor;

        mudarTema();
    }

    private void popularLista() {
        /*gastos = new ArrayList<>();

        listaGastoAdapter = new GastoAdapter(this, gastos);

        listViewGastos.setAdapter(listaGastoAdapter);*/

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(MainListPageActivity.this);

                lista = database.gastoDAO().queryAll();

                MainListPageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaGastoAdapter = new GastoAdapter(MainListPageActivity.this, lista);

                        listViewGastos.setAdapter(listaGastoAdapter);
                    }
                });
            }
        });
    }

    /*private void excluirGasto(final Gasto gasto) {
        String mensagem = R.string.confirmRemoveExpend + "\n" + gasto.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                GastosDatabase database =
                                        GastosDatabase.getDatabase(MainListPageActivity.this);

                                database.gastoDAO().delete(gasto);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);

        listaGastoAdapter.remove(gasto);

        listaGastoAdapter.notifyDataSetChanged();
    }*/

    private void excluirGasto(final Gasto gasto){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + gasto.getNome();

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
                                                GastosDatabase.getDatabase(MainListPageActivity.this);

                                        database.gastoDAO().delete(gasto);

                                        MainListPageActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaGastoAdapter.remove(gasto);
                                                listaGastoAdapter.notifyDataSetChanged();
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

    /*private void alterarGasto() {
        Gasto gasto = gastos.get(posicaoSelecionada);

        NewGastoActivity.alterarGasto(this, gasto);
        listaGastoAdapter.notifyDataSetChanged();
    }*/

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        /*super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            Bundle bundle = data.getExtras();


            String nome = bundle.getString(NewGastoActivity.NOME);
            Float valor = bundle.getFloat(NewGastoActivity.VALOR);
            int tipoOrdinal = bundle.getInt(NewGastoActivity.TIPO_GASTO);
            Boolean relevante = bundle.getBoolean(NewGastoActivity.RELEVANTE);
            String tipoPagamento = bundle.getString(NewGastoActivity.TIPO_PAGAMENTO);


            TipoGasto[] tiposGasto = TipoGasto.values();

            if (requestCode == NewGastoActivity.ALTERAR) {
                Gasto gasto = gastos.get(posicaoSelecionada);

                gasto.setNome(nome);
                gasto.setValor(valor);
                gasto.setTipoGasto(tiposGasto[tipoOrdinal]);
                gasto.setRelevante(relevante);
                gasto.setTipoPagamento(tipoPagamento);

                posicaoSelecionada = -1;
            } else {
                gastos.add(new Gasto(nome, valor, tiposGasto[tipoOrdinal], relevante, tipoPagamento));
            }

            listaGastoAdapter.notifyDataSetChanged();
        }*/

        if((requestCode == REQUEST_NOVO_GASTO || requestCode == REQUEST_ALTERAR_GASTO) && resultCode == Activity.RESULT_OK) {
            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menuItemAcicionar:
                NewGastoActivity.novoGasto(this, REQUEST_NOVO_GASTO);

                return true;

            case R.id.menuItemSobre:
                SobreActivity.sobre(this);

                return true;

            case R.id.menuItemTrocarTema:
                salvarPreferenciaTema(themeChecked);

                return true;

            case R.id.menuItemTipos:
                TiposGastoActivity.abrir(this);
                return true;

            case R.id.menuItemMetas:
                MetasActivity.metas(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.principal_gasto_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Gasto gasto = (Gasto) listViewGastos.getItemAtPosition(info.position);

        switch(item.getItemId()) {
            case R.id.menuItemEditar:
                NewGastoActivity.alterarGasto(this, REQUEST_ALTERAR_GASTO, gasto);
                return true;

            case R.id.menuItemExcluir:
                excluirGasto(gasto);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}