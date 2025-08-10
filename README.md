# BlogNest - Backend 
> **Frontend Repository:** [FrontEnd](https://github.com/invinc2005/blogNest-frontend)
>
> **Live Frontend URL:** [BlogNest](https://blog-nesttest.vercel.app/).
> 
This is the Spring Boot backend for BlogNest, a full-stack blogging application. It provides a complete RESTful API for user authentication, post management, comments, likes, and more.

## Features

- **JWT Authentication:** Secure user registration and login using JSON Web Tokens.
- **Full CRUD Operations:** Create, Read, Update, and Delete functionality for posts and comments.
- **Like System:** Users can like and unlike posts.
- **User Dashboard:** Endpoints to provide statistics for user dashboards, such as monthly post counts.
- **Cloud Storage Integration:** File uploads are handled via [Supabase Storage](https://supabase.com/storage) for scalable, cloud-based image hosting.
- **Cloud Database:** Connects to a [Supabase](https://supabase.com/database) PostgreSQL database.
- **Dockerized:** Includes a `Dockerfile` for easy containerization and deployment on platforms like [Render](https://render.com).

---

## Tech Stack

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

---

## Setup and Run Locally

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/invinc2005/blogNest-backend.git
    cd blogNest-backend
    ```

2.  **Configure `application.properties`:**
    Create a file at `src/main/resources/application.properties` and add your local or Supabase database credentials and secrets:
    ```properties
    spring.datasource.url=jdbc:postgresql://<your_db_host>:<port>/<db_name>
    spring.datasource.username=<your_db_username>
    spring.datasource.password=<your_db_password>

    SUPABASE_URL=<your_supabase_project_url>
    SUPABASE_BUCKET=<your_supabase_bucket_name>
    SUPABASE_API_KEY=<your_supabase_service_role_key>

    JWT_SECRET_KEY=<your_jwt_secret>
    ```

3.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The server will start on `http://localhost:8080`.

---

## API Endpoints

A brief overview of the main API endpoints:

| Method | Endpoint                      | Description                             |
| :----- | :---------------------------- | :-------------------------------------- |
| POST   | `/api/auth/register`          | Register a new user.                    |
| POST   | `/api/auth/login`             | Authenticate a user and get a JWT.      |
| GET    | `/api/posts`                  | Get a paginated list of all posts.      |
| POST   | `/api/posts`                  | Create a new post.                      |
| GET    | `/api/posts/{id}`             | Get a single post by its ID.            |
| PUT    | `/api/posts/{id}`             | Update an existing post.                |
| DELETE | `/api/posts/{id}`             | Delete a post.                          |
| POST   | `/api/posts/{id}/like`        | Like a post.                            |
| POST   | `/api/posts/{id}/comments`    | Add a comment to a post.                |
| GET    | `/api/users/me`               | Get the current authenticated user's profile. |
