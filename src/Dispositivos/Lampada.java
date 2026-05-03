package Dispositivos;

import Exceptions.CorInvalidaException;
import Exceptions.NivelInvalidoException;
import Interfaces.Colorivel;
import Interfaces.Regulavel;

public class Lampada extends Dispositivo implements Regulavel, Colorivel {

    private int intensidade;
    private int cor;

    // Construtor por omissão
    public Lampada() {
        super();
        this.intensidade = 0;
        this.cor = 2700;
    }

    // Construtor parametrizado
    public Lampada(String nome, String marca, String modelo, double consumoPorHora,
                   int intensidade, int cor) throws NivelInvalidoException, CorInvalidaException {
        super(nome, marca, modelo, consumoPorHora);
        setIntensidade(intensidade);
        setCor(cor);
    }

    // Construtor por cópia
    public Lampada(Lampada l) {
        super(l);
        this.intensidade = l.getIntensidade();
        this.cor = l.getCor();
    }

    public int getIntensidade() {
        return this.intensidade;
    }

    @Override
    public int getCor() {
        return this.cor;
    }

    public void setIntensidade(int intensidade) throws NivelInvalidoException {
        if (intensidade < 0 || intensidade > 100)
            throw new NivelInvalidoException("Intensidade inválida: " + intensidade);
        this.intensidade = intensidade;
    }

    @Override
    public void setCor(int cor) throws CorInvalidaException {
        if (cor < 2700 || cor > 4000)
            throw new CorInvalidaException("Cor inválida: " + cor + "K (válido: 2700-4000K)");
        this.cor = cor;
    }

    // --- Implementação de Regulavel ---
    @Override
    public int getNivel() {
        return this.intensidade;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        setIntensidade(valor);
    }

    @Override
    public double consumoAtual(){ 
        if (!isLigado()) return 0.0;
        return getConsumoPorHora() * (this.intensidade / 100.0);
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() * (this.intensidade / 100.0) / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        Lampada l = (Lampada) o;
        return this.intensidade == l.intensidade &&
                this.cor == l.cor;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Integer.hashCode(this.intensidade);
        result = 31 * result + Integer.hashCode(this.cor);
        return result;
    }

    @Override
    public String toString() {
        return "Lampada{" +
                "base=" + super.toString() +
                ", intensidade=" + this.intensidade +
                ", cor=" + this.cor + "K" +
                '}';
    }

    @Override
    public Lampada clone() {
        return new Lampada(this);
    }
}