# RAG Knowledge Base Q&A System

A RAG (Retrieval-Augmented Generation) system built with Spring Boot + PostgreSQL + Gemini API, supporting PDF document upload, vectorized storage, and intelligent Q&A.

## Quick Start

### Requirements
- Java 17+
- Docker & Docker Compose
- Google Gemini API Key

### Setup & Run

```bash
# Clone and setup
git clone <repository-url>
cd rag-demo

# Set API key
export GOOGLE_API_KEY=your-gemini-api-key-here

# Start with Docker
docker-compose up -d

# Or run with Maven
mvn spring-boot:run
```

Access: http://localhost:8080

## Demo Guide

### Step 1: Upload Your Document
- Upload a PDF document you're familiar with
- This allows you to verify system accuracy

### Step 2: Test Q&A
- Ask specific questions about your document
- Verify answers match your expectations
- Check source relevance and similarity scores

**Pro Tips:**
- Ask for specific details, numbers, or technical terms
- Test complex multi-part questions
- Verify system handles both broad and narrow queries

### Step 3: Showcase Features
**Technical Advantages:**
- ✅ Quick Docker deployment
- ✅ Modern tech stack (Spring Boot + PostgreSQL + Gemini)
- ✅ High-performance vector search
- ✅ Modular design

**Business Value:**
- 📈 Improve knowledge access efficiency
- 🎯 Increase answer accuracy (avoid hallucinations)
- 🔍 Enhanced traceability with sources
- 💰 Reduce manual support costs

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Data JPA
- **Database**: PostgreSQL + pgvector
- **AI**: Gemini API (text generation + embeddings)
- **Frontend**: Bootstrap 5 + Vanilla JavaScript
- **PDF Processing**: Apache PDFBox 3.0.5

## API Endpoints

- `POST /api/documents/upload` - Upload document
- `GET /api/documents` - List documents
- `DELETE /api/documents/{id}` - Delete document
- `POST /api/rag/ask-with-sources` - Q&A with sources

## Configuration

```yaml
# Key settings in application.yml
google:
  api-key: ${GOOGLE_API_KEY:your-gemini-api-key}
  model: gemini-2.5-flash
  embedding-model: text-embedding-004

rag:
  chunk-size: 1000
  chunk-overlap: 200
  max-results: 5
  similarity-threshold: 0.7
```

## Key Features

- 📄 PDF document upload and processing
- 🔍 Smart text chunking with overlap
- 🧠 Real Gemini embeddings (text-embedding-004)
- 🔎 Semantic search with similarity scoring
- 💬 Intelligent Q&A with source tracking
- 🎨 Modern responsive UI

## Troubleshooting

**Common Issues:**
- Database connection: Check PostgreSQL on port 5432
- API errors: Verify GOOGLE_API_KEY and network
- Upload fails: PDF only, max 10MB

## License

MIT License - see [LICENSE](LICENSE) file.

---

**The key to successful demonstration is showing the system's practicality and technical advancement!**
