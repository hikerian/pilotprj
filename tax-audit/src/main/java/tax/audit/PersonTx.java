package tax.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class PersonTx {
	private String name; // sheet 이름
	private List<TxDetail> txList = new ArrayList<>();
	
	
	public PersonTx() {
	}
	
	public PersonTx(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void add(TxDetail detail) {
		this.txList.add(detail);
	}
	
	public List<TxDetail> getTxList() {
		return this.txList;
	}
	
	public void filter(Predicate<TxDetail> predicate) {
		this.txList = this.txList.stream()
				.filter(predicate)
				.toList();
	}
	
	public int getTxCount() {
		return this.txList.size();
	}


}