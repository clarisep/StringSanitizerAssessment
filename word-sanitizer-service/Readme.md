1. I did not have Docker Engine and Docker Compose installed on my system, so I installed Docker Desktop.
2. I then ran  docker compose up --build -- This pulled and startedthe images needed, as well as build the microservice
3. The following tasks were completed:
   Restful API:
   Design and implement a RESTful API using Java Spring Boot
   Swagger Documentation:
   Integrate Swagger to generate API documentation automatically.
   Ensure that all endpoints, request parameters, and responses are well-documented using Swagger annotations.
   Database CRUD Layer with MSSQL:
   Implement CRUD operations (Create, Read, Update, Delete) for the database.
   Utilize MSSQL as the database backend to store and retrieve data.
   Unit Tests
4. Add appropriate unit tests to ensure sufficient coverage.
   Implemented

5. What would you do to enhance performance of your project?

   **Database Indexing (Not Implemented Yet)**
    - Add an index on the 'word' column in the 'sensitive_words' table.
    - Purpose: Speed up lookups and duplicate checks.
    - Example: CREATE UNIQUE INDEX idx_sensitive_word ON sensitive_words(word);


**In-Memory Caching with Caffeine (Implemented)**
- Currently used for caching frequently accessed data.
- Provides fast, in‑process caching with eviction policies (TTL, max size).
- Benefit: Reduces repeated DB hits and improves response times.

** In case of a larger microservice, we could use Redis, Connection Pool Tuning, batch inserts and Pagination

6. What additional enhancements would add to the project to make it more complete ?
   Authentication - We could add Spring Security, e.g role-based or via a JWT token
   Authorization - We could add CORS control, allow to trust only certain origins.
   Audit logging - Track who is responsible for operations
   Monitoring and metrics - Use Spring Boot Actuator for monitor and service health

