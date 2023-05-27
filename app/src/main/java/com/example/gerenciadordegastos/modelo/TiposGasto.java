package com.example.gerenciadordegastos.modelo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tipos_gasto", indices = @Index(value = {"descricao"}, unique = true))
public class TiposGasto {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String descricao;

    public TiposGasto(String descricao){
        setDescricao(descricao);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString(){
        return getDescricao();
    }
}
