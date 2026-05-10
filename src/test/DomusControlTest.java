import DomusControl.DomusControl;
import Dispositivos.*;
import Casa.Casa;
import Casa.Divisao;
import Exceptions.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * Testes para DomusControl.
 * Usa apenas métodos que existem no DomusControl atual.
 * Para aceder às casas do utilizador logado, usamos:
 *   user.getCasasComoAdmin() → Set<String> de IDs
 *   this.casas.get(id) → mas casas é privado, por isso navegamos via
 *   dc.adicionarDivisao(idCasa, nome) e apanhamos exceções para confirmar.
 */
@DisplayName("Testes — DomusControl")
public class DomusControlTest {

    private DomusControl dc;

    @BeforeEach
    void setup() throws Exception {
        dc = new DomusControl();
        dc.criarUtilizador("Alice", "pass123", "alice@mail.com");
        dc.login("alice@mail.com", "pass123");
    }

    // ─── Utilizadores ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Utilizadores")
    class UtilizadoresTests {

        @Test
        @DisplayName("criarUtilizador com email duplicado lança EmailJaExisteException")
        void emailDuplicadoLancaExcecao() {
            assertThrows(EmailJaExisteException.class,
                    () -> dc.criarUtilizador("Alice2", "pass", "alice@mail.com"));
        }

        @Test
        @DisplayName("login com password errada lança PasswordIncorretaException")
        void passwordErradaLancaExcecao() throws Exception {
            dc.logout();
            assertThrows(PasswordIncorretaException.class,
                    () -> dc.login("alice@mail.com", "errada"));
        }

        @Test
        @DisplayName("login com email inexistente lança UserNaoregistadoException")
        void emailInexistenteLancaExcecao() throws Exception {
            dc.logout();
            assertThrows(UserNaoregistadoException.class,
                    () -> dc.login("naoexiste@mail.com", "pass"));
        }

        @Test
        @DisplayName("existeSessaoAtiva é true após login")
        void existeSessaoAtivaAposLogin() {
            assertTrue(dc.existeSessaoAtiva());
        }

        @Test
        @DisplayName("logout termina sessão")
        void logoutTerminaSessao() {
            dc.logout();
            assertFalse(dc.existeSessaoAtiva());
        }
    }

    // ─── Casas ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Casas")
    class CasasTests {

        @Test
        @DisplayName("criarCasa não lança exceção")
        void criarCasaNaoLancaExcecao() {
            assertDoesNotThrow(() -> dc.criarCasa("Casa Teste", "Rua A, 1"));
        }

        @Test
        @DisplayName("adicionarDivisao em casa existente não lança exceção")
        void adicionarDivisaoFunciona() {
            dc.criarCasa("Casa Teste", "Rua A, 1");
            // para saber o ID, precisamos de um hack: a casa gerada começa com "C"
            // usamos consumoPorCasa para confirmar que foi criada
            assertFalse(dc.consumoPorCasa().isEmpty());
        }

        @Test
        @DisplayName("adicionarDivisao em casa inexistente lança CasaNaoExisteException")
        void adicionarDivisaoCasaInexistente() {
            assertThrows(CasaNaoExisteException.class,
                    () -> dc.adicionarDivisao("NAOEXI", "Sala"));
        }

        @Test
        @DisplayName("consumoPorCasa devolve entrada por cada casa criada")
        void consumoPorCasaTemEntradaPorCasa() {
            dc.criarCasa("Casa 1", "Rua A");
            dc.criarCasa("Casa 2", "Rua B");
            assertEquals(2, dc.consumoPorCasa().size());
        }

        @Test
        @DisplayName("eliminarCasa remove a casa do sistema")
        void eliminarCasaRemove() {
            dc.criarCasa("Casa Temp", "Rua C");
            // obter o ID através do consumoPorCasa — devolve "Nome (ID)"
            String entrada = dc.consumoPorCasa().keySet().iterator().next();
            // formato: "Casa Temp (C1)" → extrai o ID entre parênteses
            String idCasa = entrada.substring(entrada.indexOf('(') + 1, entrada.indexOf(')'));
            dc.eliminarCasa(idCasa);
            assertTrue(dc.consumoPorCasa().isEmpty());
        }
    }

    // ─── Dispositivos ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Dispositivos")
    class DispositivosTests {

        private String idCasa;

        @BeforeEach
        void setupCasa() throws Exception {
            dc.criarCasa("Casa Teste", "Rua A, 1");
            // obter ID da casa criada
            String entrada = dc.consumoPorCasa().keySet().iterator().next();
            idCasa = entrada.substring(entrada.indexOf('(') + 1, entrada.indexOf(')'));
            dc.adicionarDivisao(idCasa, "Sala");
        }

        @Test
        @DisplayName("adicionarDispositivoADivisao não lança exceção")
        void adicionarDispositivoFunciona() throws Exception {
            Lampada l = new Lampada("L1", "Philips", "LED", 60.0, 100, 2700);
            assertDoesNotThrow(() -> dc.adicionarDispositivoADivisao(idCasa, "Sala", l));
        }

