CREATE TABLE file_metadata (
    id SERIAL PRIMARY KEY,
    file_path TEXT NOT NULL,
    file_size BIGINT,
    last_modified TIMESTAMP,
    scanned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (file_path, last_modified)
);

CREATE TABLE scan_targets (
    id SERIAL PRIMARY KEY,
    container_name TEXT NOT NULL,
    directory_path TEXT NOT NULL,
    target_directory_path TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    pairing_keys TEXT
);
