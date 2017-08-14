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

	bin/kafka-topics.bat --zookeeper localhost:2181 --create --topic test --partitions 1 --replication-factor 1

C:\Venkatram\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-topics.bat --zookeeper localhost:2181 --create --topic TestTopic --partitions 1 --replication-factor 1


step 4. To see the topics

    bin/kafka-topics.bat --zookeeper localhost:2181 --describe --topic TestTopic
	bin/kafka-topics.bat --list --zookeeper localhost:2181

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