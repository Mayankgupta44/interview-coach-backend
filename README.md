# Interview Coach Backend

Spring Boot backend for an AI-powered interview preparation platform that helps users practice, analyze performance, and improve weak areas.

---

## 🚀 Features

- 🔐 JWT-based authentication and secure APIs  
- 🤖 AI-powered skill gap analysis using Gemini API  
- 📊 Performance evaluation and feedback generation  
- 💡 Personalized recommendations engine  
- 👤 User profile and session management  

---

## 🛠️ Tech Stack

- **Language:** Java 21  
- **Framework:** Spring Boot  
- **Database:** PostgreSQL  
- **Authentication:** JWT  
- **AI Integration:** Gemini API  
- **API Testing:** Swagger UI  

---

## ⚙️ Setup & Run

1. Clone the repository

```bash
git clone https://github.com/Mayankgupta44/interview-coach-backend.git
````

2. Configure environment variables

```env
GEMINI_API_KEY=your_api_key
```

3. Run the application

```bash
./mvnw spring-boot:run
```

---

## 📡 API Documentation

Access Swagger UI after running the app:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔄 Workflow

1. User logs in / registers
2. Selects topic and difficulty
3. AI generates interview questions
4. User submits answers
5. System evaluates responses using AI
6. Generates feedback, skill gaps, and recommendations

---

## 📌 Notes

* AI responses depend on API availability and rate limits
* Ensure valid Gemini API key is configured

---
