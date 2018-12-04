package rtp.demo.creditor.validation.beans;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import rtp.demo.creditor.domain.account.Account;
import rtp.demo.creditor.domain.account.AccountStatus;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;
import rtp.demo.creditor.validation.PaymentValidationRequest;
import rtp.demo.creditor.validation.wrappers.Accounts;
import rtp.demo.creditor.validation.wrappers.CreditorBank;
import rtp.demo.creditor.validation.wrappers.ProcessingDateTime;

public class CreditTransferMessageValidationBean {

	private ProcessingDateTime processingDateTime;

	public PaymentValidationRequest validateCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		PaymentValidationRequest validationRequest = new PaymentValidationRequest();
		CreditorBank creditor = new CreditorBank();
		creditor.setRoutingAndTransitNumber("020010001");

		validationRequest.setCreditTransferMessage(creditTransferMessage);

		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();

		StatelessKieSession kSession = kContainer.newStatelessKieSession("payments-validation-ksession");

		List<Object> facts = new ArrayList<Object>();
		facts.add(creditor);
		// facts.add(processingDateTime);
		facts.add(makeDummyAccounts());
		facts.add(validationRequest);

		kSession.execute(facts);

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
}
