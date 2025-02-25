package pdfboxprj.hikerian;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class RemoveWaterMarkMain {
	
	public static void main(String[] args) {
		File toFile = new File("C:/Temp/doc1.pdf");
		if(toFile.exists()) {
			toFile.delete();
		}
		
		File pdfFile = new File("C:/Temp/doc.pdf");
		
		try(PDDocument document = Loader.loadPDF(pdfFile)) {
			PDPage page = document.getPage(0);
			
			PdfContentStreamEditor editor = new PdfContentStreamEditor(document, page) {
				final StringBuilder recentChars = new StringBuilder();
				final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");
				final List<String> REMOVE_STR = Arrays.asList("Created with Scanner", "Pro");

				@Override
				protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement)
						throws IOException {
					String string = font.toUnicode(code);
					if(string != null) {
						recentChars.append(string);
					}
					super.showGlyph(textRenderingMatrix, font, code, displacement);
				}
				
				@Override
				protected void write(ContentStreamWriter contentStreamWriter, Operator operator, List<COSBase> operands)
						throws IOException {
					String recentText = recentChars.toString();
					recentChars.setLength(0);
					
					String operatorString = operator.getName();
					
					System.out.println("recentText: " + recentText);
					System.out.println("operatorString: " + operatorString);
					System.out.println("operands: " + operands);
					
					if(TEXT_SHOWING_OPERATORS.contains(operatorString) && REMOVE_STR.contains(recentText)) {
						return;
					}
					
					/**
					 * https://pdfbox.apache.org/docs/2.0.5/javadocs/index.html?org/apache/pdfbox/contentstream/operator/text/package-summary.html
					 */
					if("l".equals(operatorString)) { // line
						return;
					}
//					if("m".equals(operatorString)) {
//						return;
//					}
//					if("cm".equals(operatorString)) {
//						return;
//					}
//					if("S".equals(operatorString)) {
//						return;
//					}
//					if("Q".equals(operatorString)) {
//						return;
//					}
//					if("q".equals(operatorString)) {
//						return;
//					}
//					if("SC".equals(operatorString)) {
//						return;
//					}
//					if("w".equals(operatorString)) {
//						return;
//					}
//					if("ET".equals(operatorString)) {
//						return;
//					}
//					if("TJ".equals(operatorString)) {
//						return;
//					}
//					if("Tf".equals(operatorString)) {
//						return;
//					}
//					if("Tj".equals(operatorString)) {
//						return;
//					}
//					if("Tm".equals(operatorString)) {
//						return;
//					}
//					if("Tc".equals(operatorString)) {
//						return;
//					}
//					if("BT".equals(operatorString)) {
//						return;
//					}
//					if("ri".equals(operatorString)) {
//						return;
//					}
//					if("sc".equals(operatorString)) {
//						return;
//					}
//					if("cs".equals(operatorString)) {
//						return;
//					}
//					if("Do".equals(operatorString)) {
//						return;
//					}

					super.write(contentStreamWriter, operator, operands);
				}
			};
			
			editor.processPage(page);
			
			int cnt = processLinks(page);
			
			System.out.println("Link Count: " + cnt);
			
			document.save(toFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int processLinks(PDPage page) throws IOException {
        int counter = 0;
        List<PDAnnotation> annotations = page.getAnnotations();
        for (int i = 0; i < annotations.size();) {
            PDAnnotation annotation = annotations.get(i++);
            if (annotation instanceof PDAnnotationLink) {
                PDAnnotationLink link = (PDAnnotationLink) annotation;
                PDAction action = link.getAction();
                if (action instanceof PDActionURI) {
                    PDActionURI uriAction = (PDActionURI) action;
                    String uri = uriAction.getURI();
                    
                    if(uri != null) {
                    	System.out.println("link uri: " + uri);
                    	annotations.remove(--i);
                    	counter++;
                    }
                    
//                    if (uri != null && uri.contains("some-text")) {
//                        annotations.remove(--i);
//                        counter++;
//                    }
                }
            }
        }
        return counter;
    }

}
