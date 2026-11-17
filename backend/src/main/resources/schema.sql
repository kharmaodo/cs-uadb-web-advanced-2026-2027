CREATE TABLE IF NOT EXISTS etudiant (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    matricule            TEXT NOT NULL UNIQUE CHECK (length(matricule) = 7),
    lastname             TEXT NOT NULL,
    firstname            TEXT NOT NULL,
    ufr                  TEXT NOT NULL,
    telephone            TEXT,
    email_institutionnel TEXT,
    id_card              TEXT,
    id_driver            TEXT
);

CREATE TABLE IF NOT EXISTS logement_crous (
    etudiant_id    INTEGER PRIMARY KEY REFERENCES etudiant(id) ON DELETE CASCADE,
    pavillon       TEXT NOT NULL,
    numero_chambre TEXT NOT NULL,
    lit            TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS api_key (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    key_hash   TEXT NOT NULL UNIQUE,
    owner      TEXT NOT NULL,
    active     INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);