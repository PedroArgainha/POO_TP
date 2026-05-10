package Sugestoes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * RegistoInteracao — representa uma interação passada do utilizador.
 *
 * Cada vez que um utilizador faz uma operação relevante sobre um dispositivo
 * (ligar, desligar, etc.), o DomusControl regista essa interação para mais
 * tarde poder sugerir automações/escalonamentos com base nos padrões.
 *
 * Esta classe é um value object: imutável, sem setters. Uma interação passada
 * não pode ser alterada — aconteceu naquele instante, é histórica.
 *
 * Os campos guardados são:
 *   - tempo: quando aconteceu (no relógio simulado)
 *   - idCasa: em que casa
 *   - idDispositivo: que dispositivo foi afetado
 *   - tipoAcao: "LIGAR" ou "DESLIGAR" (para já)
 */
public class RegistoInteracao implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TIPO_LIGAR = "LIGAR";
    public static final String TIPO_DESLIGAR = "DESLIGAR";

    private final LocalDateTime tempo;
    private final String idCasa;
    private final int idDispositivo;
    private final String tipoAcao;

    // Construtor parametrizado
    public RegistoInteracao(LocalDateTime tempo, String idCasa, int idDispositivo, String tipoAcao) {
        this.tempo = tempo;
        this.idCasa = idCasa;
        this.idDispositivo = idDispositivo;
        this.tipoAcao = tipoAcao;
    }

    // Construtor de cópia
    public RegistoInteracao(RegistoInteracao r) {
        this.tempo = r.tempo;
        this.idCasa = r.idCasa;
        this.idDispositivo = r.idDispositivo;
        this.tipoAcao = r.tipoAcao;
    }

    // Getters (não há setters porque é imutável)
    public LocalDateTime getTempo()    { return this.tempo; }
    public String getIdCasa()          { return this.idCasa; }
    public int getIdDispositivo()      { return this.idDispositivo; }
    public String getTipoAcao()        { return this.tipoAcao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistoInteracao r = (RegistoInteracao) o;
        return this.idDispositivo == r.idDispositivo &&
                Objects.equals(this.tempo, r.tempo) &&
                Objects.equals(this.idCasa, r.idCasa) &&
                Objects.equals(this.tipoAcao, r.tipoAcao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tempo, this.idCasa, this.idDispositivo, this.tipoAcao);
    }

    @Override
    public String toString() {
        return "RegistoInteracao{" +
                "tempo=" + this.tempo +
                ", idCasa='" + this.idCasa + '\'' +
                ", idDispositivo=" + this.idDispositivo +
                ", tipoAcao='" + this.tipoAcao + '\'' +
                '}';
    }

    @Override
    public RegistoInteracao clone() {
        return new RegistoInteracao(this);
    }
}