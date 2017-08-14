step 1. Start the zookeeper server

	bin/zookeeper-server-start.bat config/zookeeper.properties

C:\xxx\kafka\kafka_2.10-0.10.2.1\bin\windows>zookeeper-server-start.bat ..\..\config\zookeeper.properties

step 2. start the kafka server(kafka broker)

	bin/kafka-server-start.bat config/server.properties

C:\xxx\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-server-start.bat ..\..\config\server.properties


step 3. creating a topic

	bin/kafka-topics.bat --zookeeper localhost:2181 --create --topic test --partitions 1 --replication-factor 1

C:\xxx\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-topics.bat --zookeeper localhost:2181 --create --topic TestTopic --partitions 1 --replication-factor 1


step 4. To see the topics

	bin/kafka-topics.bat --list --zookeeper localhost:2181

step 5. Send some messages

C:\xxx\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-console-producer.bat --broker-list localhost:9092 --topic myTopic
bin/kafka-console-producer.bat --broker-list localhost:9090 --topic test

	this is a message

step 6. Start a consumer

C:\xxx\kafka\kafka_2.10-0.10.2.1\bin\windows>kafka-console-consumer.bat --
bootstrap-server localhost:9092 --topic myTopic	bin/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test --from-begining


mvn dependency:tree