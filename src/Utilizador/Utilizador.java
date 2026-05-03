package Utilizador;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    }

    // Construtor Parametrizado para Criação de conta
    public Utilizador(String nome, String password, String email) {
        this.id = geraId();
        this.nome = nome;
        this.password = password;
        this.email = email;
        this.casasComoAdmin = new HashSet<>();
        this.casasComoUtilizador = new HashSet<>();
    }

    // construtor de cópia
    public Utilizador(Utilizador u) {
        this.id = u.getId();
        this.nome = u.getNome();
        this.password = u.getPassword();
        this.email = u.getEmail();
        this.casasComoAdmin = u.getCasasComoAdmin();
        this.casasComoUtilizador = u.getCasasComoUtilizador();
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


}