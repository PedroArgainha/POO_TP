package Interfaces;

import Casa.Casa;
import java.io.Serializable;

public interface AcaoAutomacao extends Serializable {

    void executar(Casa casa);

    String descricao();
}