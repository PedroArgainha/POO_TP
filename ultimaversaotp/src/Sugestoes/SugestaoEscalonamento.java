package Sugestoes;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * SugestaoEscalonamento — representa uma sugestão de escalonamento que o
 * sistema gera a partir da análise dos padrões de interação do utilizador.
 *
 * Uma sugestão NÃO é um escalonamento — é apenas uma proposta que o
 * utilizador pode aceitar ou rejeitar. Se aceitar, o sistema cria um
 * Escalonamento real na Casa correspondente.
 *
 * Cada sugestão guarda:
 *   - idCasa: em que casa o escalonamento seria criado
 *   - idDispositivo: que dispositivo é o alvo
 *   - tipoAcao: "LIGAR" ou "DESLIGAR"
 *   - horaSugerida: a hora média a que o user costuma fazer essa ação
 *   - numeroOcorrencias: quantas vezes o padrão foi observado (confiança)
 *
 * Esta classe é um value object: imutável, sem setters.
 */
public class SugestaoEscalonamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String idCasa;
    private final int idDispositivo;
    private final String tipoAcao;
    private final LocalTime horaSugerida;
    private final int numeroOcorrencias;

    // Construtor parametrizado
    public SugestaoEscalonamento(String idCasa, int idDispositivo, String tipoAcao,
                                 LocalTime horaSugerida, int numeroOcorrencias) {
        this.idCasa = idCasa;
        this.idDispositivo = idDispositivo;
        this.tipoAcao = tipoAcao;
        this.horaSugerida = horaSugerida;
        this.numeroOcorrencias = numeroOcorrencias;
    }

    // Construtor de cópia
    public SugestaoEscalonamento(SugestaoEscalonamento s) {
        this.idCasa = s.idCasa;
        this.idDispositivo = s.idDispositivo;
        this.tipoAcao = s.tipoAcao;
        this.horaSugerida = s.horaSugerida;
        this.numeroOcorrencias = s.numeroOcorrencias;
    }

    // Getters
    public String getIdCasa()           { return this.idCasa; }
    public int getIdDispositivo()       { return this.idDispositivo; }
    public String getTipoAcao()         { return this.tipoAcao; }
    public LocalTime getHoraSugerida()  { return this.horaSugerida; }
    public int getNumeroOcorrencias()   { return this.numeroOcorrencias; }

    /**
     * Mensagem legível para mostrar ao utilizador.
     * Ex: "Detetei que costumas LIGAR o dispositivo 5 por volta das 19:00
     *      (5 ocorrências). Queres criar um escalonamento na casa C2?"
     */
    public String mensagemUtilizador() {
        return "Detetei que costumas " + this.tipoAcao +
                " o dispositivo " + this.idDispositivo +
                " por volta das " + this.horaSugerida +
                " (" + this.numeroOcorrencias + " ocorrências) na casa " + this.idCasa + ".";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SugestaoEscalonamento s = (SugestaoEscalonamento) o;
        return this.idDispositivo == s.idDispositivo &&
                this.numeroOcorrencias == s.numeroOcorrencias &&
                Objects.equals(this.idCasa, s.idCasa) &&
                Objects.equals(this.tipoAcao, s.tipoAcao) &&
                Objects.equals(this.horaSugerida, s.horaSugerida);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCasa, this.idDispositivo, this.tipoAcao,
                this.horaSugerida, this.numeroOcorrencias);
    }

    @Override
    public String toString() {
        return "SugestaoEscalonamento{" +
                "idCasa='" + this.idCasa + '\'' +
                ", idDispositivo=" + this.idDispositivo +
                ", tipoAcao='" + this.tipoAcao + '\'' +
                ", hora=" + this.horaSugerida +
                ", ocorrencias=" + this.numeroOcorrencias +
                '}';
    }

    @Override
    public SugestaoEscalonamento clone() {
        return new SugestaoEscalonamento(this);
    }
}