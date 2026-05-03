package Acoes;

import Dispositivos.Cortina;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoFecharCortinas — fecha todas as cortinas de uma lista.
 * Especialização simples que trabalha diretamente com Cortina.
 *
 * Nota: Para abrir/fechar qualquer Abrivel, usa AcaoAbrirFecharCortinas.
 * Esta classe existe para a automação de chuva que referencia Cortinas específicas.
 */
public class AcaoFecharCortinas implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Cortina> cortinas;

    public AcaoFecharCortinas(List<Cortina> cortinas) {
        this.cortinas = new ArrayList<>(cortinas);
    }

    @Override
    public void executar() {
        for (Cortina c : this.cortinas) {
            c.fechar();
        }
    }

    @Override
    public String descricao() {
        return "Fechar " + this.cortinas.size() + " cortina(s)";
    }
}
