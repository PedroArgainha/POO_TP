package Model.Automacoes.condicoes;

import Model.Automacoes.CondicaoAutomacao;
import Model.Casa.Casa;
import Model.Dispositivos.Dispositivo;
import Model.Dispositivos.SensorChuva;

public class Condicaochuva implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final int idSensor;
    private final double limiarMmPorHora;

    public Condicaochuva(int idSensor, double limiarMmPorHora) {
        this.idSensor = idSensor;
        this.limiarMmPorHora = limiarMmPorHora;
    }

    @Override
    public boolean avaliar(Casa casa) {
        try {
            Dispositivo d = casa.getDispositivoPorId(this.idSensor);

            if (!(d instanceof SensorChuva)) {
                return false;
            }

            SensorChuva sensor = (SensorChuva) d;

            return sensor.isLigado()
                    && sensor.getValorAtual() >= this.limiarMmPorHora;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String descricao() {
        return "Pluviosidade >= " + this.limiarMmPorHora + " mm/h";
    }
}