package Acoes;

import Interfaces.Abrivel;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoAbrirFecharCortinas — abre ou fecha uma lista de dispositivos Abrivel
 * (Cortina, Persiana, PortaoGaragem).
 *
 * Usada em automações (ex: chuva → fechar cortinas)
 * e cenários (ex: "Acordar" → abrir cortinas, "Sair de Casa" → fechar tudo).
 */
public class AcaoAbrirFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Abrivel> dispositivos;
    private final boolean abrir; // true = abrir, false = fechar

    public AcaoAbrirFecharCortinas(List<Abrivel> dispositivos, boolean abrir) {
        this.dispositivos = new ArrayList<>(dispositivos);
        this.abrir = abrir;
    }

    @Override
    public void executar() {
        for (Abrivel a : this.dispositivos) {
            if (this.abrir) {
                a.abrir();
            } else {
                a.fechar();
            }
        }
    }

    @Override
    public String descricao() {
        String acao = this.abrir ? "Abrir" : "Fechar";
        return acao + " " + this.dispositivos.size() + " cortina(s)/persiana(s)";
    }
}
