package Model.Dispositivos;

import Model.Interfaces.Monitoravel;

// SensorTemperatura — mede a temperatura ambiente em °C.
public class SensorTemperatura extends Dispositivo implements Monitoravel {

    private double temperaturaAmbiente; // °C

    // Construtor por omissão
    public SensorTemperatura() {
        super();
        this.temperaturaAmbiente = 20.0;
    }

    // Construtor parametrizado
    public SensorTemperatura(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
        this.temperaturaAmbiente = 20.0;
    }

    // Construtor por cópia
    public SensorTemperatura(SensorTemperatura s) {
        super(s);
        this.temperaturaAmbiente = s.getValorAtual();
    }

    // --- Implementação de Monitoravel ---
    @Override
    public double getValorAtual() {
        return this.temperaturaAmbiente;
    }

    @Override
    public String getUnidade() {
        return "°C";
    }

    @Override
    public void simularLeitura(double valor) {
        this.temperaturaAmbiente = valor;
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
        SensorTemperatura s = (SensorTemperatura) o;
        return Double.compare(this.temperaturaAmbiente, s.temperaturaAmbiente) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.temperaturaAmbiente);
    }

    @Override
    public String toString() {
        return "SensorTemperatura{" +
                "base=" + super.toString() +
                ", temperaturaAmbiente=" + this.temperaturaAmbiente + "°C" +
                '}';
    }

    @Override
    public SensorTemperatura clone() {
        return new SensorTemperatura(this);
    }
}
