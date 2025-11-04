# Setup Guide - E-Commerce Inventory & Order Management System

This guide will help you set up and run all components of the system.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK 11 or higher**
  - Download from: https://www.oracle.com/java/technologies/downloads/
  - Verify installation: `java -version`

- **Python 3.8 or higher**
  - Download from: https://www.python.org/downloads/
  - Verify installation: `python --version` or `python3 --version`

- **Node.js 16 or higher**
  - Download from: https://nodejs.org/
  - Verify installation: `node --version`

- **pnpm** (Package manager for frontend)
  - Install: `npm install -g pnpm`
  - Verify installation: `pnpm --version`

## Project Structure

```
ecommerce-system/
├── java-core/          # Java core business logic
├── python-api/         # Python FastAPI backend
├── docs/              # Documentation
└── README.md

shadcn-ui/             # React TypeScript frontend
```

## Step-by-Step Setup

### 1. Java Core System

The Java core contains all business logic and custom data structures.

```bash
# Navigate to java-core directory
cd ecommerce-system/java-core

# Create bin directory for compiled classes
mkdir -p bin

# Compile all Java files
javac -d bin src/com/ecommerce/**/*.java

# Run the main application
java -cp bin com.ecommerce.Main
```

**Expected Output:**
- Loading messages for CSV files
- Demonstration of all operations
- Time complexity analysis

**Troubleshooting:**
- If you get "file not found" errors, make sure you're in the `java-core` directory
- The CSV files should be at `../python-api/data/` relative to the Main.java location

### 2. Python API Backend

The Python backend provides RESTful API endpoints.

```bash
# Navigate to python-api directory
cd ecommerce-system/python-api

# Create virtual environment (recommended)
python -m venv venv

# Activate virtual environment
# On Windows:
venv\Scripts\activate
# On macOS/Linux:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run the API server
uvicorn app.main:app --reload --port 8000
```

**Expected Output:**
```
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
INFO:     Started reloader process
INFO:     Started server process
INFO:     Waiting for application startup.
INFO:     Application startup complete.
```

**Access Points:**
- API Root: http://localhost:8000
- Interactive Docs: http://localhost:8000/docs
- Alternative Docs: http://localhost:8000/redoc
- Health Check: http://localhost:8000/api/v1/health

**Troubleshooting:**
- If port 8000 is in use, change the port: `--port 8001`
- Make sure CSV files exist in the `data/` directory
- Check Python version: `python --version` (should be 3.8+)

### 3. Frontend Dashboard

The React TypeScript frontend provides a visual interface.

```bash
# Navigate to frontend directory
cd shadcn-ui

# Install dependencies
pnpm install

# Run development server
pnpm run dev
```

**Expected Output:**
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help
```

**Access:**
- Open browser to: http://localhost:5173

**Troubleshooting:**
- If port 5173 is in use, Vite will automatically use the next available port
- Make sure Python API is running on port 8000
- Check for CORS errors in browser console

## Running All Components Together

### Terminal 1 - Python API
```bash
cd ecommerce-system/python-api
source venv/bin/activate  # or venv\Scripts\activate on Windows
uvicorn app.main:app --reload --port 8000
```

### Terminal 2 - Frontend
```bash
cd shadcn-ui
pnpm run dev
```

### Terminal 3 - Java Core (Optional)
```bash
cd ecommerce-system/java-core
java -cp bin com.ecommerce.Main
```

## Testing the System

### 1. API Testing with curl

**Get all products:**
```bash
curl http://localhost:8000/api/v1/products
```

**Get health status:**
```bash
curl http://localhost:8000/api/v1/health
```

**Create a new product:**
```bash
curl -X POST http://localhost:8000/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","price":99.99,"stock":10}'
```

### 2. Frontend Testing

1. Open http://localhost:5173 in your browser
2. You should see the dashboard with 4 stat cards
3. Navigate through tabs: Products, Customers, Orders, Reviews
4. Try adding new items using the "Add" buttons
5. Verify data appears in the tables

### 3. Integration Testing

1. Add a product via the frontend
2. Check the API response: http://localhost:8000/api/v1/products
3. Verify the CSV file is updated: `python-api/data/products.csv`
4. Run Java Main to see the new product in the output

## Common Issues and Solutions

### Issue: "Connection refused" error in frontend

**Solution:**
- Make sure Python API is running on port 8000
- Check the API_BASE_URL in `shadcn-ui/src/pages/Index.tsx`
- Verify CORS is enabled in `python-api/app/main.py`

### Issue: Java compilation errors

**Solution:**
- Ensure you're using Java 11 or higher
- Check that all source files are in the correct package structure
- Verify the classpath is set correctly

### Issue: Python module not found

**Solution:**
- Activate virtual environment
- Reinstall dependencies: `pip install -r requirements.txt`
- Check Python version: `python --version`

### Issue: Frontend shows "Failed to fetch data"

**Solution:**
- Verify Python API is running: http://localhost:8000/api/v1/health
- Check browser console for CORS errors
- Ensure data files exist in `python-api/data/`

## Development Workflow

### Adding New Features

1. **Update Java Core:**
   - Add new methods to service classes
   - Recompile: `javac -d bin src/com/ecommerce/**/*.java`

2. **Update Python API:**
   - Add new endpoints in `app/views/api_routes.py`
   - Add business logic in `app/services/ecommerce_service.py`
   - Server auto-reloads with `--reload` flag

3. **Update Frontend:**
   - Add new components in `shadcn-ui/src/pages/`
   - Vite auto-reloads on file changes

### Modifying CSV Data

Edit files in `python-api/data/`:
- `products.csv`
- `customers.csv`
- `orders.csv`
- `reviews.csv`

**Note:** Restart Python API after CSV changes to reload data.

## Production Deployment

### Java Core
```bash
# Create JAR file
jar cvf ecommerce-core.jar -C bin .

# Run JAR
java -jar ecommerce-core.jar
```

### Python API
```bash
# Install production server
pip install gunicorn

# Run with gunicorn
gunicorn app.main:app -w 4 -k uvicorn.workers.UvicornWorker
```

### Frontend
```bash
# Build for production
pnpm run build

# Output in dist/ directory
# Deploy to any static hosting service
```

## Additional Resources

- **Java Documentation:** `docs/class-diagram.md`
- **API Documentation:** http://localhost:8000/docs
- **Complexity Analysis:** `docs/complexity-analysis.md`
- **Full Report:** `docs/report.md`

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the documentation in `docs/`
3. Check API logs in the terminal
4. Verify all prerequisites are installed

## Quick Reference

| Component | Command | Port |
|-----------|---------|------|
| Java Core | `java -cp bin com.ecommerce.Main` | N/A |
| Python API | `uvicorn app.main:app --reload` | 8000 |
| Frontend | `pnpm run dev` | 5173 |

---

**Happy Coding! 🚀**
