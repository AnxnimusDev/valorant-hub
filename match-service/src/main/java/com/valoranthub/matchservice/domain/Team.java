package com.valoranthub.matchservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
 * Equipo dentro de una partida (teams[] del payload de VAL-MATCH-V1).
 * {@code teamId} es un identificador arbitrario de Riot ("Red"/"Blue" en
 * modos bomb), no una entidad de equipo persistente entre partidas.
 */
@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "team_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"match", "participants"})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "team_id", nullable = false, length = 20)
    private String teamId;

    @Column(name = "won", nullable = false)
    private boolean won;

    @Column(name = "rounds_played", nullable = false)
    private int roundsPlayed;

    @Column(name = "rounds_won", nullable = false)
    private int roundsWon;

    @Column(name = "num_points")
    private Integer numPoints;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<Participant> participants = new ArrayList<>();
}
