package Automacoes.condicoes;

import Automacoes.CondicaoAutomacao;
import Casa.Casa;
import Dispositivos.Dispositivo;
import Dispositivos.SensorLuminosidade;

/**
 * CondicaoLuminosidade — verifica se a luminosidade está abaixo de um limiar.
 *
 * Guarda o id do sensor (não a referência). O sensor real é obtido da Casa
 * no momento da avaliação.
 */
public class Condicaoluminosidade implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final int idSensor;
    private final double limiarLux;

    public Condicaoluminosidade(int idSensor, double limiarLux) {
        this.idSensor = idSensor;
        this.limiarLux = limiarLux;
    }

    public Condicaoluminosidade(Condicaoluminosidade c) {
        this.idSensor = c.idSensor;
        this.limiarLux = c.limiarLux;
    }

    @Override
    public boolean avaliar(Casa casa) {
        if (casa == null) return false;
        try {
            Dispositivo d = casa.getDispositivoPorId(this.idSensor);
            if (!(d instanceof SensorLuminosidade)) return false;
            SensorLuminosidade sensor = (SensorLuminosidade) d;
            return sensor.isLigado() && sensor.getValorAtual() < this.limiarLux;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String descricao() {
        return "Luminosidade do sensor #" + this.idSensor + " < " + this.limiarLux + " lux";
    }

    @Override
    public Condicaoluminosidade clone() {
        return new Condicaoluminosidade(this);
    }
}
