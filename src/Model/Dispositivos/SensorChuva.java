package Model.Dispositivos;

import Model.Interfaces.Monitoravel;

// SensorChuva — mede a pluviosidade em mm/h.
// Referido no enunciado como fonte de dados para automações.
public class SensorChuva extends Dispositivo implements Monitoravel {

    private double pluviosidade; // mm/h

    // Construtor por omissão
    public SensorChuva() {
        super();
        this.pluviosidade = 0.0;
    }

    // Construtor parametrizado
    public SensorChuva(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
        this.pluviosidade = 0.0;
    }

    // Construtor por cópia
    public SensorChuva(SensorChuva s) {
        super(s);
        this.pluviosidade = s.getValorAtual();
    }

    // --- Implementação de Monitoravel ---
    @Override
    public double getValorAtual() {
        return this.pluviosidade;
    }

    @Override
    public String getUnidade() {
        return "mm/h";
    }

    @Override
    public void simularLeitura(double valor) {
        this.pluviosidade = Math.max(0.0, valor);
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
        SensorChuva s = (SensorChuva) o;
        return Double.compare(this.pluviosidade, s.pluviosidade) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.pluviosidade);
    }

    @Override
    public String toString() {
        return "SensorChuva{" +
                "base=" + super.toString() +
                ", pluviosidade=" + this.pluviosidade + " mm/h" +
                '}';
    }

    @Override
    public SensorChuva clone() {
        return new SensorChuva(this);
    }
}
