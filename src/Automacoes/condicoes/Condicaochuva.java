package Automacoes.condicoes;

import Automacoes.CondicaoAutomacao;
import Dispositivos.SensorChuva;

/**
 * CondicaoChuva — verifica se a pluviosidade medida por um SensorChuva
 * ultrapassa um limiar definido.
 *
 * Exemplo de uso:
 *   CondicaoChuva c = new CondicaoChuva(meuSensor, 5.0);
 *   // dispara se estiver a chover mais de 5 mm/h
 */
public class Condicaochuva implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final SensorChuva sensor;
    private final double limiarMmPorHora;

    public Condicaochuva(SensorChuva sensor, double limiarMmPorHora) {
        this.sensor = sensor;
        this.limiarMmPorHora = limiarMmPorHora;
    }

    @Override
    public boolean avaliar() {
        return this.sensor.isLigado() &&
               this.sensor.getValorAtual() >= this.limiarMmPorHora;
    }

    @Override
    public String descricao() {
        return "Pluviosidade >= " + this.limiarMmPorHora + " mm/h";
    }
}