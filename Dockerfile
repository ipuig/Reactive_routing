FROM ubuntu
WORKDIR /compnets
RUN apt-get update
RUN apt-get install -y net-tools netcat tcpdump inetutils-ping openjdk make
CMD ["/bin/bash"]
