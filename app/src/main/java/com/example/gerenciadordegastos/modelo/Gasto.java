package com.example.gerenciadordegastos.modelo;

//import com.example.gerenciadordegastos.TipoGasto;
//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.PrimaryKey;
//import android.support.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity(tableName = "gastos",
        foreignKeys = @ForeignKey(entity = TiposGasto.class,
                                  parentColumns = "id",
                                  childColumns = "tipoId"))
public class Gasto {
    public static final int DINHEIRO = 1;
    public static final int CREDITO = 2;
    public static final int DEBITO = 3;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;

    private Float valor;

    //private TipoGasto tipoGasto;
    private int tipoPagamento;

    @ColumnInfo(index = true)
    private int tipoId;

    private Boolean relevante;

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

    /*public int getTipoGasto() {
        return tipoGasto;
    }*/

    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    /*public void setTipoGasto(int tipoGasto) {
        this.tipoGasto = tipoGasto;
    }*/

    public Boolean isRelevante() {
        return relevante;
    }

    public void setRelevante(Boolean relevante) {
        this.relevante = relevante;
    }

    public int getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(int tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

}
