#!/bin/bash

echo "🚀 Starting RAG Knowledge Base Q&A System..."

# Check environment variable
if [ -z "$GOOGLE_API_KEY" ]; then
    echo "❌ ERROR: Please set GOOGLE_API_KEY environment variable"
    echo "export GOOGLE_API_KEY=your-gemini-api-key-here"
    exit 1
fi

# Start all services (Docker Compose will handle dependencies)
echo "🚀 Starting all services..."
docker-compose up --abort-on-container-exit

# Check if any service failed
if [ $? -ne 0 ]; then
    echo "❌ Some services failed to start properly"
    echo "📋 Checking service status..."
    docker-compose ps
    echo "🔍 Checking logs..."
    docker-compose logs --tail=20
    exit 1
fi

echo "✅ System started!"
echo "🌐 Visit: http://localhost:8080" 