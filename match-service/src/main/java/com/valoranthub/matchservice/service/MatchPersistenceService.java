package com.valoranthub.matchservice.service;

import com.valoranthub.matchservice.domain.Match;
import com.valoranthub.matchservice.repository.MatchRepository;
import com.valoranthub.matchservice.riot.dto.MatchDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aislado de MatchImportService a propósito: si @Transactional viviera en un
 * método llamado con `this.` desde la misma clase, el proxy de Spring lo
 * ignoraría (self-invocation). Al ser un bean colaborador aparte, la llamada
 * siempre pasa por el proxy transaccional.
 */
@Service
@RequiredArgsConstructor
public class MatchPersistenceService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    /** Devuelve empty si la partida ya existía (comprobación dentro de la transacción). */
    @Transactional
    public Optional<Match> saveIfNew(MatchDto dto) {
        String matchId = dto.matchInfo().matchId();
        if (matchRepository.existsByMatchId(matchId)) {
            return Optional.empty();
        }
        Match match = matchMapper.toEntity(dto);
        return Optional.of(matchRepository.save(match));
    }
}
