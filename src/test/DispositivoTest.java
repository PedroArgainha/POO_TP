import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import Model.Dispositivos.ColunaSom;
import Model.Dispositivos.Cortina;
import Model.Dispositivos.Forno;
import Model.Dispositivos.Lampada;
import Model.Dispositivos.Relay;
import Model.Dispositivos.SensorChuva;
import Model.Exceptions.CorInvalidaException;
import Model.Exceptions.NivelInvalidoException;
import Model.Exceptions.TemperaturaInvalidaException;

/**
 * Testes para a classe abstrata Dispositivo e as subclasses mais relevantes.
 * Cobre: ligar/desligar, consumo, clone, equals.
 */
@DisplayName("Testes — Dispositivos")
public class DispositivoTest {

    // ─── Relay ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Relay")
    class RelayTests {

        @Test
        @DisplayName("começa desligado")
        void relayComecaDesligado() {
            Relay r = new Relay("Relay1", "Marca", "Modelo", 100.0);
            assertFalse(r.isLigado());
        }

        @Test
        @DisplayName("consumoAtual = 0 quando desligado")
        void consumoAtualDesligado() {
            Relay r = new Relay("Relay1", "Marca", "Modelo", 100.0);
            assertEquals(0.0, r.consumoAtual());
        }

        @Test
        @DisplayName("consumoAtual = consumoPorHora quando ligado")
        void consumoAtualLigado() {
            Relay r = new Relay("Relay1", "Marca", "Modelo", 100.0);
            r.ligar();
            assertEquals(100.0, r.consumoAtual());
        }

        @Test
        @DisplayName("ligar incrementa numeroAtivacoes")
        void ligarIncrementaAtivacoes() {
            Relay r = new Relay("Relay1", "Marca", "Modelo", 100.0);
            r.ligar();
            r.desligar();
            r.ligar();
            assertEquals(2, r.getNumeroAtivacoes());
        }

        @Test
        @DisplayName("clone é igual mas independente")
        void cloneIgualEIndependente() {
            Relay r = new Relay("Relay1", "Marca", "Modelo", 100.0);
            r.ligar();
            Relay clone = r.clone();
            assertEquals(r, clone);
            clone.desligar();
            assertTrue(r.isLigado()); // original não foi afetado
        }
    }

    // ─── Lampada ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Lampada")
    class LampadaTests {

        @Test
        @DisplayName("consumoAtual proporcional à intensidade")
        void consumoAtualProporcionalAIntensidade() throws Exception {
            Lampada l = new Lampada("Lamp1", "Philips", "LED", 100.0, 50, 2700);
            l.ligar();
            assertEquals(50.0, l.consumoAtual(), 0.001);
        }

        @Test
        @DisplayName("consumoAtual = 0 a intensidade 0")
        void consumoZeroAIntensidadeZero() throws Exception {
            Lampada l = new Lampada("Lamp1", "Philips", "LED", 100.0, 0, 2700);
            l.ligar();
            assertEquals(0.0, l.consumoAtual(), 0.001);
        }

        @Test
        @DisplayName("consumoAtual = 0 quando desligada (independente da intensidade)")
        void consumoZeroQuandoDesligada() throws Exception {
            Lampada l = new Lampada("Lamp1", "Philips", "LED", 100.0, 100, 2700);
            assertEquals(0.0, l.consumoAtual(), 0.001);
        }

        @Test
        @DisplayName("intensidade inválida lança NivelInvalidoException")
        void intensidadeInvalidaLancaExcecao() {
            assertThrows(NivelInvalidoException.class,
                    () -> new Lampada("L", "M", "Mo", 100.0, 150, 2700));
        }

        @Test
        @DisplayName("cor inválida lança CorInvalidaException")
        void corInvalidaLancaExcecao() {
            assertThrows(CorInvalidaException.class,
                    () -> new Lampada("L", "M", "Mo", 100.0, 50, 5000));
        }

