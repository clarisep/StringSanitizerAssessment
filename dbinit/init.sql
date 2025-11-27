USE sanitizerdb;
GO

IF OBJECT_ID('sensitive_words', 'U') IS NULL
BEGIN
    CREATE TABLE sensitive_words (
        id INT PRIMARY KEY IDENTITY(1,1),
        word VARCHAR(255) NOT NULL
    );
END
GO
BULK INSERT sensitive_words
FROM '/db-init/sql_sensitive_list.txt'
WITH (
    FIELDTERMINATOR = '\n',  -- each line is a new field
    ROWTERMINATOR = '\n',
    FIRSTROW = 1
);
GO
