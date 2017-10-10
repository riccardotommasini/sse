IMG_TAG=rsplab-stream-server

build:
	docker build -t  $(IMG_TAG) .

clean:
	docker rmi $(IMG_TAG)