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

## Summary

This Docker setup provides:
- Isolated, reproducible environments
- Easy deployment and scaling
- Consistent development accorss different machines
- Automated builds and health checks
- Network isolation and security

All three services work together seamlessly in a containerized environment!
