package Model.Acoes;

import Model.Casa.Casa;
import Model.Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AcaoComposta implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<AcaoAutomacao> acoes;

    public AcaoComposta(List<AcaoAutomacao> acoes) {
        this.acoes = new ArrayList<>(acoes);
    }

    @Override
    public void executar(Casa casa) {
        for (AcaoAutomacao acao : this.acoes) {
            acao.executar(casa);
        }
    }

    @Override
    public String descricao() {
        return "Ação composta: " +
                this.acoes.stream()
                        .map(AcaoAutomacao::descricao)
                        .collect(Collectors.joining(" + "));
    }
}