package com.example.gerenciadordegastos.persistencia;

/*import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;*/
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gerenciadordegastos.R;
import com.example.gerenciadordegastos.modelo.Gasto;
import com.example.gerenciadordegastos.modelo.Meta;
import com.example.gerenciadordegastos.modelo.TiposGasto;

import java.util.Date;
import java.util.concurrent.Executors;

@Database(entities = {Gasto.class, TiposGasto.class, Meta.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GastosDatabase extends RoomDatabase {

    public abstract GastoDAO gastoDAO();

    public abstract TiposGastoDAO tiposGastoDAO();

    public abstract MetaDAO metaDAO();

    private static GastosDatabase instance;

    public static GastosDatabase getDatabase(final Context context) {
        if (instance == null) {

            synchronized (GastosDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder =  Room.databaseBuilder(context,
                            GastosDatabase.class,
                            "gastos.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaTiposIniciais(context);
                                    carregaMetaInicial(context);
                                }
                            });
                        }
                    });

                    instance = (GastosDatabase) builder.build();
                }
            }
        }

        return instance;
    }

    private static void carregaTiposIniciais(final Context context){

        String[] descricoes = context.getResources().getStringArray(R.array.tipos_iniciais);

        for (String descricao : descricoes) {

            TiposGasto tipoGasto = new TiposGasto(descricao);

            instance.tiposGastoDAO().insert(tipoGasto);
        }
    }

    private static void carregaMetaInicial(final Context context){

        String[] metaInicial = context.getResources().getStringArray(R.array.meta_inicial);

        for (String valorMeta : metaInicial) {

            Meta newMeta = new Meta(valorMeta);

            newMeta.setDataMeta(new Date());

            instance.metaDAO().insert(newMeta);
        }
    }
}
