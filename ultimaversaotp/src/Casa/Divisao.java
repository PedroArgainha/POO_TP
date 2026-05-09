package Casa;
import Dispositivos.Dispositivo;
import Exceptions.CorInvalidaException;
import Exceptions.DispositivoNaoExisteException;
import Exceptions.NivelInvalidoException;
import Exceptions.TemperaturaInvalidaException;
import Interfaces.Abrivel;
import Interfaces.Bloqueavel;
import Interfaces.Colorivel;
import Interfaces.Regulavel;
import Interfaces.Temperavel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class Divisao implements Serializable {

    private String nome;
    private Map<Integer, Dispositivo> dispositivos;

    // Construtor Parametrizado
    public Divisao(String nome) {
        this.nome = nome;
        this.dispositivos = new HashMap<>();
    }

    // Construtor por cópia
    public Divisao(Divisao d) {
        this.nome = d.getNome();
        this.dispositivos = d.getDispositivos();
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void adicionarDispositivo(Dispositivo d) {
        this.dispositivos.put(d.getId(), d.clone());
    }

    public void removerDispositivo(Dispositivo d) {
        this.dispositivos.remove(d.getId());
    }

    public int getNumeroDispositivos() {
        return this.dispositivos.size();
    }

    public List<Dispositivo> listaDispositivos() {
        return this.dispositivos.values().stream()
                .map(Dispositivo::clone)
                .collect(Collectors.toList());
    }

    public double consumoTotalDivisao() {
        return this.dispositivos.values().stream()
                .mapToDouble(Dispositivo::consumoTotalDispositivo)
                .sum();
    }

    public Map<Integer, Dispositivo> getDispositivos() {
        return this.dispositivos.values().stream()
                .collect(Collectors.toMap(Dispositivo::getId, Dispositivo::clone));
    }

    @Override
    public Divisao clone() {
        return new Divisao(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Divisao divisao = (Divisao) o;
        return Objects.equals(this.nome, divisao.nome) &&
                Objects.equals(this.dispositivos, divisao.dispositivos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nome, this.dispositivos);
    }

    @Override
    public String toString(){
        return "Divisao{" +
                "nome='" + nome + '\'' +
                ", dispositivos=" + dispositivos +
                '}';
    }

    // --- Métodos de manipulação de dispositivos ---
    // Estes métodos operam diretamente no dispositivo original (não num clone)
    // para que as alterações de estado persistam.

    // Método auxiliar privado para obter o dispositivo original.
    // Centraliza a validação — evita repetir o mesmo null-check em cada método.
    private Dispositivo getDispositivoOriginal(int idDispositivo) throws DispositivoNaoExisteException {
        Dispositivo d = this.dispositivos.get(idDispositivo);
        if (d == null) {
            throw new DispositivoNaoExisteException("Dispositivo com id " + idDispositivo + " não existe nesta divisão.");
        }
        return d;
    }

    // --- ON / OFF ---

    public void ligarDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        getDispositivoOriginal(idDispositivo).ligar();
    }

    public void desligarDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        getDispositivoOriginal(idDispositivo).desligar();
    }

    // --- Regulavel (ex: volume, intensidade, abertura) ---

    public void alterarNivelDispositivo(int idDispositivo, int nivel)
            throws DispositivoNaoExisteException, NivelInvalidoException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Regulavel)) {
            throw new NivelInvalidoException("O dispositivo '" + d.getNome() + "' não é regulável.");
        }
        ((Regulavel) d).setNivel(nivel);
    }

    // --- Temperavel (ex: ArCondicionado, Forno, Frigorifico, Termostato) ---

    public void alterarTemperaturaDispositivo(int idDispositivo, double temperatura)
            throws DispositivoNaoExisteException, TemperaturaInvalidaException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Temperavel)) {
            throw new TemperaturaInvalidaException("O dispositivo '" + d.getNome() + "' não suporta regulação de temperatura.");
        }
        ((Temperavel) d).setTemperatura(temperatura);
    }

    // --- Colorivel (ex: Lampada) ---

    public void alterarCorDispositivo(int idDispositivo, int cor)
            throws DispositivoNaoExisteException, CorInvalidaException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Colorivel)) {
            throw new CorInvalidaException("O dispositivo '" + d.getNome() + "' não suporta alteração de cor.");
        }
        ((Colorivel) d).setCor(cor);
    }

    // --- Abrivel (ex: Cortina, Persiana, PortaoGaragem) ---

    public void abrirDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Abrivel)) {
            throw new DispositivoNaoExisteException("O dispositivo '" + d.getNome() + "' não é abrível.");
        }
        ((Abrivel) d).abrir();
    }

    public void fecharDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Abrivel)) {
            throw new DispositivoNaoExisteException("O dispositivo '" + d.getNome() + "' não é abrível.");
        }
        ((Abrivel) d).fechar();
    }

    // --- Bloqueavel (ex: FechaduraInteligente) ---

    public void bloquearDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Bloqueavel)) {
            throw new DispositivoNaoExisteException("O dispositivo '" + d.getNome() + "' não é bloqueável.");
        }
        ((Bloqueavel) d).bloquear();
    }

    public void desbloquearDispositivo(int idDispositivo) throws DispositivoNaoExisteException {
        Dispositivo d = getDispositivoOriginal(idDispositivo);
        if (!(d instanceof Bloqueavel)) {
            throw new DispositivoNaoExisteException("O dispositivo '" + d.getNome() + "' não é bloqueável.");
        }
        ((Bloqueavel) d).desbloquear();
    }

    // --- Simulação de tempo ---
    // Itera sobre todos os dispositivos desta divisão.
    // Para cada dispositivo que está ligado, acumula o tempo passado.
    // Este método é chamado pela Casa, que por sua vez é chamada pelo DomusControl.

    public void atualizarTempoDispositivos(double minutos) {
        for (Dispositivo d : this.dispositivos.values()) {
            d.registarTempoLigado(minutos);
        }
    }

    // ============================================================
    // Suporte para Ações (refactor de Automações/Cenários)
    // ============================================================

    /**
     * Devolve a referência REAL (não clone) do dispositivo com o id indicado,
     * ou null se não existir nesta divisão.
     *
     * Package-private de propósito: só a Casa (mesmo package) deve usar isto,
     * para resolver IDs vindos das Ações de automações/cenários.
     */
    // Altera o nome e adiciona 'public' no Divisao.java
    public Dispositivo getDispositivo(int idDispositivo) {
        return this.dispositivos.get(idDispositivo);
    }
}
