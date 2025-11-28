-- ===========================================
-- init.sql: robust initialization for sanitizerdb
-- Works with Docker, SQL Server Linux containers,
-- Spring Boot (Long id), multi-word phrases
-- ===========================================

-- 1️⃣ Create database if it doesn't exist
IF DB_ID('sanitizerdb') IS NULL
BEGIN
    PRINT 'Creating database sanitizerdb...';
    CREATE DATABASE sanitizerdb;
END
GO

-- Switch to the database
USE sanitizerdb;
GO

-- 2️⃣ Create table if it doesn't exist
IF OBJECT_ID('sensitive_words', 'U') IS NULL
BEGIN
    PRINT 'Creating table sensitive_words...';
CREATE TABLE sensitive_words (
                                 id INT PRIMARY KEY IDENTITY(1,1),
                                 word VARCHAR(1000) NOT NULL
);
END
GO
DECLARE @json NVARCHAR(MAX);

SELECT @json = BulkColumn
FROM OPENROWSET(
             BULK '/db-init/sql_sensitive_list.txt',
             SINGLE_CLOB
     ) AS j;

INSERT INTO sensitive_words (word)
SELECT value
FROM OPENJSON(@json);



PRINT 'Database initialization complete!';
