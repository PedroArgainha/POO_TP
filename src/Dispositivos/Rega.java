package Dispositivos;

// Rega — dispositivo simples de rega (válvula ou aspersor individual).
// Para sistemas com múltiplas zonas, ver SistemaRega.
public class Rega extends Dispositivo {

    // Construtor por omissão
    public Rega() {
        super();
    }

    // Construtor parametrizado
    public Rega(String nome, String marca, String modelo, double consumoPorHora) {
        super(nome, marca, modelo, consumoPorHora);
    }

    // Construtor por cópia
    public Rega(Rega r) {
        super(r);
    }

    // A rega é um dispositivo simples de ligar/desligar (como um relay):
    // quando ligada abre a válvula e consome o valor fixo nominal; quando desligada, zero.
    @Override
    public double consumoAtual() {
        return isLigado() ? getConsumoPorHora() : 0.0;
    }

    @Override
    public double consumoTotalDispositivo() {
        return getTempoLigado() * getConsumoPorHora() / 60.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rega)) return false;
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "Rega{" +
                "base=" + super.toString() +
                '}';
    }

    @Override
    public Rega clone() {
        return new Rega(this);
    }
}