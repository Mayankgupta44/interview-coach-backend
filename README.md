# AI-Powered Interview Preparation and Communication Coach (Backend)

Spring Boot backend for an AI-powered interview preparation platform that helps users practice technical interviews, analyze performance, and improve weak areas using AI.

---

## 🚀 Overview

This backend provides REST APIs for authentication, profile management, interview session creation, AI-based evaluation, and performance tracking.

---

## 🚀 Features

- 🔐 JWT-based authentication and authorization  
- 👤 User profile management (skills, role, experience)  
- 📄 Resume text storage and PDF upload support  
- 📊 Job description management  
- 🤖 AI-based skill gap analysis  
- 🎯 Interview session creation with difficulty levels  
- 🧠 AI-generated interview questions  
- ✍️ Text answer submission and evaluation  
- 🎤 Audio answer transcription support  
- 🔁 Retry and re-evaluation support  
- 📈 Performance tracking and recommendations  

---

## 🛠️ Tech Stack

- **Language:** Java 17 / 21  
- **Framework:** Spring Boot  
- **Security:** Spring Security + JWT  
- **Database:** PostgreSQL  
- **ORM:** Hibernate / JPA  
- **Build Tool:** Maven  
- **AI Integration:** Groq / Gemini API  
- **PDF Handling:** Apache PDFBox  
- **API Docs:** Swagger UI  

---

## 🏗️ Architecture

The backend follows a layered architecture:

- Controller → Handles REST APIs  
- Service → Business logic  
- Repository → Database operations  
- Entity → Database mapping  
- DTO → Request/Response handling  
- Security → JWT authentication  
- AI Layer → AI API communication  

---

## 🔄 API Workflow

1. User registers or logs in  
2. JWT token is generated  
3. User updates profile and skills  
4. Resume and job description are added  
5. AI performs skill-gap analysis  
6. User creates interview session  
7. AI generates interview questions  
8. User submits answers (text/audio)  
9. AI evaluates answers  
10. Feedback, gaps, and recommendations generated  

---

## 🗄️ Database Entities

- User  
- ResumeProfile  
- ResumeDocument  
- JobDescription  
- SkillGapReport  
- InterviewSession  
- InterviewQuestion  
- InterviewAnswer  
- AnswerEvaluation  
- AnswerAttempt  
- Recommendation  

---

## ⚙️ Setup & Run

```bash
git clone https://github.com/Mayankgupta44/interview-coach-backend.git
cd interview-coach-backend
mvn clean install
mvn spring-boot:run
🔑 Environment Variables
DATABASE_URL=your_postgresql_url
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
GROQ_API_KEY=your_ai_api_key
📡 API Documentation

Swagger UI:

http://localhost:8080/swagger-ui.html
🔗 Frontend Repository

https://github.com/Mayankgupta44/interview-coach-frontend

📌 Status

Backend is under active development with core features implemented.

🚀 Future Scope
Add unit and integration tests
Implement refresh token authentication
Add email verification
Improve AI evaluation accuracy
Add Docker support
