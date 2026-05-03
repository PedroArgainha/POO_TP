package UI;

import DomusControl.DomusControl;
import Dispositivos.*;
import Utilizador.Utilizador;
import Casa.Casa;
import Casa.Divisao;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class TextUI {

    private DomusControl model;
    private Scanner sc;

    public TextUI() {
        this.model = new DomusControl();
        this.sc = new Scanner(System.in);
    }

    // Permite iniciar com um model já carregado
    public TextUI(DomusControl model) {
        this.model = model;
        this.sc = new Scanner(System.in);
    }

    /**
     * Método principal — lança o menu inicial.
     * Antes de entrar no sistema, o utilizador tem de se registar ou fazer login.
     */
    public void run() {
        System.out.println("========================================");
        System.out.println("   Bem-vindo ao DomusControl!");
        System.out.println("   Tempo simulado: " + this.model.getTempoSimulado());
        System.out.println("========================================");

        NewMenu menuInicial = new NewMenu("DomusControl", new String[]{
                "Registar novo utilizador",
                "Fazer Login",
                "Carregar estado de ficheiro",
                "Gravar estado para ficheiro"
        });

        menuInicial.setHandler(1, this::registarUtilizador);
        menuInicial.setHandler(2, this::fazerLogin);
        menuInicial.setHandler(3, this::carregarEstado);
        menuInicial.setHandler(4, this::gravarEstado);

        menuInicial.run();

        System.out.println("Até à próxima!");
    }

    // ==================== AUTENTICAÇÃO ====================

    private void registarUtilizador() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            this.model.criarUtilizador(nome, password, email);
            System.out.println("Utilizador registado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void fazerLogin() {
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            Utilizador user = this.model.login(email, password);
            System.out.println("Login efetuado! Bem-vindo, " + user.getNome() + ".");
            menuPrincipal();
            this.model.logout();
            System.out.println("Sessão terminada.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ==================== MENU PRINCIPAL (após login) ====================

    private void menuPrincipal() {
        NewMenu menu = new NewMenu("Menu Principal", new String[]{
                "Gestão de Casas",
                "Gestão de Dispositivos",
                "Automações",
                "Escalonamentos",
                "Cenários",
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
        menu.setHandler(6, this::menuEstatisticas);
        menu.setHandler(7, this::avancarTempo);
        menu.setHandler(8, () -> System.out.println("Tempo simulado: " + this.model.getTempoSimulado()));
        menu.setHandler(9, this::gravarEstado);

        menu.run();
    }

    // ==================== GESTÃO DE CASAS ====================

    private void menuGestaoCasas() {
        NewMenu menu = new NewMenu("Gestão de Casas", new String[]{
                "Criar Casa",
                "Adicionar Divisão a Casa",
                "Adicionar Utilizador a Casa",
                "Eliminar Casa",
                "Sair de Casa"
        });

        menu.setHandler(1, this::criarCasa);
        menu.setHandler(2, this::adicionarDivisao);
        menu.setHandler(3, this::adicionarUtilizadorACasa);
        menu.setHandler(4, this::eliminarCasa);
        menu.setHandler(5, this::sairDeCasa);

        menu.run();
    }

    private void criarCasa() {
        System.out.print("Nome da casa: ");
        String nome = sc.nextLine();
        System.out.print("Morada: ");
        String morada = sc.nextLine();

        try {
            this.model.criarCasa(nome, morada);
            System.out.println("Casa criada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarDivisao() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();

        try {
            this.model.adicionarDivisao(idCasa, nomeDivisao);
            System.out.println("Divisão adicionada!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarUtilizadorACasa() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Email do utilizador: ");
        String email = sc.nextLine();

        try {
            this.model.adicionarUtilizadorACasa(idCasa, email);
            System.out.println("Utilizador adicionado à casa!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void eliminarCasa() {
        System.out.print("ID da casa a eliminar: ");
        String idCasa = sc.nextLine();

        try {
            this.model.eliminarCasa(idCasa);
            System.out.println("Casa eliminada!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void sairDeCasa() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        try {
            this.model.sairDeCasa(idCasa);
            System.out.println("Saiu da casa.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ==================== GESTÃO DE DISPOSITIVOS ====================

    private void menuGestaoDispositivos() {
        NewMenu menu = new NewMenu("Gestão de Dispositivos", new String[]{
                "Adicionar Dispositivo a Divisão",
                "Ligar Dispositivo",
                "Desligar Dispositivo",
                "Alterar Nível (Regulável)",
                "Alterar Temperatura",
                "Abrir Dispositivo",
                "Fechar Dispositivo"
        });

        menu.setHandler(1, this::adicionarDispositivo);
        menu.setHandler(2, this::ligarDispositivo);
        menu.setHandler(3, this::desligarDispositivo);
        menu.setHandler(4, this::alterarNivel);
        menu.setHandler(5, this::alterarTemperatura);
        menu.setHandler(6, this::abrirDispositivo);
        menu.setHandler(7, this::fecharDispositivo);

        menu.run();
    }

    private void adicionarDispositivo() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();

        System.out.println("\nTipos de dispositivo:");
        System.out.println("  1-Lampada  2-ColunaSom  3-Cortina  4-Persiana");
        System.out.println("  5-ArCondicionado  6-Televisao  7-Forno  8-Frigorifico");
        System.out.println("  9-Exaustor  10-Tomada  11-Relay  12-FechaduraInteligente");
        System.out.println("  13-PortaoGaragem  14-Termostato  15-SistemaRega");
        System.out.println("  16-SensorChuva  17-SensorLuminosidade  18-SensorTemperatura");
        System.out.println("  19-CamaraVigilancia");
        System.out.print("Tipo (número): ");
        String tipo = sc.nextLine();

        System.out.print("Nome do dispositivo: ");
        String nome = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Consumo por hora (Wh): ");
        double consumo;
        try {
            consumo = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Consumo inválido.");
            return;
        }

        Dispositivo d = criarDispositivo(tipo, nome, marca, modelo, consumo);
        if (d == null) {
            System.out.println("Tipo de dispositivo inválido.");
            return;
        }

        try {
            this.model.adicionarDispositivoADivisao(idCasa, nomeDivisao, d);
            System.out.println("Dispositivo '" + nome + "' (ID=" + d.getId() + ") adicionado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private Dispositivo criarDispositivo(String tipo, String nome, String marca, String modelo, double consumo) {
        // Dispositivos simples (construtor com 4 parâmetros base)
        switch (tipo) {
            case "10": return new Tomada(nome, marca, modelo, consumo);
            case "11": return new Relay(nome, marca, modelo, consumo);
            case "16": return new SensorChuva(nome, marca, modelo, consumo);
            case "17": return new SensorLuminosidade(nome, marca, modelo, consumo);
            case "18": return new SensorTemperatura(nome, marca, modelo, consumo);
            default: break;
        }

        // Dispositivos que precisam de parâmetros adicionais — criamos com valores por defeito.
        // O utilizador pode depois alterar via operações individuais (alterar nível, temperatura, etc.)
        try {
            switch (tipo) {
                case "1":  return new Lampada(nome, marca, modelo, consumo, 100, 2700);
                case "2":  return new ColunaSom(nome, marca, modelo, consumo, 0);
                case "3":  return new Cortina(nome, marca, modelo, consumo, 0);
                case "4":  return new Persiana(nome, marca, modelo, consumo, 0);
                case "5":  return new ArCondicionado(nome, marca, modelo, consumo, 22.0);
                case "6":  return new Televisao(nome, marca, modelo, consumo, 0, 1);
                case "7":  return new Forno(nome, marca, modelo, consumo, 180.0);
                case "8":  return new Frigorifico(nome, marca, modelo, consumo, 4.0);
                case "9":  return new Exaustor(nome, marca, modelo, consumo, 0);
                case "12": return new FechaduraInteligente(nome, marca, modelo, consumo, true);
                case "13": return new PortaoGaragem(nome, marca, modelo, consumo, 0);
                case "14": return new Termostato(nome, marca, modelo, consumo, 22.0);
                case "15": return new SistemaRega(nome, marca, modelo, consumo, 0);
                case "19": return new CamaraVigilancia(nome, marca, modelo, consumo, 1080);
                default:   return null;
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar dispositivo: " + e.getMessage());
            return null;
        }
    }

    private void ligarDispositivo() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;

        try {
            this.model.ligarDispositivo(idCasa, nomeDivisao, idDisp);
            System.out.println("Dispositivo ligado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void desligarDispositivo() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;

        try {
            this.model.desligarDispositivo(idCasa, nomeDivisao, idDisp);
            System.out.println("Dispositivo desligado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarNivel() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;
        System.out.print("Novo nível (0-100): ");
        int nivel = lerInteiro();
        if (nivel < 0) return;

        try {
            this.model.alterarNivelDispositivo(idCasa, nomeDivisao, idDisp, nivel);
            System.out.println("Nível alterado para " + nivel + "%!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarTemperatura() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;
        System.out.print("Nova temperatura (ºC): ");
        double temp;
        try {
            temp = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Temperatura inválida.");
            return;
        }

        try {
            this.model.alterarTemperaturaDispositivo(idCasa, nomeDivisao, idDisp, temp);
            System.out.println("Temperatura alterada!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void abrirDispositivo() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;

        try {
            this.model.abrirDispositivo(idCasa, nomeDivisao, idDisp);
            System.out.println("Dispositivo aberto!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void fecharDispositivo() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDisp = lerInteiro();
        if (idDisp < 0) return;

        try {
            this.model.fecharDispositivo(idCasa, nomeDivisao, idDisp);
            System.out.println("Dispositivo fechado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ==================== AUTOMAÇÕES ====================

    private void menuAutomacoes() {
        NewMenu menu = new NewMenu("Automações", new String[]{
                "Listar Automações",
                "Ativar Automação",
                "Desativar Automação",
                "Remover Automação"
        });

        menu.setHandler(1, () -> {
            var lista = this.model.listarAutomacoes();
            if (lista.isEmpty()) System.out.println("Nenhuma automação registada.");
            else lista.forEach(System.out::println);
        });
        menu.setHandler(2, () -> {
            System.out.print("ID da automação: ");
            this.model.ativarAutomacao(sc.nextLine());
            System.out.println("Automação ativada.");
        });
        menu.setHandler(3, () -> {
            System.out.print("ID da automação: ");
            this.model.desativarAutomacao(sc.nextLine());
            System.out.println("Automação desativada.");
        });
        menu.setHandler(4, () -> {
            System.out.print("ID da automação: ");
            this.model.removerAutomacao(sc.nextLine());
            System.out.println("Automação removida.");
        });

        menu.run();
    }

    // ==================== ESCALONAMENTOS ====================

    private void menuEscalonamentos() {
        NewMenu menu = new NewMenu("Escalonamentos", new String[]{
                "Listar Escalonamentos",
                "Remover Escalonamento"
        });

        menu.setHandler(1, () -> {
            var lista = this.model.listarEscalonamentos();
            if (lista.isEmpty()) System.out.println("Nenhum escalonamento registado.");
            else lista.forEach(System.out::println);
        });
        menu.setHandler(2, () -> {
            System.out.print("ID do escalonamento: ");
            this.model.removerEscalonamento(sc.nextLine());
            System.out.println("Escalonamento removido.");
        });

        menu.run();
    }

    // ==================== CENÁRIOS ====================

    private void menuCenarios() {
        NewMenu menu = new NewMenu("Cenários", new String[]{
                "Listar Cenários",
                "Ativar Cenário",
                "Remover Cenário"
        });

        menu.setHandler(1, () -> {
            var lista = this.model.listarCenarios();
            if (lista.isEmpty()) System.out.println("Nenhum cenário registado.");
            else lista.forEach(System.out::println);
        });
        menu.setHandler(2, () -> {
            System.out.print("ID do cenário: ");
            int n = this.model.ativarCenario(sc.nextLine());
            System.out.println("Cenário ativado! (" + n + " ações executadas)");
        });
        menu.setHandler(3, () -> {
            System.out.print("ID do cenário: ");
            this.model.removerCenario(sc.nextLine());
            System.out.println("Cenário removido.");
        });

        menu.run();
    }

    // ==================== ESTATÍSTICAS ====================

    private void menuEstatisticas() {
        NewMenu menu = new NewMenu("Estatísticas", new String[]{
                "Casa que mais consome",
                "Top 3 dispositivos por tempo (numa casa)",
                "Top 3 dispositivos por ativações (numa casa)",
                "Top 3 divisões com mais dispositivos",
                "Consumo total do sistema",
                "Consumo por casa"
        });

        menu.setHandler(1, () -> {
            Casa c = this.model.casaQueMaisConsome();
            if (c == null) System.out.println("Não existem casas.");
            else System.out.println("Casa que mais consome: " + c.getNome() + " (" + c.getId() + ") - " + String.format("%.2f", c.consumoTotalCasa()) + " Wh");
        });

        menu.setHandler(2, () -> {
            System.out.print("ID da casa: ");
            String idCasa = sc.nextLine();
            try {
                var top = this.model.top3DispositivosPorTempo(idCasa);
                System.out.println("Top 3 por tempo de utilização:");
                for (int i = 0; i < top.size(); i++) {
                    Dispositivo d = top.get(i);
                    System.out.println("  " + (i+1) + ". " + d.getNome() + " (ID=" + d.getId() + ") - " + String.format("%.1f", d.getTempoLigado()) + " min");
                }
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });

        menu.setHandler(3, () -> {
            System.out.print("ID da casa: ");
            String idCasa = sc.nextLine();
            try {
                var top = this.model.top3DispositivosPorAtivacoes(idCasa);
                System.out.println("Top 3 por número de ativações:");
                for (int i = 0; i < top.size(); i++) {
                    Dispositivo d = top.get(i);
                    System.out.println("  " + (i+1) + ". " + d.getNome() + " (ID=" + d.getId() + ") - " + d.getNumeroAtivacoes() + " ativações");
                }
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });

        menu.setHandler(4, () -> {
            var top = this.model.top3DivisoesComMaisDispositivos();
            if (top.isEmpty()) System.out.println("Nenhuma divisão encontrada.");
            else {
                System.out.println("Top 3 divisões com mais dispositivos:");
                int i = 1;
                for (var entry : top.entrySet()) {
                    System.out.println("  " + i++ + ". " + entry.getKey() + " - " + entry.getValue() + " dispositivos");
                }
            }
        });

        menu.setHandler(5, () -> System.out.println("Consumo total do sistema: " + String.format("%.2f", this.model.consumoTotalSistema()) + " Wh"));

        menu.setHandler(6, () -> {
            var mapa = this.model.consumoPorCasa();
            if (mapa.isEmpty()) System.out.println("Nenhuma casa registada.");
            else mapa.forEach((k, v) -> System.out.println("  " + k + ": " + String.format("%.2f", v) + " Wh"));
        });

        menu.run();
    }

    // ==================== TEMPO ====================

    private void avancarTempo() {
        System.out.print("Minutos a avançar: ");
        int minutos = lerInteiro();
        if (minutos <= 0) {
            System.out.println("O valor deve ser positivo.");
            return;
        }

        try {
            this.model.avancarTempo(minutos);
            System.out.println("Tempo avançado! Agora: " + this.model.getTempoSimulado());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ==================== SERIALIZAÇÃO ====================

    private void gravarEstado() {
        System.out.print("Nome do ficheiro: ");
        String ficheiro = sc.nextLine();

        try {
            this.model.gravaEstado(ficheiro);
            System.out.println("Estado gravado com sucesso em '" + ficheiro + "'!");
        } catch (Exception e) {
            System.out.println("Erro ao gravar: " + e.getMessage());
        }
    }

    private void carregarEstado() {
        System.out.print("Nome do ficheiro: ");
        String ficheiro = sc.nextLine();

        try {
            this.model = DomusControl.carregaEstado(ficheiro);
            System.out.println("Estado carregado com sucesso de '" + ficheiro + "'!");
        } catch (Exception e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }

    // ==================== HELPERS ====================

    private int lerInteiro() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
            return -1;
        }
    }
}
