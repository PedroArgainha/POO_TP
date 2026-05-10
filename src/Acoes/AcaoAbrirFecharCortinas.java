package Acoes;

import Casa.Casa;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

public class AcaoAbrirFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final boolean abrir;

    public AcaoAbrirFecharCortinas(List<Integer> idsDispositivos, boolean abrir) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.abrir = abrir;
    }

    @Override
    public void executar(Casa casa) {
        for (Integer id : this.idsDispositivos) {
            try {
                if (this.abrir) {
                    casa.abrirDispositivoPorId(id);
                } else {
                    casa.fecharDispositivoPorId(id);
                }
            } catch (Exception e) {
                System.err.println("Erro ao abrir/fechar dispositivo " + id + ": " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        String acao = this.abrir ? "Abrir" : "Fechar";
        return acao + " " + this.idsDispositivos.size() + " dispositivo(s) abrível(eis)";
    }
}