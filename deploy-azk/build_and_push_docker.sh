docker build -t validation_service:aks_latest .
docker tag validation_service:aks_latest stevekellaway/validation_service:aks_latest
docker push stevekellaway/validation_service:aks_latest