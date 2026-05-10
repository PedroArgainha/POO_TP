package Model.Interfaces;

public interface Monitoravel {
    double getValorAtual();
    String getUnidade();
    void simularLeitura(double valor);
}

