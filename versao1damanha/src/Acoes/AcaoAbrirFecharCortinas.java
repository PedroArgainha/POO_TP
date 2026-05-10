package Acoes;

import Casa.Casa;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoAbrirFecharCortinas — abre ou fecha uma lista de dispositivos Abrivel
 * (Cortina, Persiana, PortaoGaragem) identificados por id.
 *
 * Usada em automações (ex: chuva → fechar cortinas)
 * e cenários (ex: "Acordar" → abrir cortinas, "Sair de Casa" → fechar tudo).
 */
public class AcaoAbrirFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final boolean abrir; // true = abrir, false = fechar

    public AcaoAbrirFecharCortinas(List<Integer> idsDispositivos, boolean abrir) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.abrir = abrir;
    }

    public AcaoAbrirFecharCortinas(AcaoAbrirFecharCortinas a) {
        this.idsDispositivos = new ArrayList<>(a.idsDispositivos);
        this.abrir = a.abrir;
    }

    @Override
    public void executar(Casa casa) {
        if (casa == null) return;
        for (Integer id : this.idsDispositivos) {
            try {
                if (this.abrir) casa.abrirDispositivoPorId(id);
                else            casa.fecharDispositivoPorId(id);
            } catch (Exception e) {
                System.err.println("AcaoAbrirFecharCortinas: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        String acao = this.abrir ? "Abrir" : "Fechar";
        return acao + " " + this.idsDispositivos.size() + " cortina(s)/persiana(s)";
    }

    @Override
    public AcaoAbrirFecharCortinas clone() {
        return new AcaoAbrirFecharCortinas(this);
    }
}
