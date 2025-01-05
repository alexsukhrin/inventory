build:
	docker build -t inventory-image .

deploy:
	docker run --restart=on-failure:3 --env-file=env -d -it -p 8031:3000 --name inventory-app inventory-image

stop:
	docker stop inventory-app

remove:
	docker rm inventory-app

images:
	docker rmi inventory-image
