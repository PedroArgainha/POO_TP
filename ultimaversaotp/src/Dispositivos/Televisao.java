package Dispositivos;

import Exceptions.NivelInvalidoException;
import Interfaces.Regulavel;

// Televisão — permite regular o volume e mudar de canal.
public class Televisao extends Dispositivo implements Regulavel {

    private int volume;     // 0–100
    private int canal;      // >= 1

    // Construtor por omissão
    public Televisao() {
        super();
        this.volume = 0;
        this.canal = 1;
    }

    // Construtor parametrizado
    public Televisao(String nome, String marca, String modelo, double consumoPorHora,
                     int volume, int canal) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        setVolume(volume);
        setCanal(canal);
    }

    // Construtor por cópia
    public Televisao(Televisao t) {
        super(t);
        this.volume = t.getVolume();
        this.canal = t.getCanal();
    }

    // --- Getters ---
    public int getVolume() {
        return this.volume;
    }

    public int getCanal() {
        return this.canal;
    }

    // --- Setters ---
    public void setVolume(int volume) throws NivelInvalidoException {
        if (volume < 0 || volume > 100)
            throw new NivelInvalidoException("Volume inválido: " + volume);
        this.volume = volume;
    }

    public void setCanal(int canal) throws NivelInvalidoException {
        if (canal < 1)
            throw new NivelInvalidoException("Canal inválido: " + canal);
        this.canal = canal;
    }

    // --- Implementação de Regulavel (regula o volume) ---
    @Override
    public int getNivel() {
        return this.volume;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        setVolume(valor);
    }

    @Override
    public double consumoAtual(){
        return isLigado() ? getConsumoPorHora() : 0.0;
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Televisao t = (Televisao) o;
        return this.volume == t.volume && this.canal == t.canal;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Integer.hashCode(this.volume);
        result = 31 * result + Integer.hashCode(this.canal);
        return result;
    }

    @Override
    public String toString() {
        return "Televisao{" +
                "base=" + super.toString() +
                ", volume=" + this.volume +
                ", canal=" + this.canal +
                '}';
    }

    @Override
    public Televisao clone() {
        return new Televisao(this);
    }
}
