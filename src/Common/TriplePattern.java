package Common;

public class TriplePattern implements Comparable<TriplePattern> {
	private String subjectStr;
	private String predicateStr;
	private String objectStr;
	private boolean subjectVarTag;// subjectVarTag = true means that subject is
									// variable
	private boolean predicateVarTag;// predicateVarTag = true means that subject
									// is variable
	private boolean objectVarTag;// objectVarTag = true means that subject is
									// variable
	private String signatureStr;

	public TriplePattern() {
		super();
	}

	public TriplePattern(TriplePattern o) {
		this.objectStr = o.objectStr;
		this.predicateStr = o.predicateStr;
		this.subjectStr = o.subjectStr;
		this.subjectVarTag = o.subjectVarTag;
		this.predicateVarTag = o.predicateVarTag;
		this.objectVarTag = o.objectVarTag;
		this.signatureStr = o.signatureStr;
	}

	public void setSignature() {
		this.signatureStr = "";

		if (!subjectVarTag) {
			this.signatureStr += subjectStr + "\t";
		} else {
			this.signatureStr += "\t";
		}

		if (!predicateVarTag) {
			this.signatureStr += predicateStr + "\t";
		} else {
			this.signatureStr += "\t";
		}

		if (!objectVarTag) {
			this.signatureStr += objectStr + "\t";
		} else {
			this.signatureStr += "\t";
		}
	}

	public String getSignature() {
		return this.signatureStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((signatureStr == null) ? 0 : signatureStr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TriplePattern other = (TriplePattern) obj;
		if (signatureStr == null) {
			if (other.signatureStr != null)
				return false;
		} else if (!signatureStr.equals(other.signatureStr))
			return false;
		return true;
	}

	@Override
	public int compareTo(TriplePattern o) {
		return this.signatureStr.compareTo(o.signatureStr);
	}

	public String getSubjectStr() {
		return subjectStr;
	}

	public void setSubjectStr(String subjectStr) {
		this.subjectStr = subjectStr;
		setSignature();
	}

	public String getPredicateStr() {
		return predicateStr;
	}

	public void setPredicateStr(String predicateStr) {
		this.predicateStr = predicateStr;
		setSignature();
	}

	public String getObjectStr() {
		return objectStr;
	}

	public void setObjectStr(String objectStr) {
		this.objectStr = objectStr;
		setSignature();
	}

	public boolean isSubjectVar() {
		return subjectVarTag;
	}

	public void setSubjectVarTag(boolean subjectVarTag) {
		this.subjectVarTag = subjectVarTag;
	}

	public boolean isPredicateVar() {
		return predicateVarTag;
	}

	public void setPredicateVarTag(boolean predicateVarTag) {
		this.predicateVarTag = predicateVarTag;
	}

	public boolean isObjectVar() {
		return objectVarTag;
	}

	public void setObjectVarTag(boolean objectVarTag) {
		this.objectVarTag = objectVarTag;
	}

	@Override
	public String toString() {
		return "TriplePattern [subjectStr=" + subjectStr + ", predicateStr="
				+ predicateStr + ", objectStr=" + objectStr + "]";
	}

	public String toTriplePatternString() {
		return subjectStr + "\t" + predicateStr + "\t" + objectStr;
	}

}
