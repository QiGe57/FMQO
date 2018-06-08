package Common;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Mapping {
	public TreeMap<String, String> orignalRenamedMap;
	public TreeMap<String, String> renamedOrignalMap;

	public Mapping() {
		super();
		this.orignalRenamedMap = new TreeMap<String, String>();
		this.renamedOrignalMap = new TreeMap<String, String>();
	}

	public void insertPair(String preimage, String image) {
		this.orignalRenamedMap.put(preimage, image);
		this.renamedOrignalMap.put(image, preimage);
	}

	public Set<String> getDomain() {
		return this.orignalRenamedMap.keySet();
	}

	public Set<String> getRange() {
		return this.renamedOrignalMap.keySet();
	}

	@Override
	public String toString() {
		return "Mapping [orignalRenamedMap=" + orignalRenamedMap
				+ ", renamedOrignalMap=" + renamedOrignalMap + "]";
	}

	public TreeMap<String, String> getOrignalRenamedMap() {
		return orignalRenamedMap;
	}

	public void setOrignalRenamedMap(TreeMap<String, String> orignalRenamedMap) {
		this.orignalRenamedMap = orignalRenamedMap;
	}

	public TreeMap<String, String> getRenamedOrignalMap() {
		return renamedOrignalMap;
	}

	public void setRenamedOrignalMap(TreeMap<String, String> renamedOrignalMap) {
		this.renamedOrignalMap = renamedOrignalMap;
	}

}
