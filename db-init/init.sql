-- ===========================================
-- init.sql: robust initialization for sanitizerdb
-- Works with Docker, SQL Server Linux containers
-- ===========================================

-- Create database if it doesn't exist
IF DB_ID('sanitizerdb') IS NULL
BEGIN
    PRINT 'Creating database sanitizerdb...';
    CREATE DATABASE sanitizerdb;
END
GO

-- Switch to the database
USE sanitizerdb;
GO

-- 1️⃣ Create table if it doesn't exist
IF OBJECT_ID('sensitive_words', 'U') IS NULL
BEGIN
    PRINT 'Creating table sensitive_words...';
CREATE TABLE sensitive_words (
                                 id BIGINT PRIMARY KEY IDENTITY(1,1),
                                 word VARCHAR(1000) NOT NULL
);
END
GO

-- 2️⃣ Insert JSON data only if table is empty
IF NOT EXISTS (SELECT 1 FROM sensitive_words)
BEGIN
    DECLARE @json NVARCHAR(MAX);

SELECT @json = BulkColumn
FROM OPENROWSET(
             BULK '/db-init/sql_sensitive_list.txt',
             SINGLE_CLOB
     ) AS j;

INSERT INTO sensitive_words (word)
SELECT value
FROM OPENJSON(@json);

PRINT 'Sensitive words inserted from JSON file.';
END
ELSE
BEGIN
    PRINT 'Table sensitive_words already has data. Skipping insert.';
END
GO

PRINT 'Database initialization complete!';
GO