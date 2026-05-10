package Model.Dispositivos;

import Model.Exceptions.TemperaturaInvalidaException;
import Model.Interfaces.Monitoravel;
import Model.Interfaces.Temperavel;

// Termostato — define uma temperatura alvo e monitoriza a temperatura atual.
public class Termostato extends Dispositivo implements Temperavel, Monitoravel {

    private double temperaturaAlvo;   // temperatura desejada
    private double temperaturaAtual;  // leitura do sensor

    private static final double TEMP_MIN = 5.0;
    private static final double TEMP_MAX = 35.0;

    // Construtor por omissão
    public Termostato() {
        super();
        this.temperaturaAlvo = 22.0;
        this.temperaturaAtual = 20.0;
    }

    // Construtor parametrizado
    public Termostato(String nome, String marca, String modelo, double consumoPorHora,
                      double temperaturaAlvo) throws TemperaturaInvalidaException {
        super(nome, marca, modelo, consumoPorHora);
        setTemperatura(temperaturaAlvo);
        this.temperaturaAtual = 20.0;
    }

    // Construtor por cópia
    public Termostato(Termostato t) {
        super(t);
        this.temperaturaAlvo = t.getTemperatura();
        this.temperaturaAtual = t.getValorAtual();
    }

    // --- Implementação de Temperavel ---
    @Override
    public double getTemperatura() {
        return this.temperaturaAlvo;
    }

    @Override
    public void setTemperatura(double temperatura) throws TemperaturaInvalidaException {
        if (temperatura < 5.0 || temperatura > 35.0)
            throw new TemperaturaInvalidaException("Temperatura alvo inválida: " + temperatura + " (válido: 5-35°C)");
        this.temperaturaAlvo = temperatura;
    }

    // --- Implementação de Monitoravel ---
    @Override
    public double getValorAtual() {
        return this.temperaturaAtual;
    }

    @Override
    public String getUnidade() {
        return "°C";
    }

    @Override
    public void simularLeitura(double valor) {
        this.temperaturaAtual = valor;
    }

    // O termostato consome mais quando a diferença entre a temperatura atual e a temperatura alvo é maior
    // Se já está na temperatura alvo -> consumo mínimo (10% para manter)
    // Máxima diferença possível (30ºC) -> consumo 100%
    private double fatorConsumoTemperatura(){
        double diferenca = Math.abs(this.temperaturaAlvo - this.temperaturaAtual);
        double diferencaMaxima = TEMP_MAX - TEMP_MIN;
        return 0.1 + 0.9 * Math.min(diferenca / diferencaMaxima, 1.0);
    }

    @Override
    public double consumoAtual(){
        if (!isLigado()) return 0.0;
        return getConsumoPorHora() * fatorConsumoTemperatura();
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() * fatorConsumoTemperatura() / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Termostato t = (Termostato) o;
        return Double.compare(this.temperaturaAlvo, t.temperaturaAlvo) == 0 &&
                Double.compare(this.temperaturaAtual, t.temperaturaAtual) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode(this.temperaturaAlvo);
        result = 31 * result + Double.hashCode(this.temperaturaAtual);
        return result;
    }

    @Override
    public String toString() {
        return "Termostato{" +
                "base=" + super.toString() +
                ", temperaturaAlvo=" + this.temperaturaAlvo + "°C" +
                ", temperaturaAtual=" + this.temperaturaAtual + "°C" +
                '}';
    }

    @Override
    public Termostato clone() {
        return new Termostato(this);
    }
}
