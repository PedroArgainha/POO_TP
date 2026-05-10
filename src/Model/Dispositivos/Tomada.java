package Model.Dispositivos;

public class Tomada extends Dispositivo {

    // Construtor por omissão
    public Tomada() {
        super();
    }

    // Construtor parametrizado
    public Tomada(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
    }

    // Construtor por cópia
    public Tomada(Tomada t) {
        super(t);
    }

    @Override
    public double consumoAtual(){
        return isLigado() ? getConsumoPorHora() : 0.0;
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() / 60.0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tomada)) return false;
        return super.equals(o);
    }

    public String toString() {
        return "Tomada{" +
                "base=" + super.toString() +
                '}';
    }

    public Tomada clone() {
        return new Tomada(this);
    }


}