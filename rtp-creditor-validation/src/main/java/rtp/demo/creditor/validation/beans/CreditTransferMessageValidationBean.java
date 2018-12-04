package rtp.demo.creditor.validation.beans;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rtp.demo.creditor.domain.account.Account;
import rtp.demo.creditor.domain.account.AccountStatus;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;
import rtp.demo.creditor.validation.PaymentValidationRequest;
import rtp.demo.creditor.validation.routes.CreditorValidationRouteBuilder;
import rtp.demo.creditor.validation.wrappers.Accounts;
import rtp.demo.creditor.validation.wrappers.CreditorBank;

@Service
public class CreditTransferMessageValidationBean {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorValidationRouteBuilder.class);

	private final KieContainer kieContainer;

	@Autowired
	public CreditTransferMessageValidationBean(KieContainer kieContainer) {
		LOG.info("Initializing a new session.");
		this.kieContainer = kieContainer;
	}

	public PaymentValidationRequest validateCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		LOG.info("Validation Rules");

		PaymentValidationRequest validationRequest = new PaymentValidationRequest();
		CreditorBank creditor = new CreditorBank();
		creditor.setRoutingAndTransitNumber("020010001");

		validationRequest.setCreditTransferMessage(creditTransferMessage);

		StatelessKieSession kSession = kieContainer.newStatelessKieSession("payments-validation-ksession");

		List<Object> facts = new ArrayList<Object>();
		facts.add(creditor);
		// facts.add(processingDateTime);
		facts.add(makeDummyAccounts());
		facts.add(validationRequest);

		LOG.info("Incoming Payment Validation Request: " + validationRequest);
		kSession.execute(facts);
		LOG.info("Outgoing Payment Validation Request: " + validationRequest);

		return validationRequest;
	}

	private Accounts makeDummyAccounts() {
		Accounts accounts = new Accounts();

		Account account1 = new Account();
		account1.setAccountNumber("12000194212199001");
		account1.setStatus(AccountStatus.OPEN);

		accounts.getAccounts().add(account1);

		return accounts;
	}

	public KieContainer getKieContainer() {
		return kieContainer;
	}

}
