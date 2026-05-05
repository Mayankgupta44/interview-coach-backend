# AI-Powered Interview Preparation and Communication Coach (Backend)

Spring Boot backend for an AI-powered interview preparation platform that helps users practice technical interviews, analyze performance, and improve weak areas using AI.

---

## 🚀 Overview

This backend provides REST APIs for authentication, profile management, resume handling, job description management, interview session creation, AI-based answer evaluation, audio transcription, and performance tracking.

---

## ✨ Features

- JWT-based authentication and authorization
- User profile management with skills, target role, and experience level
- Resume text saving and PDF resume upload support
- Job description management
- AI-based skill gap analysis
- Interview session creation with difficulty levels
- AI-generated interview questions
- Text answer submission and AI evaluation
- Audio answer transcription support
- Retry and re-evaluation support
- Attempt history and performance tracking
- Personalized recommendations for improvement

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
- **API Documentation:** Swagger UI

---

## 🏗️ Architecture

The backend follows a layered architecture:

Controller  → Handles REST API requests
Service     → Contains business logic
Repository  → Handles database operations
Entity      → Maps Java classes to database tables
DTO         → Handles request and response data
Security    → Handles JWT authentication and authorization
AI Layer    → Communicates with external AI APIs
🔄 API Workflow
1. User registers or logs in
2. JWT token is generated
3. User updates profile and skills
4. User adds resume and job description
5. AI performs skill-gap analysis
6. User creates an interview session
7. AI generates interview questions
8. User submits answers using text or audio
9. AI evaluates submitted answers
10. Feedback, weak areas, and recommendations are generated
🗄️ Database Entities
User
ResumeProfile
ResumeDocument
JobDescription
SkillGapReport
InterviewSession
InterviewQuestion
InterviewAnswer
AnswerEvaluation
AnswerAttempt
Recommendation

---

## ⚙️ Setup & Run
- 1. Clone the Repository
git clone https://github.com/Mayankgupta44/interview-coach-backend.git
- 2. Go to the Project Folder
cd interview-coach-backend
- 3. Configure Environment Variables

Create an .env file or configure these values in your environment/application properties:

DATABASE_URL=your_postgresql_url
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
GROQ_API_KEY=your_ai_api_key
- 4. Build the Project
mvn clean install
- 5. Run the Application
mvn spring-boot:run

---

## 📡 API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui.html

---

## 🔗 Frontend Repository
https://github.com/Mayankgupta44/interview-coach-frontend
📌 Project Status

The backend is under active development, and the core features have been implemented.

---

## 🚀 Future Scope
Add unit and integration tests
Implement refresh token authentication
Add email verification
Improve AI evaluation accuracy
Add Docker support
