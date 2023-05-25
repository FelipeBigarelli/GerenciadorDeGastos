package com.example.gerenciadordegastos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciadordegastos.modelo.Gasto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GastoAdapter extends BaseAdapter {
    private Context context;
    private List<Gasto> gastos;
    private NumberFormat numberFormat;

    private static class GastoHolder {
        public TextView textViewValueNomeGasto;
        public TextView textViewValueValorGasto;
        public TextView textViewValueTipoGasto;
        public TextView textViewValueRelevante;
        public TextView textViewValueTipoPagamento;
    }

    public GastoAdapter(Context context, List<Gasto> gastos) {
        this.context = context;
        this.gastos = gastos;

        numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    @Override
    public int getCount() {
        return gastos.size();
    }

    @Override
    public Object getItem(int i) {
        return gastos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /*public void remove(int position){
        gastos.remove(gastos.get(position));
    }*/

    public void remove(Object object) {
        gastos.remove(object);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GastoHolder holder;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linha_lista_gasto, viewGroup, false);

            holder = new GastoHolder();

            holder.textViewValueNomeGasto = view.findViewById(R.id.textViewValueNomeGasto);
            holder.textViewValueValorGasto = view.findViewById(R.id.textViewValueValorGasto);
            holder.textViewValueTipoGasto = view.findViewById(R.id.textViewValueTipoGasto);
            holder.textViewValueRelevante = view.findViewById(R.id.textViewValueRelevante);
            holder.textViewValueTipoPagamento = view.findViewById(R.id.textViewValueTipoPagamento);

            view.setTag(holder);
        } else {
            holder = (GastoHolder) view.getTag();
        }

        holder.textViewValueNomeGasto.setText(gastos.get(i).getNome());

        String valorGastoFormatado = numberFormat.format(gastos.get(i).getValor());

        holder.textViewValueValorGasto.setText(valorGastoFormatado);

        switch (gastos.get(i).getTipoGasto()) {
            case 1:
                holder.textViewValueTipoGasto.setText(R.string.holderTipoGastoCasa);
                break;
            case 2:
                holder.textViewValueTipoGasto.setText(R.string.holderTipoGastoMercado);
                break;
            case 3:
                holder.textViewValueTipoGasto.setText(R.string.holderTipoGastoOutros);
                break;
        }

        if(gastos.get(i).isRelevante()) {
            holder.textViewValueRelevante.setText(R.string.holderRelevanteSim);
        } else {
            holder.textViewValueRelevante.setText(R.string.holderRelevanteNao);
        }

        holder.textViewValueTipoPagamento.setText(gastos.get(i).getTipoPagamento());

        return view;
    }
}
