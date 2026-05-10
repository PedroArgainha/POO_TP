package Acoes;

import Casa.Casa;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoDesligarTodos — desliga uma lista de dispositivos identificados por id.
 *
 * Usada nos cenários:
 *   - "Sair de Casa"  → desliga luzes, colunas, TVs, etc.
 *   - "Deitar"        → desliga tudo exceto o que se quiser manter
 */
public class AcaoDesligarTodos implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final String descricao;

    public AcaoDesligarTodos(List<Integer> idsDispositivos, String descricao) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.descricao = descricao;
    }

    public AcaoDesligarTodos(List<Integer> idsDispositivos) {
        this(idsDispositivos, "Desligar " + idsDispositivos.size() + " dispositivo(s)");
    }

    public AcaoDesligarTodos(AcaoDesligarTodos a) {
        this.idsDispositivos = new ArrayList<>(a.idsDispositivos);
        this.descricao = a.descricao;
    }

    @Override
    public void executar(Casa casa) {
        if (casa == null) return;
        for (Integer id : this.idsDispositivos) {
            try {
                casa.desligarDispositivoPorId(id);
            } catch (Exception e) {
                System.err.println("AcaoDesligarTodos: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }

    @Override
    public AcaoDesligarTodos clone() {
        return new AcaoDesligarTodos(this);
    }
}
