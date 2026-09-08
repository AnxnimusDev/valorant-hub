package com.valoranthub.matchservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Partida de VALORANT importada desde VAL-MATCH-V1
 * (matchInfo del payload de {@code /val/match/v1/matches/{matchId}}).
 */
@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"teams", "participants"})
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** matchInfo.matchId — identificador único de Riot para la partida. */
    @Column(name = "match_id", nullable = false, unique = true, length = 64)
    private String matchId;

    /** matchInfo.mapId (URL/asset id del mapa, no el nombre legible). */
    @Column(name = "map_id", length = 120)
    private String mapId;

    @Column(name = "game_version", length = 60)
    private String gameVersion;

    /** matchInfo.region (shard: na, eu, ap, kr, latam, br). */
    @Column(name = "region", length = 10)
    private String region;

    @Column(name = "queue_id", length = 40)
    private String queueId;

    @Column(name = "game_mode", length = 40)
    private String gameMode;

    @Column(name = "is_ranked", nullable = false)
    private boolean ranked;

    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @Column(name = "season_id", length = 64)
    private String seasonId;

    /** matchInfo.gameStartMillis, convertido a Instant. */
    @Column(name = "game_start", nullable = false)
    private Instant gameStart;

    @Column(name = "game_length_millis", nullable = false)
    private long gameLengthMillis;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public void addTeam(Team team) {
        teams.add(team);
        team.setMatch(this);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setMatch(this);
    }
}
