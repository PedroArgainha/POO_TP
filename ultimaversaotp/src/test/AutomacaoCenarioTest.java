import Automacoes.Automacao;
import Automacoes.CondicaoAutomacao;
import Automacoes.condicoes.Condicaochuva;
import Automacoes.condicoes.Condicaoluminosidade;
import Cenarios.Cenario;
import Sugestoes.AnalisadorPadroes;
import Sugestoes.RegistoInteracao;
import Sugestoes.SugestaoEscalonamento;
import Casa.Casa;
import Casa.Divisao;
import Dispositivos.*;
import Interfaces.AcaoAutomacao;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Testes para Automacao, Cenario e AnalisadorPadroes.
 *
 * Regra importante:
 *   adicionarDispositivoADivisao guarda um CLONE do sensor.
 *   Por isso, TODAS as alterações aos sensores (ligar, simularLeitura)
 *   têm de ser feitas através de casaTeste.getDispositivoPorId(),
 *   que devolve o objeto REAL dentro da casa.
 */
@DisplayName("Testes — Automações, Cenários e Sugestões")
public class AutomacaoCenarioTest {

    private Casa casaTeste;
    private SensorChuva sensorChuva;
    private SensorLuminosidade sensorLuz;
    private Lampada lampada;

    @BeforeEach
    void setup() throws Exception {
        casaTeste = new Casa("Casa Teste", "Rua A");
        Divisao sala = new Divisao("Sala");
        casaTeste.adicionaDivisao(sala);

        sensorChuva = new SensorChuva("SensorChuva", "Bosch", "SC1", 1.0);
        sensorLuz   = new SensorLuminosidade("SensorLuz", "Bosch", "SL1", 1.0);
        lampada     = new Lampada("Lampada", "Philips", "LED", 60.0, 100, 2700);

        casaTeste.adicionarDispositivoADivisao("Sala", sensorChuva);
        casaTeste.adicionarDispositivoADivisao("Sala", sensorLuz);
        casaTeste.adicionarDispositivoADivisao("Sala", lampada);
    }

    // ─── Automacao ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Automacao")
    class AutomacaoTests {

        @Test
        @DisplayName("verificarEExecutar dispara quando condição é verdadeira")
        void disparaQuandoCondicaoVerdadeira() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.ligar();
            s.simularLeitura(10.0); // acima do limiar 5.0

            int[] contador = {0};
            AcaoAutomacao acao = new AcaoAutomacao() {
                public void executar(Casa c) { contador[0]++; }
                public String descricao()    { return "Ação teste"; }
            };

            Automacao auto = new Automacao("Teste",
                    new Condicaochuva(sensorChuva.getId(), 5.0), acao);
            boolean executou = auto.verificarEExecutar(casaTeste);

            assertTrue(executou);
            assertEquals(1, contador[0]);
            assertEquals(1, auto.getNumeroExecucoes());
        }

        @Test
        @DisplayName("verificarEExecutar NÃO dispara quando condição é falsa")
        void naoDisparaQuandoCondicaoFalsa() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.ligar();
            s.simularLeitura(1.0); // abaixo do limiar 5.0

            int[] contador = {0};
            AcaoAutomacao acao = new AcaoAutomacao() {
                public void executar(Casa c) { contador[0]++; }
                public String descricao()    { return "Ação teste"; }
            };

            Automacao auto = new Automacao("Teste",
                    new Condicaochuva(sensorChuva.getId(), 5.0), acao);
            boolean executou = auto.verificarEExecutar(casaTeste);

