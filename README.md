# Search Engine Simulator

A full-stack, portfolio-grade Search Engine Simulator built with Java 21, Spring Boot, and React. 
This project focuses on the core Data Structures & Algorithms (DSA) that power search engines, implemented from scratch without using external search libraries like Elasticsearch or Lucene.

## Features

- **Core Search Engine**: Custom-built Inverted Index and TF-IDF Ranking.
- **Autocomplete & Prefix Suggestions**: Powered by a custom Trie data structure.
- **Spell Correction**: Levenshtein Edit Distance implementation to suggest corrections for misspelled queries.
- **Document Management**: Add, update, and delete documents with real-time indexing.
- **Search Analytics**: Visual dashboards showing query frequencies, history, and system performance.
- **Interactive Learning Hub**: Explore interactive visualizations of how the Inverted Index, Trie, and TF-IDF algorithms work internally.
- **Authentication**: JWT-based authentication via HttpOnly cookies, local registration, password reset, and Google OAuth 2.0 (configurable).

## Tech Stack

**Backend**:
- Java 21
- Spring Boot 3.4.x
- H2 Database (In-Memory Document Storage)
- Spring Security (JWT + OAuth2)
- Maven

**Frontend**:
- React 18
- Vite
- React Router DOM
- Custom Vanilla CSS (Dark Theme, Glassmorphism)
- Recharts / Chart.js (Analytics)

## Getting Started

### Prerequisites
- Java 21+
- Node.js 18+
- Git

### 1. Backend Setup
1. Open a terminal and navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Run the application using the Maven wrapper:
   ```bash
   # Windows
   ./mvnw.cmd spring-boot:run
   
   # Mac/Linux
   ./mvnw spring-boot:run
   ```
The backend runs on `http://localhost:8080`. It automatically seeds 50 sample documents and 15 learning topics on first startup.

### 2. Frontend Setup
1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
The frontend runs on `http://localhost:5173`. 

## OAuth 2.0 Configuration (Optional)
To enable "Sign in with Google", you must configure a local `.env` file in the `backend` directory:
1. Create `backend/.env`.
2. Add your Google OAuth 2.0 credentials:
   ```env
   GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
   GOOGLE_CLIENT_SECRET=your-client-secret
   ```
3. Ensure the Authorized Redirect URI in Google Cloud Console is `http://localhost:8080/login/oauth2/code/google`.

## Project Structure
- `backend/src/main/java/com/searchengine/simulator/dsa/` - Contains the pure DSA implementations (Trie, Inverted Index, Priority Queue, TF-IDF, Levenshtein).
- `frontend/src/pages/` - Contains the primary application views.
- `frontend/src/components/visualizers/` - Contains the interactive algorithm visualization tools.

## Documentation
See [ALGORITHMS.md](ALGORITHMS.md) for a detailed breakdown of the internal data structures, algorithms, and time/space complexity analysis.

## License
MIT License.
