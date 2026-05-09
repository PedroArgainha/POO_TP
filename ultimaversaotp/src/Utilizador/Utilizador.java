package Utilizador;

import Cenarios.Cenario;
import Interfaces.AcaoAutomacao;
import Sugestoes.RegistoInteracao;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import Casa.Casa;


public class Utilizador implements Serializable {

    private static int proximoId = 1;

    // login
    private final String id;
    private String nome;
    private String password;
    private String email;

    // Casas Associadas
    private Set<String> casasComoAdmin;
    private Set<String> casasComoUtilizador;
    private Map<String, Cenario> cenarios;
    private List<RegistoInteracao> historicoInteracoes;

    // Metodo de classe para gerar IDs unicos
    private static String geraId() {
        return "U" + proximoId++;
    }

    // Necessário para a serialização: ao carregar de ficheiro, o contador estático
    // precisa de ser reposicionado para evitar colisões de IDs.
    public static void setProximoId(int id) {
        proximoId = id;
    }

    // construtor por omissão -> novo utilizador
    public Utilizador() {
        this.id = geraId();
        this.nome = "";
        this.password = "";
        this.email = "";
        this.casasComoAdmin = new HashSet<>();
        this.casasComoUtilizador = new HashSet<>();
        this.cenarios = new HashMap<>();
        this.historicoInteracoes = new ArrayList<>();
    }

    // Construtor Parametrizado para Criação de conta
    public Utilizador(String nome, String password, String email) {
        this.id = geraId();
        this.nome = nome;
        this.password = password;
        this.email = email;
        this.casasComoAdmin = new HashSet<>();
        this.casasComoUtilizador = new HashSet<>();
        this.cenarios = new HashMap<>();
        this.historicoInteracoes = new ArrayList<>();
    }

    // construtor de cópia
    public Utilizador(Utilizador u) {
        this.id = u.getId();
        this.nome = u.getNome();
        this.password = u.getPassword();
        this.email = u.getEmail();
        this.casasComoAdmin = u.getCasasComoAdmin();
        this.casasComoUtilizador = u.getCasasComoUtilizador();
        this.cenarios = new HashMap<>();
        for (Cenario c : u.cenarios.values()) {
            this.cenarios.put(c.getId(), c.clone());
        }
        this.historicoInteracoes = new ArrayList<>();
        if (u.historicoInteracoes != null) {
            for (RegistoInteracao r : u.historicoInteracoes) {
                this.historicoInteracoes.add(r.clone());
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public Set<String> getCasasComoAdmin() {
        return new HashSet<>(this.casasComoAdmin);
    }

    public Set<String> getCasasComoUtilizador() {
        return new HashSet<>(this.casasComoUtilizador);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Utilizador u = (Utilizador) o;
        return Objects.equals(this.id, u.id) &&
                Objects.equals(this.nome, u.nome) &&
                Objects.equals(this.password, u.password) &&
                Objects.equals(this.email, u.email) &&
                Objects.equals(this.casasComoAdmin, u.casasComoAdmin) &&
                Objects.equals(this.casasComoUtilizador, u.casasComoUtilizador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.nome, this.password, this.email,
                this.casasComoAdmin, this.casasComoUtilizador);
    }

    @Override
    public String toString() {
        return "Utilizador{" +
                "id='" + this.id + '\'' +
                ", nome='" + this.nome + '\'' +
                ", email='" + this.email + '\'' +
                ", casasComoAdmin=" + this.casasComoAdmin +
                ", casasComoUtilizador=" + this.casasComoUtilizador +
                '}';
    }

    @Override
    public Utilizador clone() {
        return new Utilizador(this);
    }


    // Metodos auxiliares
    // os metodos auxiliares desta classe devem
    // 1. Gerir o acesso dos users às casas
    // 2. Responder a perguntas sobre si próprio
    // 3. Garantir consistência interna do próprio estado
    // 4. Identidade e operações normais - get, set, clone, hash, equals, etc.

    public boolean isAdminCasa(String idCasa){
        return this.casasComoAdmin.contains(idCasa);
    }

    public boolean isUtilizadorCasa(String idCasa){
        return this.casasComoUtilizador.contains(idCasa);
    }

    public void adicionarCasaComoAdmin(String idCasa){
        this.casasComoAdmin.add(idCasa);
        this.casasComoUtilizador.add(idCasa);
    }

    public void adicionarCasaComoUser(String idCasa){
        this.casasComoUtilizador.add(idCasa);
    }

    public void removeCasaComoAdmin(String idCasa){
        this.casasComoAdmin.remove(idCasa);
    }

    public void removeCasaComoUser(String idCasa){
        this.casasComoUtilizador.remove(idCasa);
    }

    public Set<String> getTodasAsCasas(){
        Set<String> todasAsCasas = new HashSet<>();

        todasAsCasas.addAll(getCasasComoAdmin());
        todasAsCasas.addAll(getCasasComoUtilizador());

        return todasAsCasas;
    }

    public String criarCenario(String nome, String descricao, String idCasa) {
        Cenario c = new Cenario(nome, descricao, idCasa);
        this.cenarios.put(c.getId(), c);
        return c.getId();
    }

    public void adicionarAcaoACenario(String idCenario, AcaoAutomacao acao) {
        Cenario c = this.cenarios.get(idCenario);
        if (c != null) {
            c.adicionarAcao(acao);
        }
    }

    public String getIdCasaDoCenario(String idCenario) {
        Cenario c = this.cenarios.get(idCenario);
        if (c == null) {
            return null;
        }
        return c.getIdCasa();
    }

    public int ativarCenario(String idCenario, Casa casa) {
        Cenario c = this.cenarios.get(idCenario);
        if (c == null) {
            return 0;
        }
        return c.ativar(casa);
    }

    public void removerCenario(String idCenario) {
        this.cenarios.remove(idCenario);
    }

    public List<Cenario> listarCenarios() {
        return this.cenarios.values().stream()
                .map(Cenario::clone)
                .collect(Collectors.toList());
    }

    public List<String> getIdsCenarios() {
        return new ArrayList<>(this.cenarios.keySet());
    }



    // ==================== HISTÓRICO DE INTERAÇÕES (20 valores) ====================

    public void adicionarRegistoInteracao(RegistoInteracao r) {
    if (this.historicoInteracoes == null) {
        this.historicoInteracoes = new ArrayList<>();
    }

    if (r != null) {
        this.historicoInteracoes.add(r.clone());
    }
}

    public List<RegistoInteracao> getHistoricoInteracoes() {
        List<RegistoInteracao> copia = new ArrayList<>();
        for (RegistoInteracao r : this.historicoInteracoes) {
            copia.add(r.clone());
        }
        return copia;
    }

}