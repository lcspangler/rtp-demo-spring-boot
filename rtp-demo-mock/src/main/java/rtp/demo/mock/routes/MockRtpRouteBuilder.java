package rtp.demo.mock.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockRtpRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(MockRtpRouteBuilder.class);

	// @Value("${BOOTSTRAP_SERVERS}")
	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");

	// @Value("${PRODUCER_TOPIC}")
	private String kafkaProducerTopic = System.getenv("PRODUCER_TOPIC");

	// CamelContext camelContext = new DefaultCamelContext();

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Mock RTP Routes");
		// PropertiesComponent pc = getContext().getComponent("properties",
		// PropertiesComponent.class);
		// pc.setLocation("classpath:application.properties");

		KafkaComponent kafka = new KafkaComponent();

		kafka.setBrokers("172.30.123.84:9092");

		this.getContext().addComponent("kafka", kafka);

		LOG.info("COMPONENTS: " + this.getContext().getComponentNames());

		// camelContext.addComponent("kafka", kafka);

		from("timer://foo?period=5000").setBody().constant("Hello World! " + kafkaBootstrap)
				.log(">>> ${body}" + "/////" + kafkaBootstrap + "  " + kafkaProducerTopic + "COMPONENTS: "
						+ this.getContext().getComponentNames() + "kafka component: "
						+ this.getContext().getComponent("kafka"))
				.to("kafka:my-topic-1");
	}

	public String getKafkaBootstrap() {
		return kafkaBootstrap;
	}

	public void setKafkaBootstrap(String kafkaBootstrap) {
		this.kafkaBootstrap = kafkaBootstrap;
	}

	public String getKafkaProducerTopic() {
		return kafkaProducerTopic;
	}

	public void setKafkaProducerTopic(String kafkaProducerTopic) {
		this.kafkaProducerTopic = kafkaProducerTopic;
	}

}
