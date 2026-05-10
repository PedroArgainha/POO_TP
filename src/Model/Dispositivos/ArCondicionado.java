package Model.Dispositivos;

import Model.Exceptions.TemperaturaInvalidaException;
import Model.Interfaces.Temperavel;

public class ArCondicionado extends Dispositivo implements Temperavel {

    private double temperatura;

    // Construtor por omissão
    public ArCondicionado() {
        super();
        this.temperatura = 22.0;
    }

    // Construtor parametrizado
    public ArCondicionado(String nome, String marca, String modelo, double consumoPorHora,
            double temperatura) throws TemperaturaInvalidaException {
        super(nome, marca, modelo, consumoPorHora);
        setTemperatura(temperatura);
    }

    // Construtor por cópia
    public ArCondicionado(ArCondicionado a) {
        super(a);
        this.temperatura = a.getTemperatura();
    }

    // --- Implementação de Temperavel ---
    @Override
    public double getTemperatura() {
        return this.temperatura;
    }

    @Override
    public void setTemperatura(double temperatura) throws TemperaturaInvalidaException {
        if (temperatura < 16.0 || temperatura > 30.0)
            throw new TemperaturaInvalidaException("Temperatura inválida: " + temperatura + " (válido: 16-30°C)");
        this.temperatura = temperatura;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        ArCondicionado a = (ArCondicionado) o;
        return Double.compare(this.temperatura, a.getTemperatura()) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.temperatura);
    }

    @Override
    public String toString() {
        return "ArCondicionado{" +
                "base=" + super.toString() +
                ", temperatura=" + this.temperatura +
                '}';
    }

    @Override
    public ArCondicionado clone() {
        return new ArCondicionado(this);
    }

    private double fatorConsumoTemperatura() {
        double temperaturaReferencia = 22.0;
        double diferenca = Math.abs(this.temperatura - temperaturaReferencia);

        return 1.0 + (diferenca * 0.05);
    }

    @Override
    public double consumoAtual() {
        if (!isLigado()) {
            return 0.0;
        }

        return getConsumoPorHora() * fatorConsumoTemperatura();
    }

    @Override
    public double consumoTotalDispositivo() {
        return getTempoLigado() * getConsumoPorHora() * fatorConsumoTemperatura() / 60.0;
    }


}
