package Model.Dispositivos;

// O dispositivo mais simples do sistema — apenas liga e desliga (relé puro).
// Não tem comportamento extra além do que Dispositivo já fornece.
public class Relay extends Dispositivo {

    // Construtor por omissão
    public Relay() {
        super();
    }

    // Construtor parametrizado
    public Relay(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
    }

    // Construtor por cópia
    public Relay(Relay r) {
        super(r); // Relay não tem campos próprios, o super já copia tudo
    }

    @Override
    public double consumoAtual(){
        return isLigado() ? getConsumoPorHora() : 0.0;
    }

    @Override
    public double consumoTotalDispositivo(){
        return getTempoLigado() * getConsumoPorHora() / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relay)) return false;
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "Relay{" +
                "base=" + super.toString() +
                '}';
    }

    @Override
    public Relay clone() {
        return new Relay(this);
    }
}
