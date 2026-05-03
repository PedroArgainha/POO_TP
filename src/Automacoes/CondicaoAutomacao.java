package Automacoes;

import java.io.Serializable;

/**
 * CondicaoAutomacao — contrato para qualquer condição de uma automação.
 *
 * Para criar uma nova condição basta criar uma classe que implemente esta
 * interface e escrever a lógica em avaliar().
 *
 * Extends Serializable para que a Automacao possa ser gravada em ficheiro.
 */


public interface CondicaoAutomacao extends Serializable {
    // Avalia se a condição esá satisfeita neste momento
    boolean avaliar();

    // Descrição legível da condição
    // Ex: "pluviosidade > 5.0 mm/h"
    String descricao();
}
