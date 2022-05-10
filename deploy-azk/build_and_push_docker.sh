docker build -t validation_service:azk_latest .
docker tag validation_service:azk_latest stevekellaway/validation_service:azk_latest
docker push stevekellaway/validation_service:azk_latest