package Model.Casa;
import Model.Dispositivos.Dispositivo;
import Model.Exceptions.CorInvalidaException;
import Model.Exceptions.DispositivoNaoExisteException;
import Model.Exceptions.NivelInvalidoException;
import Model.Exceptions.TemperaturaInvalidaException;
import Model.Interfaces.Abrivel;
import Model.Interfaces.Bloqueavel;
import Model.Interfaces.Colorivel;
import Model.Interfaces.Regulavel;
import Model.Interfaces.Temperavel;
import Model.Interfaces.Monitoravel;

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

    public boolean existeDispositivo(int idDispositivo) {
        return this.dispositivos.containsKey(idDispositivo);
    }

    // --- Métodos de manipulação de dispositivos ---
    // Estes métodos operam diretamente no dispositivo original (não num clone)
    // para que as alterações de estado persistam.

    // Metodo auxiliar privado para obter o dispositivo original.
    // Centraliza a validação — evita repetir o mesmo null-check em cada metodo.
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


    public Dispositivo getDispositivo(int idDispositivo)
            throws DispositivoNaoExisteException {
        return getDispositivoOriginal(idDispositivo).clone();
    }

    public String estadoDetalhadoDispositivo(int idDispositivo)
            throws DispositivoNaoExisteException {

        Dispositivo d = getDispositivoOriginal(idDispositivo);
        return formatarEstadoDispositivo(d);
    }

    public String estadoDetalhadoTodosDispositivos() {
        StringBuilder sb = new StringBuilder();

        if (this.dispositivos.isEmpty()) {
            sb.append("  (Sem dispositivos nesta divisão)\n");
            return sb.toString();
        }

        for (Dispositivo d : this.dispositivos.values()) {
            sb.append(formatarEstadoDispositivo(d));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String formatarEstadoDispositivo(Dispositivo d) {
        StringBuilder sb = new StringBuilder();

        sb.append("--- Estado de: ").append(d.getNome()).append(" ---\n");
        sb.append("ID: ").append(d.getId()).append("\n");
        sb.append("Marca: ").append(d.getMarca()).append("\n");
        sb.append("Modelo: ").append(d.getModelo()).append("\n");
        sb.append("Energia: ").append(d.isLigado() ? "LIGADO (ON)" : "DESLIGADO (OFF)").append("\n");
        sb.append("Consumo atual: ").append(String.format("%.2f", d.consumoAtual())).append(" Wh\n");
        sb.append("Consumo acumulado: ").append(String.format("%.2f", d.consumoTotalDispositivo())).append(" Wh\n");
        sb.append("Número de ativações: ").append(d.getNumeroAtivacoes()).append("\n");
        sb.append("Tempo ligado: ").append(String.format("%.2f", d.getTempoLigado())).append(" minutos\n");

        if (d instanceof Regulavel) {
            sb.append("Nível: ").append(((Regulavel) d).getNivel()).append("%\n");
        }

        if (d instanceof Temperavel) {
            sb.append("Temperatura: ").append(((Temperavel) d).getTemperatura()).append(" ºC\n");
        }

        if (d instanceof Colorivel) {
            sb.append("Cor: ").append(((Colorivel) d).getCor()).append("K\n");
        }

        if (d instanceof Abrivel) {
            sb.append("Aberto: ").append(((Abrivel) d).isAberto() ? "sim" : "não").append("\n");
        }

        if (d instanceof Bloqueavel) {
            sb.append("Bloqueado: ").append(((Bloqueavel) d).isBloqueado() ? "sim" : "não").append("\n");
        }

        if (d instanceof Monitoravel) {
            Monitoravel m = (Monitoravel) d;
            sb.append("Leitura atual: ").append(m.getValorAtual()).append(" ").append(m.getUnidade()).append("\n");
        }

        return sb.toString();
    }

    public void simularLeituraSensor(int idDispositivo, double valor)
            throws DispositivoNaoExisteException {

        Dispositivo d = getDispositivoOriginal(idDispositivo);

        if (!(d instanceof Monitoravel)) {
            throw new DispositivoNaoExisteException("Este dispositivo não é um sensor.");
        }

        ((Monitoravel) d).simularLeitura(valor);
    }

    public String resumoDispositivos() {
        StringBuilder sb = new StringBuilder();

        if (this.dispositivos.isEmpty()) {
            sb.append("  (Sem dispositivos)\n");
            return sb.toString();
        }

        for (Dispositivo d : this.dispositivos.values()) {
            sb.append("  [ID: ")
                    .append(d.getId())
                    .append("] ")
                    .append(d.getNome())
                    .append(" (")
                    .append(d.isLigado() ? "ON" : "OFF")
                    .append(")\n");
        }

        return sb.toString();
    }

}
