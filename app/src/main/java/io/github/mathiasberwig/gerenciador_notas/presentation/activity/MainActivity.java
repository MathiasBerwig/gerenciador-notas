package io.github.mathiasberwig.gerenciador_notas.presentation.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.mathiasberwig.gerenciador_notas.R;
import io.github.mathiasberwig.gerenciador_notas.data.dao.NotasDAO;
import io.github.mathiasberwig.gerenciador_notas.data.model.Nota;
import io.github.mathiasberwig.gerenciador_notas.presentation.fragment.EditarNotaFragment;
import io.github.mathiasberwig.gerenciador_notas.presentation.fragment.ListarNotasFragment;

public class MainActivity extends AppCompatActivity
        implements ListarNotasFragment.OnNotaSelecionadaListener,
        EditarNotaFragment.OnEdicaoConcluidaListener {
    private static final String TAG = MainActivity.class.getName();

    /**
     * Instância do fragmento Listar Notas.
     */
    private ListarNotasFragment listarNotasFragment;

    /**
     * Instância do Data Access Object.
     */
    private NotasDAO notasDAO;

    /**
     * Indica qual nota está selecionada no momento.
     */
    private Nota notaSelecionada;

    /**
     * Referência para o botão de ação flutuante.
     */
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtém a instância do DAO
        notasDAO = NotasDAO.getInstance(this);

        // Exibe o fragmento de listagem de notas
        prepararFragmentoListarNotas(savedInstanceState);

        // Exibe o botão de ação flutuante
        prepararFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editar_nota:
                prepararFragmentoEditarNota(false);
                return true;

            case R.id.action_excluir_nota:
                excluirNotaSelecionada();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Mostra ou oculta os menus de contexo de acordo com o estado de seleção
        final boolean mostrarItens = notaSelecionada != null;
        menu.findItem(R.id.action_excluir_nota).setVisible(mostrarItens);
        menu.findItem(R.id.action_editar_nota).setVisible(mostrarItens);

        return true;
    }

    @Override
    public void onBackPressed() {

        // Verifica se o fragmento que está sendo exibido é diferente do ListarNotasFragment. Se for,
        // exibe o dialogo para cancelar a edição
        if (!getFragmentManager().findFragmentById(R.id.fragment_container).equals(listarNotasFragment)) {
            mostrarDialogoCancelarEdicao();
        } else

        // Verifica se alguma nota está selecionada. Caso esteja, remove o foco da mesma.
        if (notaSelecionada != null) {
            removerSelecaoNota();
        } else

        // Executa o comportamento padrão
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotaSelecionada(Nota nota) {
        // Define a referência para a nota selecionada
        notaSelecionada = nota;

        // Atualiza o menu de contexto
        invalidateOptionsMenu();
    }

    @Override
    public void onEdicaoConcluida(Nota nota, boolean novaNota) {

        // Insere ou atualiza a nota no banco de dados
        if (novaNota ? notasDAO.inserir(nota) : notasDAO.update(nota) > 0) {
            // Retorna para o fragmento anterior
            getFragmentManager().popBackStackImmediate();

            // Mostra o botão de ação flutuante
            fab.show();

            // Atualiza a lista de notas
            atualizarListaNotas();
        } else {
            // Falha ao salvar alterações no banco de dados
            Toast.makeText(this, R.string.erro_salvar_nota, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onEdicaoCancelada() {
        mostrarDialogoCancelarEdicao();
    }

    /**
     * Localiza a view do botão de ação flutuante e define o listener de toque.
     */
    private void prepararFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepara o EditarNotaFragment
                prepararFragmentoEditarNota(true);
            }
        });
    }

    /**
     * Prepara o {@link ListarNotasFragment} para ser exibido nesta activity.
     *
     * @param savedInstanceState informações provenientes do estado anterior da activity. Caso
     *                           diferente de {@code null}, implica na criação  de uma nova instância
     *                           do fragmento.
     */
    private void prepararFragmentoListarNotas(Bundle savedInstanceState) {
        // Verifica se a activity hospeda o contâiner do fragmento
        if (findViewById(R.id.fragment_container) == null) return;

        // Caso a activity esteja sendo restaurada de um estado anterior (rotação de tela, por
        // exemplo), não é necessário recriar o fragmento, senão podemos sobrepor o anterior
        if (savedInstanceState != null) {
            // Obtém uma referência para o fragmento existente
            listarNotasFragment = (ListarNotasFragment) getFragmentManager().findFragmentById(R.id.fragment_container);

            return;
        }

        // Consulta as notas no banco de dados local
        ArrayList<Nota> notas = notasDAO.listar();

        // Cria o fragmento de lista de notas para ser adicionado ao layout
        listarNotasFragment = ListarNotasFragment.newInstance(notas);

        // Adiciona o fragmento ao 'fragment_container' (FrameLayout)
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, listarNotasFragment)
                .addToBackStack(ListarNotasFragment.class.getName())
                .commit();
    }

    private void prepararFragmentoEditarNota(boolean novaNota) {
        // Verifica se a activity hospeda o contâiner do fragmento
        if (findViewById(R.id.fragment_container) == null) return;

        Nota nota = notaSelecionada;

        // Verifica se a nota que será editada é válida
        if (novaNota || notaSelecionada == null) {
            nota = new Nota();
        }

        // Remove a seleção da nota anterior
        removerSelecaoNota();

        // Cria o fragmento de edição de nota para ser adicionado ao layout
        EditarNotaFragment editarNotaFragment = EditarNotaFragment.newInstance(nota, novaNota);

        // Substitui o fragmento
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editarNotaFragment)
                .addToBackStack(EditarNotaFragment.class.getName())
                .commit();

        // Oculta o botão de ação flutuante
        fab.hide();
    }

    /**
     * Consulta a lista de notas no banco de dados e atualiza o adaptador do recycler view.
     */
    private void atualizarListaNotas() {
        // Consulta as notas no banco de dados local
        ArrayList<Nota> notas = notasDAO.listar();

        // Atualiza a lista de notas utilizada pelo adapter
        listarNotasFragment.getAdapter().setNotas(notas);
    }

    /**
     * Exclui a nota que está selecionada no momento.
     */
    private void excluirNotaSelecionada() {
        if (notaSelecionada == null) return;

        if (notasDAO.delete(notaSelecionada) > 0) {
            removerSelecaoNota();
            atualizarListaNotas();
        } else {
            // Mostra mensagem de erro
            Toast.makeText(this, R.string.erro_salvar_nota, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Define como {@code null} o valor da variável {@link #notaSelecionada}; remove o destaque da
     * nota selecionada no {@code RecyclerView}; e atualiza o menu de contexto.
     */
    private void removerSelecaoNota() {
        notaSelecionada = null;                                     // Referência da Activity
        listarNotasFragment.getAdapter().setNotaSelecionada(null);  // Referência do Adapter
        invalidateOptionsMenu();                                    // Atualiza o menu de contexto
    }

    /**
     * Exibe um diálogo questionando se o usuário deseja realmente cancelar a edição. Caso selecione
     * sim, a ação de voltar/cancelar é realizada, mostrando o fragmento {@link ListarNotasFragment}.
     * Caso o usuário deseje continuar editando a nota, nenhuma ação é executada.
     */
    private void mostrarDialogoCancelarEdicao() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.msg_descartar_alteracoes_nota)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retorna para o fragmento anterior
                        getFragmentManager().popBackStack();
                        // Exibe o botão de ação flutuante
                        fab.show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}