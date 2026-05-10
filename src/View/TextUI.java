package View;

import Model.Acoes.*;
import Model.Automacoes.*;
import Model.Automacoes.condicoes.*;
import Model.Casa.Casa;
import Model.Cenarios.Cenario;
import Model.Dispositivos.*;
import Controller.DomusControl;
import Model.Interfaces.AcaoAutomacao;
import Model.Sugestoes.SugestaoEscalonamento;
import Model.Utilizador.Utilizador;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TextUI {

    private DomusControl controller;
    private final Scanner sc;

    public TextUI() {
        this.controller = new DomusControl();
        this.sc = new Scanner(System.in);
    }

    public TextUI(DomusControl controller) {
        this.controller = controller;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        System.out.println("========================================");
        System.out.println("   Bem-vindo ao DomusControl!");
        System.out.println("   Tempo simulado: " + this.controller.getTempoSimulado());
        System.out.println("========================================");

        NewMenu menuInicial = new NewMenu("Controller", new String[]{
                "Registar novo utilizador",
                "Fazer Login",
                "Carregar estado de ficheiro",
                "Gravar estado para ficheiro",
                "Carregar estado de teste"
        });

        menuInicial.setHandler(1, this::registarUtilizador);
        menuInicial.setHandler(2, this::fazerLogin);
        menuInicial.setHandler(3, this::carregarEstado);
        menuInicial.setHandler(4, this::gravarEstado);
        menuInicial.setHandler(5, this::carregarEstadoTeste);

        menuInicial.run();
        System.out.println("Até à próxima!");
    }

    // ============================================================
    // AUTENTICAÇÃO
    // ============================================================

    private void registarUtilizador() {
        String nome = pedirTexto("Nome: ");
        String email = pedirTexto("Email: ");
        String password = pedirTexto("Password: ");

        try {
            this.controller.criarUtilizador(nome, password, email);
            System.out.println("✓ Utilizador registado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void fazerLogin() {
        String email = pedirTexto("Email: ");
        String password = pedirTexto("Password: ");

        try {
            Utilizador user = this.controller.login(email, password);
            System.out.println("✓ Login efetuado! Bem-vindo, " + user.getNome() + ".");
            menuPrincipal();
            this.controller.logout();
            System.out.println("Sessão terminada.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ============================================================
    // MENU PRINCIPAL
    // ============================================================

    private void menuPrincipal() {
        NewMenu menu = new NewMenu("Menu Principal", new String[]{
                "Gestão de Casas",
                "Gestão de Dispositivos",
                "Automações",
                "Escalonamentos",
                "Cenários",
                "Sugestões Automáticas",
                "Estatísticas",
                "Avançar Tempo Simulado",
                "Ver Tempo Simulado",
                "Gravar Estado"
        });

        menu.setHandler(1, this::menuGestaoCasas);
        menu.setHandler(2, this::menuGestaoDispositivos);
        menu.setHandler(3, this::menuAutomacoes);
        menu.setHandler(4, this::menuEscalonamentos);
        menu.setHandler(5, this::menuCenarios);
        menu.setHandler(6, this::menuSugestoes);
        menu.setHandler(7, this::menuEstatisticas);
        menu.setHandler(8, this::avancarTempo);
        menu.setHandler(9, () -> System.out.println("Tempo simulado: " + this.controller.getTempoSimulado()));
        menu.setHandler(10, this::gravarEstado);

        menu.run();
    }

    // ============================================================
    // GESTÃO DE CASAS
    // ============================================================

    private void menuGestaoCasas() {
        NewMenu menu = new NewMenu("Gestão de Casas", new String[]{
                "Criar Casa",
                "Adicionar Divisão a Casa",
                "Adicionar Utilizador a Casa",
                "Tornar Utilizador Administrador de Casa",
                "Listar Utilizadores de uma Casa",
                "Eliminar Casa",
                "Sair de Casa"
        });

        menu.setHandler(1, this::criarCasa);
        menu.setHandler(2, this::adicionarDivisao);
        menu.setHandler(3, this::adicionarUtilizadorACasa);
        menu.setHandler(4, this::adicionarAdminACasa);
        menu.setHandler(5, this::listarUtilizadoresDaCasa);
        menu.setHandler(6, this::eliminarCasa);
        menu.setHandler(7, this::sairDeCasa);

        menu.run();
    }

    private void criarCasa() {
        String nome = pedirTexto("Nome da casa: ");
        String morada = pedirTexto("Morada: ");

        try {
            this.controller.criarCasa(nome, morada);
            System.out.println("✓ Casa criada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarDivisao() {
        String idCasa = pedirIdCasa();

        if (idCasa == null || idCasa.isEmpty()) {
            return;
        }

        String nomeDivisao = pedirTexto("Nome da nova divisão: ");

        try {
            this.controller.adicionarDivisao(idCasa, nomeDivisao);
            System.out.println("✓ Divisão adicionada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarUtilizadorACasa() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String email = pedirTexto("Email do utilizador: ");

        try {
            this.controller.adicionarUtilizadorACasa(idCasa, email);
            System.out.println("✓ Utilizador adicionado à casa!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarAdminACasa() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String email = pedirTexto("Email do utilizador: ");

        try {
            this.controller.adicionarAdminACasa(idCasa, email);
            System.out.println("✓ Utilizador promovido a administrador!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void eliminarCasa() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;

        try {
            this.controller.eliminarCasa(idCasa);
            System.out.println("✓ Casa eliminada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void sairDeCasa() {
        String idCasa = pedirIdCasa();

        if (idCasa == null || idCasa.isEmpty()) {
            return;
        }

        try {
            this.controller.sairDeCasa(idCasa);
            System.out.println("✓ Saiu da casa.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ============================================================
    // GESTÃO DE DISPOSITIVOS
    // ============================================================

    private void menuGestaoDispositivos() {
        NewMenu menu = new NewMenu("Gestão de Dispositivos", new String[]{
                "Adicionar Dispositivo a Divisão",
                "Ligar Dispositivo",
                "Desligar Dispositivo",
                "Alterar Nível (Regulável)",
                "Alterar Temperatura",
                "Abrir Dispositivo",
                "Fechar Dispositivo",
                "Ver Estado Detalhado",
                "Simular Leitura do Sensor"
        });

        menu.setHandler(1, this::adicionarDispositivo);
        menu.setHandler(2, this::ligarDispositivo);
        menu.setHandler(3, this::desligarDispositivo);
        menu.setHandler(4, this::alterarNivel);
        menu.setHandler(5, this::alterarTemperatura);
        menu.setHandler(6, this::abrirDispositivo);
        menu.setHandler(7, this::fecharDispositivo);
        menu.setHandler(8, this::verEstadoDetalhado);
        menu.setHandler(9, this::simularLeitura);

        menu.run();
    }

    private void adicionarDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;
        String nomeDivisao = pedirNomeDivisao(idCasa);
        if (nomeDivisao.isEmpty()) return;

        System.out.println("\nTipos de dispositivo:");
        System.out.println("  1-Lampada  2-ColunaSom  3-Cortina  4-Persiana  5-ArCondicionado");
        System.out.println("  6-Televisao  7-Forno  8-Frigorifico  9-Exaustor  10-Tomada");
        System.out.println("  11-Relay  12-Fechadura  13-Portao  14-Termostato  15-Rega");
        System.out.println("  16-SensorChuva  17-SensorLuz  18-SensorTemp  19-Camara");

        String tipo = pedirTexto("Tipo (número): ");
        String nome = pedirTexto("Nome do dispositivo: ");
        String marca = pedirTexto("Marca: ");
        String modelo = pedirTexto("Modelo: ");
        double consumo = pedirDouble("Consumo por hora (Wh): ");

        Dispositivo d = criarDispositivo(tipo, nome, marca, modelo, consumo);
        if (d == null) {
            System.out.println("Tipo inválido.");
            return;
        }

        try {
            this.controller.adicionarDispositivoADivisao(idCasa, nomeDivisao, d);
            System.out.println("✓ Dispositivo adicionado com ID=" + d.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void ligarDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return; // PARA AQUI se a casa for inválida

        String div = pedirNomeDivisao(idCasa);
        if (div == null || div.isEmpty()) return;

        int id = pedirIdDispositivo(idCasa, div);
        if (id < 0) return;

        try {
            this.controller.ligarDispositivo(idCasa, div, id);
            System.out.println("✓ Ligado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void desligarDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        try {
            this.controller.desligarDispositivo(idCasa, div, id);
            System.out.println("✓ Desligado!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void abrirDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        try {
            this.controller.abrirDispositivo(idCasa, div, id);
            System.out.println("✓ Aberto!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void fecharDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        try {
            this.controller.fecharDispositivo(idCasa, div, id);
            System.out.println("✓ Fechado!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void alterarNivel() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        int nivel = pedirInteiro("Novo nível (0-100): ");
        try {
            this.controller.alterarNivelDispositivo(idCasa, div, id, nivel);
            System.out.println("✓ Nível alterado!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void alterarTemperatura() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        double temp = pedirDouble("Temperatura: ");
        try {
            this.controller.alterarTemperaturaDispositivo(idCasa, div, id, temp);
            System.out.println("✓ Temperatura alterada!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void verEstadoDetalhado() {
        String idCasa = pedirIdCasa();

        if (idCasa == null || idCasa.isEmpty()) {
            return;
        }

        System.out.println("\n=== Ver Estado Detalhado ===");
        System.out.println("1. Ver dispositivo específico");
        System.out.println("2. Ver todas as divisões da casa");

        int opcao = pedirInteiro("Opção: ");

        try {
            if (opcao == 1) {
                String div = pedirNomeDivisao(idCasa);

                if (div == null || div.isEmpty()) {
                    return;
                }

                int id = pedirIdDispositivo(idCasa, div);

                if (id < 0) {
                    return;
                }

                System.out.println(this.controller.estadoDetalhadoDispositivo(idCasa, div, id));
            }
            else if (opcao == 2) {
                System.out.println(this.controller.estadoDetalhadoTodasDivisoes(idCasa));
            }
            else {
                System.out.println("Opção inválida.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao obter estado detalhado: " + e.getMessage());
        }
    }
    private void simularLeitura() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        if (div == null) return;
        int id = pedirIdDispositivo(idCasa, div);
        double valor = pedirDouble("Novo valor para o sensor: ");

        try {
            this.controller.simularLeituraSensor(idCasa, div, id, valor);
            System.out.println("✓ Sensor atualizado para " + valor);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ============================================================
    // AUTOMAÇÕES, ESCALONAMENTOS E CENÁRIOS
    // ============================================================

    private void menuAutomacoes() {
        NewMenu menu = new NewMenu("Automações", new String[]{
                "Criar Automação",
                "Listar Automações de uma Casa",
                "Ativar Automação",
                "Desativar Automação",
                "Remover Automação"
        });
        menu.setHandler(1, this::criarAutomacao);
        menu.setHandler(2, this::listarAutomacoes);
        menu.setHandler(3, this::ativarAutomacao);
        menu.setHandler(4, this::desativarAutomacao);
        menu.setHandler(5, this::removerAutomacao);
        menu.run();
    }

    private void criarAutomacao() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String nome = pedirTexto("Nome da automação: ");

        CondicaoAutomacao cond = criarCondicaoPorMenu(idCasa);
        if (cond == null) return;

        AcaoAutomacao acao = criarAcaoPorMenu(idCasa);
        if (acao == null) return;

        try {
            this.controller.criarAutomacao(idCasa, nome, cond, acao);
            System.out.println("✓ Automação criada!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private CondicaoAutomacao criarCondicaoPorMenu(String idCasa) {
        System.out.println("\nTipo de condição:");
        System.out.println("  1 - Chuva >= limiar");
        System.out.println("  2 - Luminosidade < limiar");
        String opcao = pedirTexto("Opção: ");

        String div = pedirNomeDivisao(idCasa);
        int idSensor = pedirIdDispositivo(idCasa, div);
        double limiar = pedirDouble("Limiar: ");

        return opcao.equals("1") ? new Condicaochuva(idSensor, limiar) : new Condicaoluminosidade(idSensor, limiar);
    }

    private void menuEscalonamentos() {
        NewMenu menu = new NewMenu("Escalonamentos", new String[]{
                "Criar Escalonamento Simples",
                "Criar com Início e Fim",
                "Listar Escalonamentos",
                "Remover Escalonamento"
        });
        menu.setHandler(1, this::criarEscalonamentoSimples);
        menu.setHandler(2, this::criarEscalonamentoComFim);
        menu.setHandler(3, this::listarEscalonamentos);
        menu.setHandler(4, this::removerEscalonamento);
        menu.run();
    }

    private void menuCenarios() {
        NewMenu menu = new NewMenu("Cenários", new String[]{
                "Criar Cenário",
                "Adicionar Ação",
                "Listar Cenários",
                "Ativar Cenário",
                "Remover Cenário"
        });
        menu.setHandler(1, this::criarCenario);
        menu.setHandler(2, this::adicionarAcaoACenario);
        menu.setHandler(3, this::listarCenarios);
        menu.setHandler(4, this::ativarCenario);
        menu.setHandler(5, this::removerCenario);
        menu.run();
    }

    // ============================================================
    // SUGESTÕES E ESTATÍSTICAS
    // ============================================================

    private void menuSugestoes() {
        NewMenu menu = new NewMenu("Sugestões", new String[]{"Ver Sugestões", "Como funciona?"});
        menu.setHandler(1, this::verSugestoesEscalonamento);
        menu.setHandler(2, this::explicarSugestoes);
        menu.run();
    }

    private void menuEstatisticas() {
        NewMenu menu = new NewMenu("Estatísticas", new String[]{
                "Casa mais gastadora", "Top Tempo (Casa)", "Top Ativações (Casa)",
                "Top Divisões", "Consumo Total", "Consumo por Casa"
        });

        menu.setHandler(1, this::estatisticaCasaMaisConsome);
        menu.setHandler(2, this::estatisticaTopTempo);
        menu.setHandler(3, this::estatisticaTopAtivacoes);
        menu.setHandler(4, this::estatisticaTopDivisoes);
        menu.setHandler(5, this::estatisticaConsumoTotalSistema);
        menu.setHandler(6, this::estatisticaConsumoPorCasa);

        menu.run();
    }

    // ============================================================
    // HELPERS E VISUALIZAÇÃO
    // ============================================================

    private String pedirIdCasa() {
        mostrarCasas();

        System.out.print("\nDigita o ID da casa: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            return "";
        }

        boolean existe = this.controller.getCasasDoUserLogado().stream()
                .anyMatch(c -> c.getId().equalsIgnoreCase(id));

        if (!existe) {
            System.out.println("Erro: A casa '" + id + "' não existe ou não tens acesso.");
            return "";
        }

        return this.controller.getCasasDoUserLogado().stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst()
                .map(Casa::getId)
                .orElse("");
    }

    private String pedirNomeDivisao(String idCasa) {
        mostrarDivisoes(idCasa);

        System.out.print("\nNome da divisão: ");
        String nome = sc.nextLine().trim();

        if (nome.isEmpty()) {
            return "";
        }

        try {
            Casa casa = this.controller.getCasasDoUserLogado().stream()
                    .filter(c -> c.getId().equalsIgnoreCase(idCasa))
                    .findFirst()
                    .orElse(null);

            if (casa == null || casa.getDivisao(nome) == null) {
                System.out.println("Erro: A divisão '" + nome + "' não existe nesta casa.");
                return "";
            }

        } catch (Exception e) {
            System.out.println("Erro ao validar divisão: " + e.getMessage());
            return "";
        }

        return nome;
    }


    private int pedirIdDispositivo(String idCasa, String nomeDivisao) {
        mostrarDispositivos(idCasa, nomeDivisao);
        System.out.print("\nID do dispositivo: ");
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private void mostrarCasas() {
        List<Casa> casas = this.controller.getCasasDoUserLogado();
        if (casas.isEmpty()) System.out.println("\n(Sem casas)");
        else {
            System.out.println("\n=== As Tuas Casas ===");
            for (Casa c : casas) System.out.println(" [" + c.getId() + "] " + c.getNome());
        }
    }

    private void mostrarDivisoes(String idCasa) {
        Casa casa = this.controller.getCasasDoUserLogado().stream().filter(c->c.getId().equals(idCasa)).findFirst().orElse(null);
        if (casa == null) return;
        System.out.println("\n=== Divisões em " + casa.getNome() + " ===");
        casa.getDivisoes().forEach((n, d) -> System.out.println(" · " + n));
    }

    private void mostrarDispositivos(String idCasa, String divNome) {
        Casa casa = this.controller.getCasasDoUserLogado().stream().filter(c->c.getId().equals(idCasa)).findFirst().orElse(null);
        if (casa == null || casa.getDivisao(divNome) == null) return;
        System.out.println("\n=== Dispositivos em " + divNome + " ===");
        for (Dispositivo d : casa.getDivisao(divNome).listaDispositivos())
            System.out.println(" [ID:" + d.getId() + "] " + d.getNome() + " (" + (d.isLigado() ? "ON" : "OFF") + ")");
    }

    // Métodos de input simplificados
    private String pedirTexto(String m) { System.out.print(m); return sc.nextLine().trim(); }
    private int pedirInteiro(String m) { System.out.print(m); try { return Integer.parseInt(sc.nextLine().trim()); } catch(Exception e) { return -1; } }
    private double pedirDouble(String m) { System.out.print(m); try { return Double.parseDouble(sc.nextLine().trim()); } catch(Exception e) { return -1.0; } }
    private boolean pedirBoolean(String m) { System.out.print(m); String r = sc.nextLine().trim().toLowerCase(); return r.equals("s") || r.equals("sim"); }
    private LocalTime pedirHora(String m) { System.out.print(m); try { return LocalTime.parse(sc.nextLine().trim()); } catch(Exception e) { return null; } }

    private List<Integer> pedirListaIds(String m) {
        System.out.print(m);
        String[] partes = sc.nextLine().trim().split(",");
        List<Integer> ids = new ArrayList<>();
        for (String p : partes) { try { ids.add(Integer.parseInt(p.trim())); } catch(Exception e) {} }
        return ids;
    }

    // Métodos que faltavam no esqueleto (Stubs para compilar)
    private void listarAutomacoes() { String id = pedirIdCasa(); try { this.controller.listarAutomacoes(id).forEach(System.out::println); } catch(Exception e){} }

    private void ativarAutomacao() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        mostrarAutomacoesDaCasa(idCasa);

        String idAutomacao = pedirTexto("ID da automação a ativar: ");

        try {
            this.controller.ativarAutomacao(idCasa, idAutomacao);
            System.out.println("✓ Automação ativada.");
        } catch (Exception e) {
            System.out.println("Erro ao ativar automação: " + e.getMessage());
        }
    }


    private void desativarAutomacao() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        mostrarAutomacoesDaCasa(idCasa);

        String idAutomacao = pedirTexto("ID da automação a desativar: ");

        try {
            this.controller.desativarAutomacao(idCasa, idAutomacao);
            System.out.println("✓ Automação desativada.");
        } catch (Exception e) {
            System.out.println("Erro ao desativar automação: " + e.getMessage());
        }
    }

    private void removerAutomacao() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        mostrarAutomacoesDaCasa(idCasa);

        String idAutomacao = pedirTexto("ID da automação a remover: ");

        try {
            this.controller.removerAutomacao(idCasa, idAutomacao);
            System.out.println("✓ Automação removida.");
        } catch (Exception e) {
            System.out.println("Erro ao remover automação: " + e.getMessage());
        }
    }

    private void mostrarEscalonamentosDaCasa(String idCasa) {
        try {
            List<Escalonamento> escalonamentos = this.controller.listarEscalonamentos(idCasa);

            if (escalonamentos.isEmpty()) {
                System.out.println("\nEsta casa não tem escalonamentos.");
                return;
            }

            System.out.println("\n=== Escalonamentos da Casa " + idCasa + " ===");
            escalonamentos.forEach(e -> System.out.println("  " + e));

        } catch (Exception e) {
            System.out.println("Erro ao listar escalonamentos: " + e.getMessage());
        }
    }

    private void criarEscalonamentoSimples() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        String nome = pedirTexto("Nome do escalonamento: ");

        LocalTime horaInicio = pedirHora("Hora de execução (HH:mm): ");
        if (horaInicio == null) {
            System.out.println("Hora inválida. Usa o formato HH:mm, por exemplo 19:30.");
            return;
        }

        System.out.println("\nAção a executar:");
        AcaoAutomacao acaoInicio = criarAcaoPorMenu(idCasa);
        if (acaoInicio == null) {
            System.out.println("Ação inválida.");
            return;
        }

        boolean repete = pedirBoolean("Repetir diariamente? (s/n): ");

        try {
            String idEscalonamento = this.controller.criarEscalonamento(
                    idCasa,
                    nome,
                    horaInicio,
                    acaoInicio,
                    repete
            );

            System.out.println("✓ Escalonamento criado com ID=" + idEscalonamento);

        } catch (Exception e) {
            System.out.println("Erro ao criar escalonamento: " + e.getMessage());
        }
    }

    private void criarEscalonamentoComFim() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        String nome = pedirTexto("Nome do escalonamento: ");

        LocalTime horaInicio = pedirHora("Hora de início (HH:mm): ");
        if (horaInicio == null) {
            System.out.println("Hora de início inválida. Usa o formato HH:mm, por exemplo 19:00.");
            return;
        }

        LocalTime horaFim = pedirHora("Hora de fim (HH:mm): ");
        if (horaFim == null) {
            System.out.println("Hora de fim inválida. Usa o formato HH:mm, por exemplo 23:00.");
            return;
        }

        System.out.println("\nAção a executar no início:");
        AcaoAutomacao acaoInicio = criarAcaoPorMenu(idCasa);
        if (acaoInicio == null) {
            System.out.println("Ação de início inválida.");
            return;
        }

        System.out.println("\nAção a executar no fim:");
        AcaoAutomacao acaoFim = criarAcaoPorMenu(idCasa);
        if (acaoFim == null) {
            System.out.println("Ação de fim inválida.");
            return;
        }

        boolean repete = pedirBoolean("Repetir diariamente? (s/n): ");

        try {
            String idEscalonamento = this.controller.criarEscalonamento(
                    idCasa,
                    nome,
                    horaInicio,
                    horaFim,
                    acaoInicio,
                    acaoFim,
                    repete
            );

            System.out.println("✓ Escalonamento criado com ID=" + idEscalonamento);

        } catch (Exception e) {
            System.out.println("Erro ao criar escalonamento: " + e.getMessage());
        }
    }

    private void listarEscalonamentos() {
        String id = pedirIdCasa();
        try {
            this.controller.listarEscalonamentos(id).forEach(System.out::println);
        } catch(Exception e){}
    }

    private void removerEscalonamento() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;

        mostrarEscalonamentosDaCasa(idCasa);

        String idEscalonamento = pedirTexto("ID do escalonamento a remover: ");

        try {
            this.controller.removerEscalonamento(idCasa, idEscalonamento);
            System.out.println("✓ Escalonamento removido.");
        } catch (Exception e) {
            System.out.println("Erro ao remover escalonamento: " + e.getMessage());
        }
    }

    private void mostrarCenariosDoUtilizador() {
        try {
            List<Cenario> cenarios = this.controller.listarCenarios();

            if (cenarios.isEmpty()) {
                System.out.println("\nNão tens cenários criados.");
                return;
            }

            System.out.println("\n=== Os Teus Cenários ===");
            cenarios.forEach(c -> System.out.println("  " + c));

        } catch (Exception e) {
            System.out.println("Erro ao listar cenários: " + e.getMessage());
        }
    }

    private void criarCenario() { String idC = pedirIdCasa(); String n = pedirTexto("Nome: "); String d = pedirTexto("Desc: "); try { this.controller.criarCenario(idC, n, d); } catch(Exception e){} }

    private void adicionarAcaoACenario() {
        mostrarCenariosDoUtilizador();

        String idCenario = pedirTexto("ID do cenário: ");

        try {
            String idCasa = this.controller.getIdCasaDoCenario(idCenario);

            AcaoAutomacao acao = criarAcaoPorMenu(idCasa);

            if (acao == null) {
                System.out.println("Nenhuma ação foi adicionada.");
                return;
            }

            this.controller.adicionarAcaoACenario(idCenario, acao);
            System.out.println("✓ Ação adicionada ao cenário.");

        } catch (Exception e) {
            System.out.println("Erro ao adicionar ação ao cenário: " + e.getMessage());
        }
    }

    private void listarCenarios() { try { this.controller.listarCenarios().forEach(System.out::println); } catch(Exception e){} }

    private void ativarCenario() {
        mostrarCenariosDoUtilizador();

        String id = pedirTexto("ID do cenário a ativar: ");

        try {
            int executadas = this.controller.ativarCenario(id);
            System.out.println("✓ Cenário ativado. Ações executadas: " + executadas);
        } catch (Exception e) {
            System.out.println("Erro ao ativar cenário: " + e.getMessage());
        }
    }

    private void removerCenario() {
        mostrarCenariosDoUtilizador();

        String id = pedirTexto("ID do cenário a remover: ");

        try {
            this.controller.removerCenario(id);
            System.out.println("✓ Cenário removido.");
        } catch (Exception e) {
            System.out.println("Erro ao remover cenário: " + e.getMessage());
        }}


    private void verSugestoesEscalonamento() {
        try {
            List<SugestaoEscalonamento> sugestoes = this.controller.gerarSugestoesEscalonamento();

            if (sugestoes.isEmpty()) {
                System.out.println("\nAinda não existem sugestões automáticas.");
                System.out.println("Para gerar sugestões, usa manualmente o mesmo dispositivo");
                System.out.println("em dias diferentes e em horários semelhantes.");
                return;
            }

            System.out.println("\n=== Sugestões Automáticas ===");

            for (int i = 0; i < sugestoes.size(); i++) {
                SugestaoEscalonamento s = sugestoes.get(i);

                System.out.println("\n[" + (i + 1) + "]");
                System.out.println(s.mensagemUtilizador());
            }

            boolean aceitar = pedirBoolean("\nQueres aceitar alguma sugestão? (s/n): ");

            if (!aceitar) {
                System.out.println("Nenhuma sugestão foi aceite.");
                return;
            }

            int opcao = pedirInteiro("Número da sugestão a aceitar: ");

            if (opcao < 1 || opcao > sugestoes.size()) {
                System.out.println("Sugestão inválida.");
                return;
            }

            SugestaoEscalonamento escolhida = sugestoes.get(opcao - 1);
            String idEscalonamento = this.controller.aceitarSugestaoEscalonamento(escolhida);

            System.out.println("✓ Escalonamento criado automaticamente com ID=" + idEscalonamento);

        } catch (Exception e) {
            System.out.println("Erro ao gerar sugestões: " + e.getMessage());
        }
    }

    private void explicarSugestoes() {
        System.out.println("\n=== Como funcionam as Sugestões Automáticas ===\n");

        System.out.println("As sugestões automáticas são geradas a partir do histórico de interações do utilizador.");
        System.out.println("Sempre que um utilizador liga ou desliga manualmente um dispositivo, o sistema regista:");
        System.out.println("  - a casa onde ocorreu a ação;");
        System.out.println("  - o ID do dispositivo;");
        System.out.println("  - o tipo de ação realizada, por exemplo LIGAR ou DESLIGAR;");
        System.out.println("  - a data e hora simulada em que a ação aconteceu.\n");

        System.out.println("Com base nesses registos, o sistema procura padrões repetidos.");
        System.out.println("Por exemplo, se o utilizador costuma ligar a mesma luz por volta das 19:00");
        System.out.println("em vários dias diferentes, o sistema interpreta isso como um possível hábito.\n");

        System.out.println("Quando é detetado um padrão suficientemente consistente, o DomusControl gera uma sugestão.");
        System.out.println("Essa sugestão propõe a criação automática de um escalonamento, para que essa ação");
        System.out.println("passe a acontecer sozinha no futuro.\n");

        System.out.println("Exemplo:");
        System.out.println("  Se o utilizador ligar a Luz da Sala às 19:00 em vários dias,");
        System.out.println("  o sistema pode sugerir:");
        System.out.println("  \"Criar escalonamento para ligar a Luz da Sala todos os dias às 19:00\".\n");

        System.out.println("O utilizador pode aceitar ou ignorar a sugestão.");
        System.out.println("Se aceitar, o sistema cria automaticamente um escalonamento associado à casa indicada.");
        System.out.println("Se ignorar, nenhuma alteração é feita ao estado da aplicação.\n");

        System.out.println("Esta funcionalidade torna o sistema mais inteligente, pois permite que o DomusControl");
        System.out.println("aprenda hábitos simples do utilizador e proponha automatizações úteis.");
    }

    private void estatisticaCasaMaisConsome() {
        try {
            System.out.println("\n=== Casa que mais consome ===");
            System.out.println(this.controller.casaQueMaisConsome());

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }

    private void estatisticaTopTempo() {
        String id = pedirIdCasa();

        try {
            List<Dispositivo> dispositivos = this.controller.top3DispositivosPorTempo(id);

            System.out.println("\n=== Top 3 dispositivos por tempo de utilização ===");

            if (dispositivos.isEmpty()) {
                System.out.println("Esta casa ainda não tem dispositivos.");
                return;
            }

            int posicao = 1;

            for (Dispositivo d : dispositivos) {
                System.out.printf(
                        "%d. [%d] %s - %.2f minutos ligado - %.2f Wh%n",
                        posicao++,
                        d.getId(),
                        d.getNome(),
                        d.getTempoLigado(),
                        d.consumoTotalDispositivo()
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }

    private void estatisticaTopAtivacoes() {
        String id = pedirIdCasa();

        try {
            List<Dispositivo> dispositivos = this.controller.top3DispositivosPorAtivacoes(id);

            System.out.println("\n=== Top 3 dispositivos por número de ativações ===");

            if (dispositivos.isEmpty()) {
                System.out.println("Esta casa ainda não tem dispositivos.");
                return;
            }

            int posicao = 1;

            for (Dispositivo d : dispositivos) {
                System.out.printf(
                        "%d. [%d] %s - %d ativações - %.2f minutos ligado%n",
                        posicao++,
                        d.getId(),
                        d.getNome(),
                        d.getNumeroAtivacoes(),
                        d.getTempoLigado()
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }

    private void estatisticaTopDivisoes() {
        try {
            Map<String, Integer> divisoes = this.controller.top3DivisoesComMaisDispositivos();

            System.out.println("\n=== Top 3 divisões com mais dispositivos ===");

            if (divisoes.isEmpty()) {
                System.out.println("Ainda não existem divisões registadas.");
                return;
            }

            int posicao = 1;

            for (Map.Entry<String, Integer> entry : divisoes.entrySet()) {
                System.out.printf(
                        "%d. %s - %d dispositivos%n",
                        posicao++,
                        entry.getKey(),
                        entry.getValue()
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }

    private void estatisticaConsumoTotalSistema() {
        try {
            double total = this.controller.consumoTotalSistema();

            System.out.printf(
                    "\nConsumo total do sistema: %.2f Wh%n",
                    total
            );

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }

    private void estatisticaConsumoPorCasa() {
        try {
            Map<String, Double> consumos = this.controller.consumoPorCasa();

            System.out.println("\n=== Consumo por casa ===");

            if (consumos.isEmpty()) {
                System.out.println("Ainda não existem casas registadas.");
                return;
            }

            for (Map.Entry<String, Double> entry : consumos.entrySet()) {
                System.out.printf(
                        "%s - %.2f Wh%n",
                        entry.getKey(),
                        entry.getValue()
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao calcular estatística: " + e.getMessage());
        }
    }


    private void avancarTempo() { int m = pedirInteiro("Minutos: "); try { this.controller.avancarTempo(m); } catch(Exception e){} }
    private void carregarEstado() {
        String ficheiro = pedirTexto("Nome do ficheiro a carregar: ");

        try {
            this.controller = DomusControl.carregaEstado(ficheiro);
            System.out.println("\nSUCESSO: O estado foi carregado de '" + ficheiro + "'.");
            System.out.println("   Tempo atual no sistema: " + this.controller.getTempoSimulado());
            System.out.println("   Pode agora proceder ao Login.");
        } catch (Exception e) {
            System.out.println("\nERRO ao carregar: O ficheiro '" + ficheiro + "' não existe ou está corrompido.");
        }
    }
    private void gravarEstado() { String f = pedirTexto("Ficheiro: "); try { this.controller.gravaEstado(f); } catch(Exception e){} }

    private Dispositivo criarDispositivo(String tipo, String nome, String marca, String modelo, double consumo) {
        try {
            switch (tipo) {
                case "1":  return new Lampada(nome, marca, modelo, consumo, 100, 2700);
                case "3":  return new Cortina(nome, marca, modelo, consumo, 0);
                case "16": return new SensorChuva(nome, marca, modelo, consumo);
                case "17": return new SensorLuminosidade(nome, marca, modelo, consumo);
                // Acrescenta os outros cases conforme necessário...
                default:   return null;
            }
        } catch (Exception e) { return null; }
    }

    private AcaoAutomacao criarAcaoPorMenu(String idCasa) {
        List<AcaoAutomacao> acoes = new ArrayList<>();

        try {
            System.out.println(this.controller.resumoDispositivosCasa(idCasa));
        } catch (Exception e) {
            System.out.println("Erro ao listar dispositivos: " + e.getMessage());
            return null;
        }

        boolean continuar = true;

        while (continuar) {
            AcaoAutomacao acao = criarAcaoUnicaPorMenu(idCasa);

            if (acao != null) {
                acoes.add(acao);
                System.out.println("✓ Ação adicionada.");
            }

            continuar = pedirBoolean("Adicionar outra ação? (s/n): ");
        }

        if (acoes.isEmpty()) {
            return null;
        }

        if (acoes.size() == 1) {
            return acoes.get(0);
        }

        return new AcaoComposta(acoes);
    }


    private AcaoAutomacao criarAcaoUnicaPorMenu(String idCasa) {
        System.out.println("\nTipo de ação:");
        System.out.println("1 - Ligar");
        System.out.println("2 - Desligar");
        System.out.println("3 - Definir nível");
        System.out.println("4 - Abrir");
        System.out.println("5 - Fechar");

        String op = pedirTexto("Opção: ");

        List<Integer> ids = pedirListaIds("IDs dos dispositivos (separados por vírgula): ");

        if (ids.isEmpty()) {
            System.out.println("Nenhum ID válido introduzido.");
            return null;
        }

        try {
            switch (op) {
                case "1":
                    this.controller.validarAcaoSobreDispositivos(idCasa, "LIGAR", ids);
                    return new AcaoLigarDispositivos(ids);

                case "2":
                    this.controller.validarAcaoSobreDispositivos(idCasa, "DESLIGAR", ids);
                    return new AcaoDesligarTodos(ids);

                case "3":
                    this.controller.validarAcaoSobreDispositivos(idCasa, "NIVEL", ids);
                    int nivel = pedirInteiro("Nível (0-100): ");
                    return new AcaoDefinirNivel(ids, nivel);

                case "4":
                    this.controller.validarAcaoSobreDispositivos(idCasa, "ABRIR", ids);
                    return new AcaoAbrirFecharCortinas(ids, true);

                case "5":
                    this.controller.validarAcaoSobreDispositivos(idCasa, "FECHAR", ids);
                    return new AcaoAbrirFecharCortinas(ids, false);

                default:
                    System.out.println("Opção inválida.");
                    return null;
            }

        } catch (Exception e) {
            System.out.println("Ação inválida: " + e.getMessage());
            return null;
        }
    }
    private void carregarEstadoTeste() {
        try {
            this.controller.popularEstadoTeste();
            System.out.println("✓ Estado de teste carregado com sucesso!");
            System.out.println("  Logins disponíveis:");
            System.out.println("    ana@teste.pt   / ana");
            System.out.println("    bruno@teste.pt / bruno");
            System.out.println("    carla@teste.pt / carla");
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar estado de teste: " + e.getMessage());
        }
    }

    private void listarUtilizadoresDaCasa() {
        String idCasa = pedirIdCasa();

        if (idCasa == null || idCasa.isEmpty()) {
            return;
        }

        try {
            Map<String, String> utilizadores = this.controller.listarUtilizadoresDaCasa(idCasa);

            if (utilizadores.isEmpty()) {
                System.out.println("\nEsta casa não tem utilizadores associados.");
                return;
            }

            System.out.println("\n=== Utilizadores da Casa " + idCasa + " ===");

            for (Map.Entry<String, String> entry : utilizadores.entrySet()) {
                System.out.println(" · " + entry.getKey() + " -> " + entry.getValue());
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void mostrarAutomacoesDaCasa(String idCasa) {
        try {
            List<Automacao> automacoes = this.controller.listarAutomacoes(idCasa);

            if (automacoes.isEmpty()) {
                System.out.println("\nEsta casa não tem automações.");
                return;
            }

            System.out.println("\n=== Automações da Casa " + idCasa + " ===");
            automacoes.forEach(a -> System.out.println("  " + a));

        } catch (Exception e) {
            System.out.println("Erro ao listar automações: " + e.getMessage());
        }
    }

}