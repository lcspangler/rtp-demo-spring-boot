package rtp.demo.mock.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import iso.std.iso._20022.tech.xsd.pacs_008_001.BranchAndFinancialInstitutionIdentification5;
import iso.std.iso._20022.tech.xsd.pacs_008_001.ClearingSystemMemberIdentification2;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FinancialInstitutionIdentification8;
import iso.std.iso._20022.tech.xsd.pacs_008_001.GroupHeader70;
import iso.std.iso._20022.tech.xsd.pacs_008_001.PaymentTypeInformation21;

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

		from("timer://foo?period=10000").setBody().constant(makeDummyRtpCreditTransferMessage()).log(">>> ${body}").to(
				"kafka:my-topic-1?serializerClass=rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer");
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

	private FIToFICustomerCreditTransferV06 makeDummyRtpCreditTransferMessage() {
		FIToFICustomerCreditTransferV06 dummyRtpCreditTransferMessage = new FIToFICustomerCreditTransferV06();
		dummyRtpCreditTransferMessage.setGrpHdr(new GroupHeader70());

		// Set Credit Transfer Message Id
		dummyRtpCreditTransferMessage.getGrpHdr().setMsgId("M2015111511021200201BFFF00000000001");

		// dummyRtpCreditTransferMessage.getGrpHdr().setCreDtTm(new);

		// Set Debtor Id
		dummyRtpCreditTransferMessage.getGrpHdr().setInstgAgt(new BranchAndFinancialInstitutionIdentification5());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt()
				.setFinInstnId(new FinancialInstitutionIdentification8());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId().getClrSysMmbId().setMmbId("021200201");

		// Set Creditor Id
		dummyRtpCreditTransferMessage.getGrpHdr().setInstdAgt(new BranchAndFinancialInstitutionIdentification5());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt()
				.setFinInstnId(new FinancialInstitutionIdentification8());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId().getClrSysMmbId().setMmbId("020010001");

		// .setInstdAgt(new BranchAndFinancialInstitutionIdentification5());

		dummyRtpCreditTransferMessage.getGrpHdr().setPmtTpInf(new PaymentTypeInformation21());
		// dummyRtpCreditTransferMessage.getGrpHdr().getPmtTpInf().s

		return dummyRtpCreditTransferMessage;
	}

}
