package utilityprj.csspath;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class CssPath {
	private String tag;
	private Set<String> classes;
	private String pseudoClass;
	
	
	public CssPath(String path) {
		this.tag = null;
		this.classes = new HashSet<>();
		this.pseudoClass = null;
		
		if(path.indexOf(".") > -1) {
			String[] tagClasses = path.split("[.]");
			this.tag = tagClasses[0];
			
			if(tagClasses[tagClasses.length - 1].indexOf(":") > -1) {
				String lastClass = tagClasses[tagClasses.length - 1];
				String[] lastClassNPseudoClass = lastClass.split("[:]+");
				tagClasses[tagClasses.length - 1] = lastClassNPseudoClass[0];
				this.pseudoClass = lastClassNPseudoClass[1];
			}
			for(int i = 1; i < tagClasses.length; i++) {
				this.classes.add(tagClasses[i]);
			}
		} else if(path.indexOf(":") > -1) {
			String[] tagNPseudoClass = path.split("[:]+");
			this.tag = tagNPseudoClass[0];
			this.pseudoClass = tagNPseudoClass[1];
		} else {
			this.tag = path;
		}
	}
	
	public CssPath(String tag, Set<String> classes, String pseudoClass) {
		super();
		this.tag = tag;
		this.classes = classes;
		this.pseudoClass = pseudoClass;
	}

	public String getTag() {
		return this.tag;
	}
	
	public Set<String> getClasses() {
		return this.classes;
	}
	
	public String getPseudoClass() {
		return this.pseudoClass;
	}
	
	public String toCssPath() {
		StringBuilder builder = new StringBuilder();
		
		if(this.tag != null) {
			builder.append(this.tag);
		}
		
		if(this.classes.size() > 0) {
			for(String className : this.classes) {
				builder.append(".");
				builder.append(className);
			}
		}
		
		if(this.pseudoClass != null) {
			builder.append(":");
			builder.append(this.pseudoClass);
		}
		
		return builder.toString();
	}

	@Override
	public String toString() {
		return "CssPath [tag=" + tag + ", classes=" + classes + ", pseudoClass=" + pseudoClass + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(classes, pseudoClass, tag);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CssPath other = (CssPath) obj;
		return Objects.equals(classes, other.classes) && Objects.equals(pseudoClass, other.pseudoClass)
				&& Objects.equals(tag, other.tag);
	}


}
