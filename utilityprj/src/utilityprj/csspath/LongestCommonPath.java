package utilityprj.csspath;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * 두 CSSPath에서 일치하는 부분만 추출하여 새로운 CSSPath를 생성.
 */
public class LongestCommonPath {
	
	
	public LongestCommonPath() {
	}
	
	public CssPathList getLongestCommonPath(String path1, String path2) {
		CssPathList pathList1 = new CssPathList(path1);
		CssPathList pathList2 = new CssPathList(path2);
		
		return this.getLongestCommonPath(pathList1, pathList2);
	}
	
	public CssPathList getLongestCommonPath(CssPathList pathList1, CssPathList pathList2) {
		List<CssPath> cssPathList1 = pathList1.getCssPathList();
		List<CssPath> cssPathList2 = pathList2.getCssPathList();
		
		int size = Math.min(cssPathList1.size(), cssPathList2.size());
		
		CssPathList lcp = new CssPathList();
		for(int i = 0; i < size;  i++) {
			CssPath path1 = cssPathList1.get(i);
			CssPath path2 = cssPathList2.get(i);
			
			String tag = null;
			Set<String> classes = null;
			String pseudoClass = null;
			
			if(Objects.equals(path1.getTag(), path2.getTag())) {
				tag = path1.getTag();
			}
			classes = new HashSet<>(path1.getClasses());
			classes.retainAll(path2.getClasses());
			
			if(Objects.equals(path1.getPseudoClass(), path2.getPseudoClass())) {
				pseudoClass = path1.getPseudoClass();
			}
			
			CssPath commonPath = new CssPath(tag, classes, pseudoClass);
			
			lcp.addPath(commonPath);
		}
		
		return lcp;
	}
	
	public static void main(String[] args) {
		String path1 = "body > div.cl-control.cl-container:nth-of-type(1) > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row.data-box > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-grid.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(1) > div > div:nth-of-type(3) > div.cl-grid-centersplit > div.cl-grid-detail.cl-grid-detail-band:nth-of-type(2) > div > div:nth-of-type(1) > div > div.cl-grid-cell-inherit > div.cl-grid-row.cl-odd-row.cl-viewing:nth-of-type(5) > div.cl-grid-cell.cl-grid-cell-inherit:nth-of-type(5) > div.cl-control.cl-default-cell.cl-grid-ctrl-inherit.cl-disabled.cl-border-none";
		String path2 = "body > div.cl-control.cl-container:nth-of-type(1) > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row.data-box > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-grid.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(1) > div > div:nth-of-type(3) > div.cl-grid-centersplit > div.cl-grid-detail.cl-grid-detail-band:nth-of-type(2) > div > div:nth-of-type(1) > div > div.cl-grid-cell-inherit > div.cl-even-row.cl-grid-row.cl-viewing:nth-of-type(6) > div.cl-grid-cell.cl-grid-cell-inherit:nth-of-type(5) > div.cl-control.cl-default-cell.cl-grid-ctrl-inherit.cl-disabled.cl-border-none";
		
		LongestCommonPath lcp = new LongestCommonPath();
		CssPathList commonPath = lcp.getLongestCommonPath(path1, path2);
		
		System.out.println("Path1: " + path1);
		System.out.println("Path2: " + path2);
		System.out.println("CPath: " + commonPath.toCssPath());
		
	}
	

}
