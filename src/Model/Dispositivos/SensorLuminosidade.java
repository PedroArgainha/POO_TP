package Model.Dispositivos;

import Model.Interfaces.Monitoravel;

// SensorLuminosidade — mede a luminosidade ambiente em lux.
// Referido no enunciado como fonte de dados para automações.
public class SensorLuminosidade extends Dispositivo implements Monitoravel {

    private double luminosidade; // lux

    // Construtor por omissão
    public SensorLuminosidade() {
        super();
        this.luminosidade = 0.0;
    }

    // Construtor parametrizado
    public SensorLuminosidade(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
        this.luminosidade = 0.0;
    }

    // Construtor por cópia
    public SensorLuminosidade(SensorLuminosidade s) {
        super(s);
        this.luminosidade = s.getValorAtual();
    }

    // --- Implementação de Monitoravel ---
    @Override
    public double getValorAtual() {
        return this.luminosidade;
    }

    @Override
    public String getUnidade() {
        return "lux";
    }

    @Override
    public void simularLeitura(double valor) {
        this.luminosidade = Math.max(0.0, valor);
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
        SensorLuminosidade s = (SensorLuminosidade) o;
        return Double.compare(this.luminosidade, s.luminosidade) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.luminosidade);
    }

    @Override
    public String toString() {
        return "SensorLuminosidade{" +
                "base=" + super.toString() +
                ", luminosidade=" + this.luminosidade + " lux" +
                '}';
    }

    @Override
    public SensorLuminosidade clone() {
        return new SensorLuminosidade(this);
    }
}
