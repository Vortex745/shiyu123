# 使用轻量级 Go 环境
FROM golang:1.21-alpine AS builder
WORKDIR /app
COPY . .
# 编译后端
RUN go mod download
RUN go build -o main .

# 运行阶段
FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/main .
# 如果你有配置文件或上传图片的目录，也要拷贝
COPY --from=builder /app/config ./config
EXPOSE 8080
CMD ["./main"]