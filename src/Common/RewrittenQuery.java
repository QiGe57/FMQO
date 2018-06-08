package Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RewrittenQuery {

	private int RewrittenQueryID;
	private BGPGraph MainPatternGraph;
	private ArrayList<BGPGraph> OptionalPatternList;
	private ArrayList<Pair<Integer, Integer>> HittingQuerySet;
	private ArrayList<Integer> sourceList;
	private ArrayList<String> bindingNames;
	private ArrayList<String[]> resultList;
	// private ArrayList<Result> resultListOfMainPattern;
	// private ArrayList<ArrayList<String[]>> resultListOfHittingQuery;

	// The first element in a pair of FilterExpressionList is the variable in
	// the filter expression;
	// The second element in a pair of FilterExpressionList is the value in
	// the filter expression;
	// Each pair maps to an expression.
	// A list of pairs is the conjunction of expressions, and each list is
	// associated with a optional pattern.
	// Since a rewritten query may have multiple optional patterns, a rewritten
	// query may have multiple lists.
	private ArrayList<ArrayList<ArrayList<Pair<String, String>>>> FilterExpressionList;

	// each list maps to a local query
	private ArrayList<ArrayList<Pair<String, String>>> ConstraintList;

	// mapping from old variable name to new variable name
	private ArrayList<TreeMap<String, String>> OriginalVarMapList;
	// mapping from new variable name to old variable name
	private ArrayList<TreeMap<String, String>> RenamedVarMapList;

	public static ArrayList<RewrittenQuery> rewriteQueriesLifeifei(
			ArrayList<FullQuery> allQueryList,
			ArrayList<Pair<Integer, Integer>> hittingQueryList,
			int rewrittenQueryID) {

		ArrayList<RewrittenQuery> resRewrittenQueryList = new ArrayList<RewrittenQuery>();
		resRewrittenQueryList.add(new RewrittenQuery());
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setRewrittenQueryID(rewrittenQueryID);

		Pair<Integer, Integer> p1 = hittingQueryList.get(0);
		LocalQuery localQuery1 = allQueryList.get(p1.first).getLocalQuery(
				p1.second);
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).HittingQuerySet
				.addAll(hittingQueryList);

		if (hittingQueryList.size() == 1) {

			ArrayList<TriplePattern> curTriplePatternList = localQuery1
					.getTriplePatternList();
			TreeMap<String, String> tmpOriginalVarMap = new TreeMap<String, String>();
			TreeMap<String, String> tmpRenamedVarMap = new TreeMap<String, String>();
			int cur_var_id = 0;
			for (int i = 0; i < curTriplePatternList.size(); i++) {
				TriplePattern tp = curTriplePatternList.get(i);
				TriplePattern new_tp = new TriplePattern();

				cur_var_id = setTriplePatternInMainPattern(tp, new_tp,
						rewrittenQueryID, cur_var_id, tmpOriginalVarMap,
						tmpRenamedVarMap);

				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).MainPatternGraph
						.addTriplePattern(new_tp);
			}

			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).OriginalVarMapList
					.add(tmpOriginalVarMap);
			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).RenamedVarMapList
					.add(tmpRenamedVarMap);
			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).sourceList
					.addAll(localQuery1.getSourceList());
			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).ConstraintList
					.add(new ArrayList<Pair<String, String>>());
			// resRewrittenQueryList.get(resRewrittenQueryList.size() -
			// 1).OptionalPatternList
			// .add(new BGPGraph());

		} else {
			Pair<Integer, Integer> p2 = hittingQueryList.get(1);
			LocalQuery localQuery2 = allQueryList.get(p2.first).getLocalQuery(
					p2.second);
			// LocalQuery commonQuery = new LocalQuery();
			int cur_var_id = 0;
			TreeMap<String, String> tmpOriginalVarMap = new TreeMap<String, String>();
			TreeMap<String, String> tmpRenamedVarMap = new TreeMap<String, String>();

			for (int j = 0; j < localQuery1.getTriplePatternList().size(); j++) {
				TriplePattern tp = localQuery1.getTriplePatternList().get(j);

				if (findTriplePatternInLocalQuery(tp,
						localQuery2.getTriplePatternList()) != -1) {
					TriplePattern new_tp = new TriplePattern();

					cur_var_id = setTriplePatternInMainPattern(tp, new_tp,
							rewrittenQueryID, cur_var_id, tmpOriginalVarMap,
							tmpRenamedVarMap);

					// commonQuery.addTriplePattern(new_tp);
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).MainPatternGraph
							.addTriplePattern(new_tp);
				}
			}

			for (int j = 0; j < hittingQueryList.size(); j++) {
				Pair<Integer, Integer> p = hittingQueryList.get(j);
				LocalQuery curLocalQuery = allQueryList.get(p.first)
						.getLocalQuery(p.second);
				cur_var_id = 0;

				TreeMap<String, String> tmpOptionalOriginalVarMap = new TreeMap<String, String>();
				TreeMap<String, String> tmpOptionalRenamedVarMap = new TreeMap<String, String>();
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).FilterExpressionList
						.add(new ArrayList<ArrayList<Pair<String, String>>>());
				ArrayList<Pair<String, String>> tmpConstraintList = new ArrayList<Pair<String, String>>();

				tmpOptionalOriginalVarMap.putAll(tmpOriginalVarMap);
				tmpOptionalRenamedVarMap.putAll(tmpRenamedVarMap);
				BGPGraph tmpOptionalPattern = new BGPGraph();

				for (int i = 0; i < curLocalQuery.getTriplePatternList().size(); i++) {

					TriplePattern tp = curLocalQuery.getTriplePatternList()
							.get(i);
					if (findTriplePatternInLocalQuery(
							tp,
							resRewrittenQueryList.get(resRewrittenQueryList
									.size() - 1).MainPatternGraph.triplePatternList) == -1) {
						ArrayList<Pair<String, String>> tmpFilterExpList = new ArrayList<Pair<String, String>>();
						TriplePattern new_tp = new TriplePattern();
						cur_var_id = setTriplePatternInOptionalPattern(tp,
								new_tp, rewrittenQueryID, j, cur_var_id,
								tmpOptionalOriginalVarMap,
								tmpOptionalRenamedVarMap, tmpFilterExpList);
						tmpOptionalPattern.addTriplePattern(new_tp);
						resRewrittenQueryList
								.get(resRewrittenQueryList.size() - 1).FilterExpressionList
								.get(resRewrittenQueryList
										.get(resRewrittenQueryList.size() - 1).FilterExpressionList
										.size() - 1).add(tmpFilterExpList);
						tmpConstraintList.addAll(tmpFilterExpList);
					}
				}
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).OptionalPatternList
						.add(tmpOptionalPattern);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).OriginalVarMapList
						.add(tmpOptionalOriginalVarMap);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).RenamedVarMapList
						.add(tmpOptionalRenamedVarMap);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).ConstraintList
						.add(tmpConstraintList);

			}
			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1).sourceList
					.addAll(localQuery1.getSourceList());
		}

		return resRewrittenQueryList;
	}

	private static int findTriplePatternInLocalQuery(TriplePattern tp,
			ArrayList<TriplePattern> tpList) {
		for (int i = 0; i < tpList.size(); i++) {
			if (tp.getSignature().equals(tpList.get(i).getSignature())) {
				return i;
			}
		}

		return -1;
	}

	private static int setTriplePatternInOptionalPattern(TriplePattern tp,
			TriplePattern new_tp, int rewrittenQueryID, int opt_var_id,
			int pre_var_count, TreeMap<String, String> tmpOriginalVarMap,
			TreeMap<String, String> tmpRenamedVarMap,
			ArrayList<Pair<String, String>> tmpConstraintList) {

		int cur_var_count = pre_var_count;

		new_tp.setSubjectVarTag(tp.isSubjectVar());
		if (tp.isSubjectVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getSubjectStr())) {
				new_tp.setSubjectStr("?rv_" + rewrittenQueryID + "_"
						+ opt_var_id + "_" + cur_var_count);
				cur_var_count++;

				tmpOriginalVarMap.put(tp.getSubjectStr(),
						new_tp.getSubjectStr());
				tmpRenamedVarMap
						.put(new_tp.getSubjectStr(), tp.getSubjectStr());
			}

			new_tp.setSubjectStr(tmpOriginalVarMap.get(tp.getSubjectStr()));
		} else {

			new_tp.setSubjectStr("?rv_" + rewrittenQueryID + "_" + opt_var_id
					+ "_" + cur_var_count);
			cur_var_count++;

			tmpConstraintList.add(new Pair<String, String>(new_tp
					.getSubjectStr(), tp.getSubjectStr()));
		}

		new_tp.setPredicateVarTag(tp.isPredicateVar());
		if (tp.isPredicateVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getPredicateStr())) {
				new_tp.setPredicateStr("?rv_" + rewrittenQueryID + "_"
						+ opt_var_id + "_" + cur_var_count);
				cur_var_count++;

				tmpOriginalVarMap.put(tp.getPredicateStr(),
						new_tp.getPredicateStr());
				tmpRenamedVarMap.put(new_tp.getPredicateStr(),
						tp.getPredicateStr());
			}
			new_tp.setPredicateStr(tmpOriginalVarMap.get(tp.getPredicateStr()));
		} else {
			new_tp.setPredicateStr(tp.getPredicateStr());
		}

		new_tp.setObjectVarTag(tp.isObjectVar());
		if (tp.isObjectVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getObjectStr())) {
				new_tp.setObjectStr("?rv_" + rewrittenQueryID + "_"
						+ opt_var_id + "_" + cur_var_count);
				cur_var_count++;

				tmpOriginalVarMap.put(tp.getObjectStr(), new_tp.getObjectStr());
				tmpRenamedVarMap.put(new_tp.getObjectStr(), tp.getObjectStr());
			}
			new_tp.setObjectStr(tmpOriginalVarMap.get(tp.getObjectStr()));
		} else {
			new_tp.setObjectStr("?rv_" + rewrittenQueryID + "_" + opt_var_id
					+ "_" + cur_var_count);
			cur_var_count++;

			tmpConstraintList.add(new Pair<String, String>(new_tp
					.getObjectStr(), tp.getObjectStr()));
		}

		return cur_var_count;
	}

	private static int setTriplePatternInMainPattern(TriplePattern tp,
			TriplePattern new_tp, int rewrittenQueryID, int pre_var_id,
			TreeMap<String, String> tmpOriginalVarMap,
			TreeMap<String, String> tmpRenamedVarMap) {

		int cur_var_id = pre_var_id;

		new_tp.setSubjectVarTag(tp.isSubjectVar());
		if (tp.isSubjectVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getSubjectStr())) {
				new_tp.setSubjectStr("?rv_" + rewrittenQueryID + "_"
						+ cur_var_id);
				cur_var_id++;
				tmpOriginalVarMap.put(tp.getSubjectStr(),
						new_tp.getSubjectStr());
				tmpRenamedVarMap
						.put(new_tp.getSubjectStr(), tp.getSubjectStr());
			}
			new_tp.setSubjectStr(tmpOriginalVarMap.get(tp.getSubjectStr()));

		} else {
			new_tp.setSubjectStr(tp.getSubjectStr());
		}

		new_tp.setPredicateVarTag(tp.isPredicateVar());
		if (tp.isPredicateVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getPredicateStr())) {
				new_tp.setPredicateStr("?rv_" + rewrittenQueryID + "_"
						+ cur_var_id);
				cur_var_id++;
				tmpOriginalVarMap.put(tp.getPredicateStr(),
						new_tp.getPredicateStr());
				tmpRenamedVarMap.put(new_tp.getPredicateStr(),
						tp.getPredicateStr());
			}
			new_tp.setPredicateStr(tmpOriginalVarMap.get(tp.getPredicateStr()));

		} else {
			new_tp.setPredicateStr(tp.getPredicateStr());
		}

		new_tp.setObjectVarTag(tp.isObjectVar());
		if (tp.isObjectVar()) {
			if (!tmpOriginalVarMap.containsKey(tp.getObjectStr())) {
				new_tp.setObjectStr("?rv_" + rewrittenQueryID + "_"
						+ cur_var_id);
				cur_var_id++;
				tmpOriginalVarMap.put(tp.getObjectStr(), new_tp.getObjectStr());
				tmpRenamedVarMap.put(new_tp.getObjectStr(), tp.getObjectStr());
			}
			new_tp.setObjectStr(tmpOriginalVarMap.get(tp.getObjectStr()));

		} else {
			new_tp.setObjectStr(tp.getObjectStr());
		}

		return cur_var_id;
	}

	public static ArrayList<RewrittenQuery> rewriteQueriesOPTIONAL(
			ArrayList<FullQuery> allQueryList, HittingSet curHittingSet,
			int rewrittenQueryID) {

		ArrayList<RewrittenQuery> resRewrittenQueryList = new ArrayList<RewrittenQuery>();
		resRewrittenQueryList.add(new RewrittenQuery());
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setRewrittenQueryID(rewrittenQueryID);

		// generate the main pattern of rewritten query
		LocalQuery mainPattern = new LocalQuery();
		TriplePattern rewrittenMainPattern = new TriplePattern(
				curHittingSet.getTriplePattern(0));
		int cur_var_id = 0;
		if (rewrittenMainPattern.isSubjectVar()) {
			rewrittenMainPattern.setSubjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isPredicateVar()) {
			rewrittenMainPattern.setPredicateStr("?rv_" + rewrittenQueryID
					+ "_" + cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isObjectVar()) {
			rewrittenMainPattern.setObjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		mainPattern.addTriplePattern(rewrittenMainPattern);
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setMainPattern(mainPattern.getTriplePatternList());

		ArrayList<Pair<Integer, Integer>> curQueryList = curHittingSet
				.getHittingQuerySet();
		ArrayList<Pair<Integer, Integer>> curLocalGroup = new ArrayList<Pair<Integer, Integer>>();
		int optionalPatternIdx = 0;

		for (int i = 0; i < curQueryList.size(); i++) {

			Pair<Integer, Integer> p1 = curQueryList.get(i);
			if (curLocalGroup.contains(p1))
				continue;

			// curRewrittenQuery.addQuery(p1);
			LocalQuery curLocalQuery1 = allQueryList.get(p1.first)
					.getLocalQuery(p1.second);

			Integer[] mainMapping = curLocalQuery1.checkSubgraph(mainPattern);

			// commonQuery is a query graph that is isomorphic to a graph G
			// where G is a combination of main pattern and a graph in an
			// optional expression
			LocalQuery commonQuery = new LocalQuery();
			commonQuery
					.constructCommonQuery(curLocalQuery1, mainPattern,
							mainMapping, rewrittenQueryID, optionalPatternIdx,
							resRewrittenQueryList.get(resRewrittenQueryList
									.size() - 1));
			optionalPatternIdx++;

			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
					.increaseFilterList();
			for (int j = i; j < curQueryList.size(); j++) {
				Pair<Integer, Integer> p2 = curQueryList.get(j);
				LocalQuery curLocalQuery2 = allQueryList.get(p2.first)
						.getLocalQuery(p2.second);

				// a mapping from curLocalQuery2 to commonQuery
				// the subscript i is the id of curLocalQuery2
				// the mapping value mappingState[i] is the id of commonQuery
				Integer[] mappingState = commonQuery
						.checkIsomorphic(curLocalQuery2);
				if (mappingState != null) {
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addQuery(p2);
					curLocalGroup.add(p2);
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addMapping(mappingState, curLocalQuery2,
									commonQuery);

					for (int k = 0; k < curLocalQuery2.getSourceList().size(); k++) {
						resRewrittenQueryList.get(
								resRewrittenQueryList.size() - 1).addSource(
								curLocalQuery2.getSourceList().get(k));
					}

					// since we can only use optional, at each time we can only
					// rewrite one query into the rewritten query
					break;
				}
			}

			// check the filter expression in the last rewritten query. If
			// the filter expression is empty, return true; otherwise,
			// return false.
			if (curLocalGroup.size() != curQueryList.size()
					&& resRewrittenQueryList.get(
							resRewrittenQueryList.size() - 1)
							.isLastFilterEmpty()) {
				resRewrittenQueryList.add(new RewrittenQuery());
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setRewrittenQueryID(rewrittenQueryID + 1);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setMainPattern(mainPattern.getTriplePatternList());
			}
		}

		return resRewrittenQueryList;
	}

	public RewrittenQuery() {
		super();
		this.MainPatternGraph = new BGPGraph();
		this.OptionalPatternList = new ArrayList<BGPGraph>();
		this.FilterExpressionList = new ArrayList<ArrayList<ArrayList<Pair<String, String>>>>();
		this.HittingQuerySet = new ArrayList<Pair<Integer, Integer>>();
		this.sourceList = new ArrayList<Integer>();
		this.bindingNames = new ArrayList<String>();
		this.resultList = new ArrayList<String[]>();
		// this.resultListOfMainPattern = new ArrayList<Result>();
		this.OriginalVarMapList = new ArrayList<TreeMap<String, String>>();
		this.RenamedVarMapList = new ArrayList<TreeMap<String, String>>();
		this.ConstraintList = new ArrayList<ArrayList<Pair<String, String>>>();
		// this.resultListOfHittingQuery = new ArrayList<ArrayList<String[]>>();
	}

	@Override
	public String toString() {
		return "RewrittenQuery [RewrittenQueryID=" + RewrittenQueryID
				+ ", MainPatternGraph=" + MainPatternGraph
				+ ", OptionalPatternList=" + OptionalPatternList
				+ ", HittingQuerySet=" + HittingQuerySet + ", sourceList="
				+ sourceList + ", bindingNames=" + bindingNames
				+ ", resultList.size()=" + resultList.size()
				+ ", FilterExpressionList=" + FilterExpressionList
				+ ", ConstraintList=" + ConstraintList
				+ ", OriginalVarMapList=" + OriginalVarMapList
				+ ", RenamedVarMapList=" + RenamedVarMapList + "]";
	}

	public int getRewrittenQueryID() {
		return RewrittenQueryID;
	}

	public void setRewrittenQueryID(int rewrittenQueryID) {
		RewrittenQueryID = rewrittenQueryID;
	}

	public void addHittingQuery(Pair<Integer, Integer> p) {
		HittingQuerySet.add(p);
	}

	public BGPGraph getMainPatternGraph() {
		return MainPatternGraph;
	}

	public void setMainPatternGraph(BGPGraph mainPatternGraph) {
		MainPatternGraph = mainPatternGraph;
	}

	public ArrayList<BGPGraph> getOptionalPatternList() {
		return OptionalPatternList;
	}

	public void setOptionalPatternList(ArrayList<BGPGraph> optionalPatternList) {
		OptionalPatternList = optionalPatternList;
	}

	public ArrayList<Pair<Integer, Integer>> getHittingQuerySet() {
		return HittingQuerySet;
	}

	public void setHittingQuerySet(
			ArrayList<Pair<Integer, Integer>> hittingQuerySet) {
		HittingQuerySet = hittingQuerySet;
	}

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

	public ArrayList<ArrayList<ArrayList<Pair<String, String>>>> getFilterExpressionList() {
		return FilterExpressionList;
	}

	public void setFilterExpressionList(
			ArrayList<ArrayList<ArrayList<Pair<String, String>>>> filterExpressionList) {
		FilterExpressionList = filterExpressionList;
	}

	public void mergeSourceList(ArrayList<Integer> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			int curSource = otherList.get(i);
			if (!this.sourceList.contains(curSource)) {
				this.sourceList.add(curSource);
			}
		}
	}

	public String toSPARQLString() {
		String sparqlStr = "select * where { ";

		for (int i = 0; i < this.MainPatternGraph.getTriplePatternList().size(); i++) {
			sparqlStr += this.MainPatternGraph.getTriplePatternList().get(i)
					.toTriplePatternString()
					+ " . \n";
		}
		for (int i = 0; i < this.getOptionalPatternList().size(); i++) {
			ArrayList<TriplePattern> curBGPTriplePatternList = this
					.getOptionalPatternList().get(i).getTriplePatternList();

			if (0 != curBGPTriplePatternList.size()) {
				if (this.getOptionalPatternList().size() > 1) {
					sparqlStr += "OPTIONAL { ";
				}
				for (int j = 0; j < curBGPTriplePatternList.size(); j++) {
					sparqlStr += curBGPTriplePatternList.get(j)
							.toTriplePatternString() + " . \n";
				}

				if (this.getFilterExpressionList().size() != 0) {
					sparqlStr += "FILTER ( ";
					int count = 0;
					for (int j = 0; j < this.getFilterExpressionList().get(i)
							.size(); j++) {
						ArrayList<Pair<String, String>> filterExpList = this
								.getFilterExpressionList().get(i).get(j);

						String filterStr = "";
						for (int expIdx = 0; expIdx < filterExpList.size(); expIdx++) {
							Pair<String, String> p = filterExpList.get(expIdx);
							if (expIdx == 0) {
								filterStr += p.first + " = " + p.second;
							} else {
								filterStr += " && " + p.first + " = "
										+ p.second;
							}
						}

						if (!filterStr.equals("")) {
							if (count == 0) {
								sparqlStr += " ( " + filterStr + " ) ";
								count++;
							} else {
								sparqlStr += " || ( " + filterStr + " ) ";
							}
						}

					}
					if (!sparqlStr.endsWith("FILTER ( ")) {
						sparqlStr += " ) ";
					} else {
						sparqlStr = sparqlStr.substring(0,
								sparqlStr.length() - 9);
					}
				}
				if (this.getOptionalPatternList().size() > 1) {
					sparqlStr += " } \n";
				}
			}
		}
		sparqlStr += " } ";

		return sparqlStr;
	}

	public void addResult(String[] r) {
		this.resultList.add(r);
	}

	public ArrayList<TreeMap<String, String>> getOriginalVarMapList() {
		return OriginalVarMapList;
	}

	public void setOriginalVarMapList(
			ArrayList<TreeMap<String, String>> originalVarMapList) {
		OriginalVarMapList = originalVarMapList;
	}

	public ArrayList<TreeMap<String, String>> getRenamedVarMapList() {
		return RenamedVarMapList;
	}

	public void setRenamedVarMapList(
			ArrayList<TreeMap<String, String>> renamedVarMapList) {
		RenamedVarMapList = renamedVarMapList;
	}

	public ArrayList<String> getBindingNames() {
		return bindingNames;
	}

	public void setBindingNames(ArrayList<String> bindingNames) {
		this.bindingNames = bindingNames;
	}

	public void addAllBindingNames(List<String> bindingNames) {
		for (int i = 0; i < bindingNames.size(); i++) {
			this.bindingNames.add("?" + bindingNames.get(i));
		}
	}

	public void addMapping(Integer[] mappingState, LocalQuery curLocalQuery2,
			LocalQuery commonQuery) {
		TreeMap<String, String> tmpRenamedVarMap = new TreeMap<String, String>();
		TreeMap<String, String> tmpOriginalVarMap = new TreeMap<String, String>();

		ArrayList<Pair<String, String>> filterExpList = new ArrayList<Pair<String, String>>();

		for (int i = 0; i < mappingState.length; i++) {
			String curOriginalStr = curLocalQuery2.getLocalBGP().IDVertexmap
					.get(i);
			String curRenamedStr = commonQuery.getLocalBGP().IDVertexmap
					.get(mappingState[i]);

			if (curOriginalStr.startsWith("?") && curRenamedStr.startsWith("?")) {
				tmpOriginalVarMap.put(curOriginalStr, curRenamedStr);
				tmpRenamedVarMap.put(curRenamedStr, curOriginalStr);
			} else if (!curOriginalStr.startsWith("?")
					&& curRenamedStr.startsWith("?")) {
				if (!this.MainPatternGraph.triplePatternList.toString()
						.contains(curOriginalStr)) {
					filterExpList.add(new Pair<String, String>(curRenamedStr,
							curOriginalStr));
				}
			}
		}

		this.RenamedVarMapList.add(tmpRenamedVarMap);
		this.OriginalVarMapList.add(tmpOriginalVarMap);
		this.FilterExpressionList.get(this.FilterExpressionList.size() - 1)
				.add(filterExpList);
		this.ConstraintList.add(filterExpList);
	}

	public void increaseFilterList() {
		this.FilterExpressionList
				.add(new ArrayList<ArrayList<Pair<String, String>>>());
	}

	public ArrayList<ArrayList<Pair<String, String>>> getConstraintList() {
		return ConstraintList;
	}

	public void setConstraintList(
			ArrayList<ArrayList<Pair<String, String>>> constraintList) {
		ConstraintList = constraintList;
	}

	public void addSource(int sourceID) {
		if (!this.sourceList.contains(sourceID)) {
			this.sourceList.add(sourceID);
		}
	}

	public boolean containsQuery(Pair<Integer, Integer> p1) {
		return this.HittingQuerySet.contains(p1);
	}

	public void addQuery(Pair<Integer, Integer> p1) {
		this.HittingQuerySet.add(p1);
	}

	public void setMainPattern(ArrayList<TriplePattern> triplePatternList) {
		this.MainPatternGraph.setTriplePatternList(triplePatternList);
		for (int i = 0; i < triplePatternList.size(); i++) {
			TriplePattern curTriplePattern = triplePatternList.get(i);
			if (curTriplePattern.isSubjectVar()
					&& !this.MainPatternGraph.VertexIDmap
							.containsKey(curTriplePattern.getSubjectStr())) {
				this.MainPatternGraph.IDVertexmap.put(
						this.MainPatternGraph.IDVertexmap.size(),
						curTriplePattern.getSubjectStr());
				this.MainPatternGraph.VertexIDmap.put(
						curTriplePattern.getSubjectStr(),
						this.MainPatternGraph.VertexIDmap.size());
			}

			if (curTriplePattern.isObjectVar()
					&& !this.MainPatternGraph.VertexIDmap
							.containsKey(curTriplePattern.getObjectStr())) {
				this.MainPatternGraph.IDVertexmap.put(
						this.MainPatternGraph.IDVertexmap.size(),
						curTriplePattern.getObjectStr());
				this.MainPatternGraph.VertexIDmap.put(
						curTriplePattern.getObjectStr(),
						this.MainPatternGraph.VertexIDmap.size());
			}
		}
	}

	public void addTriplePatternInOptional(TriplePattern curTriplePattern,
			int subjectMapping, int objectMapping) {
		BGPGraph curOptionalBGP = this.OptionalPatternList
				.get(this.OptionalPatternList.size() - 1);
		curOptionalBGP.triplePatternList.add(curTriplePattern);

		if (!curOptionalBGP.IDVertexmap.containsKey(subjectMapping)) {
			curOptionalBGP.IDVertexmap.put(subjectMapping,
					curTriplePattern.getSubjectStr());
			curOptionalBGP.VertexIDmap.put(curTriplePattern.getSubjectStr(),
					subjectMapping);
		}

		if (!curOptionalBGP.IDVertexmap.containsKey(objectMapping)) {
			curOptionalBGP.IDVertexmap.put(objectMapping,
					curTriplePattern.getObjectStr());
			curOptionalBGP.VertexIDmap.put(curTriplePattern.getObjectStr(),
					objectMapping);
		}

		if (!curOptionalBGP.AdjacencyMatrix.containsKey(subjectMapping)) {
			curOptionalBGP.AdjacencyMatrix.put(subjectMapping,
					new TreeMap<Integer, String>());
		}
		curOptionalBGP.AdjacencyMatrix.get(subjectMapping).put(objectMapping,
				curTriplePattern.getPredicateStr());
	}

	public int findSameFullQuery(Pair<Integer, Integer> p1) {
		for (int i = 0; i < this.HittingQuerySet.size(); i++) {
			// Pair<Integer, Integer> p = this.HittingQuerySet.get(i);
			if (this.HittingQuerySet.get(i).first == p1.first) {
				return i;
			}
		}

		return -1;
	}

	public int JoinWithFullQuery(ArrayList<FullQuery> allQueryList) {
		int res_count = 0;
		for (int i = 0; i < this.HittingQuerySet.size(); i++) {
			Pair<Integer, Integer> p1 = this.HittingQuerySet.get(i);

			ArrayList<String[]> tmpResList = new ArrayList<String[]>();
			FullQuery curFullQuery = allQueryList.get(p1.first);
			TreeMap<String, Integer> curVarIDMap = curFullQuery.getVarIDMap();
			TreeMap<String, String> curRenamedMap = this.RenamedVarMapList
					.get(i);
			int[] mappingPosArr = new int[this.bindingNames.size()];
			for (int j = 0; j < this.bindingNames.size(); j++) {
				String varStr = curRenamedMap.get(this.bindingNames.get(j));
				mappingPosArr[j] = curVarIDMap.get(varStr);
			}

			for (int k = 0; k < this.resultList.size(); k++) {
				String[] tmpRes = new String[curVarIDMap.size()];
				Arrays.fill(tmpRes, "");
				for (int j = 0; j < this.bindingNames.size(); j++) {
					tmpRes[mappingPosArr[j]] = this.resultList.get(k)[j];
				}
				tmpResList.add(tmpRes);
			}

			if (curFullQuery.getResultList().size() > 0) {
				int joining_pos = 0;
				for (int k = 0; k < curFullQuery.getVarIDMap().size(); k++) {
					if (!curFullQuery.getResult(0)[k].equals("")
							&& !tmpResList.get(0)[k].equals("")) {
						joining_pos = k;
						break;
					}
				}
				curFullQuery.Join(tmpResList, joining_pos);
			}
			res_count += curFullQuery.getResultList().size();
		}

		return res_count;

	}

	public void printResultsInLog(String fileStr) throws FileNotFoundException {
		PrintStream out = new PrintStream(new File(fileStr));

		for (int i = 0; i < this.resultList.size(); i++) {
			for (int j = 0; j < this.bindingNames.size(); j++) {
				out.print(this.bindingNames.get(j) + "\t"
						+ this.resultList.get(i)[j] + "\t");
			}
			out.println();
		}

		out.flush();
		out.close();

	}

	public void distributeResultsInLocalQuery(ArrayList<FullQuery> allQueryList) {
		for (int i = 0; i < this.HittingQuerySet.size(); i++) {
			Pair<Integer, Integer> p1 = this.HittingQuerySet.get(i);

			FullQuery curFullQuery = allQueryList.get(p1.first);
			LocalQuery curLocalQuery = allQueryList.get(p1.first)
					.getLocalQuery(p1.second);

			TreeMap<String, Integer> curVarIDMap = curFullQuery.getVarIDMap();
			TreeMap<String, String> tmpRenamedVarMap = this.RenamedVarMapList
					.get(i);
			int[] mappingPosArr = new int[this.bindingNames.size()];
			String[] mappingConstraintPosArr = new String[this.bindingNames
					.size()];
			Arrays.fill(mappingPosArr, -1);
			Arrays.fill(mappingConstraintPosArr, "");

			TreeMap<String, String> tmpConstraintMap = new TreeMap<String, String>();

			for (int k = 0; k < this.ConstraintList.get(i).size(); k++) {
				tmpConstraintMap.put(this.ConstraintList.get(i).get(k).first,
						this.ConstraintList.get(i).get(k).second);
			}

			for (int j = 0; j < this.bindingNames.size(); j++) {
				String varStr = this.bindingNames.get(j);
				if (tmpRenamedVarMap.containsKey(varStr)) {
					mappingPosArr[j] = curVarIDMap.get(tmpRenamedVarMap
							.get(varStr));
				}
				if (tmpConstraintMap.containsKey(varStr)) {
					mappingConstraintPosArr[j] = tmpConstraintMap.get(varStr);
					if (mappingConstraintPosArr[j].startsWith("<")
							&& mappingConstraintPosArr[j].endsWith(">")) {
						mappingConstraintPosArr[j] = mappingConstraintPosArr[j]
								.substring(1,
										mappingConstraintPosArr[j].length() - 1);
					}
				}
			}

			for (int k = 0; k < this.resultList.size(); k++) {
				String[] tmpRes = new String[curVarIDMap.size()];
				Arrays.fill(tmpRes, "");
				int tag = 0;
				for (int j = 0; j < this.bindingNames.size(); j++) {
					if (mappingPosArr[j] != -1) {
						tmpRes[mappingPosArr[j]] = this.resultList.get(k)[j];
						if (tmpRes[mappingPosArr[j]].equals("")) {
							tag = 1;
							break;
						}
					} else if (!mappingConstraintPosArr[j].equals("")) {
						if (!mappingConstraintPosArr[j].equals(this.resultList
								.get(k)[j])) {
							tag = 1;
							break;
						}
					}
				}
				if (tag == 0) {
					curLocalQuery.addResult(tmpRes);
				}
			}
		}
	}

	public void removeLocalQuery(int pos) {
		this.HittingQuerySet.remove(pos);
		this.RenamedVarMapList.remove(pos);
		this.OriginalVarMapList.remove(pos);
	}

	public void distributeResultsInHittingQuery(
			ArrayList<FullQuery> allQueryList) {
		for (int i = 0; i < this.HittingQuerySet.size(); i++) {
			Pair<Integer, Integer> p1 = this.HittingQuerySet.get(i);

			FullQuery curFullQuery = allQueryList.get(p1.first);
			ArrayList<String[]> tmpResList = new ArrayList<String[]>();

			TreeMap<String, Integer> curVarIDMap = curFullQuery.getVarIDMap();
			TreeMap<String, String> tmpRenamedVarMap = this.RenamedVarMapList
					.get(i);
			int[] mappingPosArr = new int[this.bindingNames.size()];
			String[] mappingConstraintPosArr = new String[this.bindingNames
					.size()];
			Arrays.fill(mappingPosArr, -1);
			Arrays.fill(mappingConstraintPosArr, "");

			TreeMap<String, String> tmpConstraintMap = new TreeMap<String, String>();

			for (int k = 0; k < this.ConstraintList.get(i).size(); k++) {
				tmpConstraintMap.put(this.ConstraintList.get(i).get(k).first,
						this.ConstraintList.get(i).get(k).second);
			}

			for (int j = 0; j < this.bindingNames.size(); j++) {
				String varStr = this.bindingNames.get(j);
				if (tmpRenamedVarMap.containsKey(varStr)) {
					mappingPosArr[j] = curVarIDMap.get(tmpRenamedVarMap
							.get(varStr));
				}
				if (tmpConstraintMap.containsKey(varStr)) {
					mappingConstraintPosArr[j] = tmpConstraintMap.get(varStr);
					if (mappingConstraintPosArr[j].startsWith("<")
							&& mappingConstraintPosArr[j].endsWith(">")) {
						mappingConstraintPosArr[j] = mappingConstraintPosArr[j]
								.substring(1,
										mappingConstraintPosArr[j].length() - 1);
					}
				}
			}

			for (int k = 0; k < this.resultList.size(); k++) {
				String[] tmpRes = new String[curVarIDMap.size()];
				Arrays.fill(tmpRes, "");
				int tag = 0;
				for (int j = 0; j < this.bindingNames.size(); j++) {
					if (mappingPosArr[j] != -1) {
						tmpRes[mappingPosArr[j]] = this.resultList.get(k)[j];
						if (tmpRes[mappingPosArr[j]].equals("")) {
							tag = 1;
							break;
						}
					} else if (!mappingConstraintPosArr[j].equals("")) {
						if (!mappingConstraintPosArr[j].equals(this.resultList
								.get(k)[j])) {
							tag = 1;
							break;
						}
					}
				}
				if (tag == 0) {
					tmpResList.add(tmpRes);
				}
			}
			// this.resultListOfHittingQuery.add(tmpResList);
		}
	}

	public static ArrayList<RewrittenQuery> rewriteQueries(
			ArrayList<FullQuery> allQueryList, HittingSet curHittingSet,
			int rewrittenQueryID) {

		ArrayList<RewrittenQuery> resRewrittenQueryList = new ArrayList<RewrittenQuery>();
		resRewrittenQueryList.add(new RewrittenQuery());
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setRewrittenQueryID(rewrittenQueryID);

		// generate the main pattern of rewritten query
		LocalQuery mainPattern = new LocalQuery();
		TriplePattern rewrittenMainPattern = new TriplePattern(
				curHittingSet.getTriplePattern(0));
		int cur_var_id = 0;
		if (rewrittenMainPattern.isSubjectVar()) {
			rewrittenMainPattern.setSubjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isPredicateVar()) {
			rewrittenMainPattern.setPredicateStr("?rv_" + rewrittenQueryID
					+ "_" + cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isObjectVar()) {
			rewrittenMainPattern.setObjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		mainPattern.addTriplePattern(rewrittenMainPattern);
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setMainPattern(mainPattern.getTriplePatternList());

		ArrayList<Pair<Integer, Integer>> curQueryList = curHittingSet
				.getHittingQuerySet();
		ArrayList<Pair<Integer, Integer>> curLocalGroup = new ArrayList<Pair<Integer, Integer>>();
		int optionalPatternIdx = 0;

		for (int i = 0; i < curQueryList.size(); i++) {

			Pair<Integer, Integer> p1 = curQueryList.get(i);
			if (curLocalGroup.contains(p1))
				continue;

			// curRewrittenQuery.addQuery(p1);
			LocalQuery curLocalQuery1 = allQueryList.get(p1.first)
					.getLocalQuery(p1.second);

			Integer[] mainMapping = curLocalQuery1.checkSubgraph(mainPattern);

			// commonQuery is a query graph that is isomorphic to a graph G
			// where G is a combination of main pattern and a graph in an
			// optional expression
			LocalQuery commonQuery = new LocalQuery();
			commonQuery
					.constructCommonQuery(curLocalQuery1, mainPattern,
							mainMapping, rewrittenQueryID, optionalPatternIdx,
							resRewrittenQueryList.get(resRewrittenQueryList
									.size() - 1));
			optionalPatternIdx++;

			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
					.increaseFilterList();
			for (int j = i; j < curQueryList.size(); j++) {
				Pair<Integer, Integer> p2 = curQueryList.get(j);
				LocalQuery curLocalQuery2 = allQueryList.get(p2.first)
						.getLocalQuery(p2.second);

				// a mapping from curLocalQuery2 to commonQuery
				// the subscript i is the id of curLocalQuery2
				// the mapping value mappingState[i] is the id of commonQuery
				Integer[] mappingState = commonQuery
						.checkIsomorphic(curLocalQuery2);
				if (mappingState != null) {
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addQuery(p2);
					curLocalGroup.add(p2);
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addMapping(mappingState, curLocalQuery2,
									commonQuery);

					for (int k = 0; k < curLocalQuery2.getSourceList().size(); k++) {
						resRewrittenQueryList.get(
								resRewrittenQueryList.size() - 1).addSource(
								curLocalQuery2.getSourceList().get(k));
					}
				}
			}

			// check the filter expression in the last rewritten query. If
			// the filter expression is empty, return true; otherwise,
			// return false.
			if (curLocalGroup.size() != curQueryList.size()
					&& resRewrittenQueryList.get(
							resRewrittenQueryList.size() - 1)
							.isLastFilterEmpty()) {
				resRewrittenQueryList.add(new RewrittenQuery());
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setRewrittenQueryID(rewrittenQueryID + 1);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setMainPattern(mainPattern.getTriplePatternList());
			}
		}

		return resRewrittenQueryList;
	}

	public static ArrayList<RewrittenQuery> rewriteQueriesFILTER(
			ArrayList<FullQuery> allQueryList, HittingSet curHittingSet,
			int rewrittenQueryID) {

		ArrayList<RewrittenQuery> resRewrittenQueryList = new ArrayList<RewrittenQuery>();
		resRewrittenQueryList.add(new RewrittenQuery());
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setRewrittenQueryID(rewrittenQueryID);

		// generate the main pattern of rewritten query
		LocalQuery mainPattern = new LocalQuery();
		TriplePattern rewrittenMainPattern = new TriplePattern(
				curHittingSet.getTriplePattern(0));
		int cur_var_id = 0;
		if (rewrittenMainPattern.isSubjectVar()) {
			rewrittenMainPattern.setSubjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isPredicateVar()) {
			rewrittenMainPattern.setPredicateStr("?rv_" + rewrittenQueryID
					+ "_" + cur_var_id);
			cur_var_id++;
		}

		if (rewrittenMainPattern.isObjectVar()) {
			rewrittenMainPattern.setObjectStr("?rv_" + rewrittenQueryID + "_"
					+ cur_var_id);
			cur_var_id++;
		}

		mainPattern.addTriplePattern(rewrittenMainPattern);
		resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
				.setMainPattern(mainPattern.getTriplePatternList());

		ArrayList<Pair<Integer, Integer>> curQueryList = curHittingSet
				.getHittingQuerySet();
		ArrayList<Pair<Integer, Integer>> curLocalGroup = new ArrayList<Pair<Integer, Integer>>();
		int optionalPatternIdx = 0;

		for (int i = 0; i < curQueryList.size(); i++) {

			Pair<Integer, Integer> p1 = curQueryList.get(i);
			if (curLocalGroup.contains(p1))
				continue;

			// curRewrittenQuery.addQuery(p1);
			LocalQuery curLocalQuery1 = allQueryList.get(p1.first)
					.getLocalQuery(p1.second);

			Integer[] mainMapping = curLocalQuery1.checkSubgraph(mainPattern);

			// commonQuery is a query graph that is isomorphic to a graph G
			// where G is a combination of main pattern and a graph in an
			// optional expression
			LocalQuery commonQuery = new LocalQuery();
			commonQuery
					.constructCommonQuery(curLocalQuery1, mainPattern,
							mainMapping, rewrittenQueryID, optionalPatternIdx,
							resRewrittenQueryList.get(resRewrittenQueryList
									.size() - 1));
			optionalPatternIdx++;

			resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
					.increaseFilterList();
			for (int j = i; j < curQueryList.size(); j++) {
				Pair<Integer, Integer> p2 = curQueryList.get(j);
				LocalQuery curLocalQuery2 = allQueryList.get(p2.first)
						.getLocalQuery(p2.second);

				// a mapping from curLocalQuery2 to commonQuery
				// the subscript i is the id of curLocalQuery2
				// the mapping value mappingState[i] is the id of commonQuery
				Integer[] mappingState = commonQuery
						.checkIsomorphic(curLocalQuery2);
				if (mappingState != null) {
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addQuery(p2);
					curLocalGroup.add(p2);
					resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
							.addMapping(mappingState, curLocalQuery2,
									commonQuery);

					for (int k = 0; k < curLocalQuery2.getSourceList().size(); k++) {
						resRewrittenQueryList.get(
								resRewrittenQueryList.size() - 1).addSource(
								curLocalQuery2.getSourceList().get(k));
					}
				}
			}

			// add a new local query.
			if (curLocalGroup.size() != curQueryList.size()) {
				resRewrittenQueryList.add(new RewrittenQuery());
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setRewrittenQueryID(rewrittenQueryID + 1);
				resRewrittenQueryList.get(resRewrittenQueryList.size() - 1)
						.setMainPattern(mainPattern.getTriplePatternList());
			}
		}

		return resRewrittenQueryList;
	}

	private boolean isLastFilterEmpty() {
		int filter_count = 0;

		for (int j = 0; j < this.FilterExpressionList.get(
				this.FilterExpressionList.size() - 1).size(); j++) {
			filter_count += this.FilterExpressionList
					.get(this.FilterExpressionList.size() - 1).get(j).size();
		}

		return filter_count == 0;
	}
}
