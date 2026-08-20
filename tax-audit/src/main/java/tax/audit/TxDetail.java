package tax.audit;

import java.util.Date;
import java.util.Objects;

public class TxDetail {
	private Date txDateTime; // 거래일시
	private String briefs; // 적요(체크카드, 카드입금,...)
	private String subject; // 대상자(보낸사람/받는사람)
	private String memo; // 송금메모
	private double outgoing; // 출금액
	private double income; // 입금액
	private double balance; // 잔액
	private String branch; // 거래점
	private String div; // 구분
	
	
	public TxDetail() {
	}
	
	public TxDetail(Date txDateTime, String briefs, String subject, String memo, double outgoing, double income,
			double balance, String branch, String div) {
		super();
		this.txDateTime = txDateTime;
		this.briefs = briefs;
		this.subject = subject;
		this.memo = memo;
		this.outgoing = outgoing;
		this.income = income;
		this.balance = balance;
		this.branch = branch;
		this.div = div;
	}

	public Date getTxDateTime() {
		return txDateTime;
	}

	public void setTxDateTime(Date txDateTime) {
		this.txDateTime = txDateTime;
	}

	public String getBriefs() {
		return briefs;
	}

	public void setBriefs(String briefs) {
		this.briefs = briefs;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public double getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(double outgoing) {
		this.outgoing = outgoing;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Double.valueOf(balance), branch, briefs, div, Double.valueOf(income), memo,
				Double.valueOf(outgoing), subject, txDateTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TxDetail other = (TxDetail) obj;
		return Double.doubleToLongBits(balance) == Double.doubleToLongBits(other.balance)
				&& Objects.equals(branch, other.branch) && Objects.equals(briefs, other.briefs)
				&& Objects.equals(div, other.div)
				&& Double.doubleToLongBits(income) == Double.doubleToLongBits(other.income)
				&& Objects.equals(memo, other.memo)
				&& Double.doubleToLongBits(outgoing) == Double.doubleToLongBits(other.outgoing)
				&& Objects.equals(subject, other.subject) && Objects.equals(txDateTime, other.txDateTime);
	}

	@Override
	public String toString() {
		return "TxDetail [txDateTime=" + txDateTime + ", briefs=" + briefs + ", subject=" + subject + ", memo=" + memo
				+ ", outgoing=" + outgoing + ", income=" + income + ", balance=" + balance + ", branch=" + branch
				+ ", div=" + div + "]";
	}
	
	
	

}
