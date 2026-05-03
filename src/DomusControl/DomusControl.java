package DomusControl;

import Automacoes.Automacao;
import Automacoes.CondicaoAutomacao;
import Automacoes.Escalonamento;
import Casa.Casa;
import Casa.Divisao;
import Cenarios.Cenario;
import Exceptions.*;
import Interfaces.AcaoAutomacao;
import Utilizador.Utilizador;
import Dispositivos.Dispositivo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

// esta classe vai receber um objeto Utilizador -> o User que está Logado neste momento.
public class DomusControl implements Serializable {

    private Map<String, Casa> casas;
    private Map<String, Utilizador> utilizadores;

    // variável que armazena o user que está logado no momento
    private Utilizador userLogado;

    // Tempo simulado do sistema
    private LocalDateTime tempoSimulado;

    // Automações, Escalonamentos e Cenários (18 valores)
    private Map<String, Automacao> automacoes;
    private Map<String, Escalonamento> escalonamentos;
    private Map<String, Cenario> cenarios;

    // construtor
    public DomusControl() {
        this.casas = new HashMap<>();
        this.utilizadores = new HashMap<>();
        this.userLogado = null;
        this.tempoSimulado = LocalDateTime.of(2026, 1, 1, 0, 0);
        this.automacoes = new HashMap<>();
        this.escalonamentos = new HashMap<>();
        this.cenarios = new HashMap<>();
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

    // ==================== MANIPULAÇÃO DE DISPOSITIVOS ====================
    // Qualquer utilizador com acesso à casa (admin ou utilizador) pode operar dispositivos.

    // Helper privado: valida sessão + existência da casa + acesso do utilizador.
    // Retorna o objeto Casa real (não clone) para operações internas.
    private Casa validarAcessoCasa(String idCasa) {
        Utilizador user = getUserLogado();

        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        if (!user.isUtilizadorCasa(idCasa)) {
            throw new NotAdminException("Não tem acesso a esta casa.");
        }

        return this.casas.get(idCasa);
    }

    // --- ON / OFF ---

    public void ligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.ligarDispositivo(nomeDivisao, idDispositivo);
    }

