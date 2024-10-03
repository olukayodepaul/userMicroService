-- ===================================
-- Consolidated Product-related Tables for E-commerce
-- ===================================

-- 1. users Table
-- This is table responsible for registration
CREATE TABLE users (
    id SERIAL PRIMARY KEY,                                          			-- Unique identifier for each user, part of token id
    uuid UUID NOT NULL UNIQUE,                                                  -- uuid with a unique constraint.  Unique identifier
    email VARCHAR(255) NOT NULL,                                    			-- User's email address
    password TEXT NOT NULL,                                         			-- User's hashed password
    role VARCHAR(50),                                               			-- User's role (e.g., admin, user, etc.)
    organisation_id INTEGER,                                        			-- Unique identifier for the user's organization
    password_reset_code TEXT,                                       			-- Code used for reset password
    password_reset_expiration TIMESTAMP,                        				-- Expiration time to reset password
    confirmation_link TEXT,                                         			-- Link for confirming the user's email address
    confirmation_code TEXT,                                         			-- Code used for email verification
    confirmation_token_expiration TIMESTAMP DEFAULT '1900-01-01 00:00:00',      -- Expiration time for the confirmation token
    is_active BOOLEAN DEFAULT TRUE,                                 			-- Indicates if the user account is active
    is_blacklisted BOOLEAN DEFAULT FALSE,                           			-- Indicates if the user is currently blacklisted
    blacklist_expire_at TIMESTAMP DEFAULT '1900-01-01 00:00:00',    			-- Expiration time to lift the blacklist
    created_at TIMESTAMP DEFAULT '1900-01-01 00:00:00',                 		-- Timestamp when the user account was created
    updated_at TIMESTAMP DEFAULT '1900-01-01 00:00:00'                 			-- Timestamp when the user account was last updated
);

CREATE INDEX idx_user_id ON users (id);
CREATE INDEX idx_user_uuid ON users (uuid);

CREATE TABLE blacklist (
    id SERIAL PRIMARY KEY,                                           	        -- Unique identifier for each blacklist entry
    uuid UUID NOT NULL UNIQUE,                                                  -- uuid with a unique constraint.  Unique identifier
    ip_address VARCHAR(45),                                          	        -- IP address (IPv4 or IPv6) to be blacklisted
    reason TEXT,                                                     	        -- Reason for blacklisting
    is_active BOOLEAN DEFAULT TRUE,                                  	        -- Flag to indicate if this blacklist entry is currently active
    created_at TIMESTAMP DEFAULT '1900-01-01 00:00:00',                         -- Timestamp when the entry was created
    updated_at TIMESTAMP DEFAULT '1900-01-01 00:00:00',                         -- Timestamp when the entry was last updated
    expiry_at TIMESTAMP DEFAULT '1900-01-01 00:00:00'                           -- Timestamp for when the blacklist entry expires (optional)
);

CREATE INDEX idx_user_blacklist_uuid ON blacklist (uuid);
CREATE INDEX idx_user_blacklist_id ON blacklist (id);
