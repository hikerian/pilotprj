package utilityprj.files;

import java.io.File;


public class FileList {
	
	private static void fileList(String baseDir) {
		File base = new File(baseDir);
		File[] files = base.listFiles();
		
		for(File file : files) {
			if(file.isDirectory()) {
				FileList.fileList(file);
			} else {
				FileList.print(file);
			}
		}
	}
	
	private static void fileList(File dir) {
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				FileList.fileList(file);
			} else {
				FileList.print(file);
			}
		}
	}
	
	private static void print(File file) {
		String parent = file.getParent();
		int len = "E:\\work\\dev\\workspaces\\daolis_svn\\edma\\src\\main\\java\\".length();
		parent = parent.substring(len).replace('\\', '.');
		
		System.out.println(parent + "," + file.getName());
	}
	

	public static void main(String[] args) {
		String baseDir = "E:/work/dev/workspaces/daolis_svn/edma/src/main/java";
		
		FileList.fileList(baseDir);

	}

}
