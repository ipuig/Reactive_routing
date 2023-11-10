#!/bin/bash
echo "building the image ..."
docker build −t assignment2 .

echo "Image was built successfully!"
containers= (
    "e1" "r1" "r2" "r3" "r4" "r5" "e2" "e3" "r6" "r7" "r8" 
)
image="assigment2"
volume="-v ~/.projects/compnets:/compnets"
capabilities="--cap-add=all"

echo "creating containers ..."
for container in "${containers[@]}"; do
    docker create -ti --name "$container" $capabilities $volume $image /bin/sh
done
echo "Containers created successfully"

echo "creating networks ..."
docker network create --subnet 172.20.1.0/24 enet1
docker network create --subnet 172.20.2.0/24 enet2
docker network create --subnet 172.20.3.0/24 enet3
docker network create --subnet 172.20.4.0/24 anet1
docker network create --subnet 172.20.5.0/24 anet2
docker network create --subnet 172.20.6.0/24 anet3
docker network create --subnet 172.20.7.0/24 anet4
docker network create --subnet 172.20.8.0/24 anet5
echo "Networks created successfully!"

echo "connecting networks with nodes"
docker network connect enet1 r1
docker network connect enet1 e1

docker network connect enet2 r8
docker network connect enet2 r5
docker network connect enet2 e2

docker network connect enet3 r7
docker network connect enet3 e3

docker network connect anet1 r1
docker network connect anet1 r2
docker network connect anet1 r3

docker network connect anet2 r3
docker network connect anet2 r5

docker network connect anet3 r2
docker network connect anet3 r6
docker network connect anet3 r4

docker network connect anet4 r4
docker network connect anet4 r7

docker network connect anet5 r8
docker network connect anet5 r7

echo "Done!"
