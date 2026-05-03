package Dispositivos;
import Exceptions.NivelInvalidoException;
import Interfaces.Regulavel;

public class ColunaSom extends Dispositivo implements Regulavel {
    private int volume;

    // Construtor por omissão
    public ColunaSom() {
        super();
        this.volume = 0;
    }

    // Construtor parametrizado
    public ColunaSom(String nome, String marca, String modelo, double consumoPorHora,
                     int volume) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        setVolume(volume);
    }

    // Construtor por cópia
    public ColunaSom(ColunaSom c) {
        super(c);
        this.volume = c.getVolume();
    }

    public int getVolume() {
        return this.volume;
    }

    public void setVolume(int volume) throws NivelInvalidoException {
        if (volume < 0 || volume > 100)
            throw new NivelInvalidoException("Volume inválido: " + volume);
        this.volume = volume;
    }

    // Implementação da interface Regulavel
    @Override
    public int getNivel() {
        return this.volume;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        setVolume(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        ColunaSom c = (ColunaSom) o;
        return this.volume == c.volume;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Integer.hashCode(this.volume);
    }

    @Override
    public String toString() {
        return "ColunaSom{" +
                "base=" + super.toString() +
                ", volume=" + this.volume +
                '}';
    }

    @Override
    public ColunaSom clone() {
        return new ColunaSom(this);
    }

    @Override
    public double consumoAtual() {
        if (!isLigado()) {
            return 0.0;
        }

        return getConsumoPorHora() * (this.volume / 100.0);
    }

    @Override
    public double consumoTotalDispositivo() {
        return getTempoLigado() * getConsumoPorHora() * (this.volume / 100.0) / 60.0;
    }
}