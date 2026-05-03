package Automacoes.condicoes;

import Automacoes.CondicaoAutomacao;
import Dispositivos.SensorLuminosidade;

/**
 * CondicaoLuminosidade — verifica se a luminosidade está abaixo de um limiar.
 *
 * Exemplo de uso:
 *   CondicaoLuminosidade c = new CondicaoLuminosidade(sensor, 100.0);
 *   // dispara se a luminosidade for menor que 100 lux (começa a escurecer)
 */
public class Condicaoluminosidade implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final SensorLuminosidade sensor;
    private final double limiarLux;

    public Condicaoluminosidade(SensorLuminosidade sensor, double limiarLux) {
        this.sensor = sensor;
        this.limiarLux = limiarLux;
    }

    @Override
    public boolean avaliar() {
        return this.sensor.isLigado() &&
               this.sensor.getValorAtual() < this.limiarLux;
    }

    @Override
    public String descricao() {
        return "Luminosidade < " + this.limiarLux + " lux";
    }
}