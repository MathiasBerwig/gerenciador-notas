package io.github.mathiasberwig.gerenciador_notas.presentation.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mathiasberwig.gerenciador_notas.R;
import io.github.mathiasberwig.gerenciador_notas.data.model.Nota;
import io.github.mathiasberwig.gerenciador_notas.presentation.fragment.ListarNotasFragment;

public class NotasRecyclerViewAdapter extends RecyclerView.Adapter<NotasRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = NotasRecyclerViewAdapter.class.getName();

    /**
     * Lista de notas que serão exibidas.
     */
    private ArrayList<Nota> notas;

    /**
     * Listener para callback de eventos de interação.
     */
    private final ListarNotasFragment.OnNotaSelecionadaListener notaSelecionadaListener;

    /**
     * Armazena a {@link Nota} selecionada, caso exista.
     */
    private Nota notaSelecionada;

    public NotasRecyclerViewAdapter(ArrayList<Nota> items, ListarNotasFragment.OnNotaSelecionadaListener listener) {
        notas = items == null ? new ArrayList<Nota>() : items;
        notaSelecionadaListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nota, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Define a nota e demais informações vinculadas à view
        holder.nota = notas.get(position);
        holder.tituloView.setText(notas.get(position).getTitulo());
        holder.conteudoView.setText(notas.get(position).getConteudo());

        // Define o listener para toques longos na view
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (notaSelecionadaListener != null) {
                    // Define a nota como selecionada
                    setNotaSelecionada(holder.nota);

                    // Notifica a activity que um item foi selecionado
                    notaSelecionadaListener.onNotaSelecionada(holder.nota);

                    return true;
                }
                return false;
            }
        });

        // Define o lister para toques na view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se a nota clicada é a selecionada. Caso não seja, remove a seleção
                if (!holder.nota.equals(notaSelecionada)) {
                    setNotaSelecionada(null);
                    notaSelecionadaListener.onNotaSelecionada(null);
                }
            }
        });

        // Define a cor de background do card view de acordo com a nota selecionada
        boolean selecionada = notas.get(position).equals(notaSelecionada);
        holder.cardView.setCardBackgroundColor(selecionada ? Color.LTGRAY : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    /**
     * Marca como selecionada a nota informada.
     *
     * @param nota A nota que deve receber destaque na lista, ou {@code null} para remover a seleção.
     */
    public void setNotaSelecionada(Nota nota) {
        if (notas.contains(nota) || nota == null) {
            notaSelecionada = nota;
            notifyDataSetChanged();
        } else {
            Log.i(TAG, "setNotaSelecionada: A nota informada não pertence à lista de notas.");
        }
    }

    /**
     * Retorna a nota selecionada.
     *
     * @return A nota selecionada ou {@code null}.
     */
    public Nota getNotaSelecionada() {
        return notaSelecionada;
    }

    /**
     * Obtém a lista de notas que está sendo exibida.
     *
     * @return {@link #notas}
     */
    public ArrayList<Nota> getNotas() {
        return notas;
    }

    /**
     * Substitui a lista de notas que está sendo exibida.
     *
     * @param notas lista de {@link Nota Notas} para substituir a variável {@link #notas}.
     */
    public void setNotas(ArrayList<Nota> notas) {
        this.notas = notas;
        // Notifica que as notas foram alteradas, solicitando ao adaptador que atualize as
        // informações na tela (onBindViewHolder)
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tituloView;
        public final TextView conteudoView;
        public final CardView cardView;
        public Nota nota;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tituloView = (TextView) view.findViewById(R.id.txt_nota_titulo);
            conteudoView = (TextView) view.findViewById(R.id.txt_nota_conteudo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nota + "'";
        }
    }
}
