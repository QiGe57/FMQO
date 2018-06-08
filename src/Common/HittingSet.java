package Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class HittingSet implements Comparable<HittingSet> {
	private ArrayList<TriplePattern> triplePatternList;
	private ArrayList<Pair<Integer, Integer>> HittingQuerySet;
	private ArrayList<String> signature;
	private ArrayList<Mapping> MappingList;
	private int varNum;
	private TreeMap<String, ArrayList<EdgeInfo>> adjacentList;

	public HittingSet() {
		super();
		this.triplePatternList = new ArrayList<TriplePattern>();
		this.HittingQuerySet = new ArrayList<Pair<Integer, Integer>>();
		this.signature = new ArrayList<String>();
		this.MappingList = new ArrayList<Mapping>();
		this.varNum = 0;
		this.adjacentList = new TreeMap<String, ArrayList<EdgeInfo>>();
	}

	@Override
	public String toString() {
		return "HittingSet [triplePatternList=" + triplePatternList
				+ ", HittingQuerySet=" + HittingQuerySet + "]";
	}

	public TriplePattern getTriplePattern(int idx) {
		return triplePatternList.get(idx);
	}

	public ArrayList<TriplePattern> getTriplePatternList() {
		return triplePatternList;
	}

	public void setTriplePatternList(ArrayList<TriplePattern> triplePatternList) {
		this.triplePatternList = triplePatternList;
	}

	public ArrayList<String> getSignature() {
		return signature;
	}

	public void setSignature(ArrayList<String> signature) {
		this.signature = signature;
	}

	public void updateSignature(String tpSign) {
		this.signature.add(tpSign);
		Collections.sort(this.signature);
	}

	public TreeMap<String, ArrayList<EdgeInfo>> getAdjacentList() {
		return adjacentList;
	}

	public void setAdjacentList(
			TreeMap<String, ArrayList<EdgeInfo>> adjacentList) {
		this.adjacentList = adjacentList;
	}

	public void initializeTriplePattern(TriplePattern tp) {
		Mapping curMapping = new Mapping();
		TriplePattern newTp = new TriplePattern();

		this.varNum = 0;
		if (tp.isSubjectVar()) {
			curMapping.insertPair(tp.getSubjectStr(), "?rv_" + this.varNum);
			newTp.setSubjectStr("?rv_" + this.varNum);
			newTp.setSubjectVarTag(true);
			this.varNum++;
		} else {
			newTp.setSubjectVarTag(false);
			newTp.setSubjectStr(tp.getSubjectStr());
		}

		if (tp.isPredicateVar()) {
			curMapping.insertPair(tp.getPredicateStr(), "?rv_" + this.varNum);
			newTp.setPredicateStr("?rv_" + this.varNum);
			newTp.setPredicateVarTag(true);
			this.varNum++;
		} else {
			newTp.setPredicateVarTag(false);
			newTp.setPredicateStr(tp.getPredicateStr());
		}

		if (tp.isObjectVar()) {
			curMapping.insertPair(tp.getObjectStr(), "?rv_" + this.varNum);
			newTp.setObjectStr("?rv_" + this.varNum);
			newTp.setObjectVarTag(true);
			this.varNum++;
		} else {
			newTp.setObjectVarTag(false);
			newTp.setObjectStr(tp.getObjectStr());
		}
		this.MappingList.add(curMapping);
		this.triplePatternList.add(newTp);
		this.updateSignature(newTp.getSignature());

		if (!this.adjacentList.containsKey(newTp.getSubjectStr())) {
			this.adjacentList.put(newTp.getSubjectStr(),
					new ArrayList<EdgeInfo>());
		}
		if (!this.adjacentList.containsKey(newTp.getObjectStr())) {
			this.adjacentList.put(newTp.getObjectStr(),
					new ArrayList<EdgeInfo>());
		}

		this.adjacentList.get(newTp.getSubjectStr()).add(
				new EdgeInfo(newTp.getPredicateStr(), true, newTp
						.getObjectStr()));
		this.adjacentList.get(newTp.getObjectStr()).add(
				new EdgeInfo(newTp.getPredicateStr(), false, newTp
						.getSubjectStr()));
	}

	private void addOneEdge(TriplePattern tp, String originalVarStr,
			String renamedVarStr, ArrayList<FullQuery> allQueryList) {
		Mapping curMapping = new Mapping();
		TriplePattern newTp = new TriplePattern();

		if (tp.isSubjectVar()) {
			if (tp.getSubjectStr().equals(originalVarStr)) {
				newTp.setSubjectStr(renamedVarStr);
			} else {
				newTp.setSubjectStr("?rv_" + this.varNum);
				curMapping.insertPair(tp.getSubjectStr(), "?rv_" + this.varNum);
				this.varNum++;
			}
		}

		if (tp.isPredicateVar()) {
			if (tp.getPredicateStr().equals(originalVarStr)) {
				newTp.setPredicateStr(renamedVarStr);
			} else {
				newTp.setPredicateStr("?rv_" + this.varNum);
				curMapping.insertPair(tp.getPredicateStr(), "?rv_"
						+ this.varNum);
				this.varNum++;
			}
		}

		if (tp.isObjectVar()) {
			if (tp.getObjectStr().equals(originalVarStr)) {
				newTp.setObjectStr(renamedVarStr);
			} else {
				newTp.setObjectStr("?rv_" + this.varNum);
				curMapping.insertPair(tp.getObjectStr(), "?rv_" + this.varNum);
				this.varNum++;
			}
		}

		newTp.setSubjectVarTag(tp.isSubjectVar());
		newTp.setPredicateVarTag(tp.isPredicateVar());
		newTp.setObjectVarTag(tp.isObjectVar());

		ArrayList<Pair<Integer, Integer>> tmpHittingQuerySet = this
				.getHittingQuerySet();
		for (int j = 0; j < tmpHittingQuerySet.size(); j++) {
			Pair<Integer, Integer> tmpPair = tmpHittingQuerySet.get(j);
			LocalQuery curLocalQuery = allQueryList.get(tmpPair.first)
					.getLocalQuery(tmpPair.second);

		}

		this.MappingList.add(curMapping);
		this.triplePatternList.add(newTp);
	}

	public ArrayList<Mapping> getMappingList() {
		return MappingList;
	}

	public void setMappingList(ArrayList<Mapping> mappingList) {
		MappingList = mappingList;
	}

	public ArrayList<Pair<Integer, Integer>> getHittingQuerySet() {
		return HittingQuerySet;
	}

	public void setHittingQuerySet(
			ArrayList<Pair<Integer, Integer>> hittingQuerySet) {
		HittingQuerySet = hittingQuerySet;
	}

	public void addHittingPair(int first, int second) {
		this.HittingQuerySet.add(new Pair<Integer, Integer>(first, second));
	}

	@Override
	public int compareTo(HittingSet o) {
		if (this.triplePatternList.size() != o.triplePatternList.size()) {
			return this.triplePatternList.size() - o.triplePatternList.size();
		}

		String tmpStr1 = this.signature.toString();
		String tmpStr2 = o.signature.toString();
		if (tmpStr1.compareTo(tmpStr2) != 0)
			return tmpStr1.compareTo(tmpStr2);

		return this.checkIsomorphism(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		result = prime
				* result
				+ ((triplePatternList == null) ? 0 : triplePatternList
						.hashCode());
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
		HittingSet other = (HittingSet) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		if (triplePatternList == null) {
			if (other.triplePatternList != null)
				return false;
		} else if (this.checkIsomorphism(other) != 0)
			return false;
		return true;
	}

	private int checkIsomorphism(HittingSet o) {
		Mapping tmpMapping = new Mapping();
		for (int i = 0; i < this.triplePatternList.size(); i++) {
			TriplePattern thisTp = this.triplePatternList.get(i);
			TriplePattern oTp = o.triplePatternList.get(i);

			if (!tmpMapping.orignalRenamedMap.containsKey(thisTp
					.getSubjectStr())) {
				tmpMapping.insertPair(thisTp.getSubjectStr(),
						oTp.getSubjectStr());
			} else {
				if (tmpMapping.orignalRenamedMap.get(thisTp.getSubjectStr())
						.equals(oTp.getSubjectStr())) {
					return tmpMapping.orignalRenamedMap.get(
							thisTp.getSubjectStr()).compareTo(
							oTp.getSubjectStr());
				}
			}

			if (!tmpMapping.orignalRenamedMap
					.containsKey(thisTp.getObjectStr())) {
				tmpMapping
						.insertPair(thisTp.getObjectStr(), oTp.getObjectStr());
			} else {
				if (tmpMapping.orignalRenamedMap.get(thisTp.getObjectStr())
						.equals(oTp.getObjectStr())) {
					return tmpMapping.orignalRenamedMap.get(
							thisTp.getObjectStr())
							.compareTo(oTp.getObjectStr());
				}
			}
		}

		return 0;
	}

	public int compareWithCost(HittingSet o) {
		if (this.HittingQuerySet.size() != o.HittingQuerySet.size()) {
			return this.HittingQuerySet.size() - o.HittingQuerySet.size();
		}
		return this.triplePatternList.size() - o.triplePatternList.size();
	}

	public void setHittingSet(HittingSet o) {
		this.setHittingQuerySet(o.HittingQuerySet);
		this.setMappingList(o.MappingList);
		this.setTriplePatternList(o.triplePatternList);
		this.setVarNum(o.varNum);
	}

	public int getVarNum() {
		return varNum;
	}

	public void setVarNum(int varNum) {
		this.varNum = varNum;
	}

	public static void SortInQuerySet(ArrayList<HittingSet> list, int low,
			int high) {
		if (low >= high)
			return;
		int first = low;
		int last = high;
		HittingSet key = new HittingSet();
		key.setHittingSet(list.get(first));
		while (first < last) {
			while (first < last && list.get(last).compareWithCost(key) >= 0)
				--last;
			list.get(first).setHittingSet(list.get(last));
			while (first < last && list.get(first).compareWithCost(key) <= 0)
				++first;
			list.get(last).setHittingSet(list.get(first));
		}
		list.get(first).setHittingSet(key);
		SortInQuerySet(list, low, first - 1);
		SortInQuerySet(list, first + 1, high);
	}

	public void removeQueries(HittingSet o) {
		this.HittingQuerySet.removeAll(o.HittingQuerySet);
	}

	public boolean hasBeenHit(int queryIdx) {
		for (int i = 0; i < this.HittingQuerySet.size(); i++) {
			if (this.HittingQuerySet.get(i).first == queryIdx)
				return true;
		}
		return false;
	}

}
