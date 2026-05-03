package Dispositivos;

import Interfaces.Monitoravel;

// CamaraVigilancia — monitoriza e pode gravar imagens.
public class CamaraVigilancia extends Dispositivo implements Monitoravel {

    private boolean gravando;
    private int resolucao; // em pixels (ex: 1080, 2160)

    // Construtor por omissão
    public CamaraVigilancia() {
        super();
        this.gravando = false;
        this.resolucao = 1080;
    }

    // Construtor parametrizado
    public CamaraVigilancia(String nome, String marca, String modelo, double consumoPorHora,
                            int resolucao) {
        super(nome, marca, modelo, consumoPorHora);
        this.gravando = false;
        this.resolucao = resolucao;
    }

    // Construtor por cópia
    public CamaraVigilancia(CamaraVigilancia c) {
        super(c);
        this.gravando = c.isGravando();
        this.resolucao = c.getResolucao();
    }

    // --- Getters próprios ---
    public boolean isGravando() {
        return this.gravando;
    }

    public int getResolucao() {
        return this.resolucao;
    }

    // --- Métodos de controlo ---

    // --- Implementação de Monitoravel ---
    @Override
    public double getValorAtual() {
        return this.gravando ? 1.0 : 0.0;
    }

    @Override
    public String getUnidade() {
        return "estado";
    }

    @Override
    public void simularLeitura(double valor) {
        this.gravando = valor > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        CamaraVigilancia c = (CamaraVigilancia) o;
        return this.gravando == c.gravando && this.resolucao == c.resolucao;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Boolean.hashCode(this.gravando);
        result = 31 * result + Integer.hashCode(this.resolucao);
        return result;
    }

    @Override
    public String toString() {
        return "CamaraVigilancia{" +
                "base=" + super.toString() +
                ", gravando=" + this.gravando +
                ", resolucao=" + this.resolucao + "p" +
                '}';
    }

    public void iniciarGravacao() {
        if (!isLigado()) {
            ligar();
        }
        this.gravando = true;
    }

    public void pararGravacao() {
        this.gravando = false;
    }

    @Override
    public double consumoAtual() {
        if (!isLigado()) {
            return 0.0;
        }

        if (this.gravando) {
            return getConsumoPorHora();
        }

        return getConsumoPorHora() * 0.6;
    }

    @Override
    public double consumoTotalDispositivo() {
        if (this.gravando) {
            return getTempoLigado() * getConsumoPorHora() / 60.0;
        }

        return getTempoLigado() * getConsumoPorHora() * 0.6 / 60.0;
    }

    @Override
    public CamaraVigilancia clone() {
        return new CamaraVigilancia(this);
    }
}
