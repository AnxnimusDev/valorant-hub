package com.valoranthub.matchservice.service;

import com.valoranthub.matchservice.domain.Match;
import com.valoranthub.matchservice.domain.Participant;
import com.valoranthub.matchservice.domain.Team;
import com.valoranthub.matchservice.riot.dto.MatchDto;
import com.valoranthub.matchservice.riot.dto.MatchInfoDto;
import com.valoranthub.matchservice.riot.dto.PlayerDto;
import com.valoranthub.matchservice.riot.dto.PlayerStatsDto;
import com.valoranthub.matchservice.riot.dto.TeamDto;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Traduce el MatchDto de VAL-MATCH-V1 al grafo de entidades JPA Match/Team/Participant. */
@Component
public class MatchMapper {

    public Match toEntity(MatchDto dto) {
        MatchInfoDto info = dto.matchInfo();

        Match match =
                Match.builder()
                        .matchId(info.matchId())
                        .mapId(info.mapId())
                        .gameVersion(info.gameVersion())
                        .region(info.region())
                        .queueId(info.queueId())
                        .gameMode(info.gameMode())
                        .ranked(info.isRanked())
                        .completed(info.isCompleted())
                        .seasonId(info.seasonId())
                        .gameStart(Instant.ofEpochMilli(info.gameStartMillis()))
                        .gameLengthMillis(info.gameLengthMillis())
                        .createdAt(Instant.now())
                        .build();

        Map<String, Team> teamsByRiotTeamId = new HashMap<>();
        if (dto.teams() != null) {
            for (TeamDto teamDto : dto.teams()) {
                Team team = toTeamEntity(teamDto);
                match.addTeam(team);
                teamsByRiotTeamId.put(teamDto.teamId(), team);
            }
        }

        if (dto.players() != null) {
            for (PlayerDto playerDto : dto.players()) {
                match.addParticipant(toParticipantEntity(playerDto, teamsByRiotTeamId.get(playerDto.teamId())));
            }
        }

        return match;
    }

    private Team toTeamEntity(TeamDto dto) {
        return Team.builder()
                .teamId(dto.teamId())
                .won(dto.won())
                .roundsPlayed(dto.roundsPlayed())
                .roundsWon(dto.roundsWon())
                .numPoints(dto.numPoints())
                .build();
    }

    private Participant toParticipantEntity(PlayerDto dto, Team team) {
        PlayerStatsDto stats = dto.stats();
        return Participant.builder()
                .team(team)
                .puuid(dto.puuid())
                .gameName(dto.gameName())
                .tagLine(dto.tagLine())
                .characterId(dto.characterId())
                .competitiveTier(dto.competitiveTier())
                .score(stats != null ? stats.score() : 0)
                .roundsPlayed(stats != null ? stats.roundsPlayed() : 0)
                .kills(stats != null ? stats.kills() : 0)
                .deaths(stats != null ? stats.deaths() : 0)
                .assists(stats != null ? stats.assists() : 0)
                .playtimeMillis(stats != null ? stats.playtimeMillis() : 0)
                .build();
    }
}
