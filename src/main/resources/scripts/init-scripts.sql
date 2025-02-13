--liquibase formated sql

-- changeset nomad:1
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    phone VARCHAR(20) UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'USER'
);

-- changeset nomad:2
CREATE TABLE shelters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    working_hours VARCHAR(255),
    contacts VARCHAR(255),
    map_url VARCHAR(255)
);

-- changeset nomad:3
CREATE TABLE animals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    age INT,
    description TEXT,
    shelter_id BIGINT NOT NULL,
    CONSTRAINT fk_animals_shelter FOREIGN KEY (shelter_id) REFERENCES shelters (id)
);

-- changeset nomad:4
CREATE TABLE daily_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    photo_path VARCHAR(500),
    text_report TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    has_photo BOOLEAN DEFAULT FALSE,
    has_text BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_daily_reports_user FOREIGN KEY (user_id) REFERENCES users (id)
);


-- changeset nomad:5
CREATE TABLE adoptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    animal_id BIGINT NOT NULL,
    adoption_date DATE DEFAULT CURRENT_DATE,
    trial_status VARCHAR(50),
    trial_end_date DATE,
    CONSTRAINT fk_adoptions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_adoptions_animal FOREIGN KEY (animal_id) REFERENCES animals (id)
);

-- changeset nomad:6
CREATE TABLE user_states (
    chat_id VARCHAR(255) PRIMARY KEY,
    stage VARCHAR(50) NOT NULL
);

