package Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class IntermediateResults {

	private ArrayList<ArrayList<String>> commonVarMappingList;
	private HashSet<String> mainVarSet;
	private TreeMap<String, Integer> Var2PosMap;
	private TreeMap<Integer, ArrayList<String>> Pos2VarMap;
	private ArrayList<ArrayList<Pair<Integer, Integer>>> HittingQuerySetGroup;
	private ArrayList<String[]> resultList;
	// private ArrayList<Result> resultListOfMainPattern;
	// private ArrayList<ArrayList<String[]>> resultListOfHittingQuery;

	// mapping from old variable name to new variable name
	private ArrayList<TreeMap<String, ArrayList<String>>> OriginalVarMapList;
	// mapping from new variable name to old variable name
	private ArrayList<TreeMap<String, String>> RenamedVarMapList;

	private ArrayList<ArrayList<Pair<String, String>>> ConstraintList;

	public IntermediateResults() {
		super();
		commonVarMappingList = new ArrayList<ArrayList<String>>();
		mainVarSet = new HashSet<String>();
		Var2PosMap = new TreeMap<String, Integer>();
		Pos2VarMap = new TreeMap<Integer, ArrayList<String>>();
		HittingQuerySetGroup = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
		RenamedVarMapList = new ArrayList<TreeMap<String, String>>();
		OriginalVarMapList = new ArrayList<TreeMap<String, ArrayList<String>>>();
		resultList = new ArrayList<String[]>();
		// resultListOfMainPattern = new ArrayList<Result>();
		// resultListOfHittingQuery = new ArrayList<ArrayList<String[]>>();
		ConstraintList = new ArrayList<ArrayList<Pair<String, String>>>();
	}

	public IntermediateResults(RewrittenQuery o) {
		commonVarMappingList = new ArrayList<ArrayList<String>>();
		HittingQuerySetGroup = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
		for (int i = 0; i < o.getHittingQuerySet().size(); i++) {
			HittingQuerySetGroup.add(new ArrayList<Pair<Integer, Integer>>());
			HittingQuerySetGroup.get(HittingQuerySetGroup.size() - 1).add(
					o.getHittingQuerySet().get(i));
		}
		RenamedVarMapList = o.getRenamedVarMapList();

		mainVarSet = new HashSet<String>();
		mainVarSet.addAll(o.getMainPatternGraph().getVertexIDmap().keySet());

		// resultListOfMainPattern = o.getResultListOfMainPattern();
		// resultListOfHittingQuery = o.getResultListOfHittingQuery();

		OriginalVarMapList = new ArrayList<TreeMap<String, ArrayList<String>>>();
		for (int i = 0; i < o.getOriginalVarMapList().size(); i++) {
			TreeMap<String, String> tmpMap = o.getOriginalVarMapList().get(i);
			TreeMap<String, ArrayList<String>> newMap = new TreeMap<String, ArrayList<String>>();
			Iterator<Entry<String, String>> iter = tmpMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> e = iter.next();
				String originalStr = e.getKey();
				String renamedStr = e.getValue();
				newMap.put(originalStr, new ArrayList<String>());
				newMap.get(originalStr).add(renamedStr);
			}
			OriginalVarMapList.add(newMap);
		}
		resultList = o.getResultList();

		Var2PosMap = new TreeMap<String, Integer>();
		Pos2VarMap = new TreeMap<Integer, ArrayList<String>>();
		ArrayList<String> tmpBindingNames = o.getBindingNames();
		for (int i = 0; i < tmpBindingNames.size(); i++) {
			Var2PosMap.put(tmpBindingNames.get(i), i);
			Pos2VarMap.put(i, new ArrayList<String>());
			Pos2VarMap.get(i).add(tmpBindingNames.get(i));
		}
		ConstraintList = o.getConstraintList();
	}

	@Override
	public String toString() {
		return "IntermediateResults [commonVarMappingList="
				+ commonVarMappingList + ", Var2PosMap=" + Var2PosMap
				+ ", Pos2VarMap=" + Pos2VarMap + ", HittingQuerySetGroup="
				+ HittingQuerySetGroup + ", resultList.size()="
				+ resultList.size() + ", OriginalVarMapList="
				+ OriginalVarMapList + ", RenamedVarMapList="
				+ RenamedVarMapList + ", ConstraintList=" + ConstraintList
				+ "]";
	}

	// public ArrayList<Result> getResultListOfMainPattern() {
	// return resultListOfMainPattern;
	// }
	//
	// public void setResultListOfMainPattern(
	// ArrayList<Result> resultListOfMainPattern) {
	// this.resultListOfMainPattern = resultListOfMainPattern;
	// }
	//
	// public ArrayList<ArrayList<String[]>> getResultListOfHittingQuery() {
	// return resultListOfHittingQuery;
	// }
	//
	// public ArrayList<String[]> getResultListOfHittingQuery(int idx) {
	// return resultListOfHittingQuery.get(idx);
	// }
	//
	// public void setResultListOfHittingQuery(
	// ArrayList<ArrayList<String[]>> resultListOfHittingQuery) {
	// this.resultListOfHittingQuery = resultListOfHittingQuery;
	// }

	public void addResult(String[] r) {
		this.resultList.add(r);
	}

	public void insertHittingQueryPair(Pair<Integer, Integer> p1,
			Pair<Integer, Integer> p2) {
		this.HittingQuerySetGroup.add(new ArrayList<Pair<Integer, Integer>>());
		this.HittingQuerySetGroup.get(HittingQuerySetGroup.size() - 1).add(p1);
		this.HittingQuerySetGroup.get(HittingQuerySetGroup.size() - 1).add(p2);
	}

	public ArrayList<ArrayList<Pair<Integer, Integer>>> getHittingQuerySetGroup() {
		return HittingQuerySetGroup;
	}

	public void setHittingQuerySetGroup(
			ArrayList<ArrayList<Pair<Integer, Integer>>> hittingQuerySetGroup) {
		HittingQuerySetGroup = hittingQuerySetGroup;
	}

	public int getHittingFullQueryIdx(int i) {
		return HittingQuerySetGroup.get(i).get(0).first;
	}

	public void putOriginalVarMap(TreeMap<String, String> OriginalVarMap1,
			TreeMap<String, String> OriginalVarMap2) {
		TreeMap<String, ArrayList<String>> tmpOriginalVarMap = new TreeMap<String, ArrayList<String>>();

		Iterator<Entry<String, String>> iter0 = OriginalVarMap1.entrySet()
				.iterator();
		while (iter0.hasNext()) {
			Entry<String, String> e = iter0.next();
			String originalStr = e.getKey();
			String renamedStr = e.getValue();

			if (!tmpOriginalVarMap.containsKey(originalStr)) {
				tmpOriginalVarMap.put(originalStr, new ArrayList<String>());
			}

			tmpOriginalVarMap.get(originalStr).add(renamedStr);
		}

		Iterator<Entry<String, String>> iter1 = OriginalVarMap2.entrySet()
				.iterator();
		while (iter1.hasNext()) {
			Entry<String, String> e = iter1.next();
			String originalStr = e.getKey();
			String renamedStr = e.getValue();

			if (!tmpOriginalVarMap.containsKey(originalStr)) {
				tmpOriginalVarMap.put(originalStr, new ArrayList<String>());
			}

			tmpOriginalVarMap.get(originalStr).add(renamedStr);
		}

		this.OriginalVarMapList.add(tmpOriginalVarMap);
	}

	public TreeMap<String, ArrayList<String>> getOriginalVarMap(int idx) {
		return this.OriginalVarMapList.get(idx);
	}

	public void putRenamedVarMap(TreeMap<String, String> RenamedVarMap1,
			TreeMap<String, String> RenamedVarMap2) {
		TreeMap<String, String> tmpRenamedVarMap = new TreeMap<String, String>();
		tmpRenamedVarMap.putAll(RenamedVarMap1);
		tmpRenamedVarMap.putAll(RenamedVarMap2);
		this.RenamedVarMapList.add(tmpRenamedVarMap);
	}

	public TreeMap<String, String> getRenamedVarMap(int idx) {
		return this.RenamedVarMapList.get(idx);
	}

	public void setOriginalVarMappings(RewrittenQuery thisRewrittenQuery,
			RewrittenQuery otherRewrittenQuery, int thisQueryIdx,
			int otherQueryIdx) {

		this.putOriginalVarMap(
				thisRewrittenQuery.getOriginalVarMapList().get(thisQueryIdx),
				otherRewrittenQuery.getOriginalVarMapList().get(otherQueryIdx));

		this.putRenamedVarMap(
				thisRewrittenQuery.getRenamedVarMapList().get(thisQueryIdx),
				otherRewrittenQuery.getRenamedVarMapList().get(otherQueryIdx));
	}

	public void assignResults(ArrayList<String[]> n_relation) {
		this.resultList.clear();
		this.resultList.addAll(n_relation);
	}

	public void distributeResultsInFullQuery(ArrayList<FullQuery> allQueryList) {

		for (int i = 0; i < this.getHittingQuerySetGroup().size(); i++) {
			int fullQueryIdx = this.getHittingFullQueryIdx(i);

			FullQuery curFullQuery = allQueryList.get(fullQueryIdx);
			curFullQuery.clearResults();
			TreeMap<String, Integer> curVarIDMap = curFullQuery.getVarIDMap();
			TreeMap<String, String> tmpRenamedVarMap = this.getRenamedVarMap(i);
			int[] mappingPosArr = new int[this.getResultLen()];
			String[] mappingFilterArr = new String[this.getResultLen()];
			Arrays.fill(mappingPosArr, -1);
			Arrays.fill(mappingFilterArr, "");

			TreeMap<String, String> constraintVarMap = new TreeMap<String, String>();
			for (int j = 0; j < this.ConstraintList.get(i).size(); j++) {
				constraintVarMap.put(this.ConstraintList.get(i).get(j).first,
						this.ConstraintList.get(i).get(j).second);
			}

			for (int j = 0; j < this.getResultLen(); j++) {
				String varStr = this.Pos2VarMap.get(j).get(0);
				if (tmpRenamedVarMap.containsKey(varStr)) {
					mappingPosArr[j] = curVarIDMap.get(tmpRenamedVarMap
							.get(varStr));
				} else if (constraintVarMap.containsKey(varStr)) {
					mappingFilterArr[j] = constraintVarMap.get(varStr);
					if (mappingFilterArr[j].startsWith("<")
							&& mappingFilterArr[j].endsWith(">")) {
						mappingFilterArr[j] = mappingFilterArr[j].substring(1,
								mappingFilterArr[j].length() - 1);
					}
				}
			}

			TreeSet<String> resultSet = new TreeSet<String>();
			for (int k = 0; k < this.resultList.size(); k++) {
				String[] tmpRes = new String[curVarIDMap.size()];
				Arrays.fill(tmpRes, "");
				int tag = 0;
				for (int j = 0; j < this.getResultLen(); j++) {
					if (mappingPosArr[j] != -1) {
						tmpRes[mappingPosArr[j]] = this.resultList.get(k)[j];
						if (tmpRes[mappingPosArr[j]].equals("")) {
							tag = 1;
							break;
						}
					} else if (!mappingFilterArr[j].equals("")) {
						if (!mappingFilterArr[j]
								.equals(this.resultList.get(k)[j])) {
							tag = 1;
							break;
						}
					}
				}

				if (tag == 0) {
					resultSet.add(Arrays.toString(tmpRes));
				}
			}
			curFullQuery.addAllResult(resultSet);
		}

	}

	public static LinkedList<IntermediateResults> Join(
			IntermediateResults preIntermediateResults,
			IntermediateResults curIntermediateResults) {

		LinkedList<IntermediateResults> newIntermediateResultsList = new LinkedList<IntermediateResults>();

		while (preIntermediateResults.HittingQuerySetGroup.size() != 0) {
			IntermediateResults newIntermediateResults = null;
			ArrayList<Integer> thisQueryIdxList = new ArrayList<Integer>();
			ArrayList<Integer> otherQueryIdxList = new ArrayList<Integer>();

			int tag = 0;
			for (int thisQueryIdx = 0; thisQueryIdx < preIntermediateResults.HittingQuerySetGroup
					.size(); thisQueryIdx++) {
				Pair<Integer, Integer> p1 = preIntermediateResults.HittingQuerySetGroup
						.get(thisQueryIdx).get(0);
				int otherQueryIdx = curIntermediateResults
						.findSameFullQuery(p1);

				if (otherQueryIdx != -1) {
					if (newIntermediateResults == null) {

						thisQueryIdxList.add(thisQueryIdx);
						otherQueryIdxList.add(otherQueryIdx);

						// if preIntermediateResults can join with
						// curIntermediateResults by optimized distributed join,
						// then return true
						newIntermediateResults = IntermediateResults
								.computeMapping(preIntermediateResults,
										curIntermediateResults, thisQueryIdx,
										otherQueryIdx);

						// if the common variables are in the main
						// pattern, return true; otherwise, return false
						if (!IntermediateResults
								.checkCommonVarList(newIntermediateResults)) {
							tag = 1;
							break;
						} else {
							tag = 2;
						}

					} else {
						if (newIntermediateResults.hasSameJoinScheme(
								preIntermediateResults, curIntermediateResults,
								thisQueryIdx, otherQueryIdx)) {

							thisQueryIdxList.add(thisQueryIdx);
							otherQueryIdxList.add(otherQueryIdx);
						}
					}
				}
			}

			if (tag != 0) {
				if (tag == 2) {
					OPTJoin(preIntermediateResults, curIntermediateResults,
							newIntermediateResults);
				} else {
					// reduce the number of results in preIntermediateResults
					ArrayList<String[]> preReducedResList = preIntermediateResults
							.distributeResultListOfHittingQuery(
									newIntermediateResults,
									thisQueryIdxList.get(0));
					ArrayList<String[]> curReducedResList = curIntermediateResults
							.distributeResultListOfHittingQuery(
									newIntermediateResults,
									otherQueryIdxList.get(0));

					NaiveJoin(preReducedResList, preIntermediateResults,
							curReducedResList, curIntermediateResults,
							newIntermediateResults);
				}

				Collections.sort(thisQueryIdxList);
				Collections.sort(otherQueryIdxList);

				for (int i = thisQueryIdxList.size() - 1; i >= 0; i--) {
					int pos = thisQueryIdxList.get(i);
					preIntermediateResults.removeLocalQuery(pos);

					pos = otherQueryIdxList.get(i);
					curIntermediateResults.removeLocalQuery(pos);
				}

				newIntermediateResultsList.add(newIntermediateResults);
			} else {
				if (preIntermediateResults.HittingQuerySetGroup.size() != 0) {
					newIntermediateResultsList.add(preIntermediateResults);
				}

				break;
			}
		}

		return newIntermediateResultsList;
	}

	private ArrayList<String[]> distributeResultListOfHittingQuery(
			IntermediateResults newIntermediateResults, int idx) {

		ArrayList<String[]> tmpResList = new ArrayList<String[]>();

		// TreeMap<String, Integer> curVarIDMap = this.Var2PosMap;
		TreeMap<String, String> tmpRenamedVarMap = this.RenamedVarMapList
				.get(idx);
		int[] mappingPosArr = new int[this.Pos2VarMap.size()];
		String[] mappingConstraintPosArr = new String[this.Pos2VarMap.size()];
		Arrays.fill(mappingPosArr, -1);
		Arrays.fill(mappingConstraintPosArr, "");

		TreeMap<String, String> tmpConstraintMap = new TreeMap<String, String>();

		for (int k = 0; k < this.ConstraintList.get(idx).size(); k++) {
			tmpConstraintMap.put(this.ConstraintList.get(idx).get(k).first,
					this.ConstraintList.get(idx).get(k).second);
		}

		// mappingPosArr is to map this to newIntermediateResults
		for (int j = 0; j < this.Pos2VarMap.size(); j++) {
			ArrayList<String> varList = this.Pos2VarMap.get(j);
			for (int k = 0; k < varList.size(); k++) {
				String varStr = varList.get(k);
				if (tmpRenamedVarMap.containsKey(varStr)) {
					mappingPosArr[j] = newIntermediateResults.Var2PosMap
							.get(varStr);
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
		}

		for (int k = 0; k < this.resultList.size(); k++) {
			String[] tmpRes = new String[newIntermediateResults.Pos2VarMap
					.size()];
			Arrays.fill(tmpRes, "");
			int tag = 0;
			for (int j = 0; j < this.Pos2VarMap.size(); j++) {
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

		return tmpResList;
	}

	private static boolean checkCommonVarList(
			IntermediateResults newIntermediateResults) {

		for (int i = 0; i < newIntermediateResults.commonVarMappingList.size(); i++) {
			ArrayList<String> commonVarList = newIntermediateResults.commonVarMappingList
					.get(i);

			for (int j = 0; j < commonVarList.size(); j++) {
				String[] TermArr = commonVarList.get(j).split("_");
				if (TermArr.length != 3) {
					return false;
				}
			}
		}

		return true;
	}

	private static void NaiveJoin(ArrayList<String[]> firstReducedResList,
			IntermediateResults firstIntermediateResults,
			ArrayList<String[]> secondReducedResList,
			IntermediateResults secondIntermediateResults,
			IntermediateResults newIntermediateResults) {

		if (firstReducedResList.size() == 0 || secondReducedResList.size() == 0) {
			return;
		}

		int first_joining_pos = 0, second_joining_pos = 0;
		for (int i = 0; i < firstReducedResList.get(0).length; i++) {
			if (!firstReducedResList.get(0)[i].equals("")
					&& !secondReducedResList.get(0)[i].equals("")) {
				first_joining_pos = i;
				second_joining_pos = i;
				break;
			}
		}

		sortInPos(firstReducedResList, first_joining_pos);
		sortInPos(secondReducedResList, second_joining_pos);

		int l_iter = 0, r_iter = 0, len = newIntermediateResults.getResultLen();
		ArrayList<String[]> n_relation = new ArrayList<String[]>();
		while (l_iter < firstReducedResList.size()
				&& r_iter < secondReducedResList.size()) {

			if (l_iter == firstReducedResList.size()) {
				r_iter++;
			} else if (r_iter == secondReducedResList.size()) {
				l_iter++;
			} else if (firstReducedResList.get(l_iter)[first_joining_pos]
					.compareTo(secondReducedResList.get(r_iter)[second_joining_pos]) < 0) {
				l_iter++;
			} else if (firstReducedResList.get(l_iter)[first_joining_pos]
					.compareTo(secondReducedResList.get(r_iter)[second_joining_pos]) > 0) {
				r_iter++;
			} else {
				int l_iter_end = l_iter + 1, r_iter_end = r_iter + 1;

				while (l_iter_end < firstReducedResList.size()
						&& firstReducedResList.get(l_iter_end)[first_joining_pos]
								.compareTo(firstReducedResList.get(l_iter)[first_joining_pos]) == 0) {
					l_iter_end++;
					if (l_iter_end == firstReducedResList.size()) {
						break;
					}
				}
				while (r_iter_end < secondReducedResList.size()
						&& secondReducedResList.get(r_iter_end)[second_joining_pos]
								.compareTo(secondReducedResList.get(r_iter)[second_joining_pos]) == 0) {
					r_iter_end++;
					if (r_iter_end == secondReducedResList.size()) {
						break;
					}
				}

				for (int i = l_iter; i < l_iter_end; i++) {
					for (int j = r_iter; j < r_iter_end; j++) {
						int tag = 0;
						String[] newRes = new String[len];
						for (int k = 0; k < len; k++) {

							if (!firstReducedResList.get(i)[first_joining_pos]
									.equals("")
									&& !secondReducedResList.get(j)[second_joining_pos]
											.equals("")) {
								if (firstReducedResList.get(i)[first_joining_pos]
										.equals(secondReducedResList.get(j)[second_joining_pos])) {
									newRes[k] = firstReducedResList.get(i)[first_joining_pos];
								} else {
									tag = 1;
									break;
								}
							} else if (firstReducedResList.get(i)[first_joining_pos]
									.equals("")
									&& !secondReducedResList.get(j)[second_joining_pos]
											.equals("")) {
								newRes[k] = secondReducedResList.get(j)[second_joining_pos];
							} else if (!firstReducedResList.get(i)[first_joining_pos]
									.equals("")
									&& secondReducedResList.get(j)[second_joining_pos]
											.equals("")) {
								newRes[k] = firstReducedResList.get(i)[first_joining_pos];
							} else {
								newRes[k] = "";
							}
						}
						if (0 == tag) {
							n_relation.add(newRes);
						}
					}
				}

				r_iter = r_iter_end;
				l_iter = l_iter_end;
			}
		}

		newIntermediateResults.resultList.addAll(n_relation);
	}

	private void removeLocalQuery(int pos) {
		HittingQuerySetGroup.remove(pos);
		OriginalVarMapList.remove(pos);
		RenamedVarMapList.remove(pos);
		ConstraintList.remove(pos);
	}

	private static void sortInPos(ArrayList<String[]> aResultList,
			int joining_pos) {
		if (joining_pos == 0) {
			Collections.sort(aResultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[0].compareTo(arg1[0]);
				}
			});
		} else if (joining_pos == 1) {
			Collections.sort(aResultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[1].compareTo(arg1[1]);
				}
			});
		} else if (joining_pos == 2) {
			Collections.sort(aResultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[2].compareTo(arg1[2]);
				}
			});
		} else if (joining_pos == 3) {
			Collections.sort(aResultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[3].compareTo(arg1[3]);
				}
			});
		} else {
			Collections.sort(aResultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[4].compareTo(arg1[4]);
				}
			});
		}
	}

	private static void sortResultInPos(ArrayList<Result> aResultList,
			int joining_pos) {
		if (joining_pos == 0) {
			Collections.sort(aResultList, new Comparator<Result>() {
				@Override
				public int compare(Result arg0, Result arg1) {
					// TODO Auto-generated method stub
					return arg0.itemArr[0].compareTo(arg1.itemArr[0]);
				}
			});
		} else if (joining_pos == 1) {
			Collections.sort(aResultList, new Comparator<Result>() {
				@Override
				public int compare(Result arg0, Result arg1) {
					// TODO Auto-generated method stub
					return arg0.itemArr[1].compareTo(arg1.itemArr[1]);
				}
			});
		} else if (joining_pos == 2) {
			Collections.sort(aResultList, new Comparator<Result>() {
				@Override
				public int compare(Result arg0, Result arg1) {
					// TODO Auto-generated method stub
					return arg0.itemArr[2].compareTo(arg1.itemArr[2]);
				}
			});
		} else if (joining_pos == 3) {
			Collections.sort(aResultList, new Comparator<Result>() {
				@Override
				public int compare(Result arg0, Result arg1) {
					// TODO Auto-generated method stub
					return arg0.itemArr[3].compareTo(arg1.itemArr[3]);
				}
			});
		} else {
			Collections.sort(aResultList, new Comparator<Result>() {
				@Override
				public int compare(Result arg0, Result arg1) {
					// TODO Auto-generated method stub
					return arg0.itemArr[4].compareTo(arg1.itemArr[4]);
				}
			});
		}
	}

	private static void OPTJoin(IntermediateResults firstIntermediateResults,
			IntermediateResults secondIntermediateResults,
			IntermediateResults newIntermediateResults) {

		int first_joining_pos = firstIntermediateResults
				.findCommonVarPos(newIntermediateResults.commonVarMappingList);
		int second_joining_pos = secondIntermediateResults
				.findCommonVarPos(newIntermediateResults.commonVarMappingList);
		sortInPos(firstIntermediateResults.resultList, first_joining_pos);
		sortInPos(secondIntermediateResults.resultList, second_joining_pos);

		int[] firstNewIntermediateResults2IntermediateResultsArr = new int[newIntermediateResults
				.getResultLen()];
		int[] secondNewIntermediateResults2IntermediateResultsArr = new int[newIntermediateResults
				.getResultLen()];
		for (int i = 0; i < firstNewIntermediateResults2IntermediateResultsArr.length; i++) {
			ArrayList<String> varList = newIntermediateResults.Pos2VarMap
					.get(i);
			firstNewIntermediateResults2IntermediateResultsArr[i] = -1;
			secondNewIntermediateResults2IntermediateResultsArr[i] = -1;
			for (int j = 0; j < varList.size(); j++) {
				String varStr = varList.get(j);
				if (firstIntermediateResults.Var2PosMap.containsKey(varStr)) {
					firstNewIntermediateResults2IntermediateResultsArr[i] = firstIntermediateResults.Var2PosMap
							.get(varStr);
				}
				if (secondIntermediateResults.Var2PosMap.containsKey(varStr)) {
					secondNewIntermediateResults2IntermediateResultsArr[i] = secondIntermediateResults.Var2PosMap
							.get(varStr);
				}
			}
		}

		int first_pos = 0, second_pos = 0;
		int l_iter = 0, r_iter = 0, len = newIntermediateResults.getResultLen();
		ArrayList<String[]> n_relation = new ArrayList<String[]>();
		while (l_iter < firstIntermediateResults.resultList.size()
				&& r_iter < secondIntermediateResults.resultList.size()) {

			if (l_iter == firstIntermediateResults.resultList.size()) {
				r_iter++;
			} else if (r_iter == secondIntermediateResults.resultList.size()) {
				l_iter++;
			} else if (firstIntermediateResults.resultList.get(l_iter)[first_joining_pos]
					.compareTo(secondIntermediateResults.resultList.get(r_iter)[second_joining_pos]) < 0) {
				l_iter++;
			} else if (firstIntermediateResults.resultList.get(l_iter)[first_joining_pos]
					.compareTo(secondIntermediateResults.resultList.get(r_iter)[second_joining_pos]) > 0) {
				r_iter++;
			} else {
				int l_iter_end = l_iter + 1, r_iter_end = r_iter + 1;

				while (l_iter_end < firstIntermediateResults.resultList.size()
						&& firstIntermediateResults.resultList.get(l_iter_end)[first_joining_pos]
								.compareTo(firstIntermediateResults.resultList
										.get(l_iter)[first_joining_pos]) == 0) {
					l_iter_end++;
					if (l_iter_end == firstIntermediateResults.resultList
							.size()) {
						break;
					}
				}
				while (r_iter_end < secondIntermediateResults.resultList.size()
						&& secondIntermediateResults.resultList.get(r_iter_end)[second_joining_pos]
								.compareTo(secondIntermediateResults.resultList
										.get(r_iter)[second_joining_pos]) == 0) {
					r_iter_end++;
					if (r_iter_end == secondIntermediateResults.resultList
							.size()) {
						break;
					}
				}

				for (int i = l_iter; i < l_iter_end; i++) {
					for (int j = r_iter; j < r_iter_end; j++) {
						int tag = 0;
						String[] newRes = new String[len];
						for (int k = 0; k < len; k++) {

							first_pos = firstNewIntermediateResults2IntermediateResultsArr[k];
							second_pos = secondNewIntermediateResults2IntermediateResultsArr[k];

							if (second_pos == -1) {
								newRes[k] = firstIntermediateResults.resultList
										.get(i)[first_pos];
								continue;
							}
							if (first_pos == -1) {
								newRes[k] = secondIntermediateResults.resultList
										.get(j)[second_pos];
								continue;
							}

							if (!firstIntermediateResults.resultList.get(i)[first_pos]
									.equals("")
									&& !secondIntermediateResults.resultList
											.get(j)[second_pos].equals("")) {
								if (firstIntermediateResults.resultList.get(i)[first_pos]
										.equals(secondIntermediateResults.resultList
												.get(j)[second_pos])) {
									newRes[k] = firstIntermediateResults.resultList
											.get(i)[first_pos];
								} else {
									tag = 1;
									break;
								}
							} else if (firstIntermediateResults.resultList
									.get(i)[first_pos].equals("")
									&& !secondIntermediateResults.resultList
											.get(j)[second_pos].equals("")) {
								newRes[k] = secondIntermediateResults.resultList
										.get(j)[second_pos];
							} else if (!firstIntermediateResults.resultList
									.get(i)[first_pos].equals("")
									&& secondIntermediateResults.resultList
											.get(j)[second_pos].equals("")) {
								newRes[k] = firstIntermediateResults.resultList
										.get(i)[first_pos];
							} else {
								newRes[k] = "";
							}
						}
						if (0 == tag) {
							n_relation.add(newRes);
						}
					}
				}

				r_iter = r_iter_end;
				l_iter = l_iter_end;
			}
		}

		newIntermediateResults.resultList.clear();
		newIntermediateResults.resultList.addAll(n_relation);
	}

	private int findCommonVarPos(
			ArrayList<ArrayList<String>> curCommonVarMappingList) {
		for (int i = 0; i < curCommonVarMappingList.get(0).size(); i++) {
			String varStr = curCommonVarMappingList.get(0).get(i);
			if (Var2PosMap.containsKey(varStr)) {
				return Var2PosMap.get(varStr);
			}
		}

		return -1;
	}

	private int getResultLen() {
		return this.Pos2VarMap.size();
	}

	private boolean hasSameJoinScheme(
			IntermediateResults thisIntermediateResults,
			IntermediateResults otherIntermediateResults, int thisQueryIdx,
			int otherQueryIdx) {

		Set<String> firstOriginalVarSet = thisIntermediateResults.OriginalVarMapList
				.get(thisQueryIdx).keySet();
		Set<String> secondOriginalVarSet = otherIntermediateResults.OriginalVarMapList
				.get(otherQueryIdx).keySet();

		ArrayList<String> tmpCommonVarList = new ArrayList<String>();
		tmpCommonVarList.addAll(firstOriginalVarSet);
		tmpCommonVarList.retainAll(secondOriginalVarSet);
		ArrayList<ArrayList<String>> tmpMappingList = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < tmpCommonVarList.size(); i++) {
			String originalVarStr = tmpCommonVarList.get(i);
			tmpMappingList.add(new ArrayList<String>());
			tmpMappingList.get(tmpMappingList.size() - 1).addAll(
					thisIntermediateResults.OriginalVarMapList
							.get(thisQueryIdx).get(originalVarStr));
			tmpMappingList.get(tmpMappingList.size() - 1).addAll(
					otherIntermediateResults.OriginalVarMapList.get(
							otherQueryIdx).get(originalVarStr));
		}

		if (this.compareMappingList(tmpMappingList)) {
			this.HittingQuerySetGroup
					.add(new ArrayList<Pair<Integer, Integer>>());
			this.HittingQuerySetGroup.get(this.HittingQuerySetGroup.size() - 1)
					.addAll(thisIntermediateResults.HittingQuerySetGroup
							.get(thisQueryIdx));
			this.HittingQuerySetGroup.get(this.HittingQuerySetGroup.size() - 1)
					.addAll(otherIntermediateResults.HittingQuerySetGroup
							.get(otherQueryIdx));

			this.ConstraintList.add(thisIntermediateResults.ConstraintList
					.get(thisQueryIdx));
			this.ConstraintList.get(this.ConstraintList.size() - 1).addAll(
					otherIntermediateResults.ConstraintList.get(otherQueryIdx));

			this.OriginalVarMapList
					.add(thisIntermediateResults.OriginalVarMapList
							.get(thisQueryIdx));
			Iterator<Entry<String, ArrayList<String>>> iter2 = otherIntermediateResults.OriginalVarMapList
					.get(otherQueryIdx).entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<String, ArrayList<String>> e = iter2.next();
				String varStr = e.getKey();
				ArrayList<String> list1 = e.getValue();
				if (this.OriginalVarMapList.get(
						this.OriginalVarMapList.size() - 1).containsKey(varStr)) {
					this.OriginalVarMapList
							.get(this.OriginalVarMapList.size() - 1)
							.get(varStr).addAll(list1);
				} else {
					this.OriginalVarMapList.get(
							this.OriginalVarMapList.size() - 1).put(varStr,
							list1);
				}
			}

			this.RenamedVarMapList
					.add(thisIntermediateResults.RenamedVarMapList
							.get(thisQueryIdx));
			this.RenamedVarMapList.get(this.RenamedVarMapList.size() - 1)
					.putAll(otherIntermediateResults.RenamedVarMapList
							.get(otherQueryIdx));

			return true;
		}

		return false;
	}

	private boolean compareMappingList(
			ArrayList<ArrayList<String>> tmpMappingList) {
		if (this.commonVarMappingList.size() != tmpMappingList.size()) {
			return false;
		}

		Iterator<ArrayList<String>> iter = tmpMappingList.iterator();
		while (iter.hasNext()) {
			ArrayList<String> e = iter.next();
			if (!this.commonVarMappingList.contains(e)) {
				return false;
			}
		}
		return true;
	}

	private static IntermediateResults computeMapping(
			IntermediateResults thisIntermediateResults,
			IntermediateResults otherIntermediateResults, int thisQueryIdx,
			int otherQueryIdx) {

		Set<String> firstOriginalVarSet = thisIntermediateResults.OriginalVarMapList
				.get(thisQueryIdx).keySet();
		Set<String> secondOriginalVarSet = otherIntermediateResults.OriginalVarMapList
				.get(otherQueryIdx).keySet();

		ArrayList<String> tmpCommonVarList = new ArrayList<String>();
		tmpCommonVarList.addAll(firstOriginalVarSet);
		tmpCommonVarList.retainAll(secondOriginalVarSet);

		IntermediateResults newIntermediateResults = new IntermediateResults();

		// Set<String> firstRenamedVarSet =
		// thisIntermediateResults.RenamedVarMapList
		// .get(thisQueryIdx).keySet();
		Set<String> secondRenamedVarSet = otherIntermediateResults.Var2PosMap
				.keySet();

		int pos = thisIntermediateResults.Pos2VarMap.size();
		newIntermediateResults
				.copyVarPosMap(thisIntermediateResults.Pos2VarMap);

		Iterator<String> iter = secondRenamedVarSet.iterator();
		while (iter.hasNext()) {
			String renamedVarStr = iter.next();
			String originalVarStr = otherIntermediateResults.RenamedVarMapList
					.get(otherQueryIdx).get(renamedVarStr);
			if (tmpCommonVarList.contains(originalVarStr)) {
				ArrayList<String> firstRenamedVarList = thisIntermediateResults.OriginalVarMapList
						.get(thisQueryIdx).get(originalVarStr);
				for (int renamedVarIdx = 0; renamedVarIdx < firstRenamedVarList
						.size(); renamedVarIdx++) {
					newIntermediateResults.Var2PosMap
							.put(renamedVarStr,
									newIntermediateResults.Var2PosMap
											.get(firstRenamedVarList
													.get(renamedVarIdx)));
					if (!newIntermediateResults.Pos2VarMap
							.get(newIntermediateResults.Var2PosMap
									.get(firstRenamedVarList.get(renamedVarIdx)))
							.contains(renamedVarStr)) {

						newIntermediateResults.Pos2VarMap.get(
								newIntermediateResults.Var2PosMap
										.get(firstRenamedVarList
												.get(renamedVarIdx))).add(
								renamedVarStr);
					}
				}
			} else {
				newIntermediateResults.putVarPosMapping(renamedVarStr, pos);
				pos++;
			}
		}

		for (int i = 0; i < tmpCommonVarList.size(); i++) {
			String originalVarStr = tmpCommonVarList.get(i);
			newIntermediateResults.putCommonMapping(
					originalVarStr,
					thisIntermediateResults.OriginalVarMapList
							.get(thisQueryIdx).get(originalVarStr),
					otherIntermediateResults.OriginalVarMapList.get(
							otherQueryIdx).get(originalVarStr));
		}

		newIntermediateResults.HittingQuerySetGroup
				.add(new ArrayList<Pair<Integer, Integer>>());
		newIntermediateResults.HittingQuerySetGroup.get(
				newIntermediateResults.HittingQuerySetGroup.size() - 1).addAll(
				thisIntermediateResults.HittingQuerySetGroup.get(thisQueryIdx));
		newIntermediateResults.HittingQuerySetGroup.get(
				newIntermediateResults.HittingQuerySetGroup.size() - 1).addAll(
				otherIntermediateResults.HittingQuerySetGroup
						.get(otherQueryIdx));

		newIntermediateResults.ConstraintList
				.add(thisIntermediateResults.ConstraintList.get(thisQueryIdx));
		newIntermediateResults.ConstraintList.get(
				newIntermediateResults.ConstraintList.size() - 1).addAll(
				otherIntermediateResults.ConstraintList.get(otherQueryIdx));

		newIntermediateResults.OriginalVarMapList
				.add(new TreeMap<String, ArrayList<String>>());
		newIntermediateResults.OriginalVarMapList.get(
				newIntermediateResults.OriginalVarMapList.size() - 1).putAll(
				thisIntermediateResults.OriginalVarMapList.get(thisQueryIdx));

		Iterator<Entry<String, ArrayList<String>>> iter2 = otherIntermediateResults.OriginalVarMapList
				.get(otherQueryIdx).entrySet().iterator();
		while (iter2.hasNext()) {
			Entry<String, ArrayList<String>> e = iter2.next();
			String varStr = e.getKey();
			ArrayList<String> list1 = e.getValue();
			if (newIntermediateResults.OriginalVarMapList.get(
					newIntermediateResults.OriginalVarMapList.size() - 1)
					.containsKey(varStr)) {
				newIntermediateResults.OriginalVarMapList
						.get(newIntermediateResults.OriginalVarMapList.size() - 1)
						.get(varStr).addAll(list1);
			} else {
				newIntermediateResults.OriginalVarMapList.get(
						newIntermediateResults.OriginalVarMapList.size() - 1)
						.put(varStr, list1);
			}
		}

		newIntermediateResults.RenamedVarMapList
				.add(thisIntermediateResults.RenamedVarMapList
						.get(thisQueryIdx));
		newIntermediateResults.RenamedVarMapList.get(
				newIntermediateResults.RenamedVarMapList.size() - 1).putAll(
				otherIntermediateResults.RenamedVarMapList.get(otherQueryIdx));

		newIntermediateResults.mainVarSet
				.addAll(thisIntermediateResults.mainVarSet);
		newIntermediateResults.mainVarSet
				.addAll(otherIntermediateResults.mainVarSet);

		return newIntermediateResults;
	}

	private void copyVarPosMap(TreeMap<Integer, ArrayList<String>> pos2VarMap2) {
		Iterator<Entry<Integer, ArrayList<String>>> iter = pos2VarMap2
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<String>> e = iter.next();
			int pos = e.getKey();
			ArrayList<String> varList = e.getValue();
			for (int i = 0; i < varList.size(); i++) {
				this.putVarPosMapping(varList.get(i), pos);
			}
		}
	}

	private void putCommonMapping(String originalVarStr,
			ArrayList<String> List1, ArrayList<String> List2) {
		this.commonVarMappingList.add(new ArrayList<String>());
		this.commonVarMappingList.get(this.commonVarMappingList.size() - 1)
				.addAll(List1);
		this.commonVarMappingList.get(this.commonVarMappingList.size() - 1)
				.addAll(List2);
	}

	public void putVarPosMapping(String str, int pos) {
		this.Var2PosMap.put(str, pos);
		if (!this.Pos2VarMap.containsKey(pos))
			this.Pos2VarMap.put(pos, new ArrayList<String>());
		this.Pos2VarMap.get(pos).add(str);
	}

	private int findSameFullQuery(Pair<Integer, Integer> p1) {
		for (int i = 0; i < this.HittingQuerySetGroup.size(); i++) {
			if (this.HittingQuerySetGroup.get(i).get(0).first == p1.first) {
				return i;
			}
		}
		return -1;
	}

	public int getResultSize() {
		return this.resultList.size();
	}

	public static boolean canJoin(IntermediateResults curIntermediateResults,
			RewrittenQuery curRewrittenQuery) {

		for (int thisQueryIdx = 0; thisQueryIdx < curRewrittenQuery
				.getHittingQuerySet().size(); thisQueryIdx++) {
			Pair<Integer, Integer> p1 = curRewrittenQuery.getHittingQuerySet()
					.get(thisQueryIdx);
			int otherQueryIdx = curIntermediateResults.findSameFullQuery(p1);
			if (otherQueryIdx != -1) {

				Set<String> firstOriginalVarSet = curRewrittenQuery
						.getOriginalVarMapList().get(thisQueryIdx).keySet();
				Set<String> secondOriginalVarSet = curIntermediateResults.OriginalVarMapList
						.get(otherQueryIdx).keySet();

				ArrayList<String> tmpCommonVarList = new ArrayList<String>();
				tmpCommonVarList.addAll(firstOriginalVarSet);
				tmpCommonVarList.retainAll(secondOriginalVarSet);

				if (tmpCommonVarList.size() != 0) {
					return true;
				}
			}
		}

		return false;
	}

	public void printResultsInLog(String fileStr) throws FileNotFoundException {
		PrintStream out = new PrintStream(new File(fileStr));

		out.println("Pos2VarMap=" + this.Pos2VarMap);
		for (int i = 0; i < this.HittingQuerySetGroup.size(); i++) {
			out.println("HittingQuerySetGroup.get(" + i + ")="
					+ this.HittingQuerySetGroup.get(i));
			out.println("RenamedVarMapList.get(" + i + ")="
					+ this.RenamedVarMapList.get(i));
			out.println("OriginalVarMapList.get(" + i + ")="
					+ this.OriginalVarMapList.get(i));
			out.println("ConstraintList.get(" + i + ")="
					+ this.ConstraintList.get(i));
		}
		for (int i = 0; i < this.resultList.size(); i++) {
			out.println(Arrays.toString(this.resultList.get(i)));
		}

		out.flush();
		out.close();
	}

}
