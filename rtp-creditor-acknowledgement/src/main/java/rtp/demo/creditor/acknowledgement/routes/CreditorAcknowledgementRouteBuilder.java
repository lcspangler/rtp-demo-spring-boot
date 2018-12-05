package rtp.demo.creditor.acknowledgement.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rtp.demo.creditor.acknowledgement.beans.MessageStatusReportTransformer;

@Component
public class CreditorAcknowledgementRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorAcknowledgementRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	// private String kafkaConsumerTopic = System.getenv("CONSUMER_TOPIC");

	private String kafkaConsumerTopic = "creditor-post-validation";
	private String consumerGroup = "rtp-creditor-acknowledgement-app";
	private String consumerMaxPollRecords = "5000";
	private String consumerCount = "1";
	private String consumerSeekTo = "beginning";

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Acknowledgement Routes");

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
				+ "&valueDeserializer=rtp.demo.creditor.domain.payments.serde.PaymentsDeserializer")
						.routeId("FromKafka").log("\n/// Creditor Acknowledegement Route >>> ${body}")
						.bean(MessageStatusReportTransformer.class, "toMessageStatusReport").log(">>> ${body}")
						.to("kafka:creditor-acks?serializerClass=rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer");
	}

}
