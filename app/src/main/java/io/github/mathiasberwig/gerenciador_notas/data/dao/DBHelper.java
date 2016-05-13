package io.github.mathiasberwig.gerenciador_notas.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
import java.util.Scanner;

import io.github.mathiasberwig.gerenciador_notas.R;

/**
 * Auxilia na criação, atualização, manipulação do banco de dados SQLite.
 *
 * Created by mathias.berwig on 02/05/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Nome do arquivo de banco de dados (DB_NAME.sqlite).
     */
    private static final String DB_NAME = "gerenciador-notas";

    /**
     * A versão atual do banco de dados.
     * @see #onUpgrade(SQLiteDatabase, int, int)
     */
    private static final int DB_VERSION = 1;

    private static DBHelper instance;
    private final Context context;

    /**
     * Construtor privado, seguindo o padrão {@code singleton}.
     *
     * @param context contexto para instanciar o {@link SQLiteOpenHelper}.
     */
    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Instanciador da classe (padrão {@code singleton}.
     *
     * @param context contexto para instanciar o {@link SQLiteOpenHelper}.
     * @return a instância criada ou existente desta classe.
     */
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Executa o script de criação do banco de dados
        InputStream in = context.getResources().openRawResource(R.raw.criacao_tabelas_db);
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter(";");
        try {
            while (scanner.hasNext()) {
                String sql = scanner.next().trim();

                if (sql.length() > 0) {
                    db.execSQL(sql);
                }
            }

        } finally {
            scanner.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Não haverá mudanças no banco de dados, portanto esse método será ignorado.
    }
}
