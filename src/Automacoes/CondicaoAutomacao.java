package Automacoes;

import Casa.Casa;
import java.io.Serializable;

public interface CondicaoAutomacao extends Serializable {

    boolean avaliar(Casa casa);

    String descricao();
}