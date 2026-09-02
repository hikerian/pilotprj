package tax.audit.mg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;


public class MgJsonMain {

	public static void main(String[] args) throws IOException {
		String baseDir = "E:\\work\\workspaces\\pilotprj\\.git\\pilotprj\\tax-audit\\src\\main\\resources\\mg";
		String[] fileNames = {
              "MG-최소망-해지계좌-1.json"
			, "MG-최소망-해지계좌-2.json"
			, "MG-최소망-해지계좌-3.json"
		};
		
		List<CancellationAccount> accountList = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		for(String fileName : fileNames) {
			File file = new File(baseDir, fileName);
			String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			
			JSONObject json = (JSONObject)JSONValue.parse(content);
			JSONArray datapart = (JSONArray)json.get("DATAPART");
			JSONObject datapartObj = (JSONObject)datapart.get(0);
			
			JSONObject data = (JSONObject)datapartObj.get("DATA");
			JSONObject databody = (JSONObject)data.get("DATABODY");
			JSONArray accountArray = (JSONArray)databody.get("GRID00");
			
			for(Object accObj : accountArray) {
				JSONObject accJson = (JSONObject)accObj;
				
				CancellationAccount account = objectMapper.readValue(accJson.toJSONString(), CancellationAccount.class);
				
				accountList.add(account);
			}
		}
		
		System.out.println("Account Count: " + accountList.size());
		
		List<CancellationAccount> account2024 = new ArrayList<>();
		List<CancellationAccount> account2025 = new ArrayList<>();
		
		for(CancellationAccount account : accountList) {
			String cntrcDate = account.getCntrcDate(); // 개설일
			String closeDate = account.getClosDate(); // 해지일
			
			if(cntrcDate.compareTo("20240101") < 0 && closeDate.compareTo("20240101") > 0) {
				account2024.add(account);
			}
			
			if(cntrcDate.compareTo("20251231") < 0 && closeDate.compareTo("20251231") > 0) {
				account2025.add(account);
			}
		}
		
		String filePath2024 = "C:/Tmp/mg-최소망-2024.csv";
		String filePath2025 = "C:/Tmp/mg-최소망-2025.csv";
		
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath2024), "MS949")) {
			out.write("회금고명\t");
			out.write("상품명\t");
			out.write("계좌번호\t");
			out.write("금고명\t");
			out.write("개설일\t");
			out.write("만기일\t");
			out.write("해지일\t");
			out.write("계약금액\t");
			out.write("총수령액\n");
			for(CancellationAccount account : account2024) {
				out.write(account.getLdgrClosGmgoNm());
				out.write("\t");
				out.write(account.getPrdtNm());
				out.write("\t");
				out.write(account.getAcno());
				out.write("\t");
				out.write(account.getGmgoNm());
				out.write("\t");
				out.write(account.getCntrcDate());
				out.write("\t");
				out.write(account.getCntrcClsdt());
				out.write("\t");
				out.write(account.getClosDate());
				out.write("\t");
				out.write("" + account.getCntrcAmt());
				out.write("\t");
				out.write("" + account.getClosAmt());
				out.write("\n");
			}
			out.flush();
		}
		
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath2025), "MS949")) {
			out.write("회금고명\t");
			out.write("상품명\t");
			out.write("계좌번호\t");
			out.write("금고명\t");
			out.write("개설일\t");
			out.write("만기일\t");
			out.write("해지일\t");
			out.write("계약금액\t");
			out.write("총수령액\n");
			for(CancellationAccount account : account2025) {
				out.write(account.getLdgrClosGmgoNm());
				out.write("\t");
				out.write(account.getPrdtNm());
				out.write("\t");
				out.write(account.getAcno());
				out.write("\t");
				out.write(account.getGmgoNm());
				out.write("\t");
				out.write(account.getCntrcDate());
				out.write("\t");
				out.write(account.getCntrcClsdt());
				out.write("\t");
				out.write(account.getClosDate());
				out.write("\t");
				out.write("" + account.getCntrcAmt());
				out.write("\t");
				out.write("" + account.getClosAmt());
				out.write("\n");
			}
			out.flush();
		}

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
