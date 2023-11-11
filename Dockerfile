FROM ubuntu
WORKDIR /compnets
RUN apt-get update
RUN apt-get upgrade
RUN apt-get install -y net-tools netcat tcpdump inetutils-ping openjdk-17-jdk openjdk-17-jre make
CMD ["/bin/bash"]
