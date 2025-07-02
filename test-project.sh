#!/bin/bash

echo "🧪 Testing RAG project components..."

# Check Java version
echo "1. Checking Java version..."
java -version
if [ $? -eq 0 ]; then
    echo "✅ Java environment OK"
else
    echo "❌ Java environment error"
    exit 1
fi

# Check Maven
echo "2. Checking Maven..."
mvn -version
if [ $? -eq 0 ]; then
    echo "✅ Maven environment OK"
else
    echo "❌ Maven environment error"
    exit 1
fi

# Check Docker
echo "3. Checking Docker..."
docker --version
if [ $? -eq 0 ]; then
    echo "✅ Docker environment OK"
else
    echo "❌ Docker environment error"
    exit 1
fi

# Check Docker Compose
echo "4. Checking Docker Compose..."
docker-compose --version
if [ $? -eq 0 ]; then
    echo "✅ Docker Compose environment OK"
else
    echo "❌ Docker Compose environment error"
    exit 1
fi

# Check environment variable
echo "5. Checking environment variable..."
if [ -z "$GOOGLE_API_KEY" ]; then
    echo "❌ GOOGLE_API_KEY environment variable not set"
    echo "Please run: export GOOGLE_API_KEY=your-gemini-api-key-here"
    exit 1
else
    echo "✅ GOOGLE_API_KEY environment variable is set"
fi

# Compile project
echo "6. Compiling project..."
mvn clean compile
if [ $? -eq 0 ]; then
    echo "✅ Project compiled successfully"
else
    echo "❌ Project compilation failed"
    exit 1
fi

echo "🎉 All tests passed! Project can be started."
echo "Run the following command to start the project:"
echo "./start.sh" 