package com.example.gerenciadordegastos.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gerenciadordegastos.modelo.TiposGasto;

import java.util.List;

@Dao
public interface TiposGastoDAO {
    @Insert
    long insert(TiposGasto tipoGasto);

    @Delete
    void delete(TiposGasto tipoGasto);

    @Update
    void update(TiposGasto tipoGasto);

    @Query("SELECT * FROM tipos_gasto WHERE id = :id")
    TiposGasto queryForId(long id);

    @Query("SELECT * FROM tipos_gasto ORDER BY descricao ASC")
    List<TiposGasto> queryAll();

    @Query("SELECT * FROM tipos_gasto WHERE descricao = :descricao ORDER BY descricao ASC")
    List<TiposGasto> queryForDescricao(String descricao);

    @Query("SELECT count(*) FROM tipos_gasto")
    int total();
}
