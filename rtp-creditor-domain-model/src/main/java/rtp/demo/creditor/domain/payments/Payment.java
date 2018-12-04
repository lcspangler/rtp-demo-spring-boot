package rtp.demo.creditor.domain.payments;

import java.util.ArrayList;
import java.util.List;

import rtp.demo.creditor.domain.error.PaymentValidationError;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;

public class Payment {

	private CreditTransferMessage creditTransferMessage;
	private Boolean isValidation = false;
	private List<PaymentValidationError> errors = new ArrayList<PaymentValidationError>();

	public CreditTransferMessage getCreditTransferMessage() {
		return creditTransferMessage;
	}

	public void setCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		this.creditTransferMessage = creditTransferMessage;
	}

	public Boolean getIsValidation() {
		return isValidation;
	}

	public void setIsValidation(Boolean isValidation) {
		this.isValidation = isValidation;
	}

	public List<PaymentValidationError> getErrors() {
		return errors;
	}

	public void setErrors(List<PaymentValidationError> errors) {
		this.errors = errors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creditTransferMessage == null) ? 0 : creditTransferMessage.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((isValidation == null) ? 0 : isValidation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Payment other = (Payment) obj;
		if (creditTransferMessage == null) {
			if (other.creditTransferMessage != null)
				return false;
		} else if (!creditTransferMessage.equals(other.creditTransferMessage))
			return false;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (isValidation == null) {
			if (other.isValidation != null)
				return false;
		} else if (!isValidation.equals(other.isValidation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payment [creditTransferMessage=" + creditTransferMessage + ", isValidation=" + isValidation
				+ ", errors=" + errors + "]";
	}

}
