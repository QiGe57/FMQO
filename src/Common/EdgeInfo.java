package Common;

public class EdgeInfo {
	public String predicateStr;
	public boolean direction;// true means forward;otherwise, backward
	public String targetStr;

	@Override
	public String toString() {
		return "EdgeInfo [predicateStr=" + predicateStr + ", direction="
				+ direction + ", targetStr=" + targetStr + "]";
	}

	public EdgeInfo(String predicateStr, boolean direction, String targetStr) {
		super();
		this.predicateStr = predicateStr;
		this.direction = direction;
		this.targetStr = targetStr;
	}
}
