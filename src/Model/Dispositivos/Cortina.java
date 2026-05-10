package Model.Dispositivos;

import Model.Exceptions.NivelInvalidoException;
import Model.Interfaces.Abrivel;
import Model.Interfaces.Regulavel;

// Cortina — pode abrir/fechar totalmente (Abrivel)
// ou definir uma percentagem de abertura parcial (Regulavel).
public class Cortina extends Dispositivo implements Abrivel, Regulavel {

    private int percentagemAbertura; // 0 = fechada, 100 = totalmente aberta

    // Assunção: a cortina demora 10 segundos a ir de 0% a 100%.
    // Como o tempoLigado está em minutos: 10 segundos = 10.0 / 60.0 minutos.
    private static final double TEMPO_MOVIMENTO_TOTAL_MINUTOS = 10.0 / 60.0;

    // Construtor por omissão
    public Cortina() {
        super();
        this.percentagemAbertura = 0;
    }

    // Construtor parametrizado
    public Cortina(String nome, String marca, String modelo, double consumoPorHora,
                   int percentagemAbertura) throws NivelInvalidoException {
        super(nome, marca, modelo, consumoPorHora);
        validarPercentagem(percentagemAbertura);

        // No construtor não usamos setNivel(), porque não queremos registar
        // uma ativação nem tempo de motor só por criar o objeto.
        this.percentagemAbertura = percentagemAbertura;
    }

    // Construtor por cópia
    public Cortina(Cortina c) {
        super(c);
        this.percentagemAbertura = c.getPercentagemAbertura();
    }

    // Getter próprio
    public int getPercentagemAbertura() {
        return this.percentagemAbertura;
    }

    // Validação auxiliar
    private void validarPercentagem(int valor) throws NivelInvalidoException {
        if (valor < 0 || valor > 100) {
            throw new NivelInvalidoException("Percentagem de abertura inválida: " + valor);
        }
    }

    // Calcula o tempo de movimento proporcional à diferença entre a posição atual e a nova
    private double calcularTempoMovimento(int novaPercentagem) {
        int diferenca = Math.abs(novaPercentagem - this.percentagemAbertura);
        return TEMPO_MOVIMENTO_TOTAL_MINUTOS * diferenca / 100.0;
    }

    // Implementação de Regulavel
    @Override
    public int getNivel() {
        return this.percentagemAbertura;
    }

    @Override
    public void setNivel(int valor) throws NivelInvalidoException {
        validarPercentagem(valor);

        if (valor != this.percentagemAbertura) {
            double tempoMovimento = calcularTempoMovimento(valor);

            ligar();
            registarTempoLigado(tempoMovimento);
            this.percentagemAbertura = valor;
            desligar();
        }
    }

    // Implementação de Abrivel

    public boolean isAberto() {
        return this.percentagemAbertura == 100;
    }

    public void abrir() {
        if (this.percentagemAbertura != 100) {
            double tempoMovimento = calcularTempoMovimento(100);

            ligar();
            registarTempoLigado(tempoMovimento);
            this.percentagemAbertura = 100;
            desligar();
        }
    }

    public void fechar() {
        if (this.percentagemAbertura != 0) {
            double tempoMovimento = calcularTempoMovimento(0);

            ligar();
            registarTempoLigado(tempoMovimento);
            this.percentagemAbertura = 0;
            desligar();
        }
    }

    // Consumo atual:
    // A cortina só consome enquanto o motor está ativo.
    @Override
    public double consumoAtual() {
        if (isLigado()) {
            return getConsumoPorHora();
        } else {
            return 0.0;
        }
    }

    // Consumo total:
    // Usa o tempo acumulado de funcionamento do motor.
    @Override
    public double consumoTotalDispositivo() {
        return getTempoLigado() * getConsumoPorHora() / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        Cortina c = (Cortina) o;
        return this.percentagemAbertura == c.percentagemAbertura;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Integer.hashCode(this.percentagemAbertura);
    }

    @Override
    public String toString() {
        return "Cortina{" +
                "base=" + super.toString() +
                ", percentagemAbertura=" + this.percentagemAbertura +
                '%' +
                '}';
    }

    @Override
    public Cortina clone() {
        return new Cortina(this);
    }
}