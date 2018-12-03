package rtp.demo.mock.routes;

import java.math.BigDecimal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;

@Component
public class MockRtpRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(MockRtpRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaProducerTopic = System.getenv("PRODUCER_TOPIC");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Mock RTP Routes");

		KafkaComponent kafka = new KafkaComponent();
		if (kafkaBootstrap == null) {
			LOG.info("null boostrap");
			kafka.setBrokers("172.30.123.84:9092");
		} else {
			kafka.setBrokers(kafkaBootstrap);
		}
		this.getContext().addComponent("kafka", kafka);

		LOG.info("COMPONENTS: " + this.getContext().getComponentNames());

		CreditTransferMessage creditTransferMessage = new CreditTransferMessage();
		creditTransferMessage.setCreditTransferMessageId("M2018111511021200201BFFF00000000001");
		// creditTransferMessage.setCreationDateTime(LocalDateTime.parse("2018-11-12T10:05:00"));
		creditTransferMessage.setNumberOfTransactions(Integer.valueOf("1"));
		creditTransferMessage.setPaymentAmount(new BigDecimal("512.23"));
		creditTransferMessage.setPaymentCurrency("USD");
		creditTransferMessage.setCreditorId("020010001");
		creditTransferMessage.setCreditorAccountNumber("12000194212199001");
		creditTransferMessage.setSettlementMethod("CLRG");

		from("timer://foo?period=10000").setBody().constant(creditTransferMessage).log(">>> ${body}").to(
				"kafka:my-topic-1?serializerClass=rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessageSerializer");
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
