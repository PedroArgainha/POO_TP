package Automacoes;

import Casa.Casa;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Escalonamento — representa uma regra baseada no tempo.
 *
 * Ao contrário da Automacao (que verifica uma condição em cada tick),
 * o Escalonamento dispara ações a horas específicas do dia.
 *
 * Dois modos:
 *   1. SÓ INÍCIO: executa a ação quando o tempo simulado atinge horaInicio.
 *   2. INÍCIO + FIM: executa acaoInicio em horaInicio e acaoFim em horaFim.
 *
 * O flag repeteDiariamente indica se se repete (resetDiario à meia-noite)
 * ou se é single-shot (desativa após a primeira execução completa).
 */
public class Escalonamento implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // Construtor simplificado (só início)
    public Escalonamento(String nome, LocalTime horaInicio,
                         AcaoAutomacao acaoInicio, boolean repeteDiariamente) {
        this(nome, horaInicio, null, acaoInicio, null, repeteDiariamente);
    }

    /** Construtor de cópia. */
    public Escalonamento(Escalonamento e) {
        this.id = e.id;
        this.nome = e.nome;
        this.horaInicio = e.horaInicio;
        this.horaFim = e.horaFim;
        this.acaoInicio = (e.acaoInicio != null) ? e.acaoInicio.clone() : null;
        this.acaoFim    = (e.acaoFim    != null) ? e.acaoFim.clone()    : null;
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

    public AcaoAutomacao getAcaoInicio() {
        return (this.acaoInicio != null) ? this.acaoInicio.clone() : null;
    }

    public AcaoAutomacao getAcaoFim() {
        return (this.acaoFim != null) ? this.acaoFim.clone() : null;
    }

    public boolean isAtiva()             { return this.ativa; }
    public boolean isRepeteDiariamente() { return this.repeteDiariamente; }
    public int getNumeroExecucoes()      { return this.numeroExecucoes; }

    // Setters
    public void setNome(String nome)                       { this.nome = nome; }
    public void setHoraInicio(LocalTime horaInicio)        { this.horaInicio = horaInicio; }
    public void setHoraFim(LocalTime horaFim)              { this.horaFim = horaFim; }
    public void setAcaoInicio(AcaoAutomacao acaoInicio) {
        this.acaoInicio = (acaoInicio != null) ? acaoInicio.clone() : null;
    }
    public void setAcaoFim(AcaoAutomacao acaoFim) {
        this.acaoFim = (acaoFim != null) ? acaoFim.clone() : null;
    }

    public void ativar()    { this.ativa = true; }
    public void desativar() { this.ativa = false; }

    /**
     * Verifica se as horas de disparo caem no intervalo (horaAnterior, horaAtual]
     * e, se sim, executa a ação correspondente sobre a Casa fornecida.
     */
    public void verificar(Casa casa, LocalTime horaAnterior, LocalTime horaAtual) {
        if (!this.ativa) return;
        if (this.acaoInicio == null) return;

        // Início
        if (!this.jaExecutouInicio &&
            estaDentroDoIntervalo(this.horaInicio, horaAnterior, horaAtual)) {

            this.acaoInicio.executar(casa);
            this.jaExecutouInicio = true;
            this.numeroExecucoes++;

            if (this.horaFim == null && !this.repeteDiariamente) {
                this.ativa = false;
            }
        }

        // Fim (se definido)
        if (this.horaFim != null && this.acaoFim != null &&
            this.jaExecutouInicio && !this.jaExecutouFim &&
            estaDentroDoIntervalo(this.horaFim, horaAnterior, horaAtual)) {

            this.acaoFim.executar(casa);
            this.jaExecutouFim = true;

            if (!this.repeteDiariamente) {
                this.ativa = false;
            }
        }
    }

    /** Reset diário — chamado à meia-noite pela Casa. */
    public void resetDiario() {
        if (this.repeteDiariamente) {
            this.jaExecutouInicio = false;
            this.jaExecutouFim = false;
        }
    }

    /** Trata o caso de passagem pela meia-noite. */
    private boolean estaDentroDoIntervalo(LocalTime hora, LocalTime anterior, LocalTime atual) {
        if (anterior.isBefore(atual) || anterior.equals(atual)) {
            return hora.isAfter(anterior) && !hora.isAfter(atual);
        } else {
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
