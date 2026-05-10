package Automacoes.condicoes;

import Automacoes.CondicaoAutomacao;
import Casa.Casa;
import Dispositivos.Dispositivo;
import Dispositivos.SensorChuva;

/**
 * CondicaoChuva — verifica se a pluviosidade medida por um SensorChuva
 * ultrapassa um limiar definido.
 *
 * Guarda o id do sensor (não a referência) e resolve-o para o sensor real
 * através da Casa quando avaliar() é chamado. Assim, a UI nunca precisa de
 * receber o sensor — só o id.
 */
public class Condicaochuva implements CondicaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final int idSensor;
    private final double limiarMmPorHora;

    public Condicaochuva(int idSensor, double limiarMmPorHora) {
        this.idSensor = idSensor;
        this.limiarMmPorHora = limiarMmPorHora;
    }

    public Condicaochuva(Condicaochuva c) {
        this.idSensor = c.idSensor;
        this.limiarMmPorHora = c.limiarMmPorHora;
    }

    @Override
    public boolean avaliar(Casa casa) {
        if (casa == null) return false;
        try {
            Dispositivo d = casa.getDispositivoPorId(this.idSensor);
            if (!(d instanceof SensorChuva)) return false;
            SensorChuva sensor = (SensorChuva) d;
            return sensor.isLigado() && sensor.getValorAtual() >= this.limiarMmPorHora;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String descricao() {
        return "Pluviosidade do sensor #" + this.idSensor + " >= " + this.limiarMmPorHora + " mm/h";
    }

    @Override
    public Condicaochuva clone() {
        return new Condicaochuva(this);
    }
}
