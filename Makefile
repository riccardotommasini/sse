IMG_TAG=sse

build:
	docker build -t  $(IMG_TAG) .

clean:
	docker rmi $(IMG_TAG)