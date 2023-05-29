package com.example.gerenciadordegastos.modelo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "meta")
public class Meta {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String valorMeta;

    private Date dataMeta;
    public Meta(String valorMeta) {
        setValorMeta(valorMeta);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValorMeta() {
        return valorMeta;
    }

    public void setValorMeta(@NonNull String valorMeta) {
        this.valorMeta = valorMeta;
    }

    public Date getDataMeta() {
        return dataMeta;
    }

    public void setDataMeta(Date dataMeta) {
        this.dataMeta = dataMeta;
    }

    @Override
    public String toString(){
        return getValorMeta() + "\n" + getDataMeta();
    }
}
