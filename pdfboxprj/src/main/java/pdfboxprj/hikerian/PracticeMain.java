package pdfboxprj.hikerian;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

public class PracticeMain {
	
	public static void main(String[] args) {
		File pdfFile = new File("C:/Temp/text.pdf");
		if(pdfFile.exists()) {
			pdfFile.delete();
		}
		
		try(PDDocument document = new PDDocument()) {
			// 페이지 추가
			PDPage firstPage = new PDPage();
			document.addPage(firstPage);
			
			PDPage secondPage = new PDPage();
			document.addPage(secondPage);
			
			try(PDPageContentStream contentStream = new PDPageContentStream(document, firstPage)){
				// begin the content stream
				contentStream.beginText();
				
//				contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 12);
				{
					// TTF Load
					String fontPath = "E:/work/workspaces/softdev/sdp-docs/src/main/resources/font/NanumGothic.ttf";
					PDType0Font font = PDType0Font.load(document, new FileInputStream(fontPath), true);
					contentStream.setFont(font, 12);
				}
				
				// setting the leading : line 간의 간격
				contentStream.setLeading(14.5f);
				
				// Setting the position for the line : 출력 시작 위치
				contentStream.newLineAtOffset(25f, 750f);
				
				String text = "안녕하세요. 현장소프트웨어 개발 수업입니다.";
//				String text = "Hello. lecture practice.";
				
				// adding text in the form of string
				contentStream.showText(text);
				contentStream.newLine();
				contentStream.showText(text + text);
				
				// ending the content stream
				contentStream.endText();
				
				// draw line
				{
					contentStream.moveTo(25f, 730f);
					contentStream.lineTo(425f, 730f);
					contentStream.lineTo(425f, 530f);
					contentStream.stroke();
				}
				
				// image drawing
				{
					PDImageXObject image = PDImageXObject.createFromFile("E:/work/workspaces/softdev/sdp-docs/src/main/resources/image/image.png", document);
					
					// drawing the image in the PDF Document
					contentStream.drawImage(image, 0f, 10f);
				}
			}
			
			// pdf 정보 추가
			PDDocumentInformation info = document.getDocumentInformation();
			info.setAuthor("홍길동");
			info.setTitle("PDFBox 실습 타이틀");
			info.setCreator("홍길동");
			info.setSubject("PDFBox 실습 서브젝트");
			
			// 파일 저장
			document.save(pdfFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(PDDocument document = Loader.loadPDF(pdfFile)) {
			// 페이지 수 조회
			int pageNo = document.getNumberOfPages();
			System.out.println("Number of Pages: " + pageNo);
			
			// 페이지 삭제
			document.removePage(1);
			pageNo = document.getNumberOfPages();
			System.out.println("Number of Pages: " + pageNo);
			
			// pdf 정보 조회
			PDDocumentInformation info = document.getDocumentInformation();
			System.out.println("Author: " + info.getAuthor());
			System.out.println("Title: " + info.getTitle());
			System.out.println("Creator: " + info.getCreator());
			System.out.println("Subject: " + info.getSubject());
			
			// Instantiate PDFTextStripper class
			PDFTextStripper pdfStripper = new PDFTextStripper();
			
			// Retrieving text from PDF Document
			String text = pdfStripper.getText(document);
			System.out.println(text);
			
			pdfFile.delete();
			
			// pdf 저장
			document.save(pdfFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
