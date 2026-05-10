package Acoes;

import Casa.Casa;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoDefinirNivel — define um nível (0-100) numa lista de dispositivos
 * regulaveis identificados por id.
 *
 * Funciona para qualquer dispositivo que implemente Regulavel:
 *   Lampada (intensidade), ColunaSom (volume), Exaustor (velocidade), etc.
 *
 * Usada nos cenários:
 *   - "Ver Cinema" / "Jantar com Amigos" → baixar intensidade das lâmpadas
 *   - "Jantar com Amigos"                → aumentar volume das colunas
 *   - "Deitar"                           → lâmpadas a 0%
 */
public class AcaoDefinirNivel implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Integer> idsDispositivos;
    private final int nivel;
    private final String descricao;

    public AcaoDefinirNivel(List<Integer> idsDispositivos, int nivel, String descricao) {
        this.idsDispositivos = new ArrayList<>(idsDispositivos);
        this.nivel = Math.max(0, Math.min(100, nivel)); // garante 0-100
        this.descricao = descricao;
    }

    public AcaoDefinirNivel(List<Integer> idsDispositivos, int nivel) {
        this(idsDispositivos, nivel,
             "Definir nível para " + nivel + "% em " + idsDispositivos.size() + " dispositivo(s)");
    }

    public AcaoDefinirNivel(AcaoDefinirNivel a) {
        this.idsDispositivos = new ArrayList<>(a.idsDispositivos);
        this.nivel = a.nivel;
        this.descricao = a.descricao;
    }

    @Override
    public void executar(Casa casa) {
        if (casa == null) return;
        for (Integer id : this.idsDispositivos) {
            try {
                casa.alterarNivelDispositivoPorId(id, this.nivel);
            } catch (Exception e) {
                System.err.println("AcaoDefinirNivel: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }

    @Override
    public AcaoDefinirNivel clone() {
        return new AcaoDefinirNivel(this);
    }
}
