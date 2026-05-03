package Dispositivos;

import Exceptions.NivelInvalidoException;
import Interfaces.Regulavel;

// SistemaRega — permite regular o caudal de rega em percentagem.
public class SistemaRega extends Dispositivo implements Regulavel {

    private int caudal; // 0–100 (percentagem)

    // Construtor por omissão
    public SistemaRega() {
        super();
        this.caudal = 0;
    }

    // Construtor parametrizado
    public SistemaRega(String nome, String marca, String modelo, double consumoPorHora,
                       int caudal) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        setNivel(caudal);
    }

    // Construtor por cópia
    public SistemaRega(SistemaRega s) {
        super(s);
        this.caudal = s.getCaudal();
    }

    // --- Getter próprio ---
    public int getCaudal() {
        return this.caudal;
    }

    // --- Implementação de Regulavel ---
    @Override
    public int getNivel() {
        return this.caudal;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        if (valor < 0 || valor > 100)
            throw new NivelInvalidoException("Caudal inválido: " + valor);
        this.caudal = valor;
    }

    @Override
    public double consumoAtual(){
        if (!isLigado()) return 0.0;
        return getConsumoPorHora() * (this.caudal / 100.0);
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() * (this.caudal / 100.0) / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        SistemaRega s = (SistemaRega) o;
        return this.caudal == s.caudal;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Integer.hashCode(this.caudal);
    }

    @Override
    public String toString() {
        return "SistemaRega{" +
                "base=" + super.toString() +
                ", caudal=" + this.caudal + "%" +
                '}';
    }

    @Override
    public SistemaRega clone() {
        return new SistemaRega(this);
    }
}