    public void desligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.desligarDispositivo(nomeDivisao, idDispositivo);
    }

    // --- Regulavel ---

    public void alterarNivelDispositivo(String idCasa, String nomeDivisao, int idDispositivo, int nivel)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException, NivelInvalidoException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.alterarNivelDispositivo(nomeDivisao, idDispositivo, nivel);
    }

    // --- Temperavel ---

    public void alterarTemperaturaDispositivo(String idCasa, String nomeDivisao, int idDispositivo, double temperatura)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException, TemperaturaInvalidaException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.alterarTemperaturaDispositivo(nomeDivisao, idDispositivo, temperatura);
    }

    // --- Colorivel ---

    public void alterarCorDispositivo(String idCasa, String nomeDivisao, int idDispositivo, int cor)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException, CorInvalidaException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.alterarCorDispositivo(nomeDivisao, idDispositivo, cor);
    }

    // --- Abrivel ---

    public void abrirDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.abrirDispositivo(nomeDivisao, idDispositivo);
    }

    public void fecharDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.fecharDispositivo(nomeDivisao, idDispositivo);
    }

    // --- Bloqueavel ---

    public void bloquearDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.bloquearDispositivo(nomeDivisao, idDispositivo);
    }

    public void desbloquearDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.desbloquearDispositivo(nomeDivisao, idDispositivo);
    }

    // ==================== SIMULAÇÃO DE TEMPO ====================

    public LocalDateTime getTempoSimulado() {
        return this.tempoSimulado;
    }

    public void avancarTempo(long minutos) {
        if (minutos <= 0) {
            throw new IllegalArgumentException("O tempo a avançar deve ser positivo.");
        }

        LocalTime horaAnterior = this.tempoSimulado.toLocalTime();

        // 1. Atualizar o tempo acumulado de todos os dispositivos ligados
        for (Casa casa : this.casas.values()) {
            casa.atualizarTempoDispositivos((double) minutos);
        }

        // 2. Avançar o relógio simulado
        this.tempoSimulado = this.tempoSimulado.plusMinutes(minutos);

        LocalTime horaAtual = this.tempoSimulado.toLocalTime();

        // 3. Verificar automações — cada uma avalia a sua condição e executa se necessário
        for (Automacao a : this.automacoes.values()) {
            a.verificarEExecutar();
        }

        // 4. Verificar escalonamentos — disparam em função da hora simulada
        //    Detecta se mudou de dia para fazer reset dos escalonamentos diários
        boolean mudouDeDia = horaAtual.isBefore(horaAnterior);
        if (mudouDeDia) {
            for (Escalonamento e : this.escalonamentos.values()) {
                e.resetDiario();
            }
        }
        for (Escalonamento e : this.escalonamentos.values()) {
            e.verificar(horaAnterior, horaAtual);
        }
    }

    // ==================== ESTATÍSTICAS (14 valores) ====================
    //
    // Requisitos do enunciado (secção 8.2):
    //   1. Qual é a casa que mais consome
    //   2. Para uma casa, os 3 dispositivos mais utilizados (em tempo e em nº de ativações)
    //   3. Quais as 3 divisões (e respetiva casa) com mais dispositivos
    //   4. Outros métodos de consulta relevantes

    // 1. Casa que mais consome (em Wh acumulado)
    //    Percorre todas as casas e devolve a que tem maior consumoTotalCasa().
    //    Devolve null se não existirem casas.
    public Casa casaQueMaisConsome() {
        return this.casas.values().stream()
                .max(Comparator.comparingDouble(Casa::consumoTotalCasa))
                .map(Casa::clone)
                .orElse(null);
    }

    // 2a. Top 3 dispositivos mais utilizados por TEMPO de utilização (numa casa)
    //     Ordena todos os dispositivos da casa pelo tempoLigado (descendente).
    public List<Dispositivo> top3DispositivosPorTempo(String idCasa) throws CasaNaoExisteException {
        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        Casa casa = this.casas.get(idCasa);

        return casa.getTodosDispositivos().stream()
                .sorted(Comparator.comparingDouble(Dispositivo::getTempoLigado).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    // 2b. Top 3 dispositivos mais utilizados por NÚMERO DE ATIVAÇÕES (numa casa)
    public List<Dispositivo> top3DispositivosPorAtivacoes(String idCasa) throws CasaNaoExisteException {
        if (!this.casas.containsKey(idCasa)) {
            throw new CasaNaoExisteException("Casa não existe.");
        }

        Casa casa = this.casas.get(idCasa);

        return casa.getTodosDispositivos().stream()
                .sorted(Comparator.comparingInt(Dispositivo::getNumeroAtivacoes).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    // 3. Top 3 divisões com mais dispositivos (de TODAS as casas)
    //    Devolve um Map<String, Integer> onde a key é "NomeCasa - NomeDivisão"
    //    e o value é o nº de dispositivos. Usa LinkedHashMap para manter ordem.
    public Map<String, Integer> top3DivisoesComMaisDispositivos() {
        List<Map.Entry<String, Integer>> lista = new ArrayList<>();

        for (Casa casa : this.casas.values()) {
            Map<String, Divisao> divs = casa.getDivisoes();
            for (Map.Entry<String, Divisao> entry : divs.entrySet()) {
                String descricao = casa.getNome() + " - " + entry.getKey();
                int numDisps = entry.getValue().getNumeroDispositivos();
                lista.add(new AbstractMap.SimpleEntry<>(descricao, numDisps));
            }
        }

        return lista.stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // 4. Consumo total do sistema (Wh) — soma o consumo de todas as casas.
    public double consumoTotalSistema() {
        return this.casas.values().stream()
                .mapToDouble(Casa::consumoTotalCasa)
                .sum();
    }

    // 4b. Consumo por casa — mapa com o id/nome de cada casa e o seu consumo total.
    public Map<String, Double> consumoPorCasa() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (Casa casa : this.casas.values()) {
            resultado.put(casa.getNome() + " (" + casa.getId() + ")", casa.consumoTotalCasa());
        }
        return resultado;
    }

    // ==================== AUTOMAÇÕES (18 valores) ====================

    public String criarAutomacao(String nome, CondicaoAutomacao condicao, AcaoAutomacao acao) {
        Automacao a = new Automacao(nome, condicao, acao);
        this.automacoes.put(a.getId(), a);
        return a.getId();
    }

    public void removerAutomacao(String idAutomacao) {
        this.automacoes.remove(idAutomacao);
    }

    public List<Automacao> listarAutomacoes() {
        return this.automacoes.values().stream()
                .map(Automacao::clone)
                .collect(Collectors.toList());
    }

    public void ativarAutomacao(String idAutomacao) {
        Automacao a = this.automacoes.get(idAutomacao);
        if (a != null) a.ativar();
    }

    public void desativarAutomacao(String idAutomacao) {
        Automacao a = this.automacoes.get(idAutomacao);
        if (a != null) a.desativar();
    }

    // ==================== ESCALONAMENTOS (18 valores) ====================

    public String criarEscalonamento(String nome, LocalTime horaInicio, LocalTime horaFim,
                                     AcaoAutomacao acaoInicio, AcaoAutomacao acaoFim,
                                     boolean repeteDiariamente) {
        Escalonamento e = new Escalonamento(nome, horaInicio, horaFim,
                acaoInicio, acaoFim, repeteDiariamente);
        this.escalonamentos.put(e.getId(), e);
        return e.getId();
    }

    // Versão simplificada: só hora de início, sem fim
    public String criarEscalonamento(String nome, LocalTime horaInicio,
                                     AcaoAutomacao acaoInicio, boolean repeteDiariamente) {
        Escalonamento e = new Escalonamento(nome, horaInicio, acaoInicio, repeteDiariamente);
        this.escalonamentos.put(e.getId(), e);
        return e.getId();
    }

    public void removerEscalonamento(String idEscalonamento) {
        this.escalonamentos.remove(idEscalonamento);
    }

    public List<Escalonamento> listarEscalonamentos() {
        return this.escalonamentos.values().stream()
                .map(Escalonamento::clone)
                .collect(Collectors.toList());
    }

    // ==================== CENÁRIOS (18 valores) ====================

    public String criarCenario(String nome) {
        Cenario c = new Cenario(nome);
        this.cenarios.put(c.getId(), c);
        return c.getId();
    }

    public String criarCenario(String nome, String descricao) {
        Cenario c = new Cenario(nome, descricao);
        this.cenarios.put(c.getId(), c);
        return c.getId();
    }

    public void adicionarAcaoACenario(String idCenario, AcaoAutomacao acao) {
        Cenario c = this.cenarios.get(idCenario);
        if (c != null) {
            c.adicionarAcao(acao);
        }
    }

    // Ativa um cenário: executa todas as suas ações em sequência.
    // Devolve o número de ações executadas com sucesso.
    public int ativarCenario(String idCenario) {
        Cenario c = this.cenarios.get(idCenario);
        if (c == null) return 0;
        return c.ativar();
    }

    public void removerCenario(String idCenario) {
        this.cenarios.remove(idCenario);
    }

    public List<Cenario> listarCenarios() {
        return this.cenarios.values().stream()
                .map(Cenario::clone)
                .collect(Collectors.toList());
    }

    // ==================== SERIALIZAÇÃO ====================

    public void gravaEstado(String ficheiro) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(ficheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        this.userLogado = null;
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    public static DomusControl carregaEstado(String ficheiro)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(ficheiro);
        ObjectInputStream ois = new ObjectInputStream(fis);
        DomusControl dc = (DomusControl) ois.readObject();
        ois.close();
        dc.corrigirContadoresIds();
        return dc;
    }

    private void corrigirContadoresIds() {
        int maxUserId = 0;
        for (String key : this.utilizadores.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > maxUserId) maxUserId = num;
            } catch (NumberFormatException ignored) {}
        }
        Utilizador.setProximoId(maxUserId + 1);

        int maxCasaId = 0;
        for (String key : this.casas.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > maxCasaId) maxCasaId = num;
            } catch (NumberFormatException ignored) {}
        }
        Casa.setProximoId(maxCasaId + 1);

        int maxDispId = 0;
        for (Casa casa : this.casas.values()) {
            for (Dispositivo d : casa.getTodosDispositivos()) {
                if (d.getId() > maxDispId) maxDispId = d.getId();
            }
        }
        Dispositivo.setProximoId(maxDispId + 1);

        // Corrigir IDs de automações (formato "A<numero>")
        int maxAutoId = 0;
        for (String key : this.automacoes.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > maxAutoId) maxAutoId = num;
            } catch (NumberFormatException ignored) {}
        }
        Automacao.setProximoId(maxAutoId + 1);

        // Corrigir IDs de escalonamentos (formato "ESC<numero>")
        int maxEscId = 0;
        for (String key : this.escalonamentos.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(3));
                if (num > maxEscId) maxEscId = num;
            } catch (NumberFormatException ignored) {}
        }
        Escalonamento.setProximoId(maxEscId + 1);

        // Corrigir IDs de cenários (formato "CEN<numero>")
        int maxCenId = 0;
        for (String key : this.cenarios.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(3));
                if (num > maxCenId) maxCenId = num;
            } catch (NumberFormatException ignored) {}
        }
        Cenario.setProximoId(maxCenId + 1);
    }
}
