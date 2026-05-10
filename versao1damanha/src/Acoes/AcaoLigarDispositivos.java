package Acoes;

import Casa.Casa;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoLigarDispositivos — liga uma lista de dispositivos identificados por id.
 *
 * Guarda apenas IDs. A resolução para o Dispositivo real acontece dentro da
 * Casa, no momento da execução, via casa.ligarDispositivoPorId(id). Assim,
 * a UI nunca recebe referências aos dispositivos — só lhes pede ao DomusControl
 * uma lista de ids para escolher.
 */
public class AcaoLigarDispositivos implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final String descricao;

    public AcaoLigarDispositivos(List<Integer> idsDispositivos, String descricao) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.descricao = descricao;
    }

    public AcaoLigarDispositivos(List<Integer> idsDispositivos) {
        this(idsDispositivos, "Ligar " + idsDispositivos.size() + " dispositivo(s)");
    }

    /** Construtor de cópia. Lista nova; os Integer são imutáveis. */
    public AcaoLigarDispositivos(AcaoLigarDispositivos a) {
        this.idsDispositivos = new ArrayList<>(a.idsDispositivos);
        this.descricao = a.descricao;
    }

    @Override
    public void executar(Casa casa) {
        if (casa == null) return;
        for (Integer id : this.idsDispositivos) {
            try {
                casa.ligarDispositivoPorId(id);
            } catch (Exception e) {
                // tolerância: se um dos ids já não existir, continua
                System.err.println("AcaoLigarDispositivos: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }

    @Override
    public AcaoLigarDispositivos clone() {
        return new AcaoLigarDispositivos(this);
    }
}
