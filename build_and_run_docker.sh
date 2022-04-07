docker build -t validation:latest .
#docker tag airline_stub:latest stevekellaway/airline_stub:latest
#docker push stevekellaway/airline_stub:latest
docker run -p 8081:8081/tcp --name validation_latest validation_stub:latest