        @Test
        @DisplayName("setNivel altera intensidade (interface Regulavel)")
        void setNivelAlteraIntensidade() throws Exception {
            Lampada l = new Lampada("L", "M", "Mo", 100.0, 50, 2700);
            l.setNivel(80);
            assertEquals(80, l.getNivel());
        }
    }

    // ─── ColunaSom ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("ColunaSom")
    class ColunaSomTests {

        @Test
        @DisplayName("consumo mínimo de 10% em standby (ligada com volume 0)")
        void consumoStandby() throws Exception {
            ColunaSom c = new ColunaSom("Coluna1", "Sony", "S1", 100.0, 0);
            c.ligar();
            assertTrue(c.consumoAtual() > 0.0);
        }

        @Test
        @DisplayName("volume inválido lança NivelInvalidoException")
        void volumeInvalidoLancaExcecao() {
            assertThrows(NivelInvalidoException.class,
                    () -> new ColunaSom("C", "M", "Mo", 100.0, 150));
        }
    }

    // ─── Cortina ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Cortina")
    class CortinaTests {

        @Test
        @DisplayName("começa fechada (abertura 0)")
        void comecaFechada() throws Exception {
            Cortina c = new Cortina("Cortina1", "Velux", "V1", 50.0, 0);
            assertEquals(0, c.getNivel());
        }

        @Test
        @DisplayName("consumo só quando motor está em movimento (ligado)")
        void consumoSoQuandoEmMovimento() throws Exception {
            Cortina c = new Cortina("Cortina1", "Velux", "V1", 50.0, 0);
            assertEquals(0.0, c.consumoAtual()); // parada
            c.ligar();
            assertEquals(50.0, c.consumoAtual()); // em movimento
        }

        @Test
        @DisplayName("abrir coloca abertura a 100%")
        void abrirColoca100() throws Exception {
            Cortina c = new Cortina("Cortina1", "Velux", "V1", 50.0, 0);
            c.abrir();
            assertEquals(100, c.getNivel());
        }

        @Test
        @DisplayName("fechar coloca abertura a 0%")
        void fecharColoca0() throws Exception {
            Cortina c = new Cortina("Cortina1", "Velux", "V1", 50.0, 100);
            c.fechar();
            assertEquals(0, c.getNivel());
        }
    }

    // ─── Forno ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Forno")
    class FornoTests {

        @Test
        @DisplayName("consumo proporcional à temperatura")
        void consumoProporcionalTemperatura() throws Exception {
            Forno fMin = new Forno("F1", "Bosch", "B1", 2000.0, 50.0);
            Forno fMax = new Forno("F2", "Bosch", "B1", 2000.0, 300.0);
            fMin.ligar();
            fMax.ligar();
            assertTrue(fMax.consumoAtual() > fMin.consumoAtual());
        }

        @Test
        @DisplayName("temperatura inválida lança TemperaturaInvalidaException")
        void temperaturaInvalidaLancaExcecao() {
            assertThrows(TemperaturaInvalidaException.class,
                    () -> new Forno("F", "M", "Mo", 2000.0, 400.0));
        }
    }

    // ─── SensorChuva ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SensorChuva")
    class SensorChuvaTests {

        @Test
        @DisplayName("simularLeitura atualiza o valor")
        void simularLeituraAtualiza() {
            SensorChuva s = new SensorChuva("Sensor1", "Bosch", "S1", 5.0);
            s.simularLeitura(12.5);
            assertEquals(12.5, s.getValorAtual(), 0.001);
        }

        @Test
        @DisplayName("valor negativo é normalizado para 0")
        void valorNegativoNormalizado() {
            SensorChuva s = new SensorChuva("Sensor1", "Bosch", "S1", 5.0);
            s.simularLeitura(-5.0);
            assertEquals(0.0, s.getValorAtual(), 0.001);
        }

        @Test
        @DisplayName("consumo fixo quando ligado")
        void consumoFixoQuandoLigado() {
            SensorChuva s = new SensorChuva("Sensor1", "Bosch", "S1", 5.0);
            s.ligar();
            assertEquals(5.0, s.consumoAtual(), 0.001);
        }
    }
}