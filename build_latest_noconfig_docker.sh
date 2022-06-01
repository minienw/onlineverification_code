docker build -f latest_noconfig.Dockerfile -t validation_service:latest_noconfig .
docker tag validation_service:latest_noconfig ghcr.io/minienw/validation_service:latest_noconfig
docker push ghcr.io/minienw/validation_service:latest_noconfig


