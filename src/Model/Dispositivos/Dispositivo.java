package Model.Dispositivos;

import java.io.Serializable;
import java.util.Objects;

public abstract class Dispositivo implements Serializable {

    // variável de classe para gerar IDs únicos sequenciais
    private static int proximoId = 1;

    // Necessário para a serialização: ao carregar de ficheiro, o contador estático
    // precisa de ser reposicionado para evitar colisões de IDs.
    public static void setProximoId(int id) {
        proximoId = id;
    }
    private int id;
    private String nome;
    private String marca;
    private String modelo;

    // atributos para estatísticos
    private double consumoPorHora;
    private boolean ligado;
    private int numeroAtivacoes;
    private double tempoLigado;

    // Construtor por omissão
    public Dispositivo() {
        this.id = proximoId++;
        this.nome = "";
        this.marca = "";
        this.modelo = "";
        this.consumoPorHora = 0.0;
        this.ligado = false;
        this.numeroAtivacoes = 0;
        this.tempoLigado = 0.0;
    }

    // Construtor parametrizado
    public Dispositivo(String nome, String marca, String modelo, double consumoPorHora) {
        this.id = proximoId++;
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoPorHora = consumoPorHora;
        this.ligado = false;
        this.numeroAtivacoes = 0;
        this.tempoLigado = 0.0;
    }

    // Construtor por cópia
    public Dispositivo(Dispositivo d) {
        this.id = d.getId();
        this.nome = d.getNome();
        this.marca = d.getMarca();
        this.modelo = d.getModelo();
        this.consumoPorHora = d.getConsumoPorHora();
        this.ligado = d.isLigado();
        this.numeroAtivacoes = d.getNumeroAtivacoes();
        this.tempoLigado = d.getTempoLigado();
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getMarca() {
        return this.marca;
    }

    public String getModelo() {
        return this.modelo;
    }

    public double getConsumoPorHora() {
        return this.consumoPorHora;
    }

    public boolean isLigado() {
        return this.ligado;
    }

    public int getNumeroAtivacoes() {
        return this.numeroAtivacoes;
    }

    public double getTempoLigado() {
        return this.tempoLigado;
    }

    // Setters das variáveis de instância que fazem sentido poder ser alterados
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setConsumoPorHora(double consumoPorHora) {
        this.consumoPorHora = consumoPorHora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Dispositivo d = (Dispositivo) o;
        return this.id == d.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "id=" + this.id +
                ", nome='" + this.nome + '\'' +
                ", marca='" + this.marca + '\'' +
                ", modelo='" + this.modelo + '\'' +
                ", consumoPorHora=" + this.consumoPorHora +
                ", ligado=" + this.ligado +
                ", numeroAtivacoes=" + this.numeroAtivacoes +
                ", tempoLigado=" + this.tempoLigado +
                '}';
    }

    // Métodos de instância
    public void ligar() {
        if (!this.ligado) {
            this.ligado = true;
            this.numeroAtivacoes++;
        }
    }

    public void desligar() {
        this.ligado = false;
    }

    // registar o tempo ligado
    // quando um dispositivo passa para ligado guardo o instante simulado em que
    // isso acontece
    // quando passa para OFF calcula-se o tempo ligado e somo ao valor acumulado
    // esse acumulado serve para as estatísticas dos dispostiivos mais utilizado
    public void registarTempoLigado(double minutosligados) {
        if (this.ligado) {
            this.tempoLigado += minutosligados;
        }
    }

    // Clone abstrato — cada subclasse implementa com o seu construtor de cópia
    public abstract Dispositivo clone();

    // Metodos abstratos - que todas as subclasses devem implementar
    public abstract double consumoTotalDispositivo();

    public abstract double consumoAtual();

}