REGISTRY=foxxxxx333
TAG=latest

docker-build:
	docker build -f event-management-service/Dockerfile -t $(REGISTRY)/event-management:$(TAG) .
	docker build -f user-service/Dockerfile -t $(REGISTRY)/event-management-user:$(TAG) .

docker-push:
	docker push $(REGISTRY)/event-management:$(TAG)
	docker push $(REGISTRY)/event-management-user:$(TAG)

k8s-deploy:
	kubectl apply -f event-management-service/k8s/postgres-event-deployment.yml
	kubectl apply -f user-service/k8s/postgres-user-deployment.yml
	kubectl apply -f event-management-service/k8s/event-management-deployment.yml
	kubectl apply -f user-service/k8s/user-service-deployment.yml

restart:
	kubectl rollout restart deployment event-management
	kubectl rollout restart deployment user-service

deploy: build docker-build docker-push k8s-deploy restart