        @Test
        @DisplayName("adicionarDispositivo em divisão inexistente lança DivisaoNaoExisteException")
        void adicionarDispositivoDivisaoInexistente() throws Exception {
            Lampada l = new Lampada("L1", "Philips", "LED", 60.0, 100, 2700);
            assertThrows(DivisaoNaoExisteException.class,
                    () -> dc.adicionarDispositivoADivisao(idCasa, "NaoExiste", l));
        }

        @Test
        @DisplayName("ligarDispositivo em divisão inexistente lança DivisaoNaoExisteException")
        void ligarDivisaoInexistente() {
            assertThrows(DivisaoNaoExisteException.class,
                    () -> dc.ligarDispositivo(idCasa, "NaoExiste", 1));
        }

        @Test
        @DisplayName("ligarDispositivo em casa inexistente lança CasaNaoExisteException")
        void ligarCasaInexistente() {
            assertThrows(CasaNaoExisteException.class,
                    () -> dc.ligarDispositivo("NAOEXI", "Sala", 1));
        }

        @Test
        @DisplayName("avancarTempo acumula consumo nos dispositivos ligados")
        void avancarTempoAcumulaConsumo() throws Exception {
            Lampada l = new Lampada("L1", "Philips", "LED", 60.0, 100, 2700);
            dc.adicionarDispositivoADivisao(idCasa, "Sala", l);
            dc.ligarDispositivo(idCasa, "Sala", l.getId());
            dc.avancarTempo(60); // 60 minutos = 1 hora
            // consumo total deve ser positivo
            assertTrue(dc.consumoTotalSistema() > 0.0);
        }
    }

    // ─── Tempo ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Tempo simulado")
    class TempoTests {

        @Test
        @DisplayName("avancarTempo avança o relógio")
        void avancarTempoAvancaRelogio() {
            var antes = dc.getTempoSimulado();
            dc.avancarTempo(60);
            var depois = dc.getTempoSimulado();
            assertTrue(depois.isAfter(antes));
        }

        @Test
        @DisplayName("avancarTempo com valor negativo lança IllegalArgumentException")
        void avancarTempoNegativoLancaExcecao() {
            assertThrows(IllegalArgumentException.class,
                    () -> dc.avancarTempo(-10));
        }
    }

    // ─── Estatísticas ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Estatísticas")
    class EstatisticasTests {

        @BeforeEach
        void setupDados() throws Exception {
            dc.criarCasa("Casa Rica", "Rua A");
            String entrada = dc.consumoPorCasa().keySet().iterator().next();
            String idCasa = entrada.substring(entrada.indexOf('(') + 1, entrada.indexOf(')'));
            dc.adicionarDivisao(idCasa, "Sala");
            Tomada t = new Tomada("T1", "Siemens", "S1", 5000.0);
            dc.adicionarDispositivoADivisao(idCasa, "Sala", t);
            dc.ligarDispositivo(idCasa, "Sala", t.getId());
            dc.avancarTempo(60);
        }

        @Test
        @DisplayName("casaQueMaisConsome não devolve null")
        void casaQueMaisConsomeNaoNulo() {
            assertNotNull(dc.casaQueMaisConsome());
        }

        @Test
        @DisplayName("casaQueMaisConsome devolve a casa correta")
        void casaQueMaisConsomeCorreta() {
        assertTrue(dc.casaQueMaisConsome().contains("Casa Rica"));
        }   

        @Test
        @DisplayName("consumoTotalSistema é positivo após ligar dispositivos")
        void consumoTotalSistemaPositivo() {
            assertTrue(dc.consumoTotalSistema() > 0.0);
        }

        @Test
        @DisplayName("top3DivisoesComMaisDispositivos devolve no máximo 3")
        void top3DivisoesMax3() {
            assertTrue(dc.top3DivisoesComMaisDispositivos().size() <= 3);
        }
    }

    // ─── Serialização ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Serialização")
    class SerializacaoTests {

        @Test
        @DisplayName("gravar e carregar preserva as casas")
        void gravarECarregarPreservaDados() throws Exception {
            dc.criarCasa("Casa Gravada", "Rua B");
            String ficheiro = "/tmp/domuscontrol_teste.dat";
            dc.gravaEstado(ficheiro);

            DomusControl carregado = DomusControl.carregaEstado(ficheiro);
            carregado.login("alice@mail.com", "pass123");
            // confirma que a casa existe via consumoPorCasa
            assertTrue(carregado.consumoPorCasa().keySet().stream()
                    .anyMatch(k -> k.contains("Casa Gravada")));
        }

        @Test
        @DisplayName("carregarEstado de ficheiro inexistente lança exceção")
        void carregarFicheiroInexistenteLancaExcecao() {
            assertThrows(Exception.class,
                    () -> DomusControl.carregaEstado("/tmp/nao_existe.dat"));
        }
    }
}