package tax.audit;

public class Utils {
	
	public static boolean containsAny(String target, String...opts) {
		for(String opt : opts) {
			if(target.contains(opt)) {
				return true;
			}
		}
		return false;
	}

}
