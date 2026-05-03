package DomusControl;

import Casa.Casa;
import Casa.Divisao;
import Exceptions.*;
import Utilizador.Utilizador;
import Dispositivos.Dispositivo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

// esta classe vai receber um objeto Utilizador -> o User que está Logado neste momento.
public class DomusControl {

    private Map<String, Casa> casas;
    private Map<String, Utilizador> utilizadores;

    // variável que armazena o user que está logado no momento
    // de modo a não passar o email do user em cada metodo

    private Utilizador userLogado;

        // construtor
    public DomusControl() {
        this.casas = new HashMap<>();
        this.utilizadores = new HashMap<>();
        this.userLogado = null;
    }


        // Metodos relacionados com o Login

    // Metodo de login - vai dar return a um user
    public Utilizador login(String email, String password) throws UserNaoregistadoException, PasswordIncorretaException {
        Utilizador user = getUtilizadorByEmail(email);
        if (user == null) throw new UserNaoregistadoException("O seu email está inválido");
        if (!user.getPassword().equals(password)) throw new PasswordIncorretaException("...");
        this.userLogado = user;
        return user;
    }

    // metodo de logout
    public void logout() {
        this.userLogado = null;
    }

    public boolean existeSessaoAtiva() {
        return this.userLogado != null;
    }

    // Gestão de Users



    // adicionar utilizador
    public void criarUtilizador(String nome, String password, String email) throws EmailJaExisteException {

        if (existeUtilizador(email)) {
            throw new EmailJaExisteException("Já existe um utilizador com esse e-mail");
        }

        Utilizador user = new Utilizador(nome, password, email);
        String idNovoUser = user.getId();
        this.utilizadores.put(idNovoUser, user);
    }

    // verificar se existe um Utilizador com endereço de e-mail introduzido
    // quando se cria um User novo ou se faz login
    public boolean existeUtilizador(String email) {
        return this.utilizadores.values().stream().
                anyMatch(u -> u.getEmail().equals(email));
    }


    // Gestão de Casas

    // adicionar casa
    public void criarCasa(String nome, String morada) {

        Utilizador user = getUserLogado();

        Casa casa = new Casa(nome, morada);
        String idNovaCasa = casa.getId();
        this.casas.put(idNovaCasa, casa);

        user.adicionarCasaComoAdmin(idNovaCasa); // adicionar aos dois Sets do User a casa
    }


    // Admin associa utilizador a casa
    public void adicionarUtilizadorACasa(String idCasa, String email) throws NotAdminException, UserNaoregistadoException, CasaNaoExisteException {

        Utilizador admin = getUserLogado();

        if (!admin.isAdminCasa(idCasa)) throw new NotAdminException("Não tem permissões de administrador para esta casa.");
        if (!this.casas.containsKey(idCasa)) throw new CasaNaoExisteException("Casa não existe.");
        Utilizador u = getUtilizadorByEmail(email);
        if (u == null) throw new UserNaoregistadoException("Utilizador não registado.");
        u.adicionarCasaComoUser(idCasa);
    }

    // helper: obter utilizador pelo email (ou null se não existir)
    private Utilizador getUtilizadorByEmail(String email) {
        return this.utilizadores.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst().orElse(null);
    }

    private Utilizador getUserLogado(){
        if (this.userLogado == null){
            throw new NoUserLoggedInException("Não está nenhum Utilizador Autenticado.");
        }
        return this.userLogado;
    }


    // tornar outro utilizador admin de casa
    public void adicionarAdminACasa(String idCasa, String email){

        Utilizador adminAtual = getUserLogado();

        if (!adminAtual.isAdminCasa(idCasa)) throw new NotAdminException("Não tem permissões de administrador para esta casa.");
        if (!this.casas.containsKey(idCasa)) throw new CasaNaoExisteException("Casa não existe.");
        Utilizador u = getUtilizadorByEmail(email);
        if (u == null) throw new UserNaoregistadoException("Utilizador não registado.");
        u.adicionarCasaComoAdmin(idCasa);
    }

    // eliminar Casa
    public void eliminarCasa(String idCasa)
            throws UserNaoregistadoException, CasaNaoExisteException, NotAdminException {

        Utilizador admin = getUserLogado();

        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        if (!admin.isAdminCasa(idCasa)) {
            throw new NotAdminException("Não tem permissões de administrador para esta casa.");
        }

        this.casas.remove(idCasa);

        for (Utilizador u : this.utilizadores.values()) {
            u.removeCasaComoAdmin(idCasa);
            u.removeCasaComoUser(idCasa);
        }
    }

    // Utilizador quer sair da Casa
    public void sairDeCasa(String idCasa)
            throws UserNaoregistadoException, CasaNaoExisteException {

        Utilizador u = getUserLogado();

        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        u.removeCasaComoAdmin(idCasa);
        u.removeCasaComoUser(idCasa);
    }

    // adicionar divisão a casa
    public void adicionarDivisao(String idCasa, String nomeDivisao){

        Utilizador user = getUserLogado();

        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        if (!user.isAdminCasa(idCasa)) {
            throw new NotAdminException("Não tem permissões de administrador para esta casa.");
        }

        Casa casa = this.casas.get(idCasa);
        Divisao divisao = new Divisao(nomeDivisao);
        casa.adicionaDivisao(divisao);

    }

    // adicionar dispositivo a divisão
    public void adicionarDispositivoADivisao(String idCasa, String nomeDivisao, Dispositivo dispositivo)
            throws CasaNaoExisteException, NotAdminException, DivisaoNaoExisteException {

        Utilizador user = getUserLogado();

        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        if (!user.isAdminCasa(idCasa)) {
            throw new NotAdminException("Não tem permissões de administrador para esta casa.");
        }

        Casa casa = this.casas.get(idCasa);

        casa.adicionarDispositivoADivisao(nomeDivisao, dispositivo);
    }

    // Manipulação de Dispositivos

    public void ligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo){
    }


    public void desligarDispositivo(String idDispositivo){




    }

    public void alterarNivelDispositivo (String idDispositivo){


    }

    // Estatísticas (14 valores)




}

