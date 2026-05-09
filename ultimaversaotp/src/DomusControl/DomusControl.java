package DomusControl;

import Sugestoes.SugestaoEscalonamento;
import Sugestoes.RegistoInteracao;
import Sugestoes.AnalisadorPadroes;
import Acoes.AcaoDesligarTodos;
import Acoes.AcaoLigarDispositivos;
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
import Dispositivos.SensorChuva;
import Dispositivos.SensorLuminosidade;

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
    // Método necessário para a TextUI funcionar
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

}