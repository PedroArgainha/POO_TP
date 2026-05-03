package Acoes;

import Interfaces.AcaoAutomacao;
import Interfaces.Regulavel;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoDefinirNivel — define um nível (0-100) numa lista de dispositivos Regulavel.
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

    private final List<Regulavel> dispositivos;
    private final int nivel;
    private final String descricao;

    public AcaoDefinirNivel(List<Regulavel> dispositivos, int nivel, String descricao) {
        this.dispositivos = new ArrayList<>(dispositivos);
        this.nivel = Math.max(0, Math.min(100, nivel)); // garante 0-100
        this.descricao = descricao;
    }

    public AcaoDefinirNivel(List<Regulavel> dispositivos, int nivel) {
        this(dispositivos, nivel, "Definir nível para " + nivel + "% em " + dispositivos.size() + " dispositivo(s)");
    }

    @Override
    public void executar() {
        for (Regulavel r : this.dispositivos) {
            try {
                r.setNivel(this.nivel);
            } catch (Exception e) {
                System.err.println("Erro ao definir nível: " + e.getMessage());
            }
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }
}
