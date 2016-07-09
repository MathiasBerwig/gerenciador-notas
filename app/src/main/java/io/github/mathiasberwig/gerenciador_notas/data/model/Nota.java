package io.github.mathiasberwig.gerenciador_notas.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representa uma nota de texto.
 *
 * Created by mathias.berwig on 02/05/2016.
 */
public class Nota implements Parcelable {

    private Long id;
    private String titulo;
    private String conteudo;

    public Nota() {
        // Valores padrão adicionados para prevenir exceções NullPointerException
        this.id = 0L;
        this.titulo = "";
        this.conteudo = "";
    }

    public Nota(Long id, String titulo, String conteudo) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    protected Nota(Parcel in) {
        id = in.readLong();
        titulo = in.readString();
        conteudo = in.readString();
    }

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel in) {
            return new Nota(in);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nota nota = (Nota) o;

        return (id != null ? id.equals(nota.id) : nota.id == null)
                && (titulo != null ? titulo.equals(nota.titulo) : nota.titulo == null)
                && (conteudo != null ? conteudo.equals(nota.conteudo) : nota.conteudo == null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (titulo != null ? titulo.hashCode() : 0);
        result = 31 * result + (conteudo != null ? conteudo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", conteudo='" + conteudo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeString(conteudo);
    }
}