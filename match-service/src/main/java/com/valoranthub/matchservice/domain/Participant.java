package com.valoranthub.matchservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Jugador dentro de una partida (players[] del payload de VAL-MATCH-V1).
 * El detalle de abilityCasts no se persiste aquí — ver nota en la migración
 * V1__create_match_tables.sql.
 */
@Entity
@Table(name = "participants", uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "puuid"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"match", "team"})
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    /** Nullable: se resuelve al mapear teams[] antes que players[] al importar. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "puuid", nullable = false, length = 78)
    private String puuid;

    @Column(name = "game_name", length = 64)
    private String gameName;

    @Column(name = "tag_line", length = 16)
    private String tagLine;

    /** characterId del payload — id del agente (uuid), no su nombre legible. */
    @Column(name = "character_id", length = 64)
    private String characterId;

    @Column(name = "competitive_tier")
    private Integer competitiveTier;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "rounds_played", nullable = false)
    private int roundsPlayed;

    @Column(name = "kills", nullable = false)
    private int kills;

    @Column(name = "deaths", nullable = false)
    private int deaths;

    @Column(name = "assists", nullable = false)
    private int assists;

    @Column(name = "playtime_millis", nullable = false)
    private long playtimeMillis;
}
