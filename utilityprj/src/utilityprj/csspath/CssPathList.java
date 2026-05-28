package utilityprj.csspath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CssPathList {
	private List<CssPath> cssPathList;
	
	
	public CssPathList() {
		this.cssPathList = new ArrayList<>();
	}
	
	public CssPathList(String cssPaths) {
		this();
		
		String[] cssPathArray = cssPaths.split("\\s*>\\s*");
		
		for(String pathStr : cssPathArray) {
			this.cssPathList.add(new CssPath(pathStr));
		}
	}
	
	public void addPath(CssPath path) {
		this.cssPathList.add(path);
	}
	
	public CssPathList(List<CssPath> cssPathList) {
		super();
		this.cssPathList = cssPathList;
	}

	public List<CssPath> getCssPathList() {
		return this.cssPathList;
	}

	public String toCssPath() {
		StringBuilder builder = new StringBuilder();

		Iterator<CssPath> cssPathIterator = this.cssPathList.iterator();
		if(cssPathIterator.hasNext()) {
			CssPath path = cssPathIterator.next();
			builder.append(path.toCssPath());
		}
		
		while(cssPathIterator.hasNext()) {
			builder.append(" > ");
			CssPath path = cssPathIterator.next();
			builder.append(path.toCssPath());
		}
		
		return builder.toString();
	}
	


}
