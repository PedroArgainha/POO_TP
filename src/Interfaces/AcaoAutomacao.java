package Interfaces;

import java.io.Serializable;

/**
 * AcaoAutomacao — contrato para qualquer ação executável pelo sistema.
 *
 * Esta interface é partilhada entre Automações, Escalonamentos e Cenários.
 * Vive no package Interfaces (junto com Regulavel, Abrivel, etc.) porque
 * é uma abstração transversal — não pertence a nenhum componente específico.
 *
 * Para criar uma nova ação basta implementar esta interface.
 * Exemplos: AcaoDesligarTodos, AcaoAbrirCortinas, AcaoDefinirNivel, ...
 */
public interface AcaoAutomacao extends Serializable {

    /** Executa a ação sobre os dispositivos relevantes. */
    void executar();

    /**
     * Descrição legível da ação, usada nos menus e relatórios.
     * Ex: "Fechar todas as cortinas da sala"
     */
    String descricao();
}
