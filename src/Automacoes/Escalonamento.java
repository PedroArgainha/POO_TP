package Automacoes;

import Interfaces.AcaoAutomacao;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Escalonamento — representa uma regra baseada no tempo.
 *
 * Ao contrário da Automacao (que verifica uma condição em cada tick),
 * o Escalonamento dispara ações a horas específicas do dia.
 *
 * Dois modos de funcionamento:
 *   1. SÓ INÍCIO: executa a ação quando o tempo simulado atinge horaInicio.
 *      Ex: "Abrir cortinas às 08:00"
 *
 *   2. INÍCIO + FIM: executa a acaoInicio quando atinge horaInicio,
 *      e executa a acaoFim quando atinge horaFim.
 *      Ex: "Luz ligada das 19:00 às 23:00"
 *
 * O flag repeteDiariamente indica se o escalonamento se repete todos os dias
 * ou se é single-shot (desativa-se após a primeira execução).
 *
 * Exemplo de uso:
 *   Escalonamento e = new Escalonamento("Luz corredor",
 *       LocalTime.of(19, 0), LocalTime.of(23, 0),
 *       new AcaoLigarDispositivos(luzes), new AcaoDesligarTodos(luzes), true);
 */
public class Escalonamento implements Serializable {

    private static final long serialVersionUID = 1L;

    // Gerador de IDs únicos
    private static int proximoId = 1;
    private static String geraId() { return "ESC" + proximoId++; }

    public static void setProximoId(int id) { proximoId = id; }

    private final String id;
    private String nome;
    private LocalTime horaInicio;
    private LocalTime horaFim;          // null se não tem hora de fim
    private AcaoAutomacao acaoInicio;
    private AcaoAutomacao acaoFim;      // null se não tem ação de fim
    private boolean ativa;
    private boolean repeteDiariamente;

    // Controlo interno: evita executar múltiplas vezes no mesmo "tick"
    private boolean jaExecutouInicio;
    private boolean jaExecutouFim;
    private int numeroExecucoes;

    // Construtor completo (início + fim)
    public Escalonamento(String nome, LocalTime horaInicio, LocalTime horaFim,
                         AcaoAutomacao acaoInicio, AcaoAutomacao acaoFim,
                         boolean repeteDiariamente) {
        this.id = geraId();
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.acaoInicio = acaoInicio;
        this.acaoFim = acaoFim;
        this.ativa = true;
        this.repeteDiariamente = repeteDiariamente;
        this.jaExecutouInicio = false;
        this.jaExecutouFim = false;
        this.numeroExecucoes = 0;
    }

    // Construtor simplificado (só início, sem hora de fim)
    public Escalonamento(String nome, LocalTime horaInicio,
                         AcaoAutomacao acaoInicio, boolean repeteDiariamente) {
        this(nome, horaInicio, null, acaoInicio, null, repeteDiariamente);
    }

    // Construtor de cópia
    public Escalonamento(Escalonamento e) {
        this.id = e.id;
        this.nome = e.nome;
        this.horaInicio = e.horaInicio;
        this.horaFim = e.horaFim;
        this.acaoInicio = e.acaoInicio;
        this.acaoFim = e.acaoFim;
        this.ativa = e.ativa;
        this.repeteDiariamente = e.repeteDiariamente;
        this.jaExecutouInicio = e.jaExecutouInicio;
        this.jaExecutouFim = e.jaExecutouFim;
        this.numeroExecucoes = e.numeroExecucoes;
    }

    // Getters
    public String getId()               { return this.id; }
    public String getNome()             { return this.nome; }
    public LocalTime getHoraInicio()    { return this.horaInicio; }
    public LocalTime getHoraFim()       { return this.horaFim; }
    public AcaoAutomacao getAcaoInicio(){ return this.acaoInicio; }
    public AcaoAutomacao getAcaoFim()   { return this.acaoFim; }
    public boolean isAtiva()            { return this.ativa; }
    public boolean isRepeteDiariamente(){ return this.repeteDiariamente; }
    public int getNumeroExecucoes()     { return this.numeroExecucoes; }

