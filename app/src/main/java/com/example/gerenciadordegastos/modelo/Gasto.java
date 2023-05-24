package com.example.gerenciadordegastos.modelo;

import com.example.gerenciadordegastos.TipoGasto;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.math.BigDecimal;

@Entity
public class Gasto {
    public static final int HOUSE = 1;
    public static final int MARKETPLACE = 2;
    public static final int OTHERS = 3;

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String nome;
    private Float valor;
    //private TipoGasto tipoGasto;
    private int tipoGasto;
    private Boolean relevante;
    private String tipoPagamento;

    public Gasto (String nome) {
        setNome(nome);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public int getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(int tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public Boolean isRelevante() {
        return relevante;
    }

    public void setRelevante(Boolean relevante) {
        this.relevante = relevante;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

}
