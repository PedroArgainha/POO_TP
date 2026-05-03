package Casa;
import Dispositivos.Dispositivo;

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

}

