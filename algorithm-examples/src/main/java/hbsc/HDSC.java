package hbsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDSC {
	private final Logger log = LoggerFactory.getLogger(HDSC.class);
	private HDSCMetadata metadata;
	
	
	public HDSC() {
		this.metadata = new HDSCMetadata();
	}

	
	
	
	protected Double distance(Double val1, Double val2) {
		return Math.abs(val1 - val2);
	}

}
