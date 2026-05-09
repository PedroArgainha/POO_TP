import Casa.Casa;
import Casa.Divisao;
import Dispositivos.*;
import Exceptions.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Casa e Divisao.
 * Cobre: adicionar dispositivos, ligar/desligar, consumo, clone.
 */
@DisplayName("Testes — Casa e Divisao")
public class CasaDivisaoTest {

    private Casa casa;
    private Divisao sala;
    private Lampada lampada;

    @BeforeEach
    void setup() throws Exception {
        casa = new Casa("Casa Teste", "Rua Teste, 1");
        sala = new Divisao("Sala");
        lampada = new Lampada("Lampada Sala", "Philips", "LED60", 60.0, 100, 2700);
    }

    // ─── Divisao ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Divisao")
    class DivisaoTests {

        @Test
        @DisplayName("começa sem dispositivos")
        void comecaSemDispositivos() {
            assertEquals(0, sala.getNumeroDispositivos());
        }

        @Test
        @DisplayName("adicionarDispositivo incrementa contador")
        void adicionarDispositivoIncrementa() {
            sala.adicionarDispositivo(lampada);
            assertEquals(1, sala.getNumeroDispositivos());
        }

        @Test
        @DisplayName("getDispositivos devolve cópia — alterações não afetam o original")
        void getDispositivosDevolveCopiа() {
            sala.adicionarDispositivo(lampada);
            sala.getDispositivos().clear(); // altera a cópia
            assertEquals(1, sala.getNumeroDispositivos()); // original intacto
        }

        @Test
        @DisplayName("ligar dispositivo pelo ID")
        void ligarDispositivoPorId() throws Exception {
            sala.adicionarDispositivo(lampada);
            sala.ligarDispositivo(lampada.getId());
            // para verificar, temos de ir buscar a lista
            Dispositivo d = sala.listaDispositivos().get(0);
            assertTrue(d.isLigado());
        }

        @Test
        @DisplayName("desligar dispositivo pelo ID")
        void desligarDispositivoPorId() throws Exception {
            sala.adicionarDispositivo(lampada);
            sala.ligarDispositivo(lampada.getId());
            sala.desligarDispositivo(lampada.getId());
            Dispositivo d = sala.listaDispositivos().get(0);
            assertFalse(d.isLigado());
        }

        @Test
        @DisplayName("ligar ID inexistente lança DispositivoNaoExisteException")
        void ligarIdInexistenteLancaExcecao() {
            assertThrows(DispositivoNaoExisteException.class,
                    () -> sala.ligarDispositivo(9999));
        }

        @Test
        @DisplayName("consumoTotalDivisao soma os consumos")
        void consumoTotalDivisaoSoma() throws Exception {
            Tomada t = new Tomada("Tomada1", "Siemens", "T1", 200.0);
            sala.adicionarDispositivo(lampada);
            sala.adicionarDispositivo(t);
            sala.ligarDispositivo(lampada.getId());
            sala.ligarDispositivo(t.getId());
            sala.atualizarTempoDispositivos(60); // 60 minutos = 1h
            // lampada 60Wh + tomada 200Wh = 260Wh
            assertEquals(260.0, sala.consumoTotalDivisao(), 1.0);
        }

        @Test
        @DisplayName("clone é igual mas independente")
        void cloneIgualIndependente() {
            sala.adicionarDispositivo(lampada);
            Divisao clone = sala.clone();
            assertEquals(sala.getNome(), clone.getNome());
            assertEquals(sala.getNumeroDispositivos(), clone.getNumeroDispositivos());
        }
    }

    // ─── Casa ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Casa")
    class CasaTests {

        @Test
        @DisplayName("ID gerado começa com 'C'")
        void idComecaComC() {
            assertTrue(casa.getId().startsWith("C"));
        }

        @Test
        @DisplayName("adicionaDivisao funciona")
        void adicionaDivisao() {
            casa.adicionaDivisao(sala);
            assertNotNull(casa.getDivisao("Sala"));
        }

        @Test
        @DisplayName("getDivisao de nome inexistente devolve null")
        void getDivisaoInexistenteDevolveNull() {
            assertNull(casa.getDivisao("NaoExiste"));
        }

        @Test
        @DisplayName("adicionarDispositivoADivisao funciona")
        void adicionarDispositivoADivisao() throws Exception {
            casa.adicionaDivisao(sala);
            casa.adicionarDispositivoADivisao("Sala", lampada);
            assertEquals(1, casa.getDivisao("Sala").getNumeroDispositivos());
        }

        @Test
        @DisplayName("adicionarDispositivoADivisao em divisão inexistente lança DivisaoNaoExisteException")
        void adicionarDispositivoDivisaoInexistente() {
            assertThrows(DivisaoNaoExisteException.class,
                    () -> casa.adicionarDispositivoADivisao("NaoExiste", lampada));
        }

        @Test
        @DisplayName("ligarDispositivo na Casa funciona")
        void ligarDispositivoNaCasa() throws Exception {
            casa.adicionaDivisao(sala);
            casa.adicionarDispositivoADivisao("Sala", lampada);
            casa.ligarDispositivo("Sala", lampada.getId());
            Dispositivo d = casa.getDivisao("Sala").listaDispositivos().get(0);
            assertTrue(d.isLigado());
        }

        @Test
        @DisplayName("consumoTotalCasa soma todas as divisões")
        void consumoTotalCasaSomaTodasDivisoes() throws Exception {
            Divisao quarto = new Divisao("Quarto");
            Tomada t = new Tomada("T1", "M", "Mo", 100.0);
            casa.adicionaDivisao(sala);
            casa.adicionaDivisao(quarto);
            casa.adicionarDispositivoADivisao("Sala", lampada);
            casa.adicionarDispositivoADivisao("Quarto", t);
            casa.ligarDispositivo("Sala", lampada.getId());
            casa.ligarDispositivo("Quarto", t.getId());
            casa.atualizarTempoDispositivos(60);
            // lampada 60Wh + tomada 100Wh = 160Wh
            assertEquals(160.0, casa.consumoTotalCasa(), 1.0);
        }

        @Test
        @DisplayName("clone é igual mas independente")
        void cloneIgualIndependente() {
            casa.adicionaDivisao(sala);
            Casa clone = casa.clone();
            assertEquals(casa.getNome(), clone.getNome());
            assertEquals(casa.getId(), clone.getId());
        }
    }
}