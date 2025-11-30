# Multi-stage build for React Frontend
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY shadcn-ui/package.json shadcn-ui/pnpm-lock.yaml ./

# Install pnpm and dependencies
RUN npm install -g pnpm && \
    pnpm install --frozen-lockfile

# Copy source code
COPY shadcn-ui/ .

# Build the application
RUN pnpm run build

# Production stage with nginx
FROM nginx:alpine

# Copy built files to nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY shadcn-ui/nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]