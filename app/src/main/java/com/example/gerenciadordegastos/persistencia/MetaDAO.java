package com.example.gerenciadordegastos.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gerenciadordegastos.modelo.Gasto;
import com.example.gerenciadordegastos.modelo.Meta;
import com.example.gerenciadordegastos.modelo.TiposGasto;

import java.util.List;

@Dao
public interface MetaDAO {
    @Insert
    long insert(Meta meta);

    @Delete
    void delete(Meta meta);

    @Update
    void update(Meta meta);

    @Query("SELECT * FROM meta WHERE id = :id")
    Meta queryForId(long id);

    @Query("SELECT * FROM meta ORDER BY id")
    List<Meta> queryAll();

    @Query("SELECT * FROM meta ORDER BY id DESC LIMIT 1")
    int getId();

}
