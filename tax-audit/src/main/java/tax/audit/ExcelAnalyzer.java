package tax.audit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class ExcelAnalyzer {
	private static final String txDateTimeFormat = "yyyy.MM.dd HH:mm:ss";
	
	private static List<PersonTx> parse(String filePath) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(ExcelAnalyzer.txDateTimeFormat);
		
		List<PersonTx> txInfo = new ArrayList<>();
		
		try(Workbook workbook = WorkbookFactory.create(new File(filePath))) {
			int sheetCount = workbook.getNumberOfSheets();
			for(int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				String shName = sheet.getSheetName();
				
				PersonTx personTx = new PersonTx(sheet.getSheetName());
				txInfo.add(personTx);
				int lastRowIdx = sheet.getLastRowNum();
				
				for(int r = 1; r <= lastRowIdx; r++) {
					Row row = sheet.getRow(r);
					Cell cell1 = row.getCell(0); // 거래일시
					String txDateTimeStr = cell1.getStringCellValue();
					Date txDateTime = dateFormat.parse(txDateTimeStr);
					
					Cell cell2 = row.getCell(1);
					String briefs = cell2.getStringCellValue(); // 적요
					if(briefs != null && (
							briefs.contains("체크카드")
							|| briefs.contains("국민카드")
							|| briefs.contains("KB츌금")
							|| briefs.contains("기일츌금")
							|| briefs.contains("지로츌금")
							)) {
						continue;
					}
					
					Cell cell3 = row.getCell(2);
					String subject = cell3.getStringCellValue(); // 대상자(보낸사람/받는사람)
//					if(subject == null || (
//							(shName.contains("조동일") && subject.contains("최소망"))
//							|| (shName.contains("최소망") && subject.contains("조동일"))
//							) == false) {
//						continue;
//					}
					
					Cell cell4 = row.getCell(3);
					String memo = cell4.getStringCellValue(); // 송금메모
					
					Cell cell5 = row.getCell(4);
					double outgoing = cell5.getNumericCellValue(); // 출금액
					
					Cell cell6 = row.getCell(5);
					double income = cell6.getNumericCellValue(); // 입금액
					
					Cell cell7 = row.getCell(6);
					double balance = cell7.getNumericCellValue(); // 잔액
					
					Cell cell8 = row.getCell(7);
					String branch = cell8.getStringCellValue(); // 거래점
					
					Cell cell9 = row.getCell(8);
					String div = cell9.getStringCellValue(); // 구분
					
					
					personTx.add(new TxDetail(txDateTime, briefs, subject, memo, outgoing, income, balance, branch, div));
				}
				
			}
			
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return txInfo;
	}
	
	private static void print(PersonTx personTx, String dirPath, String name) {
		final String fileName = name + ".csv";
		final String[] header = {"거래일시", "적요", "보낸분/받는분", "송금메모", "출금액", "입금액", "잔액", "거래점", "구분"}; 
		
		final SimpleDateFormat dateFormat = new SimpleDateFormat(ExcelAnalyzer.txDateTimeFormat);
		
		CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
				.setHeader(header)
				.get();
		
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(dirPath, fileName), Charset.forName("ms949"));
				CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
			
			List<TxDetail> txList = personTx.getTxList();
			for(TxDetail tx : txList) {
				csvPrinter.printRecord(
						dateFormat.format(tx.getTxDateTime())  // 거래일시
						, tx.getBriefs() // 적요(체크카드, 카드입금,...)
						, tx.getSubject() // 대상자(보낸사람/받는사람)
						, tx.getMemo() // 송금메모
						, tx.getOutgoing() // 출금액
						, tx.getIncome() // 입금액
						, tx.getBalance() // 잔액
						, tx.getBranch() // 거래점
						, tx.getDiv() // 구분
						);
			}
			
			csvPrinter.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void mutualTransfer(PersonTx tx, String dirPath) {
		String name = tx.getName();
		final String subjectFilter = name.contains("조동일") ? "최소망" : "조동일";
		
		tx.filter(detail -> {
			String subject = detail.getSubject();
			return subject != null && subject.contains(subjectFilter);
		});
		
		ExcelAnalyzer.print(tx, dirPath, name + "_상호이체");
	}
	
	private static void salaryIncome(PersonTx tx, String dirPath) {
		String name = tx.getName();
		if(name.contains("조동일") == false) {
			return;
		}
		
		tx.filter(detail -> {
			double income = detail.getIncome();
			if(income == 0) {
				return false;
			}
			
			String briefs = detail.getBriefs();
			String subject = detail.getSubject();
			
			String[] opts = {"급여", "경비", "비용", "신구", "토마토", "산단"};
			
			return Utils.containsAny(briefs, opts)
					|| Utils.containsAny(subject, opts);
		});
		
		ExcelAnalyzer.print(tx, dirPath, name + "_급여수익");
	}

	public static void main(String[] args) {
		final String filePath = "C:/Temp/조동일최소망(국민은행_입출금내역).xlsx";
		final String dirPath = "C:/Temp";
		
		List<PersonTx> txInfo = ExcelAnalyzer.parse(filePath);
		
		for(PersonTx tx : txInfo) {
//			ExcelAnalyzer.mutualTransfer(tx, dirPath);
			
			
			ExcelAnalyzer.salaryIncome(tx, dirPath);
			
		}

	}



}
