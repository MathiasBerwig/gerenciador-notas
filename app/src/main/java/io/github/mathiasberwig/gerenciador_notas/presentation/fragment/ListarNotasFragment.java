package io.github.mathiasberwig.gerenciador_notas.presentation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.mathiasberwig.gerenciador_notas.R;
import io.github.mathiasberwig.gerenciador_notas.data.model.Nota;
import io.github.mathiasberwig.gerenciador_notas.presentation.adapter.NotasRecyclerViewAdapter;

/**
 * Fragmento representando uma lista de {@link Nota notas}.
 * <p/>
 * Activities contendo este fragmento devem implementar a interface {@link OnNotaSelecionadaListener}.
 */
public class ListarNotasFragment extends Fragment {
    private static final String TAG = ListarNotasFragment.class.getName();

    public static final String EXTRA_LISTA_NOTAS = "notas";
    public static final String EXTRA_NOTA_SELECIONADA = "notaSelecionada";

    /**
     * Listener responsável por receber notificações de interações (onLongClick) com os itens da
     * lista.
     */
    private OnNotaSelecionadaListener interactionListener;

    /**
     * Adaptador de lista de notas utilizado com o RecyclerView.
     */
    private NotasRecyclerViewAdapter adapter;

    /**
     * Construtor vazio mandatório para o gerenciador de fragmentos instancia-lo (em mudanças de
     * orientação da tela, por exemplo).
     */
    public ListarNotasFragment() {
    }

    /**
     * Construtor auxiliar para esta classe.
     *
     * @param notas a lista de notas que deve ser exibida pelo fragmento.
     * @return Instância criada conforme os parâmetros solicitados.
     */
    public static ListarNotasFragment newInstance(ArrayList<Nota> notas) {
        ListarNotasFragment fragment = new ListarNotasFragment();

        // Define os parâmetros de inicialização
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_LISTA_NOTAS, notas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_notas, container, false);

        if (view instanceof RecyclerView) {
            // Cria a RecyclerView e define o gerenciador de layout da mesma
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            // Obtém os parâmetros de criação do fragmento ou da instância anterior
            final Bundle options = savedInstanceState == null ? getArguments() : savedInstanceState;
            ArrayList<Nota> notas = options.getParcelableArrayList(EXTRA_LISTA_NOTAS);
            Nota notaSelecionada = options.getParcelable(EXTRA_NOTA_SELECIONADA);

            // Cria  e define o adaptador
            adapter = new NotasRecyclerViewAdapter(notas, interactionListener);
            recyclerView.setAdapter(adapter);

            // Localiza o FAB e define o listener de scroll para oculta-lo durante a navegação
            final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                    if (dy > 1)
                        // Scroll vertical para baixo. Oculta o FAB
                        fab.hide();
                    else if (dy < 0)
                        // Scroll vertical para cima. Mostra o FAB
                        fab.show();
                }
            });

            // Define a nota selecionada previamente, caso exista
            if (notaSelecionada != null) {
                adapter.setNotaSelecionada(notaSelecionada);

                // Força a atualização do menu de contexto na Activity
                interactionListener.onNotaSelecionada(notaSelecionada);
            }
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            interactionListener = (OnNotaSelecionadaListener) activity;
        } catch (ClassCastException ex) {
            throw new RuntimeException(activity.toString() + " deve implementar a interface OnNotaSelecionadaListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            interactionListener = (OnNotaSelecionadaListener) context;
        } catch (ClassCastException ex) {
            throw new RuntimeException(context.toString() + " deve implementar a interface OnNotaSelecionadaListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Salva a lista de notas atual e a nota selecionada
        outState.putParcelableArrayList(EXTRA_LISTA_NOTAS, adapter.getNotas());
        outState.putParcelable(EXTRA_NOTA_SELECIONADA, adapter.getNotaSelecionada());
    }

    /**
     * Obtém o adaptador utilizado na RecyclerView deste fragmento.
     *
     * @return {@link #adapter}
     */
    public NotasRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    /**
     * Esta interface deve ser implementada por activities que contém este fragmento, de modo a
     * permitir que a activity seja notificada quando uma {@link Nota} foi selecionada (toque longo).
     * <p/>
     * Veja o treinamento no Android Developers sobre <a href="http://developer.android.com/training/basics/fragments/communicating.html">
     * Comunicação com outros fragmentos</a> para mais informações.
     */
    public interface OnNotaSelecionadaListener {
        /**
         * Executado quando uma {@link Nota} é selecionada por meio de um
         * {@link android.view.View.OnLongClickListener toque longo}.
         *
         * @param nota a nota que foi selecionada pelo usuário.
         */
        void onNotaSelecionada(Nota nota);
    }
}
