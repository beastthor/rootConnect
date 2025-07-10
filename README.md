# rootConnect – Social Matching Platform (Spring Boot + JWT)

RealRoots is a Spring Boot–based RESTful API that helps users find meaningful social connections by matching them based on shared interests and location.

> 🔐 Secured using JWT Authentication  
> 🧭 Matching logic based on common interests & location  
> 🛠️ Technologies: Java 20, Spring Boot, PostgreSQL, JWT, JPA, Lombok

---

## 📌 Features

- ✅ User registration and JWT-based login
- 🔐 Secure endpoints using Spring Security
- 🧬 Update profile (bio, location, personality type)
- 🌍 Search users by location (excluding self)
- 🎯 Match users by shared interests
- 📚 Interest seeding at app startup
- 📜 Fetch available interests (GET /interests)

---

## 🔧 Tech Stack

| Layer         | Technology                      |
|---------------|----------------------------------|
| Backend       | Java 20, Spring Boot             |
| Security      | Spring Security, JWT             |
| Database      | PostgreSQL, Spring Data JPA      |
| ORM/Mapping   | Hibernate, JPA                   |
| Tooling       | Maven, Lombok, Postman, IntelliJ |
| Testing       | JUnit (Planned)                  |

---

## 🚀 API Endpoints

### 🔐 Authentication

- `POST /api/auth/register` – Register a new user
- `POST /api/auth/login` – Login and receive JWT

### 👤 User Profile

- `GET /api/users/me` – Get current profile
- `PATCH /api/users/me` – Update profile (bio, location, personality type)

### 💡 Interests

- `POST /api/users/interests` – Update user interests
- `GET /api/users/interests` – Get all available interests

### 🌍 Social Matching

- `GET /api/users/search?location=Toronto` – Search users by location
- `GET /api/users/match` – Match users by shared interests
- `GET /api/users/match?location=Toronto` – Match users filtered by location

---

## 🛠 Setup Instructions

1. **Clone the repo:**

```bash
git clone https://github.com/yourusername/realroots.git
cd realroots
