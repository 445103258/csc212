#!/bin/bash

# E-Commerce System Docker Deployment Script
# This script builds and starts all services

set -e

echo "=========================================="
echo "E-Commerce System - Docker Deployment"
echo "=========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker is not installed"
    echo "Please install Docker from: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Error: Docker Compose is not installed"
    echo "Please install Docker Compose from: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✓ Docker is installed"
echo "✓ Docker Compose is installed"
echo ""

# Stop any existing containers
echo "Stopping existing containers..."
docker-compose down 2>/dev/null || true
echo ""

# Build and start services
echo "Building and starting services..."
echo "This may take a few minutes on first run..."
echo ""

docker-compose up --build -d

echo ""
echo "=========================================="
echo "Deployment Status"
echo "=========================================="
echo ""

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 5

# Check service status
docker-compose ps

echo ""
echo "=========================================="
echo "Access Information"
echo "=========================================="
echo ""
echo "✓ Frontend Dashboard: http://localhost"
echo "✓ API Documentation:  http://localhost:8000/docs"
echo "✓ API Health Check:   http://localhost:8000/health"
echo ""
echo "=========================================="
echo "Useful Commands"
echo "=========================================="
echo ""
echo "View logs (all services):"
echo "  docker-compose logs -f"
echo ""
echo "View Java Core output:"
echo "  docker logs ecommerce-java-core"
echo ""
echo "Stop all services:"
echo "  docker-compose down"
echo ""
echo "Restart a service:"
echo "  docker-compose restart [service-name]"
echo ""
echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="