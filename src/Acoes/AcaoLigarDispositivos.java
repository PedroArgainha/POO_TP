package Acoes;

import Dispositivos.Dispositivo;
import Interfaces.AcaoAutomacao;

import java.util.ArrayList;
import java.util.List;

/**
 * AcaoLigarDispositivos — liga todos os dispositivos de uma lista.
 *
 * Usada em automações (ex: luminosidade baixa → ligar lâmpadas)
 * e cenários (ex: "Acordar" → ligar luzes do quarto).
 */
public class AcaoLigarDispositivos implements AcaoAutomacao {

    private static final long serialVersionUID = 1L;

    private final List<Dispositivo> dispositivos;
    private final String descricao;

    public AcaoLigarDispositivos(List<Dispositivo> dispositivos, String descricao) {
        this.dispositivos = new ArrayList<>(dispositivos);
        this.descricao = descricao;
    }

    public AcaoLigarDispositivos(List<Dispositivo> dispositivos) {
        this(dispositivos, "Ligar " + dispositivos.size() + " dispositivo(s)");
    }

    @Override
    public void executar() {
        for (Dispositivo d : this.dispositivos) {
            d.ligar();
        }
    }

    @Override
    public String descricao() {
        return this.descricao;
    }
}
