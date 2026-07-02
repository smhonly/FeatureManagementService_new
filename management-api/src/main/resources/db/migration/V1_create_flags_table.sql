-- Feature flags table
CREATE TABLE flags (
    id          BIGSERIAL    PRIMARY KEY,
    flag_key    VARCHAR(128) NOT NULL UNIQUE,
    env         VARCHAR(32)  NOT NULL DEFAULT 'default',
    app         VARCHAR(64)  NOT NULL DEFAULT 'default',
    flag_type   VARCHAR(32)  NOT NULL,                       -- boolean / pct / targeting
    state       VARCHAR(16)  NOT NULL DEFAULT 'active',      -- active / archived
    definition  TEXT         NOT NULL,                       -- JSON: { type, ... }
    version     BIGINT       NOT NULL DEFAULT 1,             -- optimistic lock
    description VARCHAR(512),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(64),
    updated_by  VARCHAR(64)
);

CREATE INDEX idx_flags_state     ON flags (state);
CREATE INDEX idx_flags_flag_type ON flags (flag_type);
CREATE INDEX idx_flags_env_app   ON flags (env, app);

-- Per-version snapshot of a flag. One row per state change.
-- findAsOf: SELECT ... WHERE flag_key=? AND updated_at<=? ORDER BY updated_at DESC LIMIT 1
CREATE TABLE flag_history (
    id          BIGSERIAL    PRIMARY KEY,
    flag_key    VARCHAR(128) NOT NULL,
    env         VARCHAR(32)  NOT NULL DEFAULT 'default',
    app         VARCHAR(64)  NOT NULL DEFAULT 'default',
    version     BIGINT       NOT NULL,
    flag_type   VARCHAR(32)  NOT NULL,
    state       VARCHAR(16)  NOT NULL,
    definition  TEXT         NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    updated_by  VARCHAR(64),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_flag_history_key_at    ON flag_history (flag_key, updated_at);
CREATE INDEX idx_flag_history_env_app_key ON flag_history (env, app, flag_key, updated_at DESC);