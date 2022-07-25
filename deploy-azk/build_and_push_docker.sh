docker build -t validation_service:azk_latest .
docker tag validation_service:azk_latest ghcr.io/minienw/validation_service:azk_latest
docker push ghcr.io/minienw/validation_service:azk_latest