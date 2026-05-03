package Casa;

import Dispositivos.Dispositivo;
import Exceptions.CorInvalidaException;
import Exceptions.DispositivoNaoExisteException;
import Exceptions.DivisaoNaoExisteException;
import Exceptions.NivelInvalidoException;
import Exceptions.TemperaturaInvalidaException;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Casa implements Serializable {

    private static int proximoId = 1;

    private final String id;
    private String nome;
    private String morada;
    private Map<String, Divisao> divisoes;

    // Metodo de classe para gerar IDs únicos
    private static String geraId() {
        return "C" + proximoId++;
    }

    // Necessário para a serialização: ao carregar de ficheiro, o contador estático
    // precisa de ser reposicionado para evitar colisões de IDs.
    public static void setProximoId(int id) {
        proximoId = id;
    }

    // Construtor por omissão
    public Casa() {
        this.id = geraId();
        this.nome = "";
        this.morada = "";
        this.divisoes = new HashMap<>();
    }

    // Construtor parametrizado
    public Casa(String nome, String morada, Map<String, Divisao> divisoes) {
        this.id = geraId();
        this.nome = nome;
        this.morada = morada;
        this.divisoes = new HashMap<>();

        if (divisoes != null) {
            for (Map.Entry<String, Divisao> entry : divisoes.entrySet()) {
                this.divisoes.put(entry.getKey(), entry.getValue().clone());
            }
        }
    }

    // Para criar apenas casa na Interface
    public Casa(String nome, String morada) {
        this.id = geraId();
        this.nome = nome;
        this.morada = morada;
        this.divisoes = new HashMap<>();
    }

    // Construtor de cópia
    public Casa(Casa c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.morada = c.getMorada();
        this.divisoes = new HashMap<>();

        for (Map.Entry<String, Divisao> entry : c.divisoes.entrySet()) {
            this.divisoes.put(entry.getKey(), entry.getValue().clone());
        }
    }

    // Getters
    public String getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getMorada() {
        return this.morada;
    }

    public Map<String, Divisao> getDivisoes() {
        Map<String, Divisao> copia = new HashMap<>();

        for (Map.Entry<String, Divisao> entry : this.divisoes.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }

        return copia;
    }

    public Divisao getDivisao(String nome) {
        if (this.divisoes.containsKey(nome)) {
            return this.divisoes.get(nome).clone();
        }
        return null;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setDivisoes(Map<String, Divisao> divisoes) {
        this.divisoes = new HashMap<>();

        if (divisoes != null) {
            for (Map.Entry<String, Divisao> entry : divisoes.entrySet()) {
                this.divisoes.put(entry.getKey(), entry.getValue().clone());
            }
        }
    }

    // Métodos de instância
    public boolean existeDivisao(String nome) {
        return this.divisoes.containsKey(nome);
    }

    public void adicionaDivisao(Divisao d) {
        if (d != null && !this.divisoes.containsKey(d.getNome())) {
            this.divisoes.put(d.getNome(), d.clone());
        }
    }

    public void adicionarDispositivoADivisao(String nomeDivisao, Dispositivo dispositivo)
            throws DivisaoNaoExisteException {

        if (!this.divisoes.containsKey(nomeDivisao)) {
            throw new DivisaoNaoExisteException("Divisão não existe.");
        }

        Divisao divisao = this.divisoes.get(nomeDivisao);

        divisao.adicionarDispositivo(dispositivo);
    }

    public void removeDivisao(String nome) {
        this.divisoes.remove(nome);
    }

    // --- Métodos de manipulação de dispositivos ---
    // Delegam para a Divisão correspondente, que opera no dispositivo original.

    // Método auxiliar privado: obtém a divisão REAL (não clone) do mapa interno.
    // Centraliza a validação para evitar duplicação de código.
    private Divisao getDivisaoInterna(String nomeDivisao) throws DivisaoNaoExisteException {
        Divisao div = this.divisoes.get(nomeDivisao);
        if (div == null) {
            throw new DivisaoNaoExisteException("Divisão '" + nomeDivisao + "' não existe.");
        }
        return div;
    }

    public void ligarDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.ligarDispositivo(idDispositivo);
    }

    public void desligarDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.desligarDispositivo(idDispositivo);
    }


    public void alterarNivelDispositivo(String nomeDivisao, int idDispositivo, int nivel)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException, NivelInvalidoException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.alterarNivelDispositivo(idDispositivo, nivel);
    }

    public void alterarTemperaturaDispositivo(String nomeDivisao, int idDispositivo, double temperatura)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException, TemperaturaInvalidaException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.alterarTemperaturaDispositivo(idDispositivo, temperatura);
    }

    public void alterarCorDispositivo(String nomeDivisao, int idDispositivo, int cor)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException, CorInvalidaException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.alterarCorDispositivo(idDispositivo, cor);
    }

    public void abrirDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.abrirDispositivo(idDispositivo);
    }

    public void fecharDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.fecharDispositivo(idDispositivo);
    }

    public void bloquearDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.bloquearDispositivo(idDispositivo);
    }

    public void desbloquearDispositivo(String nomeDivisao, int idDispositivo)
            throws DivisaoNaoExisteException, DispositivoNaoExisteException {
        Divisao div = getDivisaoInterna(nomeDivisao);
        div.desbloquearDispositivo(idDispositivo);
    }

    // --- Simulação de tempo ---
    // Propaga o avanço de tempo a TODAS as divisões desta casa.

    public void atualizarTempoDispositivos(double minutos) {
        for (Divisao div : this.divisoes.values()) {
            div.atualizarTempoDispositivos(minutos);
        }
    }

    // --- Métodos para estatísticas ---

    // Consumo total da casa: soma o consumo de todas as divisões.
    public double consumoTotalCasa() {
        double total = 0.0;
        for (Divisao div : this.divisoes.values()) {
            total += div.consumoTotalDivisao();
        }
        return total;
    }

    // Número total de dispositivos na casa.
    public int getNumeroTotalDispositivos() {
        int total = 0;
        for (Divisao div : this.divisoes.values()) {
            total += div.getNumeroDispositivos();
        }
        return total;
    }

    // Devolve TODOS os dispositivos de todas as divisões (clones, para consulta).
    public java.util.List<Dispositivo> getTodosDispositivos() {
        java.util.List<Dispositivo> todos = new java.util.ArrayList<>();
        for (Divisao div : this.divisoes.values()) {
            todos.addAll(div.listaDispositivos());
        }
        return todos;
    }

    @Override
    public Casa clone() {
        return new Casa(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Casa casa = (Casa) o;
        return Objects.equals(this.id, casa.id) &&
                Objects.equals(this.nome, casa.nome) &&
                Objects.equals(this.morada, casa.morada) &&
                Objects.equals(this.divisoes, casa.divisoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.nome, this.morada, this.divisoes);
    }

    @Override
    public String toString() {
        return "Casa{" +
                "id='" + this.id + '\'' +
                ", nome='" + this.nome + '\'' +
                ", morada='" + this.morada + '\'' +
                ", divisoes=" + this.divisoes +
                '}';
    }
}