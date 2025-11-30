# Docker Deployment Guide

## Overview

This guide explains how to deploy the E-Commerce Inventory & Order Management System using Docker and Docker Compose.

## Architecture

The system consists of three containerized services:

1. **Python API Backend** (FastAPI) - Port 8000
2. **React Frontend Dashboard** (Nginx) - Port 80
3. **Java Core System** (Phase II BST Implementation)

```
┌─────────────────────────────────────────────────────┐
│                  Docker Network                     │
│                                                     │
│  ┌──────────────┐    ┌──────────────┐             │
│  │   Frontend   │───▶│   API        │             │
│  │   (Nginx)    │    │   (FastAPI)  │             │
│  │   Port 80    │    │   Port 8000  │             │
│  └──────────────┘    └──────────────┘             │
│                            │                        │
│                            │                        │
│                      ┌──────────────┐              │
│                      │  Java Core   │              │
│                      │  (Phase II)  │              │
│                      └──────────────┘              │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- 2GB RAM minimum
- 5GB disk space

## Quick Start

### 1. Build and Start All Services

```bash
cd ecommerce-system
docker-compose up --build
```

This command will:
- Build all three Docker images
- Start all containers
- Create a shared network
- Mount data volumes

### 2. Access the Application

- **Frontend Dashboard**: http://localhost
- **API Documentation**: http://localhost:8000/docs
- **API Health Check**: http://localhost:8000/health

### 3. View Java Core Output

```bash
docker logs ecommerce-java-core
```

This will show the Phase II demonstration output including:
- Data loading confirmation
- BST operations demonstration
- Big-O complexity comparison

## Docker Commands

### Start Services (Detached Mode)

```bash
docker-compose up -d
```

### Stop Services

```bash
docker-compose down
```

### Stop and Remove Volumes

```bash
docker-compose down -v
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api
docker-compose logs -f frontend
docker-compose logs -f java-core
```

### Rebuild Specific Service

```bash
docker-compose build api
docker-compose build frontend
docker-compose build java-core
```

### Restart Specific Service

```bash
docker-compose restart api
docker-compose restart frontend
docker-compose restart java-core
```

### Check Service Status

```bash
docker-compose ps
```

### Execute Commands in Container

```bash
# Access API container
docker exec -it ecommerce-api bash

# Access Frontend container
docker exec -it ecommerce-frontend sh

