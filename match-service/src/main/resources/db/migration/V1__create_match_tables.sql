-- Esquema base de match-service: partidas importadas desde VAL-MATCH-V1
-- (https://developer.riotgames.com/apis#val-match-v1), con equipos y
-- participantes normalizados. El detalle round-by-round (roundResults) y los
-- casts de habilidades no se persisten aquí: ai-analysis-service los procesa
-- directamente sobre el payload crudo y guarda sus resultados en MongoDB
-- (Fase 3) — match-service solo mantiene el resumen relacional.

CREATE TABLE matches (
    id                 BIGSERIAL PRIMARY KEY,
    match_id           VARCHAR(64)  NOT NULL,
    map_id             VARCHAR(120),
    game_version       VARCHAR(60),
    region             VARCHAR(10),
    queue_id           VARCHAR(40),
    game_mode          VARCHAR(40),
    is_ranked          BOOLEAN      NOT NULL DEFAULT FALSE,
    is_completed       BOOLEAN      NOT NULL DEFAULT TRUE,
    season_id          VARCHAR(64),
    game_start         TIMESTAMPTZ  NOT NULL,
    game_length_millis BIGINT       NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_matches_match_id UNIQUE (match_id)
);

CREATE INDEX idx_matches_match_id  ON matches (match_id);
CREATE INDEX idx_matches_game_start ON matches (game_start);

CREATE TABLE teams (
    id            BIGSERIAL PRIMARY KEY,
    match_id      BIGINT      NOT NULL REFERENCES matches (id) ON DELETE CASCADE,
    team_id       VARCHAR(20) NOT NULL,
    won           BOOLEAN     NOT NULL DEFAULT FALSE,
    rounds_played INT         NOT NULL DEFAULT 0,
    rounds_won    INT         NOT NULL DEFAULT 0,
    num_points    INT,
    CONSTRAINT uq_teams_match_team UNIQUE (match_id, team_id)
);

CREATE INDEX idx_teams_match_id ON teams (match_id);

CREATE TABLE participants (
    id               BIGSERIAL PRIMARY KEY,
    match_id         BIGINT      NOT NULL REFERENCES matches (id) ON DELETE CASCADE,
    team_id          BIGINT      REFERENCES teams (id) ON DELETE SET NULL,
    puuid            VARCHAR(78) NOT NULL,
    game_name        VARCHAR(64),
    tag_line         VARCHAR(16),
    character_id     VARCHAR(64),
    competitive_tier INT,
    score            INT    NOT NULL DEFAULT 0,
    rounds_played    INT    NOT NULL DEFAULT 0,
    kills            INT    NOT NULL DEFAULT 0,
    deaths           INT    NOT NULL DEFAULT 0,
    assists          INT    NOT NULL DEFAULT 0,
    playtime_millis  BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_participants_match_puuid UNIQUE (match_id, puuid)
);

CREATE INDEX idx_participants_match_id ON participants (match_id);
CREATE INDEX idx_participants_puuid    ON participants (puuid);
