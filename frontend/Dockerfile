# --- Giai đoạn 1: Build code React/Vite ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# --- Giai đoạn 2: Bỏ lên Nginx để chạy web ---
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html

# XÓA ĐƯỜNG TẮT, DÙNG FILE NGINX.CONF CỦA ÔNG
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]