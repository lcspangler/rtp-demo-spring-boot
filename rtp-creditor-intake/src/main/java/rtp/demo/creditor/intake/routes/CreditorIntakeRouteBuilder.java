package rtp.demo.creditor.intake.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditorIntakeRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorIntakeRouteBuilder.class);

	// @Value("${BOOTSTRAP_SERVERS}")
	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");

	// @Value("${PRODUCER_TOPIC}")
	private String kafkaConsumerTopic = System.getenv("CONSUMER_TOPIC");

	private String consumerGroup = "my-group";
	private String consumerMaxPollRecords = "5000";
	private String consumerCount = "1";
	private String consumerSeekTo = "beginning";

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Mock RTP Routes");
		// PropertiesComponent pc = getContext().getComponent("properties",
		// PropertiesComponent.class);
		// pc.setLocation("classpath:application.properties");

		KafkaComponent kafka = new KafkaComponent();
		if (kafkaBootstrap == null) {
			LOG.info("null boostrap");
			kafka.setBrokers("172.30.123.84:9092");
		} else {
			kafka.setBrokers(kafkaBootstrap);
		}

		this.getContext().addComponent("kafka", kafka);

		LOG.info("COMPONENTS: " + this.getContext().getComponentNames());

		from("kafka:" + kafkaConsumerTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords=" + consumerMaxPollRecords
				+ "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo + "&groupId=" + consumerGroup)
						.routeId("FromKafka").log("${body}");
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
