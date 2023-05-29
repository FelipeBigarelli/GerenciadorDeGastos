package com.example.gerenciadordegastos.persistencia;

/*import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;*/

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gerenciadordegastos.modelo.Gasto;

import java.util.List;

@Dao
public interface GastoDAO {
    @Insert
    long insert(Gasto gasto);

    @Delete
    void delete(Gasto gasto);

    @Update
    void update(Gasto gasto);

    @Query("SELECT * FROM gastos WHERE id = :id")
    Gasto queryForId(long id);

    @Query("SELECT * FROM gastos ORDER BY nome ASC")
    List<Gasto> queryAll();

    @Query("SELECT * FROM gastos WHERE tipoId = :id ORDER BY nome ASC")
    List<Gasto> queryForTipoId(long id);

    @Query("SELECT SUM(valor) AS total FROM gastos")
    float getTotalSomaGastos();
}
