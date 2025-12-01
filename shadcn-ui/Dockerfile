# Multi-stage build for React Frontend
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files (FIXED: removed shadcn-ui/ prefix)
COPY package.json pnpm-lock.yaml ./

# Install pnpm and dependencies
RUN npm install -g pnpm && \
    pnpm install --frozen-lockfile

# Copy source code (FIXED: changed shadcn-ui/ to .)
COPY . .

# Build the application
RUN pnpm run build

# Production stage with nginx
FROM nginx:alpine

# Copy built files to nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration (FIXED: removed shadcn-ui/ prefix)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