# Access Java Core container
docker exec -it ecommerce-java-core sh
```

## Service Details

### 1. Python API Backend

**Dockerfile**: `python-api/Dockerfile`

**Key Features**:
- FastAPI framework
- Uvicorn ASGI server
- CSV data loading
- RESTful API endpoints

**Environment Variables**:
- `PYTHONUNBUFFERED=1`: Real-time log output

**Health Check**:
- Endpoint: `/health`
- Interval: 30 seconds
- Timeout: 10 seconds

### 2. React Frontend Dashboard

**Dockerfile**: `shadcn-ui/Dockerfile`

**Build Process**:
1. Node.js build stage (pnpm)
2. Nginx production stage

**Key Features**:
- Multi-stage build for smaller image
- Nginx reverse proxy
- API proxy configuration
- Static asset caching
- React Router support

**Nginx Configuration**:
- API proxy: `/api/*` → `http://api:8000/`
- SPA routing: All routes → `index.html`
- Gzip compression enabled
- Static asset caching (1 year)

### 3. Java Core System

**Dockerfile**: `java-core/Dockerfile`

**Build Process**:
1. Eclipse Temurin JDK build stage
2. Eclipse Temurin JRE runtime stage

**Key Features**:
- Multi-stage build for smaller image
- Alpine Linux base (minimal)
- Compiled Java classes
- CSV data access

**Execution**:
- Runs once on startup
- Demonstrates Phase II BST operations
- Outputs to container logs
- Restarts on failure

## Troubleshooting

### Issue: Port Already in Use

**Error**: `Bind for 0.0.0.0:80 failed: port is already allocated`

**Solution**:
```bash
# Check what's using port 80
sudo lsof -i :80

# Stop the conflicting service or change port in docker-compose.yml
ports:
  - "8080:80"  # Use port 8080 instead
```

### Issue: Build Fails

**Error**: `failed to solve: process did not complete successfully`

**Solution**:
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

### Issue: Container Exits Immediately

**Check logs**:
```bash
docker-compose logs java-core
```

**Common causes**:
- Missing CSV files
- Compilation errors
- File path issues

**Solution**:
```bash
# Verify data files exist
ls -la python-api/data/

# Rebuild with verbose output
docker-compose up --build java-core
```

### Issue: API Not Accessible from Frontend

**Check network**:
```bash
docker network inspect ecommerce-system_ecommerce-network
```

**Solution**:
- Ensure all services are on the same network
- Check API health: `curl http://localhost:8000/health`
- Verify nginx proxy configuration

### Issue: Frontend Shows 502 Bad Gateway

**Causes**:
- API service not ready
- Network misconfiguration

**Solution**:
```bash
# Check API status
docker-compose ps api

# Restart API
docker-compose restart api

# Check API logs
docker-compose logs api
```

## Performance Optimization

### 1. Build Cache

Docker uses layer caching. To optimize:

```dockerfile
# Copy dependency files first
COPY package.json pnpm-lock.yaml ./
RUN pnpm install

# Then copy source code
COPY . .
```

### 2. Multi-stage Builds

All Dockerfiles use multi-stage builds:
- **Build stage**: Full toolchain (JDK, Node.js)
- **Runtime stage**: Minimal runtime (JRE, Nginx)

Result: 50-70% smaller images

### 3. Resource Limits

Add resource limits in `docker-compose.yml`:

```yaml
services:
  api:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

## Production Deployment

### 1. Environment Variables

Create `.env` file:

```env
# API Configuration
API_PORT=8000
API_HOST=0.0.0.0

# Frontend Configuration
FRONTEND_PORT=80

# Database (if using)
DB_HOST=localhost
DB_PORT=5432
```

Update `docker-compose.yml`:

```yaml
services:
  api:
    env_file:
      - .env
```

### 2. HTTPS Configuration

Add SSL certificates to nginx:

```nginx
server {
    listen 443 ssl http2;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # ... rest of configuration
}
```

### 3. Docker Swarm / Kubernetes

For production orchestration, consider:
- **Docker Swarm**: Built-in orchestration
- **Kubernetes**: Enterprise-grade orchestration

## Monitoring

### 1. Container Stats

```bash
docker stats
```

### 2. Health Checks

```bash
# Check all health statuses
docker-compose ps

# Manual health check
curl http://localhost:8000/health
```

### 3. Log Aggregation

Use Docker logging drivers:

```yaml
services:
  api:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## Cleanup

### Remove All Containers and Images

```bash
# Stop all services
docker-compose down

# Remove all containers
docker container prune -f

# Remove all images
docker image prune -a -f

# Remove all volumes
docker volume prune -f

# Remove all networks
docker network prune -f
```

### Complete System Cleanup

```bash
docker system prune -a --volumes -f
```

**Warning**: This removes ALL Docker data, not just this project!

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Deploy

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker images
        run: docker-compose build
      
      - name: Run tests
        run: docker-compose up -d && sleep 10 && curl http://localhost:8000/health
      
      - name: Push to registry
        run: |
          docker tag ecommerce-api:latest registry.example.com/ecommerce-api:latest
          docker push registry.example.com/ecommerce-api:latest
```

## Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Review this guide's troubleshooting section
3. Check Docker documentation: https://docs.docker.com

## Summary

This Docker setup provides:
- ✅ Isolated, reproducible environments
- ✅ Easy deployment and scaling
- ✅ Consistent development and production
- ✅ Automated builds and health checks
- ✅ Network isolation and security

All three services work together seamlessly in a containerized environment!