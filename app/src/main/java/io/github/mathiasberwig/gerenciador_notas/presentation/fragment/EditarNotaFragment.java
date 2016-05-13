package io.github.mathiasberwig.gerenciador_notas.presentation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.github.mathiasberwig.gerenciador_notas.R;
import io.github.mathiasberwig.gerenciador_notas.data.model.Nota;

/**
 * Fragmento representando uma {@link Nota} editável.
 * <p/>
 * Activities contendo este fragmento devem implementar a interface {@link OnEdicaoConcluidaListener}.
 */
public class EditarNotaFragment extends Fragment {
    private static final String TAG = EditarNotaFragment.class.getName();

    public static final String EXTRA_NOTA = "nota";
    public static final String EXTRA_NOVA_NOTA = "novaNota";

    // Referências de componentes gráficos
    private EditText edtTituloNota;
    private EditText edtConteudoNota;

    /**
     * Referência para a nota que está sendo editada no momento.
     */
    private Nota nota;

    /**
     * Indica se a {@link #nota} é nova (ainda não está no banco de dados local).
     */
    private boolean novaNota;

    /**
     * Listener responsável por receber notificações de interações com a nota que está sendo editada.
     */
    private OnEdicaoConcluidaListener edicaoConcluidaListener;

    /**
     * Construtor vazio mandatório para o gerenciador de fragmentos instancia-lo (em mudanças de
     * orientação da tela, por exemplo).
     */
    public EditarNotaFragment() {
    }

    /**
     * Construtor auxiliar esta classe.
     *
     * @param nota a nota que será editada.
     * @param novaNota {@code true} caso a nota deva ser inserida no banco de dados após o salvamento.
     *                 {@code false} para realizar a operação de atualização.
     * @return Instância criada conforme os parâmetros solicitados.
     */
    public static EditarNotaFragment newInstance(Nota nota, boolean novaNota) {
        EditarNotaFragment fragment = new EditarNotaFragment();

        // Define os parâmetros de inicialização
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_NOTA, nota);
        args.putBoolean(EXTRA_NOVA_NOTA, novaNota);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_nota, container, false);

        // Obtém os parâmetros de criação do fragmento ou da instância anterior
        final Bundle options = savedInstanceState == null ? getArguments() : savedInstanceState;

        // Obtém a nota informada na criação da instância
        nota = options.getParcelable(EXTRA_NOTA);
        novaNota = options.getBoolean(EXTRA_NOVA_NOTA);

        // Localiza os EditText
        edtTituloNota = (EditText) view.findViewById(R.id.txt_nota_titulo);
        edtConteudoNota = (EditText) view.findViewById(R.id.txt_nota_conteudo);

        // Atualiza as informações da nota na IU
        updateViews();

        // Localiza as views de botões e define os respectivos listeners
        configurarBotoes(view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_NOTA, nota);
        outState.putBoolean(EXTRA_NOVA_NOTA, novaNota);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            edicaoConcluidaListener = (OnEdicaoConcluidaListener) activity;
        } catch (ClassCastException ex) {
            throw new RuntimeException(activity.toString() + " deve implementar a interface OnEdicaoConcluidaListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            edicaoConcluidaListener = (OnEdicaoConcluidaListener) context;
        } catch (ClassCastException ex) {
            throw new RuntimeException(context.toString() + " deve implementar a interface OnEdicaoConcluidaListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        edicaoConcluidaListener = null;
    }

    /**
     * Atualiza as informações do {@link R.id#txt_nota_titulo} e {@link R.id#txt_nota_conteudo} a
     * partir da variável {@link #nota}.
     */
    public void updateViews() {
        if (nota == null) return;
        edtTituloNota.setText(nota.getTitulo());
        edtConteudoNota.setText(nota.getConteudo());
    }

    /**
     * Atualiza as informações da {@link #nota} a partir da interface gráfica.
     */
    public void updateNota() {
        if (nota == null) return;
        nota.setTitulo(edtTituloNota.getText().toString());
        nota.setConteudo(edtConteudoNota.getText().toString());
    }

    /**
     * Encontra as views de botões no fragmento e define os eventos {@code onClick} para os botões
     * {@link R.id#btn_salvar} e {@link R.id#btn_cancelar_edicao}.
     *
     * @param view A view que contém os botões em questão.
     */
    private void configurarBotoes(View view) {
        Button btnSalvar = (Button) view.findViewById(R.id.btn_salvar);
        Button btnCancelar = (Button) view.findViewById(R.id.btn_cancelar_edicao);

        if (btnSalvar == null || btnCancelar == null) return;

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNota();
                edicaoConcluidaListener.onEdicaoConcluida(nota, novaNota);
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edicaoConcluidaListener.onEdicaoCancelada();
            }
        });
    }


    /**
     * Esta interface deve ser implementada por activities que contém este fragmento, de modo a
     * permitir que a activity seja notificada quando este fragmento conclui a edição de uma {@link Nota}.
     * <p/>
     * Veja o treinamento no Android Developers sobre <a href="http://developer.android.com/training/basics/fragments/communicating.html">
     * Comunicação com outros fragmentos</a> para mais informações.
     */
    public interface OnEdicaoConcluidaListener {
        /**
         * Executado quando uma {@link Nota} é alterada.
         *
         * @param nota a nota com as modificações realizadas.
         * @param novaNota {@code true } indica que a {@code nota} não está armazenada no banco de
         *                 dados local.
         */
        void onEdicaoConcluida(Nota nota, boolean novaNota);

        /**
         * Executado quando a edição de uma nota é cancelada.
         */
        void onEdicaoCancelada();
    }
}
