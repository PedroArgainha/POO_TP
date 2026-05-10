package Model.Dispositivos;

import Model.Exceptions.NivelInvalidoException;
import Model.Interfaces.Regulavel;

// Exaustor — permite regular a velocidade de extração.
public class Exaustor extends Dispositivo implements Regulavel {

    private int velocidade; // 0–100 (percentagem da potência máxima)

    // Construtor por omissão
    public Exaustor() {
        super();
        this.velocidade = 0;
    }

    // Construtor parametrizado
    public Exaustor(String nome, String marca, String modelo, double consumoPorHora,
                    int velocidade) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        setNivel(velocidade);
    }

    // Construtor por cópia
    public Exaustor(Exaustor e) {
        super(e);
        this.velocidade = e.getVelocidade();
    }

    // --- Getter próprio ---
    public int getVelocidade() {
        return this.velocidade;
    }

    // --- Implementação de Regulavel ---
    @Override
    public int getNivel() {
        return this.velocidade;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        if (valor < 0 || valor > 100)
            throw new NivelInvalidoException("Velocidade inválida: " + valor);
        this.velocidade = valor;
    }

    @Override
    public double consumoAtual(){
        if (!isLigado()) return 0.0;
        return getConsumoPorHora() * (this.velocidade / 100.0);
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() * (this.velocidade / 100.0) / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Exaustor e = (Exaustor) o;
        return this.velocidade == e.velocidade;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Integer.hashCode(this.velocidade);
    }

    @Override
    public String toString() {
        return "Exaustor{" +
                "base=" + super.toString() +
                ", velocidade=" + this.velocidade +
                '%' + '}';
    }

    @Override
    public Exaustor clone() {
        return new Exaustor(this);
    }
}
