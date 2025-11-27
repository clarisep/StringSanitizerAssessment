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

-- 3️⃣ Bulk insert from file (plain text)
--    Each line is a phrase; supports multi-word phrases
--    UTF-8 safe
BULK INSERT sensitive_words (word)
FROM '/db-init/sql_sensitive_list.txt'
WITH (
    FIELDTERMINATOR = '\n',   -- treat entire line as one field
    ROWTERMINATOR = '\n',
    FIRSTROW = 1,
    CODEPAGE = '65001',       -- UTF-8
    DATAFILETYPE = 'char'
);
GO

-- 4️⃣ Optional: trim spaces from words
UPDATE sensitive_words
SET word = LTRIM(RTRIM(word));
GO

PRINT 'Database initialization complete!';
