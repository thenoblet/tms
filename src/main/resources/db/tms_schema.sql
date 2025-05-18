-- Create the database (run this separately if needed)
-- CREATE DATABASE taskdb;

-- Connect to the database if not already connected
-- \c taskdb

-- Enable UUID extension if you want to use UUIDs instead of BIGINT for IDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Main tasks table
CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(100) NOT NULL,
                                     description TEXT,
                                     priority VARCHAR(20) NOT NULL
                                         CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
                                     due_date DATE NOT NULL,
                                     status VARCHAR(20) NOT NULL
                                         CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'IN_PROGRESS'))
                                                                         DEFAULT 'PENDING',
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     CONSTRAINT due_date_future CHECK (due_date >= CURRENT_DATE)
);

-- Tags table (many-to-many relationship with tasks)
CREATE TABLE IF NOT EXISTS tags (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT unique_tag_name UNIQUE (name)
);

-- Junction table for task-tag relationships
CREATE TABLE IF NOT EXISTS task_tags (
                                         task_id BIGINT NOT NULL,
                                         tag_id BIGINT NOT NULL,
                                         PRIMARY KEY (task_id, tag_id),
                                         FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                                         FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_task_status ON tasks(status);
CREATE INDEX idx_task_priority ON tasks(priority);
CREATE INDEX idx_task_due_date ON tasks(due_date);
CREATE INDEX idx_tag_name ON tags(name);

-- Create function for updating the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER trigger_update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at();

-- Sample data insertion
INSERT INTO tasks (title, description, priority, due_date, status) VALUES
                                                                       ('Complete project proposal', 'Draft and submit the project proposal document', 'HIGH', CURRENT_DATE + 7, 'PENDING'),
                                                                       ('Review code changes', 'Review pull requests from development team', 'MEDIUM', CURRENT_DATE + 3, 'COMPLETED'),
                                                                       ('Fix critical bug', 'Resolve production issue reported by client', 'CRITICAL', CURRENT_DATE + 1, 'IN_PROGRESS'),
                                                                       ('Prepare presentation', 'Create slides for client meeting', 'MEDIUM', CURRENT_DATE + 10, 'PENDING');

INSERT INTO tags (name) VALUES
                            ('urgent'),
                            ('development'),
                            ('documentation'),
                            ('meeting'),
                            ('bugfix');

INSERT INTO task_tags (task_id, tag_id) VALUES
                                            (1, 3),  -- Complete project proposal -> documentation
                                            (2, 2),  -- Review code changes -> development
                                            (3, 1),  -- Fix critical bug -> urgent
                                            (3, 5),  -- Fix critical bug -> bugfix
                                            (4, 4);  -- Prepare presentation -> meeting