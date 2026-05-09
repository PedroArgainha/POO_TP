package Sugestoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AnalisadorPadroes — analisa o histórico de interações de um utilizador
 * e gera sugestões de escalonamentos.
 *
 * O algoritmo é simples mas eficaz para o requisito 8.4 do enunciado:
 *
 *   1. Agrupa as interações por (casa, dispositivo, tipo de ação).
 *      Cada grupo são todas as vezes que o utilizador fez a mesma
 *      operação no mesmo dispositivo da mesma casa.
 *
 *   2. Para cada grupo, agrupa as interações por hora arredondada
 *      (slot horário). Por exemplo, ligações às 18:55 e 19:10 caem
 *      no slot 19:00.
 *
 *   3. Se um slot tem MIN_OCORRENCIAS interações em DIAS DIFERENTES
 *      (não é o mesmo dia repetido), gera uma sugestão.
 *
 *   4. A hora sugerida é a média das horas das ocorrências do slot,
 *      em vez do slot arredondado: produz uma sugestão mais precisa.
 *
 * O analisador é stateless — não guarda nada, só faz a análise.
 * Por isso pode ser uma simples classe utilitária.
 */
public class AnalisadorPadroes {

    // Quantas ocorrências (em dias diferentes) são necessárias para sugerir.
    private static final int MIN_OCORRENCIAS = 3;

    // Tolerância em minutos para agrupar interações no mesmo "slot horário".
    // 60 = arredonda à hora; 30 = arredonda à meia-hora; 15 = a cada 15 min.
    private static final int MINUTOS_POR_SLOT = 60;

    /**
     * Analisa um histórico de interações e devolve uma lista de sugestões.
     *
     * @param historico lista de todas as interações registadas do utilizador
     * @param idsAutomacoesExistentes para evitar sugerir o que já existe
     *                               (passa-se uma lista de "chaves" representando
     *                               escalonamentos já criados; nesta versão
     *                               passamos uma lista vazia para simplificar)
     * @return lista de sugestões geradas
     */
    public static List<SugestaoEscalonamento> analisar(List<RegistoInteracao> historico) {
        List<SugestaoEscalonamento> sugestoes = new ArrayList<>();

        if (historico == null || historico.isEmpty()) {
            return sugestoes;
        }

        // PASSO 1: agrupar interações por (idCasa, idDispositivo, tipoAcao)
        Map<String, List<RegistoInteracao>> grupos = agruparPorChave(historico);

        // PASSO 2 e 3: para cada grupo, procurar slots horários populares
        for (Map.Entry<String, List<RegistoInteracao>> entry : grupos.entrySet()) {
            List<RegistoInteracao> interacoesDoGrupo = entry.getValue();

            // Subagrupar por slot horário
            Map<Integer, List<RegistoInteracao>> porSlot = agruparPorSlotHorario(interacoesDoGrupo);

            // Para cada slot, ver se tem ocorrências suficientes
            for (Map.Entry<Integer, List<RegistoInteracao>> slot : porSlot.entrySet()) {
                List<RegistoInteracao> nesteSlot = slot.getValue();

                int diasDistintos = contarDiasDistintos(nesteSlot);

                if (diasDistintos >= MIN_OCORRENCIAS) {
                    // PASSO 4: gerar sugestão com a hora média
                    SugestaoEscalonamento s = construirSugestao(nesteSlot, diasDistintos);
                    if (s != null) {
                        sugestoes.add(s);
                    }
                }
            }
        }

        return sugestoes;
    }

    /**
     * Agrupa as interações pela chave composta (idCasa + idDispositivo + tipoAcao).
     * O resultado é um Map cujas keys são strings tipo "C2:5:LIGAR".
     */
    private static Map<String, List<RegistoInteracao>> agruparPorChave(List<RegistoInteracao> historico) {
        Map<String, List<RegistoInteracao>> grupos = new HashMap<>();

        for (RegistoInteracao r : historico) {
            String chave = r.getIdCasa() + ":" + r.getIdDispositivo() + ":" + r.getTipoAcao();
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(r);
        }

        return grupos;
    }

    /**
     * Agrupa as interações por "slot horário" (arredondamento à hora mais próxima).
     * Devolve um Map onde a key é o slot (em minutos desde meia-noite, dividido pelo
     * tamanho do slot) e o value é a lista de interações nesse slot.
     */
    private static Map<Integer, List<RegistoInteracao>> agruparPorSlotHorario(List<RegistoInteracao> lista) {
        Map<Integer, List<RegistoInteracao>> porSlot = new HashMap<>();

        for (RegistoInteracao r : lista) {
            LocalTime hora = r.getTempo().toLocalTime();
            int minutosDoDia = hora.getHour() * 60 + hora.getMinute();
            int slot = minutosDoDia / MINUTOS_POR_SLOT;
            porSlot.computeIfAbsent(slot, k -> new ArrayList<>()).add(r);
        }

        return porSlot;
    }

    /**
     * Conta quantos dias DISTINTOS estão presentes na lista de interações.
     * Não basta ter 3 ocorrências — têm de ser em dias diferentes,
     * caso contrário um utilizador que liga e desliga 5 vezes seguidas no
     * mesmo dia geraria uma sugestão falsa.
     */
    private static int contarDiasDistintos(List<RegistoInteracao> lista) {
        Set<LocalDate> dias = new HashSet<>();
        for (RegistoInteracao r : lista) {
            dias.add(r.getTempo().toLocalDate());
        }
        return dias.size();
    }

    /**
     * Constrói a sugestão final a partir de uma lista de interações no mesmo
     * slot. A hora sugerida é a média das horas das ocorrências.
     */
    private static SugestaoEscalonamento construirSugestao(List<RegistoInteracao> lista, int diasDistintos) {
        if (lista.isEmpty()) return null;

        // Calcular a hora média (em minutos desde meia-noite)
        long totalMinutos = 0;
        for (RegistoInteracao r : lista) {
            LocalTime h = r.getTempo().toLocalTime();
            totalMinutos += h.getHour() * 60L + h.getMinute();
        }
        int mediaMinutos = (int) (totalMinutos / lista.size());
        LocalTime horaMedia = LocalTime.of(mediaMinutos / 60, mediaMinutos % 60);

        // Os campos comuns vêm da primeira interação (todas têm os mesmos valores)
        RegistoInteracao primeira = lista.get(0);

        return new SugestaoEscalonamento(
                primeira.getIdCasa(),
                primeira.getIdDispositivo(),
                primeira.getTipoAcao(),
                horaMedia,
                diasDistintos
        );
    }
}