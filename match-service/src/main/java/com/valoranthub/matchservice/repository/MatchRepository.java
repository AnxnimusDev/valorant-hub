package com.valoranthub.matchservice.repository;

import com.valoranthub.matchservice.domain.Match;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

    boolean existsByMatchId(String matchId);

    /** Para filtrar ids ya importados antes de gastar llamadas a Riot API en ellos. */
    @Query("select m.matchId from Match m where m.matchId in :matchIds")
    List<String> findExistingMatchIds(@Param("matchIds") Collection<String> matchIds);
}
