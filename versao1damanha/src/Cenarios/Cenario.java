package Cenarios;

import Casa.Casa;
import Interfaces.AcaoAutomacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cenario — conjunto de ações executadas em sequência quando o utilizador
 * o ativa manualmente.
 *
 * Ao contrário das Automações (que disparam sozinhas), um Cenário é sempre
 * ativado pelo utilizador. Cada cenário sabe a que casa pertence (idCasa)
 * para que, no momento da ativação, as ações possam operar sobre os
 * dispositivos certos.
 *
 * Exemplos do enunciado:
 *   "Sair de Casa"      → desliga luzes + fecha cortinas + desliga colunas
 *   "Jantar com Amigos" → baixa luzes da sala + aumenta volume colunas
 *   "Deitar"            → desliga tudo no quarto + fecha cortinas
 *   "Acordar"           → abre cortinas + liga luzes suavemente
 */
public class Cenario implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int proximoId = 1;
    private static String geraId() { return "CEN" + proximoId++; }
    public static void setProximoId(int id) { proximoId = id; }

    private final String id;
    private String nome;
    private String descricao;
    private final String idCasa;        // a que casa este cenário pertence
    private List<AcaoAutomacao> acoes;

    private int numeroAtivacoes;


    /** Construtor por omissão */
    public Cenario() {
        this.id = geraId();
        this.nome = "";
        this.descricao = "";
        this.idCasa = null;
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }

    /** Construtor mínimo (sem casa — útil para testes pontuais) */
    public Cenario(String nome) {
        this.id = geraId();
        this.nome = nome;
        this.descricao = "";
        this.idCasa = null;
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }

    /** Construtor com nome, descrição e casa associada (uso normal) */
    public Cenario(String nome, String descricao, String idCasa) {
        this.id = geraId();
        this.nome = nome;
        this.descricao = descricao;
        this.idCasa = idCasa;
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }

    /** Construtor de cópia. Lista nova, ações clonadas. */
    public Cenario(Cenario c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.descricao = c.getDescricao();
        this.idCasa = c.getIdCasa();
        this.acoes = new ArrayList<>();
        for (AcaoAutomacao a : c.acoes) {
            this.acoes.add(a.clone());
        }
        this.numeroAtivacoes = c.getNumeroAtivacoes();
    }


    // Getters

    public String getId()              { return this.id; }
    public String getNome()            { return this.nome; }
    public String getDescricao()       { return this.descricao; }
    public String getIdCasa()          { return this.idCasa; }
    public int getNumeroAtivacoes()    { return this.numeroAtivacoes; }
    public int getNumeroAcoes()        { return this.acoes.size(); }

    /** Devolve uma cópia profunda da lista de ações. */
    public List<AcaoAutomacao> getAcoes() {
        List<AcaoAutomacao> copia = new ArrayList<>();
        for (AcaoAutomacao a : this.acoes) {
            copia.add(a.clone());
        }
        return copia;
    }

    // Setters
    public void setNome(String nome)           { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // Manipulação

    public void adicionarAcao(AcaoAutomacao acao) {
        if (acao != null) this.acoes.add(acao.clone());
    }

    public void removerAcao(int indice) {
        if (indice >= 0 && indice < this.acoes.size()) {
            this.acoes.remove(indice);
        }
    }

    public void limparAcoes() {
        this.acoes.clear();
    }

    public boolean estaCompleto() {
        return !this.acoes.isEmpty();
    }

    /**
     * Ativa o cenário — executa todas as ações em sequência sobre a Casa.
     * Se uma ação falhar, continua para a seguinte.
     * Devolve o número de ações executadas com sucesso.
     */
    public int ativar(Casa casa) {
        int executadas = 0;
        for (AcaoAutomacao acao : this.acoes) {
            try {
                acao.executar(casa);
                executadas++;
            } catch (Exception e) {
                System.err.println("Erro ao executar ação " + acao.descricao() + ": " + e.getMessage());
            }
        }
        this.numeroAtivacoes++;
        return executadas;
    }

    // Métodos standard

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cenario c = (Cenario) o;
        return Objects.equals(this.id, c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cenario{");
        sb.append("id='").append(this.id).append('\'');
        sb.append(", nome='").append(this.nome).append('\'');
        sb.append(", idCasa='").append(this.idCasa).append('\'');
        sb.append(", ativacoes=").append(this.numeroAtivacoes);
        sb.append(", acoes=[");
        for (int i = 0; i < this.acoes.size(); i++) {
            sb.append(this.acoes.get(i).descricao());
            if (i < this.acoes.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public Cenario clone() {
        return new Cenario(this);
    }
}
