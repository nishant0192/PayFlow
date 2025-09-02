CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    kyc_status VARCHAR(20) DEFAULT 'PENDING',
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    phone_verified BOOLEAN DEFAULT false,
    login_attempts INTEGER DEFAULT 0,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Insert admin user (password: Admin@123)
INSERT INTO users (email, phone, password_hash, first_name, last_name, role, kyc_status, email_verified, phone_verified)
VALUES ('admin@payflow.com', '9999999999', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOemskbZS3Ag4rH1d7XbE.Q4Dz/FWqh.O', 'Admin', 'User', 'ADMIN', 'VERIFIED', true, true);
