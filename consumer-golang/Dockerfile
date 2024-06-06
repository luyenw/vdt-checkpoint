FROM golang:1.21.0-bullseye AS build
WORKDIR /go/src/app
COPY . .
RUN go mod download
RUN go build -o main main.go

FROM debian:bullseye-slim
COPY --from=build /go/src/app/main /usr/local/bin/main
WORKDIR /app
RUN chmod +x /usr/local/bin/main
CMD [ "/usr/local/bin/main" ]