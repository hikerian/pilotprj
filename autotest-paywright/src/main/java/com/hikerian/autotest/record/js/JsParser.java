package com.hikerian.autotest.record.js;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsParser {
	private final Logger log = LoggerFactory.getLogger(JsParser.class);
	
	private final Pattern varNmPattern = Pattern.compile("^([a-zA-Z]+)([0-9]*)$");
	
	private final CompilerEnvirons env;
	
	private static final JsParser instance = new JsParser();
	
	public static JsParser getInstance() {
		return JsParser.instance;
	}
	

	private JsParser() {
		CompilerEnvirons env = new CompilerEnvirons();
		env.setRecoverFromErrors(true);
		env.setAllowMemberExprAsFunctionName(true);
		env.setReservedKeywordAsIdentifier(true);
//		env.setLanguageVersion(Context.VERSION_ECMASCRIPT);
		env.setLanguageVersion(Context.VERSION_ES6);
		
		this.env = env;
	}
	
	public FuncCall parse(String syntax) {
		if(syntax == null) {
			return null;
		}
		syntax = syntax.trim();
		if(syntax.startsWith("await ")) {
			syntax = syntax.substring("await ".length());
		}
		final String script = syntax;
		
		Parser jsParser = new Parser(this.env);
		
		AstRoot root = null;
		try {
			root = jsParser.parse(script, "action.js", 0);
		} catch(RuntimeException e) {
			/* Rhino는 aync/await와 같은 구문을 처리하지 못함.
			 * EX)
			 * const page2Promise = page1.waitForEvent('popup');
			 * await page1.getByRole('link', { name: '블로그' }).click();
			 * const page2 = await page2Promise;"
			 */
			this.log.error("JavaScript parse error: {}\n syntax: {}", e, syntax);
			throw e;
		}
		
		final List<FuncCall> funcCallList = new ArrayList<>();
		
		root.visit(node -> {
			if(node instanceof FunctionCall call) {
				AstNode target = call.getTarget();
				if(target instanceof PropertyGet property) {
					String method = property.getProperty().getIdentifier();
					String targetSource = target.toSource();
					if(targetSource.endsWith("." + method)) {
						targetSource = targetSource.substring(0, targetSource.length() - ("." + method).length());
					}
					
					List<FuncArg> args = new ArrayList<>();
					
					for(AstNode arg : call.getArguments()) {
						int pos = arg.getAbsolutePosition();
						String value = script.substring(pos, pos + arg.getLength());
						
						args.add(new FuncArg(value, arg.getType()));
					}
					FuncCall funcCall = new FuncCall(method, targetSource, args.size() > 0 ? args : null);
					
					if(funcCallList.size() == 0) {
						funcCallList.add(funcCall);
					} else {
						FuncCall child = funcCallList.getLast();
						child.setParent(funcCall);
					}
				}
			}
			
			return true;
		});
		
		return funcCallList.getFirst();
	}
	
	public VariableName parseVarName(String target) {
		Matcher matcher = this.varNmPattern.matcher(target);
		if(matcher.matches()) {
			String name = matcher.group(1);
			String seqTxt = matcher.group(2);
			
			return new VariableName(name, "".equals(seqTxt) ? 0 : Integer.parseInt(seqTxt));
		}
		
		this.log.warn("not variable name: {}", target);
		
		return null;
	}
	
	public static class FuncCall {
		private FuncCall parent;
		
		private String name;
		private String target;
		private List<FuncArg> args;
		
		FuncCall(String name, String target, List<FuncArg> args) {
			this.name = name;
			this.target = target;
			this.args = args;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getTarget() {
			return this.target;
		}
		
		public List<FuncArg> getArgs() {
			return this.args;
		}
		
		public void setParent(FuncCall parent) {
			this.parent = parent;
		}
		
		public FuncCall getParent() {
			return this.parent;
		}

		@Override
		public String toString() {
			return "func [parent=" + this.parent + ", name=" + this.name + ", target=" + this.target + ", args=" + this.args + "]";
		}

	}
	
	public static class FuncArg {
		private String value;
		private int type;
		
		FuncArg(String value, int type) {
			this.value = value;
			this.type = type;
		}
		
		public String getValue() {
			return this.value;
		}
		
		/**
		 * @see org.mozilla.javascript.Token.typeToName
		 * @return
		 */
		public int getType() {
			return this.type;
		}

		@Override
		public String toString() {
			return "arg [value=" + this.value + ", type=" + Token.typeToName(this.type) + "]";
		}
		
	}
	
	public static class VariableName {
		private final String name;
		private final int seq;
		
		
		VariableName(String name, int seq) {
			this.name = name;
			this.seq = seq;
		}
		
		public String getName() {
			return this.name;
		}
		
		public int getSeq() {
			return this.seq;
		}

		@Override
		public String toString() {
			return "VariableName [name=" + this.name + ", seq=" + this.seq + "]";
		}

	}



}
