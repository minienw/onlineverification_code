docker build -t validation:latest .
docker run -p 8081:8081/tcp --name validation_latest validation:latest

