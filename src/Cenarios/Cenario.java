package Cenarios;

import Interfaces.AcaoAutomacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cenario — representa um conjunto de ações que são executadas em sequência
 * quando o utilizador ativa o cenário manualmente.
 *
 * Ao contrário das Automações (que disparam sozinhas quando uma condição
 * se verifica), um Cenário é sempre ativado pelo utilizador.
 *
 * Exemplos do enunciado:
 *   "Sair de Casa"      → desliga luzes + fecha cortinas + desliga colunas
 *   "Jantar com Amigos" → baixa luzes da sala + aumenta volume colunas
 *   "Deitar"            → desliga tudo no quarto + fecha cortinas
 *   "Acordar"           → abre cortinas + liga luzes suavemente
 *
 * Exemplo de uso:
 *
 *   Cenario sairDeCasa = new Cenario("Sair de Casa");
 *   sairDeCasa.adicionarAcao(new AcaoDesligarTodos(todasAsLuzes));
 *   sairDeCasa.adicionarAcao(new AcaoAbrirFecharCortinas(todasAsCortinas, false));
 *   sairDeCasa.adicionarAcao(new AcaoDesligarTodos(todasAsColunas));
 *   sairDeCasa.ativar(); // executa as 3 ações em sequência
 */



public class Cenario implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private static int proximoId = 1;
    private static String geraId() { return "CEN" + proximoId++; }
    public static void setProximoId(int id) { proximoId = id; }

    private final String id;
    private String nome;
    private String descricao;
    private List<AcaoAutomacao> acoes;
    
    private int numeroAtivacoes;


    /** Construtor por omissão */
    public Cenario() {
        this.id = geraId();
        this.nome = "";
        this.descricao = "";
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }
 
    /** Construtor com nome */
    public Cenario(String nome) {
        this.id = geraId();
        this.nome = nome;
        this.descricao = "";
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }
 
    /** Construtor com nome e descrição */
    public Cenario(String nome, String descricao) {
        this.id = geraId();
        this.nome = nome;
        this.descricao = descricao;
        this.acoes = new ArrayList<>();
        this.numeroAtivacoes = 0;
    }
 
    /** Construtor por cópia */
    public Cenario(Cenario c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.descricao = c.getDescricao();
        this.acoes = new ArrayList<>(c.getAcoes()); // cópia da lista
        this.numeroAtivacoes = c.getNumeroAtivacoes();
    }


    // Getters
 
    public String getId()              { return this.id; }
    public String getNome()            { return this.nome; }
    public String getDescricao()       { return this.descricao; }
    public int getNumeroAtivacoes()    { return this.numeroAtivacoes; }
    public int getNumeroAcoes()        { return this.acoes.size(); }
 
    /** Devolve uma cópia da lista de ações (proteção do estado interno) */
    public List<AcaoAutomacao> getAcoes() {
        return new ArrayList<>(this.acoes);
    }
 
    // Setters
 
    public void setNome(String nome)           { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao;  }

    /**
     * -----------------------------------------
     */

    public void adicionarAcao (AcaoAutomacao acao){
        if (acao!=null) this.acoes.add(acao);
    }

    public void removerAcao (int indice){
        if (indice >= 0 && indice < this.acoes.size()){
            this.acoes.remove(indice);
        }
    }

    public void limparAcoes(){
        this.acoes.clear();
    }

    public boolean estaCompleto(){  // indica se o cenário tem pelo menos uma acao definida
        return !this.acoes.isEmpty();
    }

    /**
     * Lógica principal
     * 
     * Ativa o cenário - executa todas as ações em sequência
     * 
     * Se uma ação falhar, o cenário continua para a seguinte
     * 
     * Devolve o numero de ações executadas com sucesso
     */

    public int ativar(){
        int executadas = 0;
        for (AcaoAutomacao acao : this.acoes) {
            try {
                acao.executar();
                executadas++;
            } catch (Exception e) {
                System.err.println("Erro ao executar ação" + acao.descricao() + ": " + e.getMessage());
            }
        }
        this.numeroAtivacoes++;
        return executadas;
    }

    /**
     * Métodos Standard
     */
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

