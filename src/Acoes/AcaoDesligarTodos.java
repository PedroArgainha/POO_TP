package Acoes;

import Dispositivos.Dispositivo;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoDesligarTodos — desliga todos os dispositivos de uma lista.
 *
 * Usada nos cenários:
 *   - "Sair de Casa"  → desliga luzes, colunas, TVs, etc.
 *   - "Deitar"        → desliga tudo exceto o que se quiser manter
 */
public class AcaoDesligarTodos implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Dispositivo> dispositivos;
    private final String descricao;

    public AcaoDesligarTodos(List<Dispositivo> dispositivos, String descricao) {
        this.dispositivos = new ArrayList<>(dispositivos);
        this.descricao = descricao;
    }

    public AcaoDesligarTodos(List<Dispositivo> dispositivos) {
        this(dispositivos, "Desligar " + dispositivos.size() + " dispositivo(s)");
    }

    @Override
    public void executar() {
        for (Dispositivo d : this.dispositivos) {
            d.desligar();
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }
}
