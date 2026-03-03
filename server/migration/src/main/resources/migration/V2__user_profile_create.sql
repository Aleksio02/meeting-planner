CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY REFERENCES users(id),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    company VARCHAR(255),
    social JSON
)