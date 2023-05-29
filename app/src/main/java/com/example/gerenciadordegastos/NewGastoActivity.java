package com.example.gerenciadordegastos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gerenciadordegastos.modelo.Gasto;
import com.example.gerenciadordegastos.modelo.TiposGasto;
import com.example.gerenciadordegastos.persistencia.GastosDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewGastoActivity extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String NOME = "NOME";
    public static final String RELEVANTE = "RELEVANTE";
    public static final String VALOR = "VALOR";
    public static final String TIPO_GASTO = "TIPO_GASTO";
    public static final String TIPO_PAGAMENTO = "TIPO_PAGAMENTO";
    public static final String MODO = "MODO";
    public static final int NOVO = 1;
    public static final int ALTERAR = 2;
    private int modo;
    private String nomeOriginal;
    private Float valorOriginal;
    /*private TipoGasto tipoGastoOriginal;
    private Boolean relevanteOriginal;
    private String tipoPagamentoOriginal;*/


    private EditText editTextNomeGasto, editTextValorGasto;
    private RadioGroup radioGroupTipoPagamento;
    private CheckBox cbGastoRelevante;
    private Spinner spinnerTiposGasto;
    private List<TiposGasto> listaTipos;

    private Gasto gasto;

    public static void novoGasto(Activity activity, int requestCode){

        Intent intent = new Intent(activity, NewGastoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterarGasto(AppCompatActivity activity, int requestCode, Gasto gasto) {
        Intent intent = new Intent(activity, NewGastoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(NOME, gasto.getNome());
        intent.putExtra(VALOR, gasto.getValor());
        intent.putExtra(TIPO_GASTO, gasto.getTipoId());
        intent.putExtra(RELEVANTE, gasto.isRelevante());
        intent.putExtra(TIPO_PAGAMENTO, gasto.getTipoPagamento());
        intent.putExtra(ID, gasto.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNomeGasto = findViewById(R.id.editTextNomeGasto);
        editTextValorGasto = findViewById(R.id.editTextNumberDecimal);
        radioGroupTipoPagamento = findViewById(R.id.radioGroupTiposPagamento);
        spinnerTiposGasto = findViewById(R.id.spinnerSelecionarTiposGastos);
        cbGastoRelevante = findViewById(R.id.checkBoxGastoRelevante);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        carregaTipos();

        if(bundle != null) {
            modo = bundle.getInt(MODO, NOVO);

            if(modo == NOVO) {
                setTitle(getString(R.string.setTitleNovoGasto));

                gasto = new Gasto("");
            } else {
                setTitle(R.string.setTitleAlterarGasto);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);

                        GastosDatabase database = GastosDatabase.getDatabase(NewGastoActivity.this);

                        gasto = database.gastoDAO().queryForId(id);

                        NewGastoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editTextNomeGasto.setText(gasto.getNome());
                                editTextValorGasto.setText(String.valueOf(gasto.getValor()));

                                int posicao = posicaoTipo(gasto.getTipoId());
                                spinnerTiposGasto.setSelection(posicao);

                                int tipoPagamento = gasto.getTipoPagamento();

                                RadioButton button;

                                switch(tipoPagamento) {
                                    case Gasto.DINHEIRO:
                                        button = findViewById(R.id.radioButtonDinheiro);
                                        button.setChecked(true);
                                        break;

                                    case Gasto.CREDITO:
                                        button = findViewById(R.id.radioButtonCredito);
                                        button.setChecked(true);
                                        break;

                                    case Gasto.DEBITO:
                                        button = findViewById(R.id.radioButtonDebito);
                                        button.setChecked(true);
                                        break;
                                }

                                boolean relevante = bundle.getBoolean(RELEVANTE);
                                cbGastoRelevante.setChecked(relevante);
                            }
                        });
                    }
                });

                /*nomeOriginal = bundle.getString(NOME);
                editTextNomeGasto.setText(nomeOriginal);

                valorOriginal = bundle.getFloat(VALOR);
                editTextValorGasto.setText(String.valueOf(valorOriginal));

                int tipoPagamento = radioGroupTipoPagamento.getCheckedRadioButtonId();

                RadioButton button;

                switch(tipoPagamento) {
                    case Gasto.DINHEIRO:
                        button = findViewById(R.id.radioButtonDinheiro);
                        button.setChecked(true);
                        break;

                    case Gasto.CREDITO:
                        button = findViewById(R.id.radioButtonCredito);
                        button.setChecked(true);
                        break;

                    case Gasto.DEBITO:
                        button = findViewById(R.id.radioButtonDebito);
                        button.setChecked(true);
                        break;
                }*/

                /*String tiposGasto = bundle.getString((TIPO_GASTO));
                for(int position = 0; 0 < spinnerTiposGasto.getAdapter().getCount(); position++) {
                    String valor = (String) spinnerTiposGasto.getItemAtPosition(position);

                    if(valor.equals(tiposGasto)){
                        spinnerTiposGasto.setSelection(position);
                        break;
                    }
                }*/
            }
        }
    }

    private int posicaoTipo(int tipoId){

        for (int pos = 0; pos < listaTipos.size(); pos++){

            TiposGasto t = listaTipos.get(pos);

            if (t.getId() == tipoId){
                return pos;
            }
        }

        return -1;
    }

    private void carregaTipos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(NewGastoActivity.this);

                listaTipos = database.tiposGastoDAO().queryAll();

                NewGastoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<TiposGasto> spinnerAdapter =
                                new ArrayAdapter<>(NewGastoActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        listaTipos);

                        spinnerTiposGasto.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    public void salvarGasto() {
        String nomeGasto = editTextNomeGasto.getText().toString();
        String valorGasto = editTextValorGasto.getText().toString();
        /*TipoGasto tipoSelecionado = null;
        String spinnerTiposGastoSelecionado = (String) spinnerTiposGasto.getSelectedItem();
        String checkBoxMessage = "";
        String spinnerTipoSelecionado = (String) spinnerTipoPagamento.getSelectedItem();
        String spinnerMessage;*/



        if(nomeGasto == null || nomeGasto.trim().isEmpty()) {
            Toast.makeText(this, R.string.erroNomeGasto, Toast.LENGTH_SHORT).show();
            editTextNomeGasto.requestFocus();

            return;
        }

        if(valorGasto == null || valorGasto.trim().isEmpty()) {
            Toast.makeText(this, R.string.valorDoGasto, Toast.LENGTH_SHORT).show();
            editTextValorGasto.requestFocus();

            return;
        }

        int tipoPagamento = -1;

        switch (radioGroupTipoPagamento.getCheckedRadioButtonId()) {
            case R.id.radioButtonDinheiro:
                tipoPagamento = Gasto.DINHEIRO;
                break;

            case R.id.radioButtonCredito:
                tipoPagamento = Gasto.CREDITO;
                break;

            case R.id.radioButtonDebito:
                tipoPagamento = Gasto.DEBITO;
                break;

            default:
                tipoPagamento = -1;
        }

        TiposGasto tipo = (TiposGasto) spinnerTiposGasto.getSelectedItem();
        /*if (tipo != null){
            gasto.setTipoId(tipo.getId());
        }*/


        /*if (cbGastoRelevante.isChecked()) {
            checkBoxMessage += getString(R.string.gastoRelevante);
        } else {
            checkBoxMessage += getString(R.string.gastoNaoRelevante);
        }*/

        /*Intent intent = new Intent();

        intent.putExtra(NOME, nomeGasto);
        intent.putExtra(VALOR, Float.valueOf(valorGasto).floatValue());

        int id = radioGroupTipoGasto.getCheckedRadioButtonId();


        intent.putExtra(TIPO_GASTO, tipoSelecionado.ordinal());


        intent.putExtra(RELEVANTE, cbGastoRelevante.isChecked());
        intent.putExtra(TIPO_PAGAMENTO, spinnerTipoSelecionado);*/

        boolean isRelevante = cbGastoRelevante.isChecked();

        //String tipoPagamento = (String) spinnerTipoPagamento.getSelectedItem();

        //GastosDatabase database = GastosDatabase.getDatabase(this);

        /*if(spinnerTiposGasto != null) {
            spinnerMessage = getString(R.string.tipoPagamentoSelecionado);
        } else {
            spinnerMessage = getString(R.string.nenhumPagamentoSelecionado);
        }*/

        gasto.setNome(nomeGasto);
        gasto.setValor(Float.valueOf(valorGasto));
        gasto.setTipoId(tipo.getId());
        gasto.setRelevante(isRelevante);
        gasto.setTipoPagamento(tipoPagamento);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GastosDatabase database = GastosDatabase.getDatabase(NewGastoActivity.this);

                if (modo == NOVO) {

                    database.gastoDAO().insert(gasto);

                } else {

                    database.gastoDAO().update(gasto);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    /*private void popularSpinnerTipoPagamento() {
        ArrayList<String> list = new ArrayList<>();

        list.add(getString(R.string.spinnerDinheiro));
        list.add(getString(R.string.spinnerCredito));
        list.add(getString(R.string.spinnerDebito));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
        spinnerTipoPagamento.setAdapter(adapter);
    }*/

    /*private void popularSpinnerTiposGasto() {
        ArrayList<String> list = new ArrayList<>();

        list.add(R.string.);
        list.add(getString(R.string.spinnerCredito));
        list.add(getString(R.string.spinnerDebito));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
        spinnerTipoPagamento.setAdapter(adapter);
    }*/

    public void limparCampos() {
        editTextNomeGasto.setText(null);
        editTextValorGasto.setText(null);
        radioGroupTipoPagamento.clearCheck();
        cbGastoRelevante.setChecked(false);


        editTextNomeGasto.requestFocus();

        Toast.makeText(this, R.string.limparCamposNovoGasto, Toast.LENGTH_SHORT).show();
    }

    /*public void mostrarGastoCompleto(View view) {
        String nomeGasto = editTextNomeGasto.getText().toString();
        String valorGasto = editTextValorGasto.getText().toString();
        String radioButtonSelected = "";
        String checkBoxMessage = "";
        String spinnerTipoSelecionado = (String) spinnerTipoPagamento.getSelectedItem();
        String spinnerMessage;

        if(spinnerTipoSelecionado != null) {
            spinnerMessage = getString(R.string.tipoPagamentoSelecionado);
        } else {
            spinnerMessage = getString(R.string.nenhumPagamentoSelecionado);
        }

        if(nomeGasto == null || nomeGasto.trim().isEmpty()) {
            Toast.makeText(this, R.string.erroNomeGasto, Toast.LENGTH_SHORT).show();
            editTextNomeGasto.requestFocus();

            return;
        }

        if(valorGasto == null || valorGasto.trim().isEmpty()) {
            Toast.makeText(this, R.string.valorDoGasto, Toast.LENGTH_SHORT).show();
            editTextValorGasto.requestFocus();

            return;
        }

        switch (radioGroupTipoGasto.getCheckedRadioButtonId()) {
            case R.id.radioButtonContasCasa:
                radioButtonSelected =
                        getString(R.string.radioButtonContasCasa);
                break;

            case R.id.radioButtonMercado:
                radioButtonSelected =
                        getString(R.string.radioButtonMercado);
                break;

            case R.id.radioButtonOutros:
                radioButtonSelected =
                        getString(R.string.radioButtonOutros);
                break;

            default:
                radioButtonSelected =
                        getString(R.string.nenhumTipoGastoSelecionado);
        }

        if (cbGastoRelevante.isChecked()) {
            checkBoxMessage += getString(R.string.gastoRelevante);
        } else {
            checkBoxMessage += getString(R.string.gastoNaoRelevante);
        }

        Toast.makeText(
                this,
                "Tipo de Gasto:" +
                " " +
                radioButtonSelected.trim() +
                "\n" +
                checkBoxMessage.trim(),
                Toast.LENGTH_SHORT).show();

        Toast.makeText(
                this,
                getString(R.string.toastPrintNomeGasto) +
                " " +
                nomeGasto.trim() +
                getString(R.string.toastPrintValorGasto) +
                valorGasto.trim(),
                Toast.LENGTH_SHORT).show();

        Toast.makeText(
                this,
                spinnerTipoSelecionado +
                " " +
                spinnerMessage,
                Toast.LENGTH_SHORT).show();

    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.novo_gasto_opcoes, menu);

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

            case R.id.menuItemSalvarGasto:
                salvarGasto();
                return true;

            case android.R.id.home:
                cancelar();
                return true;

            case R.id.menuItemLimparCampos:
                limparCampos();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}