package Model.Dispositivos;

import Model.Exceptions.NivelInvalidoException;
import Model.Interfaces.Abrivel;
import Model.Interfaces.Regulavel;

// Persiana — semelhante à Cortina: abre/fecha totalmente ou parcialmente.
public class Persiana extends Dispositivo implements Abrivel, Regulavel {

    private int percentagemAbertura; // 0 = fechada, 100 = totalmente aberta

    // Construtor por omissão
    public Persiana() {
        super();
        this.percentagemAbertura = 0;
    }

    // Construtor parametrizado
    public Persiana(String nome, String marca, String modelo, double consumoPorHora,
                    int percentagemAbertura) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        setNivel(percentagemAbertura);
    }

    // Construtor por cópia
    public Persiana(Persiana p) {
        super(p);
        this.percentagemAbertura = p.getPercentagemAbertura();
    }

    // --- Getter próprio ---
    public int getPercentagemAbertura() {
        return this.percentagemAbertura;
    }

    // --- Implementação de Regulavel ---
    @Override
    public int getNivel() {
        return this.percentagemAbertura;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        if (valor < 0 || valor > 100)
            throw new NivelInvalidoException("Percentagem de abertura inválida: " + valor);
        this.percentagemAbertura = valor;
    }

    // --- Implementação de Abrivel ---
    @Override
    public boolean isAberto() {
        return this.percentagemAbertura == 100;
    }

    @Override
    public void abrir() {
        this.percentagemAbertura = 100;
    }

    @Override
    public void fechar() {
        this.percentagemAbertura = 0;
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
        Persiana p = (Persiana) o;
        return this.percentagemAbertura == p.percentagemAbertura;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Integer.hashCode(this.percentagemAbertura);
    }

    @Override
    public String toString() {
        return "Persiana{" +
                "base=" + super.toString() +
                ", percentagemAbertura=" + this.percentagemAbertura +
                '%' + '}';
    }

    @Override
    public Persiana clone() {
        return new Persiana(this);
    }
}
