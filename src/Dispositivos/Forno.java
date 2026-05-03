package Dispositivos;

import Exceptions.TemperaturaInvalidaException;
import Interfaces.Temperavel;

// Forno — permite definir a temperatura de cozedura.
public class Forno extends Dispositivo implements Temperavel {

    private double temperatura;
    
    private static final double TEMP_MIN = 50.0; 
    private static final double TEMP_MAX = 300.0; // 50–300°C

    // Construtor por omissão
    public Forno() {
        super();
        this.temperatura = 180.0;
    }

    // Construtor parametrizado
    public Forno(String nome, String marca, String modelo, double consumoPorHora,
                 double temperatura) throws TemperaturaInvalidaException {
        super(nome, marca, modelo, consumoPorHora);
        setTemperatura(temperatura);
    }

    // Construtor por cópia
    public Forno(Forno f) {
        super(f);
        this.temperatura = f.getTemperatura();
    }

    // --- Implementação de Temperavel ---
    @Override
    public double getTemperatura() {
        return this.temperatura;
    }

    @Override
    public void setTemperatura(double temperatura) throws TemperaturaInvalidaException {
        if (temperatura < 50.0 || temperatura > 300.0)
            throw new TemperaturaInvalidaException("Temperatura inválida: " + temperatura + " (válido: 50-300°C)");
        this.temperatura = temperatura;
    }

    // A 50ºC (mínimo) trabalha a 20%; a 300ªc (máx) trabalha a 100%
    // Formula: fator = 0.2 + 0.8 x (temp-50) / (300-50)
    private double fatorConsumoTemperatura(){
        return 0.2 + 0.8 * (this.temperatura - TEMP_MIN) / (TEMP_MAX - TEMP_MIN);
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
        Forno f = (Forno) o;
        return Double.compare(this.temperatura, f.temperatura) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.temperatura);
    }

    @Override
    public String toString() {
        return "Forno{" +
                "base=" + super.toString() +
                ", temperatura=" + this.temperatura + "°C" +
                '}';
    }

    @Override
    public Forno clone() {
        return new Forno(this);
    }
}
