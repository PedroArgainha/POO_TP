package Model.Dispositivos;

import Model.Exceptions.TemperaturaInvalidaException;
import Model.Interfaces.Temperavel;

// Frigorifico — permite definir a temperatura de refrigeração.
public class Frigorifico extends Dispositivo implements Temperavel {

    private double temperatura; // -20 a 8°C

    private static final double TEMP_MIN = -20.0;
    private static final double TEMP_MAX = 8.0;

    // Construtor por omissão
    public Frigorifico() {
        super();
        this.temperatura = 4.0;
    }

    // Construtor parametrizado
    public Frigorifico(String nome, String marca, String modelo, double consumoPorHora,
                       double temperatura) throws TemperaturaInvalidaException {
        super(nome, marca, modelo, consumoPorHora);
        setTemperatura(temperatura);
    }

    // Construtor por cópia
    public Frigorifico(Frigorifico f) {
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
        if (temperatura < -20.0 || temperatura > 8.0)
            throw new TemperaturaInvalidaException("Temperatura inválida: " + temperatura + " (válido: -20 a 8°C)");
        this.temperatura = temperatura;
    }

    // a 8ºC (mais quente) o compressor trabalha menos -> fator ~ 0.5
    // a -20ºC (mais frio) o compressor trabalha mais -> fator ~ 1.0
    // Fórmula: fator = 0.5 + 0.5 × (TEMP_MAX - temp) / (TEMP_MAX - TEMP_MIN)

    private double fatorConsumoTemperatura(){
        return 0.5 + 0.5 * (TEMP_MAX - this.temperatura) / (TEMP_MAX - TEMP_MIN);
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
        Frigorifico f = (Frigorifico) o;
        return Double.compare(this.temperatura, f.temperatura) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Double.hashCode(this.temperatura);
    }

    @Override
    public String toString() {
        return "Frigorifico{" +
                "base=" + super.toString() +
                ", temperatura=" + this.temperatura + "°C" +
                '}';
    }

    @Override
    public Frigorifico clone() {
        return new Frigorifico(this);
    }
}
