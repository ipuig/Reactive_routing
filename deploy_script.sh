docker network create --subnet 172.20.1.0/24 enet1
docker network create --subnet 172.20.2.0/24 enet2
docker network create --subnet 172.20.3.0/24 enet3
docker network create --subnet 172.20.4.0/24 anet1
docker network create --subnet 172.20.5.0/24 anet2
docker network create --subnet 172.20.6.0/24 anet3
docker network create --subnet 172.20.7.0/24 anet4
docker network create --subnet 172.20.8.0/24 anet5

docker create -ti --name r1 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r2 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r3 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r4 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r5 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r6 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r7 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name r8 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash

docker create -ti --name e1 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name e2 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash
docker create -ti --name e3 --cap-add=all -v ~/.projects/compnets:/compnets assignment2 /bin/bash

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
