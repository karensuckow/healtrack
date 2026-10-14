CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    inactive_at TIMESTAMP
);

CREATE TABLE working_hours (
    id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_start TIME,
    break_end TIME
);

CREATE TABLE time_off (
    id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255)
);

CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL,
    client_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    is_paid BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_client FOREIGN KEY (client_id) REFERENCES users(id)
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY UNIQUE NOT NULL,
    appointment_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);