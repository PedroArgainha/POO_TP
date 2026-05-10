package Interfaces;

import Casa.Casa;
import java.io.Serializable;

/**
 * AcaoAutomacao — contrato para qualquer ação executável pelo sistema.
 *
 * Esta interface é partilhada entre Automações, Escalonamentos e Cenários.
 * Vive no package Interfaces (junto com Regulavel, Abrivel, etc.) porque
 * é uma abstração transversal — não pertence a nenhum componente específico.
 *
 * REGRA DE ENCAPSULAMENTO:
 * As implementações guardam IDENTIFICADORES (ids de dispositivos) e
 * recebem a Casa real apenas no momento da execução. Assim, a UI
 * nunca precisa de receber referências a Dispositivos — só passa ids
 * e textos. As alterações de estado acontecem dentro do model, no
 * caminho Casa → Divisao → Dispositivo.
 *
 * Para criar uma nova ação basta implementar esta interface.
 * Exemplos: AcaoDesligarTodos, AcaoAbrirCortinas, AcaoDefinirNivel, ...
 */
public interface AcaoAutomacao extends Serializable {

    /**
     * Executa a ação sobre os dispositivos da Casa indicada.
     * A Casa é fornecida pelo "executor" (Automacao/Cenario/Escalonamento)
     * para que a ação possa resolver os ids para dispositivos reais.
     */
    void executar(Casa casa);

    /**
     * Descrição legível da ação, usada nos menus e relatórios.
     * Ex: "Fechar todas as cortinas da sala"
     */
    String descricao();

    /**
     * Devolve uma cópia independente da ação.
     * As implementações devem garantir que duas instâncias não
     * partilham listas de ids mutáveis.
     */
    AcaoAutomacao clone();
}
