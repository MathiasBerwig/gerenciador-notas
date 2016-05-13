package io.github.mathiasberwig.gerenciador_notas.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import io.github.mathiasberwig.gerenciador_notas.data.model.Nota;

/**
 * Data Access Object responsável por manipular registros de
 * {@link io.github.mathiasberwig.gerenciador_notas.data.model.Nota Notas} no banco de dados SQLite
 * desta aplicação.
 *
 * Created by mathias.berwig on 02/05/2016.
 */
public class NotasDAO {
    private static final String TAG = NotasDAO.class.getName();

    /**
     * Instância da classe (singleton).
     */
    private static NotasDAO instance;

    /**
     * Auxiliar de conexões do banco de dados.
     */
    private final SQLiteOpenHelper databaseHelper;

    /**
     * Constrói uma nova instância do NotasDAO utilizando o SQLiteOpenHelper informado.
     *
     * @param databaseHelper instância do auxiliar de manipulação do banco de dados.
     */
    private NotasDAO(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Obtém uma instância desta classe.
     *
     * @param context Contexto para acessar o {@link DBHelper}.
     * @return A instância criada ou existente desta classe.
     * @see #instance
     */
    public static NotasDAO getInstance(Context context) {
        if (instance == null) {
            instance = new NotasDAO(DBHelper.getInstance(context));
        }
        return instance;
    }

    /**
     * Lista todas as {@link Nota notas} do banco de dados local.
     *
     * @return todos os registros da tabela {@link Notas#NOME_TABELA}.
     * @see <a href="http://bit.ly/1whYCa6">Android Developers - Ler informações de um banco de dados</a>
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public ArrayList<Nota> listar() {
        ArrayList<Nota> result = null;
        Cursor cursor = null;
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        try {
            String[] columns = {Notas._ID,
                    Notas.TITULO,
                    Notas.CONTEUDO};
            cursor = database.query(Notas.NOME_TABELA, columns, null, null, null, null, null);
            result = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Nota nota = new Nota(
                    cursor.getLong(cursor.getColumnIndexOrThrow(Notas._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Notas.TITULO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Notas.CONTEUDO))
                );
                result.add(nota);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Não foi possível listar todas as notas.", ex);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                    Log.e(TAG, "Não foi possível fechar o cursor.");
                }
            }
            database.close();
        }
        return result;
    }

    /**
     * Insere uma {@link Nota} na tabela {@link Notas#NOME_TABELA}.
     *
     * @param nota a nota a ser inserida.
     * @return {@code true} caso a nota tenha sido inserida com sucesso.
     * @see <a href="http://bit.ly/1D3oTNG">Android Developers - Colocar informações no banco de dados</a>
     */
    public boolean inserir(Nota nota) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        boolean sucesso = false;
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Notas.TITULO, nota.getTitulo());
            values.put(Notas.CONTEUDO, nota.getConteudo());
            long rowId = database.insert(Notas.NOME_TABELA, null, values);
            sucesso = rowId != -1;
            nota.setId(rowId);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Não foi possível completar a inserção de [" + nota + "]", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
        return sucesso;
    }

    /**
     * Atualiza uma {@link Nota} na tabela {@link Notas#NOME_TABELA}.
     *
     * @param nota a nota a ser atualizada.
     * @return número de linhas afetadas.
     * @see <a href="http://bit.ly/1tOS68i">Atualizar informações do banco de dados</a>
     */
    public int update(Nota nota) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int linhasAfetadas = 0;
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Notas.TITULO, nota.getTitulo());
            values.put(Notas.CONTEUDO, nota.getConteudo());
            String[] whereArgs = {String.valueOf(nota.getId())};
            linhasAfetadas =+ database.update(Notas.NOME_TABELA, values, Notas._ID + " = ?", whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Não foi possível completar a atualização de [" + nota + "]", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
        return linhasAfetadas;
    }

    /**
     * Exclui uma {@link Nota} da tabela {@link Notas#NOME_TABELA}.
     *
     * @param note a nota a ser excluída.
     * @return número de linhas afetadas.
     * @see <a href="http://bit.ly/1syEh1A">Android Developers - Excluir informações do banco de dados</a>
     */
    public int delete(Nota note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int linhasAfetadas = 0;
        database.beginTransaction();
        try {
            String[] whereArgs = {String.valueOf(note.getId())};
            linhasAfetadas =+ database.delete(Notas.NOME_TABELA, Notas._ID + " = ?", whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Não foi possível excluir [" + note + "]", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
        return linhasAfetadas;
    }

    /** Constantes com nomes da tabela de notas. */
    private static class Notas implements BaseColumns {
        private static final String NOME_TABELA = "notas";
        private static final String TITULO = "titulo";
        private static final String CONTEUDO = "conteudo";
    }
}
