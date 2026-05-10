package Acoes;

import Casa.Casa;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoFecharCortinas — fecha uma lista de dispositivos abríveis identificados
 * por id (cortinas, persianas, portões — qualquer Abrivel).
 *
 * Usada na automação clássica do enunciado: chuva → fechar cortinas.
 */
public class AcaoFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;

    public AcaoFecharCortinas(List<Integer> idsDispositivos) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
    }

    public AcaoFecharCortinas(AcaoFecharCortinas a) {
        this.idsDispositivos = new ArrayList<>(a.idsDispositivos);
    }

    @Override
    public void executar(Casa casa) {
        if (casa == null) return;
        for (Integer id : this.idsDispositivos) {
            try {
                casa.fecharDispositivoPorId(id);
            } catch (Exception e) {
                System.err.println("AcaoFecharCortinas: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return "Fechar " + this.idsDispositivos.size() + " cortina(s)";
    }

    @Override
    public AcaoFecharCortinas clone() {
        return new AcaoFecharCortinas(this);
    }
}