    // Setters
    public void setNome(String nome)                       { this.nome = nome; }
    public void setHoraInicio(LocalTime horaInicio)        { this.horaInicio = horaInicio; }
    public void setHoraFim(LocalTime horaFim)              { this.horaFim = horaFim; }
    public void setAcaoInicio(AcaoAutomacao acaoInicio)    { this.acaoInicio = acaoInicio; }
    public void setAcaoFim(AcaoAutomacao acaoFim)          { this.acaoFim = acaoFim; }

    // Ativar / Desativar
    public void ativar()    { this.ativa = true; }
    public void desativar() { this.ativa = false; }

    /**
     * Lógica principal — chamada pelo DomusControl em cada avanço de tempo.
     *
     * Recebe a hora ANTERIOR e a hora ATUAL do relógio simulado.
     * Verifica se a horaInicio caiu dentro do intervalo [horaAnterior, horaAtual].
     *
     * Porquê usar intervalo em vez de igualdade?
     * Porque se o utilizador avançar 60 minutos de uma vez,
     * a hora exata pode ser "saltada". Com intervalo, apanhamos o tick correto.
     */
    public void verificar(LocalTime horaAnterior, LocalTime horaAtual) {
        if (!this.ativa) return;
        if (this.acaoInicio == null) return;

        // Verificar se horaInicio caiu no intervalo
        if (!this.jaExecutouInicio && estaDentroDoIntervalo(this.horaInicio, horaAnterior, horaAtual)) {
            this.acaoInicio.executar();
            this.jaExecutouInicio = true;
            this.numeroExecucoes++;

            // Se não tem hora de fim e não repete, desativar
            if (this.horaFim == null && !this.repeteDiariamente) {
                this.ativa = false;
            }
        }

        // Verificar hora de fim (se definida)
        if (this.horaFim != null && this.acaoFim != null &&
            this.jaExecutouInicio && !this.jaExecutouFim &&
            estaDentroDoIntervalo(this.horaFim, horaAnterior, horaAtual)) {

            this.acaoFim.executar();
            this.jaExecutouFim = true;

            // Se não repete, desativar após executar início e fim
            if (!this.repeteDiariamente) {
                this.ativa = false;
            }
        }
    }

    /**
     * Reset diário — chamado quando o dia muda (meia-noite).
     * Permite que escalonamentos diários voltem a disparar no dia seguinte.
     */
    public void resetDiario() {
        if (this.repeteDiariamente) {
            this.jaExecutouInicio = false;
            this.jaExecutouFim = false;
        }
    }

    /**
     * Verifica se um dado momento 'hora' está dentro do intervalo (anterior, atual].
     * Trata o caso de passagem pela meia-noite.
     */
    private boolean estaDentroDoIntervalo(LocalTime hora, LocalTime anterior, LocalTime atual) {
        if (anterior.isBefore(atual) || anterior.equals(atual)) {
            // Caso normal: ex 14:00 → 15:00, hora=14:30 → true
            return hora.isAfter(anterior) && !hora.isAfter(atual);
        } else {
            // Passagem pela meia-noite: ex 23:30 → 00:30
            return hora.isAfter(anterior) || !hora.isAfter(atual);
        }
    }

    // Métodos standard

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Escalonamento e = (Escalonamento) o;
        return Objects.equals(this.id, e.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Escalonamento{id='").append(this.id);
        sb.append("', nome='").append(this.nome);
        sb.append("', inicio=").append(this.horaInicio);
        if (this.horaFim != null) sb.append(", fim=").append(this.horaFim);
        sb.append(", ativa=").append(this.ativa);
        sb.append(", repete=").append(this.repeteDiariamente);
        sb.append(", execucoes=").append(this.numeroExecucoes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Escalonamento clone() {
        return new Escalonamento(this);
    }
}
