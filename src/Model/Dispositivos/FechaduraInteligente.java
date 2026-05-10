package Model.Dispositivos;

import Model.Interfaces.Bloqueavel;

// FechaduraInteligente — permite bloquear/desbloquear uma fechadura.
public class FechaduraInteligente extends Dispositivo implements Bloqueavel {

    private boolean bloqueada;

    // Construtor por omissão
    public FechaduraInteligente() {
        super();
        this.bloqueada = true;
    }

    // Construtor parametrizado
    public FechaduraInteligente(String nome, String marca, String modelo, double consumoPorHora,
                                boolean bloqueada) {
        super(nome, marca, modelo, consumoPorHora);
        this.bloqueada = bloqueada;
    }

    // Construtor por cópia
    public FechaduraInteligente(FechaduraInteligente f) {
        super(f);
        this.bloqueada = f.isBloqueado();
    }

    // --- Implementação de Bloqueavel ---
    public boolean isBloqueado() {
        return this.bloqueada;
    }

    public void bloquear() {
        this.bloqueada = true;
    }

    public void desbloquear() {
        this.bloqueada = false;
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
        FechaduraInteligente f = (FechaduraInteligente) o;
        return this.bloqueada == f.bloqueada;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Boolean.hashCode(this.bloqueada);
    }

    @Override
    public String toString() {
        return "FechaduraInteligente{" +
                "base=" + super.toString() +
                ", bloqueada=" + this.bloqueada +
                '}';
    }

    @Override
    public FechaduraInteligente clone() {
        return new FechaduraInteligente(this);
    }
}
