package Automacoes.condicoes;

import Automacoes.CondicaoAutomacao;
import Casa.Casa;
import Dispositivos.Dispositivo;
import Dispositivos.SensorLuminosidade;

public class Condicaoluminosidade implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final int idSensor;
    private final double limiarLux;

    public Condicaoluminosidade(int idSensor, double limiarLux) {
        this.idSensor = idSensor;
        this.limiarLux = limiarLux;
    }

    @Override
    public boolean avaliar(Casa casa) {
        try {
            Dispositivo d = casa.getDispositivoPorId(this.idSensor);

            if (!(d instanceof SensorLuminosidade)) {
                return false;
            }

            SensorLuminosidade sensor = (SensorLuminosidade) d;

            return sensor.isLigado()
                    && sensor.getValorAtual() < this.limiarLux;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String descricao() {
        return "Luminosidade < " + this.limiarLux + " lux";
    }
}