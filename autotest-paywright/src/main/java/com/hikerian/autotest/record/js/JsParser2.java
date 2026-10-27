package com.hikerian.autotest.record.js;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;


/**
 * Google Closure-Compiler.
 * 시간이 오래 걸림!! Driver를 수정하는 쪽으로 정리 필요....
 */
public class JsParser2 {
	private final Logger log = LoggerFactory.getLogger(JsParser2.class);
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		// 1. 분석할 자바스크립트 소스코드 변수 정의
		String jsCode = """
async ({ page }) => {
  await page.goto('http://ustiblue.com:8791/index.do');
  await page.getByRole('textbox', { name: '아이디' }).click();
  await page.getByRole('textbox', { name: '아이디' }).fill('****');
  await page.getByRole('textbox', { name: '아이디' }).press('Tab');
  await page.getByRole('textbox', { name: '비밀번호' }).fill('****');
  await page.getByRole('textbox', { name: '비밀번호' }).press('Enter');

  
  await page1.goto('http://211.109.22.33:8791/index.do');
  await page1.getByRole('menubar').filter({ hasText: '학생서비스학사행정일반행정정보통계공통관리기본업무' }).click();
  await page1.getByRole('menuitem', { name: '복무관리' }).click();
  await page1.getByRole('menuitem', { name: '출장관리' }).click();
  await page1.getByRole('menuitem', { name: '출장경비기준관리' }).click();
  await page1.getByRole('combobox', { name: '검색어를 입력해 주세요' }).click();
  const page2Promise = page1.waitForEvent('popup');
  await page1.getByRole('link', { name: '블로그' }).click();
  const page2 = await page2Promise;
}
				""";

		try {
			// 1. Compiler 생성
			Compiler compiler = new Compiler();
			
			// 2. Compiler 옵션
			CompilerOptions options = new CompilerOptions();
			options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT);
			
			// 3. JavaScript 소스 생성
			SourceFile source = SourceFile.fromCode("sample.js", jsCode);
			
			// 4. Compiler 초기화
			compiler.init(List.of(), List.of(source), options);
			
			// 5. JavaScript 파싱
			compiler.parse();
			
			// 6. 오류검사
			if(!compiler.getErrors().isEmpty()) {
				compiler.getErrors().forEach(System.err::println);
				return;
			}
			
			// 7. AST 조회
			Node root = compiler.getRoot();
			
			// 8. AST 출력
			printAst(root, 0);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Time Spent: " + (end - start) + " ms."); //Time Spent: 360 ms.
	}
	private static void printAst(Node node, int depth) {
		String indent = "  ".repeat(depth);
		
		System.out.println(indent + "(" + depth + ")" + node.getToken()
			+ (node.isName() || node.isStringLit() || node.isStringKey() ? " : " + node.getString() : "")
			+ " - " + node.getQualifiedName() + (node.isGetProp() ? " => " + node.getString() : ""));
		
//		System.out.println(node);
		
		for(Node child = node.getFirstChild(); child != null; child = child.getNext()) {
			printAst(child, depth + 1);
		}
		
	}

}
