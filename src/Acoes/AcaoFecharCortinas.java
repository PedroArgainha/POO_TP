package Acoes;

import Casa.Casa;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

public class AcaoFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsCortinas;

    public AcaoFecharCortinas(List<Integer> idsCortinas) {
        this.idsCortinas = new ArrayList<>(idsCortinas);
    }

    @Override
    public void executar(Casa casa) {
        for (Integer id : this.idsCortinas) {
            try {
                casa.fecharDispositivoPorId(id);
            } catch (Exception e) {
                System.err.println("Erro ao fechar cortina " + id + ": " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return "Fechar " + this.idsCortinas.size() + " cortina(s)";
    }
}