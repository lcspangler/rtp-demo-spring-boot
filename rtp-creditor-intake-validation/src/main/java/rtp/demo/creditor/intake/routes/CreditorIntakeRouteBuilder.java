package rtp.demo.creditor.intake.routes;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rtp.demo.creditor.domain.account.Account;
import rtp.demo.creditor.intake.beans.CreditTransferMessageTransformer;
import rtp.demo.creditor.intake.query.InfinispanKafkaQueryBuilder;

@Component
public class CreditorIntakeRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorIntakeRouteBuilder.class);

	// private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	// private String kafkaConsumerTopic = System.getenv("CONSUMER_TOPIC");

	private String kafkaBootstrap = "172.30.27.66:9092";
	private String kafkaConsumerTopic = "creditor-ctms";
	private String consumerGroup = "rtp-creditor-validation-app";
	private String consumerMaxPollRecords = "5000";
	private String consumerCount = "1";
	private String consumerSeekTo = "beginning";

	private RemoteCacheManager cacheContainer;

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Intake Routes");

		cacheContainer = cacheContainer();

		LOG.info("cache: " + cacheContainer.getCache().toString() + " " + cacheContainer.getCache().getName() + " "
				+ cacheContainer.getCache().isEmpty() + " " + cacheContainer.getCache().getProtocolVersion() + " "
				+ cacheContainer.getCache().getRemoteCacheManager().getCache() + " "
				+ cacheContainer.getCache().getRemoteCacheManager().getCacheNames());

		CamelContext context = this.getContext();

		SimpleRegistry registry = new SimpleRegistry();
		registry.put("cacheContainer", cacheContainer);
		registry.put("queryBuilder", new InfinispanKafkaQueryBuilder());
		CompositeRegistry compositeRegistry = new CompositeRegistry();
		compositeRegistry.addRegistry(context.getRegistry());
		compositeRegistry.addRegistry(registry);
		((org.apache.camel.impl.DefaultCamelContext) context).setRegistry(compositeRegistry);

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
						.bean(CreditTransferMessageTransformer.class, "toCreditTransferMessage").log(" >>> ${body}")
						.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.QUERY)
						.to("infinispan://mycache?cacheContainer=#cacheContainer&queryBuilder=#queryBuilder")
						.log(" Retrieved >>> ${body}");
		// .bean(CreditTransferMessageValidationBean.class,
		// "validateCreditTransferMessage")
		// .log(" >>> ${body}").bean(PaymentTransformer.class, "toPayment").log(" >>>
		// ${body}")
		// .to("kafka:creditor-post-validation?serializerClass=rtp.demo.creditor.domain.payments.serde.PaymentSerializer");
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

	public RemoteCacheManager getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(RemoteCacheManager cacheContainer) {
		this.cacheContainer = cacheContainer;
	}

	public void setCacheContainer(String cacheContainer) {
		this.cacheContainer = cacheContainer();
	}

	private static RemoteCacheManager cacheContainer() {

		ConfigurationBuilder clientBuilder;
		clientBuilder = new ConfigurationBuilder();
		clientBuilder.addServer().host("172.30.202.127").port(11333);
		clientBuilder.security().authentication().enable().serverName("jdg-server").saslMechanism("DIGEST-MD5")
				.callbackHandler(new TestCallbackHandler("developer", "ApplicationRealm", "password1!".toCharArray()));

		clientBuilder.marshaller(new ProtoStreamMarshaller());
		clientBuilder.forceReturnValues(true);
		// ConfigurationBuilder builder = new ConfigurationBuilder();
		// builder.addServer().host("localhost").port(11222);

		RemoteCacheManager cacheManager = new RemoteCacheManager(clientBuilder.build());

		SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
		ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
		Class<?> marshaller = Account.class;
		String memoSchemaFile = null;
		try {
			memoSchemaFile = protoSchemaBuilder.fileName("file.proto").packageName("test").addClass(marshaller)
					.build(serCtx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RemoteCache<String, String> metadataCache = cacheManager
				.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
		metadataCache.put("file.proto", memoSchemaFile);

		return cacheManager;

	}

	public static class TestCallbackHandler implements CallbackHandler {
		final private String username;
		final private char[] password;
		final private String realm;

		public TestCallbackHandler(String username, String realm, char[] password) {
			this.username = username;
			this.password = password;
			this.realm = realm;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					NameCallback nameCallback = (NameCallback) callback;
					nameCallback.setName(username);
				} else if (callback instanceof PasswordCallback) {
					PasswordCallback passwordCallback = (PasswordCallback) callback;
					passwordCallback.setPassword(password);
				} else if (callback instanceof AuthorizeCallback) {
					AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
					authorizeCallback.setAuthorized(
							authorizeCallback.getAuthenticationID().equals(authorizeCallback.getAuthorizationID()));
				} else if (callback instanceof RealmCallback) {
					RealmCallback realmCallback = (RealmCallback) callback;
					realmCallback.setText(realm);
				} else {
					throw new UnsupportedCallbackException(callback);
				}
			}
		}
	}
}
