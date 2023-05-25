package com.example.gerenciadordegastos.persistencia;

/*import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;*/
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gerenciadordegastos.modelo.Gasto;

@Database(entities = {Gasto.class}, version = 1, exportSchema = false)
public abstract class GastosDatabase extends RoomDatabase {

    public abstract GastoDAO gastoDAO();

    private static GastosDatabase instance;

    public static GastosDatabase getDatabase(final Context context) {
        if(instance == null) {
            synchronized (GastosDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context, GastosDatabase.class, "gastos.db").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }
}