            assertFalse(executou);
            assertEquals(0, contador[0]);
        }

        @Test
        @DisplayName("automação desativada não dispara")
        void desativadaNaoDispara() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.ligar();
            s.simularLeitura(10.0); // acima do limiar — mas a automação está desativada

            int[] contador = {0};
            AcaoAutomacao acao = new AcaoAutomacao() {
                public void executar(Casa c) { contador[0]++; }
                public String descricao()    { return "Ação teste"; }
            };

            Automacao auto = new Automacao("Teste",
                    new Condicaochuva(sensorChuva.getId(), 5.0), acao);
            auto.desativar();
            auto.verificarEExecutar(casaTeste);

            assertEquals(0, contador[0]);
        }

        @Test
        @DisplayName("ativar/desativar altera o estado")
        void ativarDesativarAlteraEstado() {
            Automacao auto = new Automacao();
            auto.ativar();
            assertTrue(auto.isAtiva());
            auto.desativar();
            assertFalse(auto.isAtiva());
        }

        @Test
        @DisplayName("estaCompleta é false quando sem condição ou ação")
        void estaCompletaFalseSemCondicaoOuAcao() {
            Automacao auto = new Automacao();
            assertFalse(auto.estaCompleta());
        }

        @Test
        @DisplayName("clone preserva nome e id")
        void clonePreserveNomeId() {
            Automacao auto = new Automacao();
            auto.setNome("TesteClone");
            Automacao clone = auto.clone();
            assertEquals(auto.getNome(), clone.getNome());
            assertEquals(auto.getId(), clone.getId());
        }
    }

    // ─── Condições ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Condições")
    class CondicoesTests {

        @Test
        @DisplayName("CondicaoChuva avalia true quando sensor acima do limiar")
        void condicaoChuvaTrue() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.ligar();
            s.simularLeitura(8.0);
            Condicaochuva c = new Condicaochuva(sensorChuva.getId(), 5.0);
            assertTrue(c.avaliar(casaTeste));
        }

        @Test
        @DisplayName("CondicaoChuva avalia false quando sensor abaixo do limiar")
        void condicaoChuvaFalse() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.ligar();
            s.simularLeitura(2.0);
            Condicaochuva c = new Condicaochuva(sensorChuva.getId(), 5.0);
            assertFalse(c.avaliar(casaTeste));
        }

        @Test
        @DisplayName("CondicaoChuva avalia false quando sensor desligado")
        void condicaoChuvaFalseDesligado() throws Exception {
            SensorChuva s = (SensorChuva) casaTeste.getDispositivoPorId(sensorChuva.getId());
            s.simularLeitura(10.0); // acima do limiar mas sensor desligado
            Condicaochuva c = new Condicaochuva(sensorChuva.getId(), 5.0);
            assertFalse(c.avaliar(casaTeste));
        }

        @Test
        @DisplayName("CondicaoChuva avalia false com ID de sensor inexistente")
        void condicaoChuvaFalseIdInexistente() {
            Condicaochuva c = new Condicaochuva(9999, 5.0);
            assertFalse(c.avaliar(casaTeste));
        }

        @Test
        @DisplayName("CondicaoLuminosidade avalia true quando abaixo do limiar")
        void condicaoLuzTrue() throws Exception {
            SensorLuminosidade s = (SensorLuminosidade) casaTeste.getDispositivoPorId(sensorLuz.getId());
            s.ligar();
            s.simularLeitura(50.0); // abaixo do limiar 100.0
            Condicaoluminosidade c = new Condicaoluminosidade(sensorLuz.getId(), 100.0);
            assertTrue(c.avaliar(casaTeste));
        }

        @Test
        @DisplayName("CondicaoLuminosidade avalia false quando acima do limiar")
        void condicaoLuzFalse() throws Exception {
            SensorLuminosidade s = (SensorLuminosidade) casaTeste.getDispositivoPorId(sensorLuz.getId());
            s.ligar();
            s.simularLeitura(200.0); // acima do limiar 100.0
            Condicaoluminosidade c = new Condicaoluminosidade(sensorLuz.getId(), 100.0);
            assertFalse(c.avaliar(casaTeste));
        }
    }

    // ─── Cenario ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Cenario")
    class CenarioTests {

        @Test
        @DisplayName("ativar executa todas as ações e devolve o número correto")
        void ativarExecutaTodasAsAcoes() {
            int[] contador = {0};
            AcaoAutomacao a1 = new AcaoAutomacao() {
                public void executar(Casa c) { contador[0]++; }
                public String descricao()    { return "A1"; }
            };
            AcaoAutomacao a2 = new AcaoAutomacao() {
                public void executar(Casa c) { contador[0]++; }
                public String descricao()    { return "A2"; }
            };

            Cenario c = new Cenario("Teste", "Desc", casaTeste.getId());
            c.adicionarAcao(a1);
            c.adicionarAcao(a2);
            int executadas = c.ativar(casaTeste);

            assertEquals(2, executadas);
            assertEquals(2, contador[0]);
            assertEquals(1, c.getNumeroAtivacoes());
        }

        @Test
        @DisplayName("ativar cenário vazio devolve 0 ações")
        void ativarVazioDevolvezero() {
            Cenario c = new Cenario("Vazio", "Sem ações", casaTeste.getId());
            assertEquals(0, c.ativar(casaTeste));
        }

        @Test
        @DisplayName("estaCompleto é false quando sem ações")
        void estaCompletoFalseSemAcoes() {
            Cenario c = new Cenario("SemAcoes", "Desc", casaTeste.getId());
            assertFalse(c.estaCompleto());
        }

        @Test
        @DisplayName("estaCompleto é true quando tem ações")
        void estaCompletoTrueComAcoes() {
            Cenario c = new Cenario("ComAcoes", "Desc", casaTeste.getId());
            c.adicionarAcao(new AcaoAutomacao() {
                public void executar(Casa ca) {}
                public String descricao()     { return "A"; }
            });
            assertTrue(c.estaCompleto());
        }

        @Test
        @DisplayName("removerAcao remove a ação na posição correta")
        void removerAcaoRemoveCorretamente() {
            int[] c1 = {0}, c2 = {0};
            AcaoAutomacao a1 = new AcaoAutomacao() {
                public void executar(Casa c) { c1[0]++; }
                public String descricao()    { return "A1"; }
            };
            AcaoAutomacao a2 = new AcaoAutomacao() {
                public void executar(Casa c) { c2[0]++; }
                public String descricao()    { return "A2"; }
            };

            Cenario c = new Cenario("Teste", "Desc", casaTeste.getId());
            c.adicionarAcao(a1);
            c.adicionarAcao(a2);
            c.removerAcao(0); // remove a1
            c.ativar(casaTeste);

            assertEquals(0, c1[0]); // a1 não foi executada
            assertEquals(1, c2[0]); // a2 foi executada
        }

        @Test
        @DisplayName("clone preserva nome, descricao e id")
        void clonePreserveNomeEId() {
            Cenario c = new Cenario("Original", "Descrição", casaTeste.getId());
            Cenario clone = c.clone();
            assertEquals(c.getNome(), clone.getNome());
            assertEquals(c.getId(), clone.getId());
            assertEquals(c.getDescricao(), clone.getDescricao());
        }
    }

    // ─── AnalisadorPadroes ────────────────────────────────────────────────────

    @Nested
    @DisplayName("AnalisadorPadroes")
    class AnalisadorPadroesTests {

        @Test
        @DisplayName("histórico vazio devolve lista vazia")
        void historicoVazioDevolveVazio() {
            List<SugestaoEscalonamento> sugestoes =
                    AnalisadorPadroes.analisar(new ArrayList<>());
            assertTrue(sugestoes.isEmpty());
        }

        @Test
        @DisplayName("padrão em 3+ dias distintos gera sugestão")
        void padraRepetidoGeraSugestao() {
            List<RegistoInteracao> historico = new ArrayList<>();
            for (int dia = 1; dia <= 4; dia++) {
                historico.add(new RegistoInteracao(
                        LocalDateTime.of(2026, 1, dia, 19, 0),
                        "C1", 5, RegistoInteracao.TIPO_LIGAR));
            }
            List<SugestaoEscalonamento> sugestoes = AnalisadorPadroes.analisar(historico);
            assertFalse(sugestoes.isEmpty());
        }

        @Test
        @DisplayName("padrão em menos de 3 dias NÃO gera sugestão")
        void padraoPoucosDiasNaoGeraSugestao() {
            List<RegistoInteracao> historico = new ArrayList<>();
            for (int dia = 1; dia <= 2; dia++) {
                historico.add(new RegistoInteracao(
                        LocalDateTime.of(2026, 1, dia, 19, 0),
                        "C1", 5, RegistoInteracao.TIPO_LIGAR));
            }
            List<SugestaoEscalonamento> sugestoes = AnalisadorPadroes.analisar(historico);
            assertTrue(sugestoes.isEmpty());
        }

        @Test
        @DisplayName("mesmo dia repetido várias vezes NÃO gera sugestão")
        void mesmoDiaRepetidoNaoGeraSugestao() {
            List<RegistoInteracao> historico = new ArrayList<>();
            for (int hora = 8; hora < 13; hora++) {
                historico.add(new RegistoInteracao(
                        LocalDateTime.of(2026, 1, 1, hora, 0),
                        "C1", 5, RegistoInteracao.TIPO_LIGAR));
            }
            List<SugestaoEscalonamento> sugestoes = AnalisadorPadroes.analisar(historico);
            assertTrue(sugestoes.isEmpty());
        }

        @Test
        @DisplayName("sugestão gerada tem a hora e o idCasa corretos")
        void sugestaoTemDadosCorretos() {
            List<RegistoInteracao> historico = new ArrayList<>();
            for (int dia = 1; dia <= 5; dia++) {
                historico.add(new RegistoInteracao(
                        LocalDateTime.of(2026, 1, dia, 19, 0),
                        "C1", 3, RegistoInteracao.TIPO_DESLIGAR));
            }
            List<SugestaoEscalonamento> sugestoes = AnalisadorPadroes.analisar(historico);
            assertFalse(sugestoes.isEmpty());
            SugestaoEscalonamento s = sugestoes.get(0);
            assertEquals("C1", s.getIdCasa());
            assertEquals(3, s.getIdDispositivo());
            assertEquals(19, s.getHoraSugerida().getHour());
        }
    }
}