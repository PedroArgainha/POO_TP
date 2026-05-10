package Automacoes;

import Cenarios.Condicoes.CondicaoAutomacao;
import Casa.Casa;

import java.io.Serializable;
import java.util.Objects;

/**
 * Automacao — representa uma regra automática do tipo SE <condição> ENTÃO <ação>.
 *
 * Uma automação é definida por:
 *   - um nome descritivo (ex: "Fechar cortinas quando chove")
 *   - uma condição (implementação de CondicaoAutomacao)
 *   - uma ação     (implementação de AcaoAutomacao)
 *   - um flag ativa: permite suspender/reativar a automação sem a apagar
 *
 * O método verificarEExecutar(Casa) é chamado pela Casa em cada
 * avanço de tempo simulado. Se a condição for verdadeira e a automação
 * estiver ativa, a ação é executada.
 *
 * Exemplo de uso:
 *
 *   CondicaoAutomacao chuva = new Condicaochuva(idSensor, 5.0);
 *   AcaoAutomacao fechar    = new AcaoFecharCortinas(idsCortinas);
 *   Automacao a = new Automacao("Fechar cortinas se chove", chuva, fechar);
 *   a.verificarEExecutar(casa);
 */
public class Automacao implements Serializable {

    private static final long serialVersionUID = 1L;

    // Gerador de IDs únicos (mesmo padrão de Dispositivos e Utilizador)
    private static int proximoId = 1;
    private static String geraId() { return "A" + proximoId++; }
    public static void setProximoId(int id) { proximoId = id; }

    private final String id;
    private String nome;
    private CondicaoAutomacao condicao;
    private AcaoAutomacao acao;
    private boolean ativa;

    private int numeroExecucoes;

    // CONSTRUTORES
    public Automacao() {
        this.id = geraId();
        this.nome = "";
        this.condicao = null;
        this.acao = null;
        this.ativa = false;
        this.numeroExecucoes = 0;
    }

    public Automacao(String nome, CondicaoAutomacao condicao, AcaoAutomacao acao) {
        this.id = geraId();
        this.nome = nome;
        this.condicao = condicao;
        this.acao = acao;
        this.ativa = true;
        this.numeroExecucoes = 0;
    }

    /**
     * Construtor de cópia.
     * Clona condição e ação para que duas instâncias não partilhem
     * referências mutáveis (listas internas, descrições configuráveis).
     */
    public Automacao(Automacao a) {
        this.id = a.getId();
        this.nome = a.getNome();
        this.condicao = (a.condicao != null) ? a.condicao.clone() : null;
        this.acao = (a.acao != null) ? a.acao.clone() : null;
        this.ativa = a.isAtiva();
        this.numeroExecucoes = a.getNumeroExecucoes();
    }

    // Getters
    public String getId()                   { return this.id; }
    public String getNome()                 { return this.nome; }

    public CondicaoAutomacao getCondicao()  {
        return (this.condicao != null) ? this.condicao.clone() : null;
    }

    public AcaoAutomacao getAcao()          {
        return (this.acao != null) ? this.acao.clone() : null;
    }

    public boolean isAtiva()                { return this.ativa; }
    public int getNumeroExecucoes()         { return this.numeroExecucoes; }

    // Setters
    public void setNome(String nome)                    { this.nome = nome; }
    public void setCondicao(CondicaoAutomacao condicao) {
        this.condicao = (condicao != null) ? condicao.clone() : null;
    }
    public void setAcao(AcaoAutomacao acao)             {
        this.acao = (acao != null) ? acao.clone() : null;
    }

    // Ativar / Desativar
    public void ativar() { this.ativa = true; }
    public void desativar() { this.ativa = false; }

    /**
     * Lógica principal — chamada pela Casa em cada avanço do tempo.
     * Devolve true se a ação foi executada, false caso contrário.
     */
    public boolean verificarEExecutar(Casa casa) {
        if (!this.ativa) return false;
        if (this.condicao == null || this.acao == null) return false;

        if (this.condicao.avaliar(casa)) {
            this.acao.executar(casa);
            this.numeroExecucoes++;
            return true;
        }
        return false;
    }

    public boolean estaCompleta() {
        return this.condicao != null && this.acao != null;
    }

    // Métodos Standard

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automacao a = (Automacao) o;
        return Objects.equals(this.id, a.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Automacao{" +
                "id='" + this.id + '\'' +
                ", nome='" + this.nome + '\'' +
                ", ativa=" + this.ativa +
                ", execucoes=" + this.numeroExecucoes +
                ", condicao=" + (this.condicao != null ? this.condicao.descricao() : "nenhuma") +
                ", acao=" + (this.acao != null ? this.acao.descricao() : "nenhuma") +
                '}';
    }

    @Override
    public Automacao clone() {
        return new Automacao(this);
    }
}
