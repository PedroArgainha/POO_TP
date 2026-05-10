package Controller;

import Model.Sugestoes.SugestaoEscalonamento;
import Model.Sugestoes.RegistoInteracao;
import Model.Sugestoes.AnalisadorPadroes;
import Model.Acoes.AcaoAbrirFecharCortinas;
import Model.Acoes.AcaoDesligarTodos;
import Model.Acoes.AcaoFecharCortinas;
import Model.Acoes.AcaoLigarDispositivos;
import Model.Automacoes.Automacao;
import Model.Automacoes.CondicaoAutomacao;
import Model.Automacoes.Escalonamento;
import Model.Automacoes.condicoes.Condicaochuva;
import Model.Automacoes.condicoes.Condicaoluminosidade;
import Model.Casa.Casa;
import Model.Casa.Divisao;
import Model.Cenarios.Cenario;
import Model.Exceptions.*;
import Model.Interfaces.AcaoAutomacao;
import Model.Utilizador.Utilizador;
import Model.Dispositivos.ArCondicionado;
import Model.Dispositivos.ColunaSom;
import Model.Dispositivos.Cortina;
import Model.Dispositivos.Dispositivo;
import Model.Dispositivos.Forno;
import Model.Dispositivos.Frigorifico;
import Model.Dispositivos.Lampada;
import Model.Dispositivos.PortaoGaragem;
import Model.Dispositivos.SensorChuva;
import Model.Dispositivos.SensorLuminosidade;
import Model.Dispositivos.SensorTemperatura;
import Model.Dispositivos.Televisao;

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

    // construtor
    public DomusControl() {
        this.casas = new HashMap<>();
        this.utilizadores = new HashMap<>();
        this.userLogado = null;
        this.tempoSimulado = LocalDateTime.of(2026, 1, 1, 0, 0);
    }

    // Metodo de login - vai dar return a um user
    public Utilizador login(String email, String password) throws UserNaoregistadoException, PasswordIncorretaException {
        Utilizador user = getUtilizadorByEmail(email);
        if (user == null) throw new UserNaoregistadoException("O seu email está inválido");
        if (!user.getPassword().equals(password)) throw new PasswordIncorretaException("...");
        this.userLogado = user;
        return user.clone();
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

    // Regista interações manuais do utilizador para permitir sugestões automáticas (20 valores).
    private void registarInteracao(String idCasa, int idDispositivo, String tipoAcao) {
        Utilizador user = getUserLogado();
        RegistoInteracao registo = new RegistoInteracao(
                this.tempoSimulado,
                idCasa,
                idDispositivo,
                tipoAcao
        );
        user.adicionarRegistoInteracao(registo);
    }

    // --- ON / OFF ---

    public void ligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.ligarDispositivo(nomeDivisao, idDispositivo);
        registarInteracao(idCasa, idDispositivo, RegistoInteracao.TIPO_LIGAR);
    }

    public void desligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {
        Casa casa = validarAcessoCasa(idCasa);
        casa.desligarDispositivo(nomeDivisao, idDispositivo);
        registarInteracao(idCasa, idDispositivo, RegistoInteracao.TIPO_DESLIGAR);
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

        for (Casa casa : this.casas.values()) {
            casa.atualizarTempoDispositivos((double) minutos);
        }

        this.tempoSimulado = this.tempoSimulado.plusMinutes(minutos);

        LocalTime horaAtual = this.tempoSimulado.toLocalTime();
        boolean mudouDeDia = horaAtual.isBefore(horaAnterior);

        for (Casa casa : this.casas.values()) {
            casa.verificarAutomacoes();
            casa.verificarEscalonamentos(horaAnterior, horaAtual, mudouDeDia);
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
    public String casaQueMaisConsome() {
    return this.casas.values().stream()
            .max(Comparator.comparingDouble(Casa::consumoTotalCasa))
            .map(c -> c.getId() + " - " + c.getNome())
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
    public List<Dispositivo> top3DispositivosPorAtivacoes(String idCasa) {
        Casa casa = validarAcessoCasa(idCasa);

        return casa.getTodosDispositivos().stream()
                .sorted(
                        Comparator.<Dispositivo>comparingInt(Dispositivo::getNumeroAtivacoes)
                                .reversed()
                                .thenComparing(Dispositivo::getNome)
                                .thenComparingInt(Dispositivo::getId)
                )
                .limit(3)
                .collect(Collectors.toList());
    }
    // 3. Top 3 divisões com mais dispositivos (de TODAS as casas)
    //    Devolve um Map<String, Integer> onde a key é "NomeCasa - NomeDivisão"
    //    e o value é o nº de dispositivos. Usa LinkedHashMap para manter ordem.
    public Map<String, Integer> top3DivisoesComMaisDispositivos() {
        return this.casas.values().stream()
                .flatMap(casa ->
                        casa.getDivisoes().entrySet().stream()
                                .map(entry -> new AbstractMap.SimpleEntry<>(
                                        casa.getId() + " - " + casa.getNome() + " / " + entry.getKey(),
                                        entry.getValue().getNumeroDispositivos()
                                ))
                )
                .sorted((e1, e2) -> {
                    int cmp = Integer.compare(e2.getValue(), e1.getValue());

                    if (cmp != 0) {
                        return cmp;
                    }

                    return e1.getKey().compareTo(e2.getKey());
                })
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
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
        return this.casas.values().stream()
                .sorted(
                        Comparator.comparing(Casa::getNome)
                                .thenComparing(Casa::getId)
                )
                .collect(Collectors.toMap(
                        casa -> casa.getId() + " - " + casa.getNome(),
                        Casa::consumoTotalCasa,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }
    // ==================== AUTOMAÇÕES (18 valores) ====================

    public String criarAutomacao(String idCasa, String nome,
                                 CondicaoAutomacao condicao,
                                 AcaoAutomacao acao) {
        Casa casa = validarAcessoCasa(idCasa);
        return casa.criarAutomacao(nome, condicao, acao);
    }

    public void removerAutomacao(String idCasa, String idAutomacao) {
        Casa casa = validarAcessoCasa(idCasa);
        casa.removerAutomacao(idAutomacao);
    }

    public List<Automacao> listarAutomacoes(String idCasa) {
        Casa casa = validarAcessoCasa(idCasa);
        return casa.listarAutomacoes();
    }

    public void ativarAutomacao(String idCasa, String idAutomacao) {
        Casa casa = validarAcessoCasa(idCasa);
        casa.ativarAutomacao(idAutomacao);
    }

    public void desativarAutomacao(String idCasa, String idAutomacao) {
        Casa casa = validarAcessoCasa(idCasa);
        casa.desativarAutomacao(idAutomacao);
    }



    // ==================== ESCALONAMENTOS (18 valores) ====================

    public String criarEscalonamento(String idCasa, String nome,
                                     LocalTime horaInicio, LocalTime horaFim,
                                     AcaoAutomacao acaoInicio,
                                     AcaoAutomacao acaoFim,
                                     boolean repeteDiariamente) {
        Casa casa = validarAcessoCasa(idCasa);
        return casa.criarEscalonamento(nome, horaInicio, horaFim,
                acaoInicio, acaoFim, repeteDiariamente);
    }

    public String criarEscalonamento(String idCasa, String nome,
                                     LocalTime horaInicio,
                                     AcaoAutomacao acaoInicio,
                                     boolean repeteDiariamente) {
        Casa casa = validarAcessoCasa(idCasa);
        return casa.criarEscalonamento(nome, horaInicio, acaoInicio, repeteDiariamente);
    }

    public void removerEscalonamento(String idCasa, String idEscalonamento) {
        Casa casa = validarAcessoCasa(idCasa);
        casa.removerEscalonamento(idEscalonamento);
    }

    public List<Escalonamento> listarEscalonamentos(String idCasa) {
        Casa casa = validarAcessoCasa(idCasa);
        return casa.listarEscalonamentos();
    }

    // ==================== CENÁRIOS (18 valores) ====================

    public String criarCenario(String idCasa, String nome, String descricao) {
        Casa casa = validarAcessoCasa(idCasa);
        Utilizador user = getUserLogado();

        if (!user.isUtilizadorCasa(casa.getId())) {
            throw new NotAdminException("Não tem acesso a esta casa.");
        }

        return user.criarCenario(nome, descricao, idCasa);
    }

    public void adicionarAcaoACenario(String idCenario, AcaoAutomacao acao) {
        Utilizador user = getUserLogado();
        user.adicionarAcaoACenario(idCenario, acao);
    }

    public int ativarCenario(String idCenario) {
        Utilizador user = getUserLogado();

        String idCasa = user.getIdCasaDoCenario(idCenario);
        if (idCasa == null) return 0;

        Casa casa = validarAcessoCasa(idCasa);

        return user.ativarCenario(idCenario, casa);
    }

    public void removerCenario(String idCenario) {
        Utilizador user = getUserLogado();
        user.removerCenario(idCenario);
    }

    public List<Cenario> listarCenarios() {
        Utilizador user = getUserLogado();
        return user.listarCenarios();
    }

    // ==================== SUGESTÕES AUTOMÁTICAS (20 valores) ====================

    public List<SugestaoEscalonamento> gerarSugestoesEscalonamento() {
        Utilizador user = getUserLogado();
        return AnalisadorPadroes.analisar(user.getHistoricoInteracoes()).stream()
                .map(SugestaoEscalonamento::clone)
                .collect(Collectors.toList());
    }

    public String aceitarSugestaoEscalonamento(SugestaoEscalonamento sugestao) {
        if (sugestao == null) {
            throw new IllegalArgumentException("Sugestão inválida.");
        }

        AcaoAutomacao acao;
        if (RegistoInteracao.TIPO_LIGAR.equals(sugestao.getTipoAcao())) {
            acao = new AcaoLigarDispositivos(Collections.singletonList(sugestao.getIdDispositivo()));
        } else if (RegistoInteracao.TIPO_DESLIGAR.equals(sugestao.getTipoAcao())) {
            acao = new AcaoDesligarTodos(Collections.singletonList(sugestao.getIdDispositivo()));
        } else {
            throw new IllegalArgumentException("Tipo de ação não suportado: " + sugestao.getTipoAcao());
        }

        String nome = "Sugestão automática: " + sugestao.getTipoAcao() +
                " dispositivo " + sugestao.getIdDispositivo() +
                " às " + sugestao.getHoraSugerida();

        return criarEscalonamento(
                sugestao.getIdCasa(),
                nome,
                sugestao.getHoraSugerida(),
                acao,
                true
        );
    }

    // ==================== SERIALIZAÇÃO ====================

    public void gravaEstado(String ficheiro) throws IOException {
        Utilizador anterior = this.userLogado;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ficheiro))) {
            this.userLogado = null;
            oos.writeObject(this);
        } finally {
            this.userLogado = anterior;
        }
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
            } catch (NumberFormatException ignored) {
            }
        }
        Utilizador.setProximoId(maxUserId + 1);

        int maxCasaId = 0;
        for (String key : this.casas.keySet()) {
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > maxCasaId) maxCasaId = num;
            } catch (NumberFormatException ignored) {
            }
        }
        Casa.setProximoId(maxCasaId + 1);

        int maxDispId = 0;
        for (Casa casa : this.casas.values()) {
            for (Dispositivo d : casa.getTodosDispositivos()) {
                if (d.getId() > maxDispId) maxDispId = d.getId();
            }
        }
        Dispositivo.setProximoId(maxDispId + 1);

        // Corrigir IDs de automações
        int maxAutoId = 0;
        for (Casa casa : this.casas.values()) {
            for (String key : casa.getIdsAutomacoes()) {
                try {
                    int num = Integer.parseInt(key.substring(1));
                    if (num > maxAutoId) maxAutoId = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Automacao.setProximoId(maxAutoId + 1);

        // Corrigir IDs de escalonamentos
        int maxEscId = 0;
        for (Casa casa : this.casas.values()) {
            for (String key : casa.getIdsEscalonamentos()) {
                try {
                    int num = Integer.parseInt(key.substring(3));
                    if (num > maxEscId) maxEscId = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Escalonamento.setProximoId(maxEscId + 1);

        // Corrigir IDs de cenários
        int maxCenId = 0;
        for (Utilizador u : this.utilizadores.values()) {
            for (String key : u.getIdsCenarios()) {
                try {
                    int num = Integer.parseInt(key.substring(3));
                    if (num > maxCenId) maxCenId = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Cenario.setProximoId(maxCenId + 1);
    }



    // ============================================================
    // Metodo necessário para a TextUI funcionar
    // ============================================================
    public List<Casa> getCasasDoUserLogado() {
        Utilizador user = getUserLogado();
        
        Set<String> idsCasas = new HashSet<>();
        
        // Aqui usamos os nomes exatos que estão no teu Utilizador.java
        idsCasas.addAll(user.getCasasComoAdmin());
        idsCasas.addAll(user.getCasasComoUtilizador());

        return idsCasas.stream()
                .map(id -> this.casas.get(id))
                .filter(Objects::nonNull)
                .map(Casa::clone) 
                .collect(Collectors.toList());
    }

    public void simularLeituraSensor(String idCasa, String nomeDivisao, int idDispositivo, double valor)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {

        Casa casa = validarAcessoCasa(idCasa);
        casa.simularLeituraSensor(nomeDivisao, idDispositivo, valor);
    }

    /**
     * Popula o sistema com um estado de exemplo, para testar o programa
     * sem ter de digitar tudo à mão.
     *
     * Cria:
     *   - 3 utilizadores
     *   - 2 casas (Casa da Ana, Casa do Bruno) com várias divisões
     *   - ~15 dispositivos espalhados por divisões
     *   - sensores com leituras simuladas (chuva forte, está escuro)
     *   - 2 automações na Casa da Ana (chuva → fechar, escuro → ligar)
     *   - 1 escalonamento (luzes da sala 19:00–23:00)
     *   - 2 cenários (Sair de Casa, Acordar)
     *
     * Logins de teste:
     *   ana@teste.pt    / ana
     *   bruno@teste.pt  / bruno
     *   carla@teste.pt  / carla
     *
     * Lança IllegalStateException se já existirem utilizadores no sistema.
     */
    public void popularEstadoTeste() throws Exception {
        if (!this.utilizadores.isEmpty()) {
            throw new IllegalStateException(
                "Já existem utilizadores — estado de teste só carrega num sistema vazio.");
        }

        // 1. Utilizadores
        criarUtilizador("Ana",   "ana",   "ana@teste.pt");
        criarUtilizador("Bruno", "bruno", "bruno@teste.pt");
        criarUtilizador("Carla", "carla", "carla@teste.pt");

        // 2. Casa 1 — admin: Ana
        login("ana@teste.pt", "ana");
        criarCasa("Casa da Ana", "Rua das Flores 12, Braga");
        String idCasaAna = ultimoIdCasaCriada();

        adicionarDivisao(idCasaAna, "Sala");
        adicionarDivisao(idCasaAna, "Cozinha");
        adicionarDivisao(idCasaAna, "Quarto");
        adicionarDivisao(idCasaAna, "Exterior");

        // ids dos dispositivos que vamos precisar para automações/cenários
        Lampada luzSala     = new Lampada("Luz Sala",     "Philips", "Hue",     9.0, 80, 2700);
        Cortina cortinaSala = new Cortina("Cortina Sala", "Generic", "C1",      30.0, 100);
        ColunaSom colunaSala= new ColunaSom("Coluna Sala","Sonos",   "One",     12.0, 30);
        Televisao tvSala    = new Televisao("TV Sala",    "LG",      "OLED",    110.0, 20, 5);

        adicionarDispositivoADivisao(idCasaAna, "Sala", luzSala);
        adicionarDispositivoADivisao(idCasaAna, "Sala", cortinaSala);
        adicionarDispositivoADivisao(idCasaAna, "Sala", colunaSala);
        adicionarDispositivoADivisao(idCasaAna, "Sala", tvSala);

        Lampada luzCozinha = new Lampada("Luz Cozinha", "IKEA", "Tradfri", 8.0, 100, 4000);
        Frigorifico frigo  = new Frigorifico("Frigorifico", "Bosch", "F100", 50.0, 4.0);
        Forno forno        = new Forno("Forno", "Bosch", "O200", 2000.0, 180.0);

        adicionarDispositivoADivisao(idCasaAna, "Cozinha", luzCozinha);
        adicionarDispositivoADivisao(idCasaAna, "Cozinha", frigo);
        adicionarDispositivoADivisao(idCasaAna, "Cozinha", forno);

        Lampada luzQuarto       = new Lampada("Luz Quarto", "Philips", "Hue", 9.0, 50, 2700);
        Cortina cortinaQuarto   = new Cortina("Cortina Quarto", "Generic", "C1", 30.0, 0);
        ArCondicionado acQuarto = new ArCondicionado("AC Quarto", "Daikin", "AC500", 1500.0, 22.0);

        adicionarDispositivoADivisao(idCasaAna, "Quarto", luzQuarto);
        adicionarDispositivoADivisao(idCasaAna, "Quarto", cortinaQuarto);
        adicionarDispositivoADivisao(idCasaAna, "Quarto", acQuarto);

        SensorChuva sensorChuva = new SensorChuva("Sensor Chuva", "Bosch", "S1", 0.5);
        SensorLuminosidade sensorLux = new SensorLuminosidade("Sensor Lux", "Bosch", "S2", 0.5);
        SensorTemperatura sensorTemp = new SensorTemperatura("Sensor Temp", "Bosch", "S3", 0.5);

        adicionarDispositivoADivisao(idCasaAna, "Exterior", sensorChuva);
        adicionarDispositivoADivisao(idCasaAna, "Exterior", sensorLux);
        adicionarDispositivoADivisao(idCasaAna, "Exterior", sensorTemp);

        // Capturar os ids ANTES de ligar (os ids são definidos no construtor do dispositivo)
        int idLuzSala       = luzSala.getId();
        int idCortinaSala   = cortinaSala.getId();
        int idColunaSala    = colunaSala.getId();
        int idTvSala        = tvSala.getId();
        int idLuzCozinha    = luzCozinha.getId();
        int idLuzQuarto     = luzQuarto.getId();
        int idCortinaQuarto = cortinaQuarto.getId();
        int idAcQuarto      = acQuarto.getId();
        int idSensorChuva   = sensorChuva.getId();
        int idSensorLux     = sensorLux.getId();
        int idSensorTemp    = sensorTemp.getId();

        // Ligar sensores (estes precisam de estar ligados para reportar leituras)
        ligarDispositivo(idCasaAna, "Exterior", idSensorChuva);
        ligarDispositivo(idCasaAna, "Exterior", idSensorLux);
        ligarDispositivo(idCasaAna, "Exterior", idSensorTemp);

        // Simular leituras nos sensores reais (acedendo via casa, que devolve
        // referências reais — estamos dentro do model, é legítimo)

        simularLeituraSensor(idCasaAna, "Exterior", idSensorChuva, 7.5);
        simularLeituraSensor(idCasaAna, "Exterior", idSensorLux, 50.0);
        simularLeituraSensor(idCasaAna, "Exterior", idSensorTemp, 18.0);

        // 3. Casa 2 — admin: Bruno; Carla é utilizadora
        logout();
        login("bruno@teste.pt", "bruno");
        criarCasa("Casa do Bruno", "Avenida Central 5, Guimarães");
        String idCasaBruno = ultimoIdCasaCriada();

        adicionarDivisao(idCasaBruno, "Sala");
        adicionarDivisao(idCasaBruno, "Garagem");

        adicionarDispositivoADivisao(idCasaBruno, "Sala",
            new Lampada("Luz Sala B", "IKEA", "Tradfri", 8.0, 100, 3000));
        adicionarDispositivoADivisao(idCasaBruno, "Garagem",
            new PortaoGaragem("Portão", "Hörmann", "G1", 200.0, 0));

        adicionarUtilizadorACasa(idCasaBruno, "carla@teste.pt");

        // 4. Voltar à Ana para criar automações/escalonamentos/cenários da casa dela
        logout();
        login("ana@teste.pt", "ana");

        // Automação 1: chuva forte → fechar cortinas da casa toda
        List<Integer> idsCortinas = Arrays.asList(idCortinaSala, idCortinaQuarto);
        criarAutomacao(idCasaAna,
            "Fechar cortinas se chover",
            new Condicaochuva(idSensorChuva, 5.0),
            new AcaoFecharCortinas(idsCortinas));

        // Automação 2: luminosidade baixa → ligar todas as lâmpadas
        List<Integer> idsLampadas = Arrays.asList(idLuzSala, idLuzCozinha, idLuzQuarto);
        criarAutomacao(idCasaAna,
            "Ligar luzes se estiver escuro",
            new Condicaoluminosidade(idSensorLux, 100.0),
            new AcaoLigarDispositivos(idsLampadas));

        // Escalonamento: luzes da sala 19:00–23:00, repete diariamente
        List<Integer> idsLuzesSala = Arrays.asList(idLuzSala);
        criarEscalonamento(idCasaAna,
            "Luzes Sala à noite",
            LocalTime.of(19, 0),
            LocalTime.of(23, 0),
            new AcaoLigarDispositivos(idsLuzesSala),
            new AcaoDesligarTodos(idsLuzesSala),
            true);

        // Cenário "Sair de Casa": desliga tudo (menos sensores e frigorífico)
        // e fecha todas as cortinas
        List<Integer> idsParaDesligar = Arrays.asList(
            idLuzSala, idLuzCozinha, idLuzQuarto,
            idColunaSala, idTvSala, idAcQuarto);
        String idSair = criarCenario(idCasaAna, "Sair de Casa", "Desliga tudo ao sair");
        adicionarAcaoACenario(idSair,
            new AcaoDesligarTodos(idsParaDesligar, "Desligar dispositivos da casa"));
        adicionarAcaoACenario(idSair,
            new AcaoAbrirFecharCortinas(idsCortinas, false));

        // Cenário "Acordar": abre cortinas do quarto, liga luz suave
        String idAcordar = criarCenario(idCasaAna, "Acordar", "Abre cortinas e liga luz");
        adicionarAcaoACenario(idAcordar,
            new AcaoAbrirFecharCortinas(Arrays.asList(idCortinaQuarto), true));
        adicionarAcaoACenario(idAcordar,
            new AcaoLigarDispositivos(Arrays.asList(idLuzQuarto), "Ligar luz do quarto"));

        // Logout no fim — deixa o sistema pronto para o utilizador fazer login
        logout();
    }

    /**
     * Helper privado para apanhar o id da casa que o utilizador logado
     * acabou de criar (a mais recente das que ele administra).
     */

    /**
 * Devolve o ID da casa mais recentemente criada pelo utilizador logado.
 * Usado internamente pelo popularEstadoTeste().
 */
private String ultimoIdCasaCriada() {
    Utilizador user = getUserLogado();
    // getCasasComoAdmin devolve um Set — para apanhar o último
    // aproveitamos que o ID tem formato "C1", "C2", etc.
    // e devolvemos o que tem o número mais alto
    return user.getCasasComoAdmin().stream()
            .max(Comparator.comparingInt(id -> Integer.parseInt(id.substring(1))))
            .orElseThrow(() -> new IllegalStateException("Nenhuma casa encontrada."));
}

    public String estadoDetalhadoDispositivo(String idCasa, String nomeDivisao, int idDispositivo)
            throws CasaNaoExisteException, DivisaoNaoExisteException, DispositivoNaoExisteException {

        Casa casa = validarAcessoCasa(idCasa);
        return casa.estadoDetalhadoDispositivo(nomeDivisao, idDispositivo);
    }

    public String estadoDetalhadoTodasDivisoes(String idCasa)
            throws CasaNaoExisteException {

        Casa casa = validarAcessoCasa(idCasa);
        return casa.estadoDetalhadoTodasDivisoes();
    }

public Map<String, String> listarUtilizadoresDaCasa(String idCasa)
        throws CasaNaoExisteException, NotAdminException {

    Utilizador user = getUserLogado();

    if (!this.casas.containsKey(idCasa)) {
        throw new CasaNaoExisteException("Casa não existe.");
    }

    if (!user.isUtilizadorCasa(idCasa)) {
        throw new NotAdminException("Não tem acesso a esta casa.");
    }

    Map<String, String> resultado = new LinkedHashMap<>();

    for (Utilizador u : this.utilizadores.values()) {

        if (u.isAdminCasa(idCasa)) {
            resultado.put(
                    u.getNome() + " (" + u.getEmail() + ")",
                    "Administrador"
            );
        }
        else if (u.isUtilizadorCasa(idCasa)) {
            resultado.put(
                    u.getNome() + " (" + u.getEmail() + ")",
                    "Utilizador"
            );
        }
    }

    return resultado;
    }

    public String getIdCasaDoCenario(String idCenario) {
        Utilizador user = getUserLogado();

        String idCasa = user.getIdCasaDoCenario(idCenario);

        if (idCasa == null) {
            throw new IllegalArgumentException("Cenário não existe.");
        }

        return idCasa;
    }

    public String resumoDispositivosCasa(String idCasa)
            throws CasaNaoExisteException {

        Casa casa = validarAcessoCasa(idCasa);
        return casa.resumoDispositivosPorDivisao();
    }

    public void validarAcaoSobreDispositivos(String idCasa, String tipoAcao, List<Integer> idsDispositivos)
            throws CasaNaoExisteException, DispositivoNaoExisteException, NivelInvalidoException {

        Casa casa = validarAcessoCasa(idCasa);
        casa.validarAcaoSobreDispositivos(tipoAcao, idsDispositivos);
    }
}