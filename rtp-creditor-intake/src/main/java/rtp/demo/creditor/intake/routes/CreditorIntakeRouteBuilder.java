package rtp.demo.creditor.intake.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rtp.demo.creditor.intake.beans.CreditTransferMessageTransformer;

@Component
public class CreditorIntakeRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorIntakeRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	// private String kafkaConsumerTopic = System.getenv("CONSUMER_TOPIC");

	private String kafkaConsumerTopic = "creditor-ctms";
	private String consumerGroup = "rtp-creditor-intake-app";
	private String consumerMaxPollRecords = "5000";
	private String consumerCount = "1";
	private String consumerSeekTo = "beginning";

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Intake Routes");

		KafkaComponent kafka = new KafkaComponent();
		if (kafkaBootstrap == null) {
			LOG.info("null boostrap");
			kafka.setBrokers("172.30.27.66:9092");
		} else {
			kafka.setBrokers(kafkaBootstrap);
		}
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaConsumerTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords=" + consumerMaxPollRecords
				+ "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo + "&groupId=" + consumerGroup
				+ "&valueDeserializer=rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer")
						.routeId("FromKafka").log("\n/// Creditor Intake Route >>> ${body}")
						.bean(CreditTransferMessageTransformer.class, "toCreditTransferMessage").log(">>> ${body}")
						.to("kafka:creditor-pre-validation?serializerClass=rtp.demo.creditor.domain.rtp.simplified.serde.CreditTransferMessageSerializer");
	}

	public String getKafkaBootstrap() {
		return kafkaBootstrap;
	}

	public void setKafkaBootstrap(String kafkaBootstrap) {
		this.kafkaBootstrap = kafkaBootstrap;
	}

	public String getKafkaConsumerTopic() {
		return kafkaConsumerTopic;
	}

	public void setKafkaConsumerTopic(String kafkaConsumerTopic) {
		this.kafkaConsumerTopic = kafkaConsumerTopic;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public String getConsumerMaxPollRecords() {
		return consumerMaxPollRecords;
	}

	public void setConsumerMaxPollRecords(String consumerMaxPollRecords) {
		this.consumerMaxPollRecords = consumerMaxPollRecords;
	}

	public String getConsumerCount() {
		return consumerCount;
	}

	public void setConsumerCount(String consumerCount) {
		this.consumerCount = consumerCount;
	}

	public String getConsumerSeekTo() {
		return consumerSeekTo;
	}

	public void setConsumerSeekTo(String consumerSeekTo) {
		this.consumerSeekTo = consumerSeekTo;
	}

}
