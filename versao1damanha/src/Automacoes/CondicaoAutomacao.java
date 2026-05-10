package Automacoes;

import Casa.Casa;
import java.io.Serializable;

/**
 * CondicaoAutomacao — contrato para qualquer condição de uma automação.
 *
 * REGRA DE ENCAPSULAMENTO:
 * As implementações guardam IDENTIFICADORES (id do sensor + outros
 * parâmetros como limiares) e recebem a Casa real apenas no momento
 * da avaliação. A UI nunca tem de manipular Sensores — só passa ids.
 *
 * Para criar uma nova condição basta criar uma classe que implemente esta
 * interface e escrever a lógica em avaliar().
 *
 * Extends Serializable para que a Automacao possa ser gravada em ficheiro.
 */
public interface CondicaoAutomacao extends Serializable {

    /**
     * Avalia a condição contra o estado atual da casa.
     * A casa é fornecida pelo executor (Automacao) para que a condição
     * possa resolver o id do sensor para a leitura real.
     */
    boolean avaliar(Casa casa);

    /**
     * Descrição legível da condição.
     * Ex: "pluviosidade > 5.0 mm/h"
     */
    String descricao();

    /**
     * Devolve uma cópia independente da condição.
     */
    CondicaoAutomacao clone();
}
