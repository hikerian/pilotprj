package tax.audit.mg;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancellationAccount {
	@JsonProperty("GMGOCD")
	private String gmgocd;
	
	/**
	 * 금고
	 */
	@JsonProperty("GMGO_NM")
	private String gmgoNm;
	
	/**
	 * 계좌번호
	 */
	@JsonProperty("ACNO")
	private String acno;
	
	/**
	 * 상품명
	 */
	@JsonProperty("PRDT_NM")
	private String prdtNm;
	
	/**
	 * 개설일
	 */
	@JsonProperty("CNTRC_DATE")
	private String cntrcDate;
	
	/**
	 * 만기일
	 */
	@JsonProperty("CNTRC_CLSDT")
	private String cntrcClsdt;
	
	/**
	 * 해지일
	 */
	@JsonProperty("CLOS_DATE")
	private String closDate;
	
	/**
	 * 계약금액
	 */
	@JsonProperty("CNTRC_AMT")
	private long cntrcAmt;
	
	/**
	 * 총수령액
	 */
	@JsonProperty("CLOS_AMT")
	private long closAmt;
	
	@JsonProperty("INT_AMT")
	private long intAmt;
	
	@JsonProperty("TAX")
	private long tax;
	
	@JsonProperty("CLOS_GMGO_CD")
	private String closGmgoCd;
	
	@JsonProperty("LDGR_CLOS_GMGO_NM")
	private String ldgrClosGmgoNm;
	
	@JsonProperty("CLOS_CHL_KND_GBCD_NM")
	private String closChlKndGbcdNm;
	
	@JsonProperty("LDGR_CLOS_MEDU_NM")
	private String ldgrClosMeduNm;
	
	@JsonProperty("ACT_NKNM")
	private String actNknm;
	
	@JsonProperty("TAX_GBCD")
	private String taxGbcd;
	
	@JsonProperty("SAV_CLOS_GBCD")
	private String savClosGbcd;
	
	@JsonProperty("ITAX")
	private long itax;
	
	@JsonProperty("RTAX")
	private long rtax;
	
	@JsonProperty("FTAX")
	private long ftax;
	
	@JsonProperty("IAMT_ACNO")
	private String iamtAcno;
	
	@JsonProperty("IAMT_ACNO_PRDT_NM")
	private String iamtAcnoPrdtNm;
	
	@JsonProperty("TRNS_GMGO_NM")
	private String trnsGmgoNm;
	
	@JsonProperty("ACT_GBCD")
	private String actGbcd;
	
	@JsonProperty("TAX_GBCD_NM")
	private String taxGbcdNm;
	
	
	public CancellationAccount() {
	}


	public String getGmgocd() {
		return gmgocd;
	}


	public void setGmgocd(String gmgocd) {
		this.gmgocd = gmgocd;
	}


	public String getGmgoNm() {
		return gmgoNm;
	}


	public void setGmgoNm(String gmgoNm) {
		this.gmgoNm = gmgoNm;
	}


	public String getAcno() {
		return acno;
	}


	public void setAcno(String acno) {
		this.acno = acno;
	}


	public String getPrdtNm() {
		return prdtNm;
	}


	public void setPrdtNm(String prdtNm) {
		this.prdtNm = prdtNm;
	}


	public String getCntrcDate() {
		return cntrcDate;
	}


	public void setCntrcDate(String cntrcDate) {
		this.cntrcDate = cntrcDate;
	}


	public String getCntrcClsdt() {
		return cntrcClsdt;
	}


	public void setCntrcClsdt(String cntrcClsdt) {
		this.cntrcClsdt = cntrcClsdt;
	}


	public String getClosDate() {
		return closDate;
	}


	public void setClosDate(String closDate) {
		this.closDate = closDate;
	}


	public long getCntrcAmt() {
		return cntrcAmt;
	}


	public void setCntrcAmt(long cntrcAmt) {
		this.cntrcAmt = cntrcAmt;
	}


	public long getClosAmt() {
		return closAmt;
	}


	public void setClosAmt(long closAmt) {
		this.closAmt = closAmt;
	}


	public long getIntAmt() {
		return intAmt;
	}


	public void setIntAmt(long intAmt) {
		this.intAmt = intAmt;
	}


	public long getTax() {
		return tax;
	}


	public void setTax(long tax) {
		this.tax = tax;
	}


	public String getClosGmgoCd() {
		return closGmgoCd;
	}


	public void setClosGmgoCd(String closGmgoCd) {
		this.closGmgoCd = closGmgoCd;
	}


	public String getLdgrClosGmgoNm() {
		return ldgrClosGmgoNm;
	}


	public void setLdgrClosGmgoNm(String ldgrClosGmgoNm) {
		this.ldgrClosGmgoNm = ldgrClosGmgoNm;
	}


	public String getClosChlKndGbcdNm() {
		return closChlKndGbcdNm;
	}


	public void setClosChlKndGbcdNm(String closChlKndGbcdNm) {
		this.closChlKndGbcdNm = closChlKndGbcdNm;
	}


	public String getLdgrClosMeduNm() {
		return ldgrClosMeduNm;
	}


	public void setLdgrClosMeduNm(String ldgrClosMeduNm) {
		this.ldgrClosMeduNm = ldgrClosMeduNm;
	}


	public String getActNknm() {
		return actNknm;
	}


	public void setActNknm(String actNknm) {
		this.actNknm = actNknm;
	}


	public String getTaxGbcd() {
		return taxGbcd;
	}


	public void setTaxGbcd(String taxGbcd) {
		this.taxGbcd = taxGbcd;
	}


	public String getSavClosGbcd() {
		return savClosGbcd;
	}


	public void setSavClosGbcd(String savClosGbcd) {
		this.savClosGbcd = savClosGbcd;
	}


	public long getItax() {
		return itax;
	}


	public void setItax(long itax) {
		this.itax = itax;
	}


	public long getRtax() {
		return rtax;
	}


	public void setRtax(long rtax) {
		this.rtax = rtax;
	}


	public long getFtax() {
		return ftax;
	}


	public void setFtax(long ftax) {
		this.ftax = ftax;
	}


	public String getIamtAcno() {
		return iamtAcno;
	}


	public void setIamtAcno(String iamtAcno) {
		this.iamtAcno = iamtAcno;
	}


	public String getIamtAcnoPrdtNm() {
		return iamtAcnoPrdtNm;
	}


	public void setIamtAcnoPrdtNm(String iamtAcnoPrdtNm) {
		this.iamtAcnoPrdtNm = iamtAcnoPrdtNm;
	}


	public String getTrnsGmgoNm() {
		return trnsGmgoNm;
	}


	public void setTrnsGmgoNm(String trnsGmgoNm) {
		this.trnsGmgoNm = trnsGmgoNm;
	}


	public String getActGbcd() {
		return actGbcd;
	}


	public void setActGbcd(String actGbcd) {
		this.actGbcd = actGbcd;
	}


	public String getTaxGbcdNm() {
		return taxGbcdNm;
	}


	public void setTaxGbcdNm(String taxGbcdNm) {
		this.taxGbcdNm = taxGbcdNm;
	}


	@Override
	public int hashCode() {
		return Objects.hash(acno, actGbcd, actNknm, Long.valueOf(closAmt), closChlKndGbcdNm, closDate, closGmgoCd,
				Long.valueOf(cntrcAmt), cntrcClsdt, cntrcDate, Long.valueOf(ftax), gmgoNm, gmgocd, iamtAcno,
				iamtAcnoPrdtNm, Long.valueOf(intAmt), Long.valueOf(itax), ldgrClosGmgoNm, ldgrClosMeduNm, prdtNm,
				Long.valueOf(rtax), savClosGbcd, Long.valueOf(tax), taxGbcd, taxGbcdNm, trnsGmgoNm);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CancellationAccount other = (CancellationAccount) obj;
		return Objects.equals(acno, other.acno) && Objects.equals(actGbcd, other.actGbcd)
				&& Objects.equals(actNknm, other.actNknm) && closAmt == other.closAmt
				&& Objects.equals(closChlKndGbcdNm, other.closChlKndGbcdNm) && Objects.equals(closDate, other.closDate)
				&& Objects.equals(closGmgoCd, other.closGmgoCd) && cntrcAmt == other.cntrcAmt
				&& Objects.equals(cntrcClsdt, other.cntrcClsdt) && Objects.equals(cntrcDate, other.cntrcDate)
				&& ftax == other.ftax && Objects.equals(gmgoNm, other.gmgoNm) && Objects.equals(gmgocd, other.gmgocd)
				&& Objects.equals(iamtAcno, other.iamtAcno) && Objects.equals(iamtAcnoPrdtNm, other.iamtAcnoPrdtNm)
				&& intAmt == other.intAmt && itax == other.itax && Objects.equals(ldgrClosGmgoNm, other.ldgrClosGmgoNm)
				&& Objects.equals(ldgrClosMeduNm, other.ldgrClosMeduNm) && Objects.equals(prdtNm, other.prdtNm)
				&& rtax == other.rtax && Objects.equals(savClosGbcd, other.savClosGbcd) && tax == other.tax
				&& Objects.equals(taxGbcd, other.taxGbcd) && Objects.equals(taxGbcdNm, other.taxGbcdNm)
				&& Objects.equals(trnsGmgoNm, other.trnsGmgoNm);
	}


	@Override
	public String toString() {
		return "CancellationAccount [gmgocd=" + gmgocd + ", gmgoNm=" + gmgoNm + ", acno=" + acno + ", prdtNm=" + prdtNm
				+ ", cntrcDate=" + cntrcDate + ", cntrcClsdt=" + cntrcClsdt + ", closDate=" + closDate + ", cntrcAmt="
				+ cntrcAmt + ", closAmt=" + closAmt + ", intAmt=" + intAmt + ", tax=" + tax + ", closGmgoCd="
				+ closGmgoCd + ", ldgrClosGmgoNm=" + ldgrClosGmgoNm + ", closChlKndGbcdNm=" + closChlKndGbcdNm
				+ ", ldgrClosMeduNm=" + ldgrClosMeduNm + ", actNknm=" + actNknm + ", taxGbcd=" + taxGbcd
				+ ", savClosGbcd=" + savClosGbcd + ", itax=" + itax + ", rtax=" + rtax + ", ftax=" + ftax
				+ ", iamtAcno=" + iamtAcno + ", iamtAcnoPrdtNm=" + iamtAcnoPrdtNm + ", trnsGmgoNm=" + trnsGmgoNm
				+ ", actGbcd=" + actGbcd + ", taxGbcdNm=" + taxGbcdNm + "]";
	}
	
	

}
