build:
	docker build -t inventory-image .

deploy:
	docker run --restart=on-failure:3 --env-file=env -d -it -p 8031:3000 --rm --name inventory-app inventory-image

stop:
	docker stop $(docker ps -q)

images:
	docker rmi $(docker images -q)
