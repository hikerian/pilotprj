package utilityprj.selfhealing;

public class CSSPathSimilarity {
	
	public static void main(String[] args) {
		String path1 = "body > div.cl-control.cl-container:nth-of-type(1) > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row.data-box > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-grid.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(1) > div > div:nth-of-type(3) > div.cl-grid-centersplit > div.cl-grid-detail.cl-grid-detail-band:nth-of-type(2) > div > div:nth-of-type(1) > div > div.cl-grid-cell-inherit > div.cl-grid-row.cl-odd-row.cl-viewing:nth-of-type(5) > div.cl-grid-cell.cl-grid-cell-inherit:nth-of-type(5) > div.cl-control.cl-default-cell.cl-grid-ctrl-inherit.cl-disabled.cl-border-none";
		String path2 = "body > div.cl-control.cl-container:nth-of-type(1) > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row.data-box > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-grid.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(1) > div > div:nth-of-type(3) > div.cl-grid-centersplit > div.cl-grid-detail.cl-grid-detail-band:nth-of-type(2) > div > div:nth-of-type(1) > div > div.cl-grid-cell-inherit > div.cl-even-row.cl-grid-row.cl-viewing:nth-of-type(6) > div.cl-grid-cell.cl-grid-cell-inherit:nth-of-type(5) > div.cl-control.cl-default-cell.cl-grid-ctrl-inherit.cl-disabled.cl-border-none";
		
		double score = LCSImplementation.calculateSimilarity(path1, path2);
		
		System.out.println("LCSImplementation Score: " + score);
		
		
		double score2 = CosineSimilarity.calculateCosineSimilarity(path1, path2);
		
		System.out.println("CosineSimilarity Score: " + score2);
	}

}
