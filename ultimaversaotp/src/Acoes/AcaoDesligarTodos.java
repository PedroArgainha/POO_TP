package Acoes;

import Casa.Casa;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void executar(Casa casa) {
        for (Integer id : this.idsDispositivos) {
            try {
                casa.desligarDispositivoPorId(id);
            } catch (Exception e) {
                System.err.println("Erro ao desligar dispositivo " + id + ": " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }
}