package Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class LocalQuery {
	private BGPGraph LocalBGP;
	private ArrayList<Integer> sourceList;
	private ArrayList<String[]> resultList;

	public ArrayList<Integer> getSourceList() {
		return sourceList;
	}

	public void setSourceList(ArrayList<Integer> sourceList) {
		this.sourceList = sourceList;
	}

	public ArrayList<String[]> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<String[]> resultList) {
		this.resultList = resultList;
	}

	public void addResult(String[] r) {
		this.resultList.add(r);
	}

	public String[] getResult(int idx) {
		return this.resultList.get(idx);
	}

	@Override
	public String toString() {
		return "LocalQuery [LocalBGP=" + LocalBGP + ", sourceList="
				+ sourceList + ", resultList=" + resultList + "]";
	}

	public String toSPARQLString() {
		String triplePatternsStr = "";

		for (int i = 0; i < this.LocalBGP.triplePatternList.size(); i++) {
			TriplePattern curPattern = this.LocalBGP.triplePatternList.get(i);

			String curTriplePatternStr = "";

			curTriplePatternStr += curPattern.getSubjectStr() + "\t";
			curTriplePatternStr += curPattern.getPredicateStr() + "\t";
			curTriplePatternStr += curPattern.getObjectStr() + " .";
			triplePatternsStr += curTriplePatternStr + "\n";
		}

		return "select * where { " + triplePatternsStr + "}";
	}

	public BGPGraph getLocalBGP() {
		return LocalBGP;
	}

	public void setLocalBGP(BGPGraph localBGP) {
		LocalBGP = localBGP;
	}

	public LocalQuery() {
		super();
		this.LocalBGP = new BGPGraph();
		this.sourceList = new ArrayList<Integer>();
		this.resultList = new ArrayList<String[]>();
	}

	public void constructCommonQuery(LocalQuery curLocalQuery1,
			LocalQuery mainPattern, Integer[] mainMapping,
			int rewritternQueryID, int optionalPatternIdx,
			RewrittenQuery curRewrittenQuery) {

		// TriplePattern mainTriplePattern = mainPattern.LocalBGP
		// .getTriplePattern(0);
		// this.LocalBGP.addTriplePattern(mainTriplePattern);
		curRewrittenQuery.getOptionalPatternList().add(new BGPGraph());

		if (curLocalQuery1.getTriplePatternList().size() == 1) {
			this.LocalBGP = mainPattern.LocalBGP;
			return;
		}

		int subjectID = 0, objectID = 0, subjectMapping = 0, objectMapping = 0;
		int var_id = 0;
		TreeMap<Integer, Integer> tmpVarID2NewIDMap = new TreeMap<Integer, Integer>();
		for (int i = 0; i < curLocalQuery1.LocalBGP.triplePatternList.size(); i++) {
			TriplePattern curTriplePattern = new TriplePattern(
					curLocalQuery1.LocalBGP.getTriplePattern(i));
			int tag = 0;

			subjectID = curLocalQuery1.LocalBGP.VertexIDmap
					.get(curTriplePattern.getSubjectStr());
			subjectMapping = searchInMapping(mainMapping, subjectID);
			if (subjectMapping == -1) {
				tag++;
				if (!tmpVarID2NewIDMap.containsKey(subjectID)) {
					var_id = tmpVarID2NewIDMap.size()
							+ mainPattern.LocalBGP.VertexIDmap.size();
					tmpVarID2NewIDMap.put(subjectID, var_id);
				}
				subjectMapping = tmpVarID2NewIDMap.get(subjectID);
				curTriplePattern.setSubjectStr("?rv_" + rewritternQueryID + "_"
						+ optionalPatternIdx + "_" + subjectMapping);
			} else {
				curTriplePattern
						.setSubjectStr(mainPattern.getLocalBGP().IDVertexmap
								.get(subjectMapping));
			}

			objectID = curLocalQuery1.LocalBGP.VertexIDmap.get(curTriplePattern
					.getObjectStr());
			objectMapping = searchInMapping(mainMapping, objectID);
			if (objectMapping == -1) {
				tag++;
				if (!tmpVarID2NewIDMap.containsKey(objectID)) {
					var_id = tmpVarID2NewIDMap.size()
							+ mainPattern.LocalBGP.VertexIDmap.size();
					tmpVarID2NewIDMap.put(objectID, var_id);
				}
				objectMapping = tmpVarID2NewIDMap.get(objectID);
				curTriplePattern.setObjectStr("?rv_" + rewritternQueryID + "_"
						+ optionalPatternIdx + "_" + objectMapping);
			} else {
				curTriplePattern
						.setObjectStr(mainPattern.getLocalBGP().IDVertexmap
								.get(objectMapping));
			}

			this.LocalBGP.addTriplePattern(curTriplePattern);

			if (tag > 0) {
				curRewrittenQuery.addTriplePatternInOptional(curTriplePattern,
						subjectMapping, objectMapping);
			}
		}
	}

	private int searchInMapping(Integer[] mainMapping, int objectID) {
		for (int i = 0; i < mainMapping.length; i++) {
			if (mainMapping[i] == objectID)
				return i;
		}

		return -1;
	}

	public void addTriplePattern(TriplePattern rewrittenMainPattern) {
		this.LocalBGP.addTriplePattern(rewrittenMainPattern);
	}

	public ArrayList<TriplePattern> getTriplePatternList() {
		return this.LocalBGP.getTriplePatternList();
	}

	public Integer[] checkSubgraph(LocalQuery sub) {
		return this.LocalBGP.checkSubgraph(sub.LocalBGP);
	}

	public Integer[] checkIsomorphic(LocalQuery sub) {
		return this.LocalBGP.checkIsomorphic(sub.LocalBGP);
	}

	public void sort() {
		this.LocalBGP.sort();
	}
}
