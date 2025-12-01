1. Installed Docker Desktop.

2. Run:  docker compose up --build 

   This pulled and started the images needed, as well as build the microservice. This might take a while.
   Once the services is up, go to http://localhost:8080/swagger-ui/index.html

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
   I have added Junit Tests and Integration Tests. An H2 in memory database was used when running the tests.
   The integration tests are excluded when running the mvn build.

5. What would you do to enhance performance of your project?

   Database Indexing (Not Implemented Yet)
    - Add an index on the 'word' column in the 'sensitive_words' table.
    - Purpose: Speed up lookups and duplicate checks.
    - Example: CREATE UNIQUE INDEX idx_sensitive_word ON sensitive_words(word);


   In-Memory Caching with Caffeine (Implemented)
    - Currently used for caching frequently accessed data.
      - Provides fast, in‑process caching with eviction policies (TTL, max size).
      - Benefit: Reduces repeated DB hits and improves response times.
    
   In case of a larger microservice, we could use Redis, Connection Pool Tuning, batch inserts and pagination

6. What additional enhancements would add to the project to make it more complete ?
   Authentication - We could add Spring Security, e.g. role-based or via a JWT token
   Authorization - We could add CORS control, allow to trust only certain origins.  
   Audit logging - Track who is responsible for database operations
   Monitoring and metrics - Use Spring Boot Actuator for monitor and service health
   Use logback for logging along with Slf4j

