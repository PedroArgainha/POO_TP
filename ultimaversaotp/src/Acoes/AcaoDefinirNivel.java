package Acoes;

import Casa.Casa;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

public class AcaoDefinirNivel implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final int nivel;
    private final String descricao;

    public AcaoDefinirNivel(List<Integer> idsDispositivos, int nivel, String descricao) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.nivel = Math.max(0, Math.min(100, nivel));
        this.descricao = descricao;
    }

    public AcaoDefinirNivel(List<Integer> idsDispositivos, int nivel) {
        this(idsDispositivos, nivel,
                "Definir nível para " + nivel + "% em " + idsDispositivos.size() + " dispositivo(s)");
    }

    @Override
    public void executar(Casa casa) {
        for (Integer id : this.idsDispositivos) {
            try {
                casa.alterarNivelDispositivoPorId(id, this.nivel);
            } catch (Exception e) {
                System.err.println("Erro ao definir nível no dispositivo " + id + ": " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }
}