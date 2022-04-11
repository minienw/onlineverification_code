docker build -t validation:latest .
#docker tag validation:latest stevekellaway/validation:latest
#docker push stevekellaway/validation:latest
docker run -p 8081:8081/tcp --name validation_latest validation_stub:latest

