package UI;

import Acoes.*;
import Automacoes.*;
import Automacoes.condicoes.*;
import Casa.Casa;
import Dispositivos.*;
import Interfaces.AcaoAutomacao;
import Utilizador.Utilizador;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextUI {

    private DomusControl model;
    private final Scanner sc;

    public TextUI() {
        this.model = new DomusControl();
        this.sc = new Scanner(System.in);
    }

    public TextUI(DomusControl model) {
        this.model = model;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        System.out.println("========================================");
        System.out.println("   Bem-vindo ao DomusControl!");
        System.out.println("   Tempo simulado: " + this.model.getTempoSimulado());
        System.out.println("========================================");

        NewMenu menuInicial = new NewMenu("DomusControl", new String[]{
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

    private void carregarEstadoTeste() {
        try {
            this.model.popularEstadoTeste();
            System.out.println("✓ Estado de teste carregado!");
            System.out.println();
            System.out.println("Logins disponíveis:");
            System.out.println("  ana@teste.pt   / ana    (admin Casa da Ana — tem automações)");
            System.out.println("  bruno@teste.pt / bruno  (admin Casa do Bruno)");
            System.out.println("  carla@teste.pt / carla  (utilizadora da Casa do Bruno)");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ============================================================
    // AUTENTICAÇÃO
    // ============================================================

    private void registarUtilizador() {
        String nome = pedirTexto("Nome: ");
        String email = pedirTexto("Email: ");
        String password = pedirTexto("Password: ");

        try {
            this.model.criarUtilizador(nome, password, email);
            System.out.println("✓ Utilizador registado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void fazerLogin() {
        String email = pedirTexto("Email: ");
        String password = pedirTexto("Password: ");

        try {
            Utilizador user = this.model.login(email, password);
            System.out.println("✓ Login efetuado! Bem-vindo, " + user.getNome() + ".");
            menuPrincipal();
            this.model.logout();
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
        menu.setHandler(9, () -> System.out.println("Tempo simulado: " + this.model.getTempoSimulado()));
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
                "Eliminar Casa",
                "Sair de Casa"
        });

        menu.setHandler(1, this::criarCasa);
        menu.setHandler(2, this::adicionarDivisao);
        menu.setHandler(3, this::adicionarUtilizadorACasa);
        menu.setHandler(4, this::adicionarAdminACasa);
        menu.setHandler(5, this::eliminarCasa);
        menu.setHandler(6, this::sairDeCasa);

        menu.run();
    }

    private void criarCasa() {
        String nome = pedirTexto("Nome da casa: ");
        String morada = pedirTexto("Morada: ");

        try {
            this.model.criarCasa(nome, morada);
            System.out.println("✓ Casa criada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarDivisao() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String nomeDivisao = pedirTexto("Nome da nova divisão: ");

        try {
            this.model.adicionarDivisao(idCasa, nomeDivisao);
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
            this.model.adicionarUtilizadorACasa(idCasa, email);
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
            this.model.adicionarAdminACasa(idCasa, email);
            System.out.println("✓ Utilizador promovido a administrador!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void eliminarCasa() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;

        try {
            this.model.eliminarCasa(idCasa);
            System.out.println("✓ Casa eliminada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void sairDeCasa() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;

        try {
            this.model.sairDeCasa(idCasa);
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
            this.model.adicionarDispositivoADivisao(idCasa, nomeDivisao, d);
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
            this.model.ligarDispositivo(idCasa, div, id);
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
            this.model.desligarDispositivo(idCasa, div, id);
            System.out.println("✓ Desligado!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void abrirDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        try {
            this.model.abrirDispositivo(idCasa, div, id);
            System.out.println("✓ Aberto!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void fecharDispositivo() {
        String idCasa = pedirIdCasa();
        if (idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        int id = pedirIdDispositivo(idCasa, div);
        try {
            this.model.fecharDispositivo(idCasa, div, id);
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
            this.model.alterarNivelDispositivo(idCasa, div, id, nivel);
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
            this.model.alterarTemperaturaDispositivo(idCasa, div, id, temp);
            System.out.println("✓ Temperatura alterada!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void verEstadoDetalhado() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        if (div == null) return;
        int id = pedirIdDispositivo(idCasa, div);
        if (id < 0) return;

        try {
            // Vamos buscar a casa e o dispositivo para mostrar os dados reais
            Casa casa = this.model.getCasasDoUserLogado().stream()
                    .filter(c -> c.getId().equals(idCasa)).findFirst().orElse(null);
            
            Dispositivo d = casa.getDivisao(div).getDispositivo(id);
            
            System.out.println("\n--- Estado de: " + d.getNome() + " ---");
            System.out.println("ID: " + d.getId());
            System.out.println("Energia: " + (d.isLigado() ? "LIGADO (ON)" : "DESLIGADO (OFF)"));
            System.out.println("Consumo acumulado: " + String.format("%.2f", d.consumoTotalDispositivo()) + " Wh");
            
            // Mostrar info extra dependendo do tipo
            if (d instanceof SensorChuva) 
                System.out.println("Chuva atual: " + ((SensorChuva) d).getValorAtual() + " mm/h");
            if (d instanceof SensorLuminosidade) 
                System.out.println("Luz atual: " + ((SensorLuminosidade) d).getValorAtual() + " lux");
            if (d instanceof Cortina)
                System.out.println("Abertura: " + ((Cortina) d).getNivel() + "%");
                
        } catch (Exception e) { System.out.println("Erro ao obter estado."); }
    }

    private void simularLeitura() {
        String idCasa = pedirIdCasa();
        if (idCasa == null || idCasa.isEmpty()) return;
        String div = pedirNomeDivisao(idCasa);
        if (div == null) return;
        int id = pedirIdDispositivo(idCasa, div);
        double valor = pedirDouble("Novo valor para o sensor: ");

        try {
            // Nota: Para isto funcionar, precisas de ter um método no DomusControl 
            // ou aceder via Casa para fazer d.simularLeitura(valor)
            Casa casa = this.model.getCasasDoUserLogado().stream()
                    .filter(c -> c.getId().equals(idCasa)).findFirst().orElse(null);
            
            Dispositivo d = casa.getDivisao(div).getDispositivo(id);
            
            if (d instanceof SensorChuva) {
                ((SensorChuva) d).simularLeitura(valor);
                System.out.println("✓ Sensor de Chuva atualizado para " + valor + " mm/h");
            } else if (d instanceof SensorLuminosidade) {
                ((SensorLuminosidade) d).simularLeitura(valor);
                System.out.println("✓ Sensor de Luz atualizado para " + valor + " lux");
            } else {
                System.out.println("Este dispositivo não permite simular leituras.");
            }
        } catch (Exception e) { System.out.println("Erro ao simular leitura."); }
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

        AcaoAutomacao acao = criarAcaoPorMenu();
        if (acao == null) return;

        try {
            this.model.criarAutomacao(idCasa, nome, cond, acao);
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
        menu.setHandler(5, () -> System.out.println("Total: " + String.format("%.2f", this.model.consumoTotalSistema()) + " Wh"));
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
        
        // Validação imediata:
        // Verificamos se o ID digitado existe na lista de casas do utilizador
        boolean existe = this.model.getCasasDoUserLogado().stream()
                                   .anyMatch(c -> c.getId().equals(id));
        
        if (!existe && !id.isEmpty()) {
            System.out.println("Erro: A casa '" + id + "' não existe ou não tens acesso.");
            return null; // Retorna null para indicar erro imediato
        }
        return id;
    }

    private String pedirNomeDivisao(String idCasa) {
        mostrarDivisoes(idCasa);
        System.out.print("\nNome da divisão: ");
        String nome = sc.nextLine().trim();

        // Validação imediata da divisão
        try {
            // Procuramos a casa para ver se ela tem essa divisão
            Casa casa = this.model.getCasasDoUserLogado().stream()
                                  .filter(c -> c.getId().equals(idCasa))
                                  .findFirst().orElse(null);
            
            if (casa == null || casa.getDivisao(nome) == null) {
                System.out.println("Erro: A divisão '" + nome + "' não existe nesta casa.");
                return null;
            }
        } catch (Exception e) { return null; }
        
        return nome;
    }

    private int pedirIdDispositivo(String idCasa, String nomeDivisao) {
        mostrarDispositivos(idCasa, nomeDivisao);
        System.out.print("\nID do dispositivo: ");
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private void mostrarCasas() {
        List<Casa> casas = this.model.getCasasDoUserLogado();
        if (casas.isEmpty()) System.out.println("\n(Sem casas)");
        else {
            System.out.println("\n=== As Tuas Casas ===");
            for (Casa c : casas) System.out.println(" [" + c.getId() + "] " + c.getNome());
        }
    }

    private void mostrarDivisoes(String idCasa) {
        Casa casa = this.model.getCasasDoUserLogado().stream().filter(c->c.getId().equals(idCasa)).findFirst().orElse(null);
        if (casa == null) return;
        System.out.println("\n=== Divisões em " + casa.getNome() + " ===");
        casa.getDivisoes().forEach((n, d) -> System.out.println(" · " + n));
    }

    private void mostrarDispositivos(String idCasa, String divNome) {
        Casa casa = this.model.getCasasDoUserLogado().stream().filter(c->c.getId().equals(idCasa)).findFirst().orElse(null);
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
    private void listarAutomacoes() { String id = pedirIdCasa(); try { this.model.listarAutomacoes(id).forEach(System.out::println); } catch(Exception e){} }
    private void ativarAutomacao() { String idC = pedirIdCasa(); String idA = pedirTexto("ID Auto: "); try { this.model.ativarAutomacao(idC, idA); } catch(Exception e){} }
    private void desativarAutomacao() { String idC = pedirIdCasa(); String idA = pedirTexto("ID Auto: "); try { this.model.desativarAutomacao(idC, idA); } catch(Exception e){} }
    private void removerAutomacao() { String idC = pedirIdCasa(); String idA = pedirTexto("ID Auto: "); try { this.model.removerAutomacao(idC, idA); } catch(Exception e){} }
    private void criarEscalonamentoSimples() { /* Implementação similar a criarAutomacao */ }
    private void criarEscalonamentoComFim() { /* Implementação similar a criarAutomacao */ }
    private void listarEscalonamentos() { String id = pedirIdCasa(); try { this.model.listarEscalonamentos(id).forEach(System.out::println); } catch(Exception e){} }
    private void removerEscalonamento() { String idC = pedirIdCasa(); String idE = pedirTexto("ID Esc: "); try { this.model.removerEscalonamento(idC, idE); } catch(Exception e){} }
    private void criarCenario() { String idC = pedirIdCasa(); String n = pedirTexto("Nome: "); String d = pedirTexto("Desc: "); try { this.model.criarCenario(idC, n, d); } catch(Exception e){} }
    private void adicionarAcaoACenario() { String id = pedirTexto("ID Cenário: "); AcaoAutomacao a = criarAcaoPorMenu(); try { this.model.adicionarAcaoACenario(id, a); } catch(Exception e){} }
    private void listarCenarios() { try { this.model.listarCenarios().forEach(System.out::println); } catch(Exception e){} }
    private void ativarCenario() { String id = pedirTexto("ID: "); try { this.model.ativarCenario(id); } catch(Exception e){} }
    private void removerCenario() { String id = pedirTexto("ID: "); try { this.model.removerCenario(id); } catch(Exception e){} }
    private void verSugestoesEscalonamento() { try { this.model.gerarSugestoesEscalonamento(); } catch(Exception e){} }
    private void explicarSugestoes() { System.out.println("Baseado em padrões de 3 dias..."); }
    private void estatisticaCasaMaisConsome() { try { System.out.println(this.model.casaQueMaisConsome()); } catch(Exception e){} }
    private void estatisticaTopTempo() { String id = pedirIdCasa(); try { this.model.top3DispositivosPorTempo(id); } catch(Exception e){} }
    private void estatisticaTopAtivacoes() { String id = pedirIdCasa(); try { this.model.top3DispositivosPorAtivacoes(id); } catch(Exception e){} }
    private void estatisticaTopDivisoes() { try { this.model.top3DivisoesComMaisDispositivos(); } catch(Exception e){} }
    private void estatisticaConsumoPorCasa() { try { this.model.consumoPorCasa(); } catch(Exception e){} }
    private void avancarTempo() { int m = pedirInteiro("Minutos: "); try { this.model.avancarTempo(m); } catch(Exception e){} }
    private void carregarEstado() {
        String ficheiro = pedirTexto("Nome do ficheiro a carregar: ");

        try {
            this.model = DomusControl.carregaEstado(ficheiro);
            System.out.println("\nSUCESSO: O estado foi carregado de '" + ficheiro + "'.");
            System.out.println("   Tempo atual no sistema: " + this.model.getTempoSimulado());
            System.out.println("   Pode agora proceder ao Login.");
        } catch (Exception e) {
            System.out.println("\nERRO ao carregar: O ficheiro '" + ficheiro + "' não existe ou está corrompido.");
        }
    }
    private void gravarEstado() { String f = pedirTexto("Ficheiro: "); try { this.model.gravaEstado(f); } catch(Exception e){} }

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

    private AcaoAutomacao criarAcaoPorMenu() {
        System.out.println("\n1-Ligar 2-Desligar 3-Nível 4-Abrir 5-Fechar");
        String op = pedirTexto("Opção: ");
        List<Integer> ids = pedirListaIds("IDs (separados por vírgula): ");
        if (ids.isEmpty()) return null;
        switch (op) {
            case "1": return new AcaoLigarDispositivos(ids);
            case "2": return new AcaoDesligarTodos(ids);
            case "3": int n = pedirInteiro("Nível: "); return new AcaoDefinirNivel(ids, n);
            case "4": return new AcaoAbrirFecharCortinas(ids, true);
            case "5": return new AcaoAbrirFecharCortinas(ids, false);
            default: return null;
        }
    }
}