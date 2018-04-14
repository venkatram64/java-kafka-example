step 1. Start the zookeeper server

	bin/zookeeper-server-start.bat config/zookeeper.properties

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>zookeeper-server-start.bat ..\..\config\zookeeper.properties

step 2. start the kafka server(kafka broker)

	bin/kafka-server-start.bat config/server.properties

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-server-start.bat ..\..\config\server.properties

to start multiple brokers
cp config\server.properties config\server-1.properties

open the server-1.properties

change the broker.id=1
listeners=PLAINTEXT://:9093
log.dirs=/tmp/kafka-logs-1

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-server-start.bat ..\..\config\server-1.properties

cp config\server.properties config\server-2.properties

change the broker.id=2
listeners=PLAINTEXT://:9094
log.dirs=/tmp/kafka-logs-2

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-server-start.bat ..\..\config\server-2.properties

step 3. creating a topic (topic managment tool)

	bin/windows/kafka-topics.bat --zookeeper localhost:2181 --create --topic test --partitions 1 --replication-factor 1

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-topics.bat --zookeeper localhost:2181 --create --topic TestTopic --partitions 1 --replication-factor 1


step 4. To see the topics

    bin/kafka-topics.bat --zookeeper localhost:2181 --describe --topic TestTopic
	bin/windows/kafka-topics.bat --list --zookeeper localhost:2181

step 5. Send some messages

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-console-producer.bat --broker-list localhost:9092 --topic TestTopic

bin/kafka-console-producer.bat --broker-list localhost:9092 --topic TestTopic

this is a message

step 6. Start a consumer

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic myTopic

bin/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic TestTopic

this is a message


mvn dependency:tree

log.retention.ms
log.retention.bytes

max.in.flight.requests.per.connection

Custom partiioner
partitioner.class ="xxx"
speed.sensor.name="tss"


step 1: https://github.com/ldaniels528/trifecta/releases

step 2: create a dir ".trifecta" in Users

step 3: To run the trifecta, create config.properties file in ".trifecta" dir and add the following
trifecta.zookeeper.host=localhost:2181

step 4: go to trifecta dir run the following command

java -cp lib* play.core.server.ProdServerStart

or goto bin dir run the bat file

https://github.com/jkutner/heroku-metrics-spring

netstat -a -n -o

taskkill /F /pid <port number>
netstat -nao | find "9092"

*********************************************************
step1: start the zookeeper server

D:\freelance-work\kafka_2.11-0.11.0.0>  bin\windows\zookeeper-server-start.bat config\zookeeper.propertie

step2: start the kafka server(kafka broker)

D:\freelance-work\kafka_2.11-0.11.0.0>  bin\windows\kafka-server-start.bat config\server.properties

step3:

to see the topics: start the following command

D:\trifecta\bin> trifecta-ui.bat
http://localhost:9000/

****************************
C:\Users\<username>\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Docker

docker installation on windows 10

1. your IP will be 192.168.99.100
2. The docker machine that ships with Docker Toolbox comes with only 2GB of RAM,
which is not enough for Kafka. You need to increase it to 4GB (at least)
by running the following commands:

docker-machine rm default
docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=2 default

3. Run the command

docker-machine env default --shell cmd

or

docker-machine env default --shell powershell

4. In a new command prompt, paste the output from the command above into the terminal

!!!! MAKE SURE TO COPY YOUR OUTPUT, NOT THE CODE BELOW !!!!
set DOCKER_TLS_VERIFY=1
set DOCKER_HOST=tcp://192.168.99.100:2376
set DOCKER_CERT_PATH=...
set DOCKER_MACHINE_NAME=default

************************************

# Docker for Mac >= 1.12, Linux, Docker for Windows 10
docker run --rm -it \
           -p 2181:2181 -p 3030:3030 -p 8081:8081 \
           -p 8082:8082 -p 8083:8083 -p 9092:9092 \
           -e ADV_HOST=127.0.0.1 \
           landoop/fast-data-dev

# Docker toolbox
docker run --rm -it \
          -p 2181:2181 -p 3030:3030 -p 8081:8081 \
          -p 8082:8082 -p 8083:8083 -p 9092:9092 \
          -e ADV_HOST=192.168.99.100 \
          landoop/fast-data-dev

# Kafka command lines tools(run following command to create the topics)
docker run --rm -it --net=host landoop/fast-data-dev bash

to see the UI http://192.168.99.100:3030/

Create topic

kafka-topics --zookeeper 192.168.99.100:2181 --create --topic first_topic --partitions 3 --replication-factor 1

kafka-topics --zookeeper 192.168.99.100:2181 --create --topic test_topic --partitions 3 --replication -factor 1 --config cleanup.policy=compact


To list topic

kafka-topics --zookeeper 192.168.99.100:2181 --list

To delete topic

kafka-topics --zookeeper 192.168.99.100:2181 --delete --topic first_topic

To describe topic

kafka-topics --zookeeper 192.168.99.100:2181 --describe --topic first_topic
To send messages

kafka-console-producer --broker-list 192.168.99.100:9092 --topic first_topic

To receive messages

kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic first_topic

kafka-console-consumer --bootstrap-server localhost:9092 --topic nifi_topic

To receive messages

kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic first_topic --from-begining

To receive messages from commiit

kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic first_topic --consumer-property group.id=mygroup --from-begining


docker ps

docker exec -it 515a7b91e6a4 tail -f /var/log/broker.log

log compaction:
first run the :
docker run --rm -it --net=host landoop/fast-data-dev bash
case 1: create topic
kafka-topics --zookeeper 192.168.99.100:2181 --create \
 --topic employee-salary-compact \
 --partitions 1 --replication-factor 1\
 --config cleanup.policy=compact \
 --config min.cleanable.dirty.ratio=0.005 \
 --config segment.ms=100000

case 2 : run the following command
 kafka-console-consumer --bootstrap-server 192.168.99.100:9092 \
 --topic employee-salary-compact \
 --from-beginning \
 --property print.key=true \
 --property key.separator=,
case 3 : run the following command
  kafka-console-producer --broker-list 192.168.99.100:9092 \
  --topic employee-salary-compact \
  --property parse.key=true \
  --property key.separator=,

paste below messages in producer console:

123,{"Venkatram":"1000000"}
124,{"Veerareddy":"12121566"}
125,{"Srijan":"90987666"}

1231,{"Lisa":"123123123"}
1242,{"Feema":"12121566"}
1253,{"Srijan":"90987666009"}

123,{"Venkatram":"100000234"}
124,{"Veerareddy":"1212001566"}



