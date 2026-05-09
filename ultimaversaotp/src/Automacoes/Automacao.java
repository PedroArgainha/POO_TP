package Automacoes;

import Interfaces.AcaoAutomacao;
import java.io.Serializable;
import java.util.Objects;
import Casa.Casa;
/**
 * Automacao — representa uma regra automática do tipo SE <condição> ENTÃO <ação>.
 *
 * Uma automação é definida por:
 *   - um nome descritivo (ex: "Fechar cortinas quando chove")
 *   - uma condição (implementação de CondicaoAutomacao)
 *   - uma ação     (implementação de AcaoAutomacao)
 *   - um flag ativa: permite suspender/reativar a automação sem a apagar
 *
 * O método verificarEExecutar() é chamado pelo DomusControl em cada
 * avanço de tempo simulado, tipo se a condição se verificar e a automação
 * estiver ativa, a ação é executada automaticamente.
 *
 * Exemplo de uso:
 *
 *   CondicaoAutomacao chuva = new CondicaoChuva(sensorChuva, 5.0);
 *   AcaoAutomacao fechar   = new AcaoFecharCortinas(listaCortinas);
 *   Automacao a = new Automacao("Fechar cortinas se chove", chuva, fechar);
 *   a.verificarEExecutar();
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
    // COnstrutor por omissão
    public Automacao(){
        this.id = geraId();
        this.nome = "";
        this.condicao = null;
        this.acao = null;
        this.ativa = false;
        this.numeroExecucoes = 0;
    }

    // Construtor parametrizado principal
    public Automacao(String nome, CondicaoAutomacao condicao, AcaoAutomacao acao) {
        this.id = geraId();
        this.nome = nome;
        this.condicao = condicao;
        this.acao = acao;
        this.ativa = true; // começa ativa por omissão
        this.numeroExecucoes = 0;
    }

    // coontrutor por cópia
    public Automacao(Automacao a) {
        this.id = a.getId();
        this.nome = a.getNome();
        this.condicao = a.getCondicao();
        this.acao = a.getAcao();
        this.ativa = a.isAtiva();
        this.numeroExecucoes = a.getNumeroExecucoes();
    }


    // Getters
    public String getId()                   { return this.id; }
    public String getNome()                 { return this.nome; }
    public CondicaoAutomacao getCondicao()  { return this.condicao; }
    public AcaoAutomacao getAcao()          { return this.acao; }
    public boolean isAtiva()               { return this.ativa; }
    public int getNumeroExecucoes()         { return this.numeroExecucoes; }


    // Setters
    public void setNome(String nome)                    { this.nome = nome; }
    public void setCondicao(CondicaoAutomacao condicao) { this.condicao = condicao; }
    public void setAcao(AcaoAutomacao acao)             { this.acao = acao; }


    // Ativar / Desativar
    public void ativar() { this.ativa = true; } 
    public void desativar() { this.ativa = false; } // suspende a automação sem a apagar

    /**
     * Lógica Principal
     * 
     * Este método deve ser chamada pelo DomusControl em cada avanço do tempo
     * return true se a ação foi executada, caso contrário false
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
    /**
     * Indica se esta automatação está pronta para ser usada
     * (tem condição e ação definidas)
     */
    public boolean estaCompleta(){
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
