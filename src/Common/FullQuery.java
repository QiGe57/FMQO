package Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

public class FullQuery {
	private String SPARQLStr;
	private BGPGraph QueryPattern;
	private TreeMap<String, Integer> VarIDMap;
	private TreeMap<Integer, String> IDVarMap;
	private ArrayList<LocalQuery> localQueryList;
	private ArrayList<String[]> resultList;

	// private ArrayList<Boolean> TagList;

	public String getSPARQLStr() {
		return SPARQLStr;
	}

	public void setSPARQLStr(String sPARQLStr) {
		SPARQLStr = sPARQLStr;
	}

	public BGPGraph getQueryPattern() {
		return QueryPattern;
	}

	public void setQueryPattern(BGPGraph queryPattern) {
		QueryPattern = queryPattern;
	}

	public ArrayList<LocalQuery> getLocalQueryList() {
		return localQueryList;
	}

	public void setLocalQueryList(ArrayList<LocalQuery> localQueryList) {
		this.localQueryList = localQueryList;
	}

	public LocalQuery getLocalQuery(int idx) {
		return this.localQueryList.get(idx);
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

	public TreeMap<String, Integer> getVarIDMap() {
		return VarIDMap;
	}

	public void setVarIDMap(TreeMap<String, Integer> varIDMap) {
		VarIDMap = varIDMap;
	}

	public TreeMap<Integer, String> getIDVarMap() {
		return IDVarMap;
	}

	public void setIDVarMap(TreeMap<Integer, String> iDVarMap) {
		IDVarMap = iDVarMap;
	}

	@Override
	public String toString() {
		return "FullQuery [SPARQLStr=" + SPARQLStr + ", QueryPattern="
				+ QueryPattern + ", VarIDMap=" + VarIDMap + ", IDVarMap="
				+ IDVarMap + ", localQueryList=" + localQueryList
				+ ", resultList=" + resultList + "]";
	}

	public FullQuery(String sPARQLStr) {
		super();
		SPARQLStr = sPARQLStr;
		this.QueryPattern = new BGPGraph();
		this.localQueryList = new ArrayList<LocalQuery>();
		this.resultList = new ArrayList<String[]>();
		this.VarIDMap = new TreeMap<String, Integer>();
		this.IDVarMap = new TreeMap<Integer, String>();
	}

	public void addTriplePattern(TriplePattern myPattern) {
		this.QueryPattern.addTriplePattern(myPattern);
	}

	public void Join(ArrayList<String[]> r_relation, int joining_pos) {
		if (joining_pos == 0) {
			Collections.sort(this.resultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[0].compareTo(arg1[0]);
				}
			});
			Collections.sort(r_relation, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[0].compareTo(arg1[0]);
				}
			});
		} else if (joining_pos == 1) {
			Collections.sort(this.resultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[1].compareTo(arg1[1]);
				}
			});
			Collections.sort(r_relation, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[1].compareTo(arg1[1]);
				}
			});
		} else if (joining_pos == 2) {
			Collections.sort(this.resultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[2].compareTo(arg1[2]);
				}
			});
			Collections.sort(r_relation, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[2].compareTo(arg1[2]);
				}
			});
		} else if (joining_pos == 3) {
			Collections.sort(this.resultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[3].compareTo(arg1[3]);
				}
			});
			Collections.sort(r_relation, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[3].compareTo(arg1[3]);
				}
			});
		} else {
			Collections.sort(this.resultList, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[4].compareTo(arg1[4]);
				}
			});
			Collections.sort(r_relation, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					return arg0[4].compareTo(arg1[4]);
				}
			});
		}

		int l_iter = 0, r_iter = 0, len = this.resultList.get(0).length;
		ArrayList<String[]> n_relation = new ArrayList<String[]>();
		while (l_iter < this.resultList.size() && r_iter < r_relation.size()) {
			if (l_iter == this.resultList.size()) {
				r_iter++;
			} else if (r_iter == r_relation.size()) {
				l_iter++;
			} else if (this.resultList.get(l_iter)[joining_pos]
					.compareTo(r_relation.get(r_iter)[joining_pos]) < 0) {
				l_iter++;
			} else if (this.resultList.get(l_iter)[joining_pos]
					.compareTo(r_relation.get(r_iter)[joining_pos]) > 0) {
				r_iter++;
			} else {
				int l_iter_end = l_iter + 1, r_iter_end = r_iter + 1;

				while (l_iter_end < this.resultList.size()
						&& this.resultList.get(l_iter_end)[joining_pos]
								.compareTo(this.resultList.get(l_iter)[joining_pos]) == 0) {
					l_iter_end++;
					if (l_iter_end == this.resultList.size()) {
						break;
					}
				}
				while (r_iter_end < r_relation.size()
						&& r_relation.get(r_iter_end)[joining_pos]
								.compareTo(r_relation.get(r_iter)[joining_pos]) == 0) {
					r_iter_end++;
					if (r_iter_end == r_relation.size()) {
						break;
					}
				}

				for (int i = l_iter; i < l_iter_end; i++) {
					for (int j = r_iter; j < r_iter_end; j++) {
						int tag = 0;
						String[] newRes = new String[len];
						for (int k = 0; k < len; k++) {
							if (!this.resultList.get(i)[k].equals("")
									&& !r_relation.get(j)[k].equals("")) {
								if (this.resultList.get(i)[k].equals(r_relation
										.get(j)[k])) {
									newRes[k] = this.resultList.get(i)[k];
								} else {
									tag = 1;
									break;
								}
							} else if (this.resultList.get(i)[k].equals("")
									&& !r_relation.get(j)[k].equals("")) {
								newRes[k] = r_relation.get(j)[k];
							} else if (!this.resultList.get(i)[k].equals("")
									&& r_relation.get(j)[k].equals("")) {
								newRes[k] = this.resultList.get(i)[k];
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

		this.resultList.clear();
		this.resultList.addAll(n_relation);
	}

	public void clearResults() {
		this.resultList.clear();
	}

	public void addLocalQueries(ArrayList<LocalQuery> curLocalQueryList) {
		this.localQueryList.addAll(curLocalQueryList);
	}

	public void addAllResult(TreeSet<String> resultSet) {
		Iterator<String> iter = resultSet.iterator();
		while (iter.hasNext()) {
			String str = iter.next();
			str = str.substring(1, str.length() - 1);
			this.resultList.add(str.split(","));
		}
	}

	public void reduceSources(
			HashMap<Integer, ArrayList<Integer>> STGAdjacentList) {

		int[][] JGAdjacentMatrix = new int[this.localQueryList.size()][this.localQueryList
				.size()];
		ArrayList<ArrayList<Integer>> newSourceList = new ArrayList<ArrayList<Integer>>();
		int SourceID = 0;

		for (int i = 0; i < JGAdjacentMatrix.length; i++) {
			Arrays.fill(JGAdjacentMatrix[i], 0);
		}

		for (int i = 0; i < JGAdjacentMatrix.length; i++) {

			newSourceList.add(new ArrayList<Integer>());
			for (int j = i + 1; j < this.localQueryList.size(); j++) {

				for (int k = 0; k < this.localQueryList.get(i).getLocalBGP().IDVertexmap
						.size(); k++) {
					String curVar = this.localQueryList.get(i).getLocalBGP().IDVertexmap
							.get(k);
					if (!curVar.startsWith("?"))
						continue;
					if (this.localQueryList.get(j).getLocalBGP().VertexIDmap
							.containsKey(curVar)) {

						JGAdjacentMatrix[i][j] = 1;
						JGAdjacentMatrix[j][i] = 1;
						break;
					}
				}
			}
		}

		ArrayList<Integer> curSouceList = this.localQueryList.get(0)
				.getSourceList();
		LinkedList<Integer[]> Queue = new LinkedList<Integer[]>();
		int VertexNum = this.localQueryList.size();
		for (int j = 0; j < curSouceList.size(); j++) {
			SourceID = curSouceList.get(j);
			Integer[] mappingState = new Integer[this.localQueryList.size()];
			Arrays.fill(mappingState, -1);
			mappingState[0] = SourceID;
			Queue.add(mappingState);
		}

		Integer[] curState, newState;
		int tag = 0;
		while (Queue.size() != 0) {
			curState = Queue.pollLast();

			int cur_pos = FindUnmatchingPos(curState);
			if (-1 == cur_pos) {
				for (int i = 0; i < curState.length; i++) {
					if (!newSourceList.get(i).contains(curState[i])) {
						newSourceList.get(i).add(curState[i]);
					}
				}
			} else {
				int first_neighbor_pos = 0;
				for (int i = 0; i < cur_pos; i++) {
					if (JGAdjacentMatrix[cur_pos][i] == 1) {
						first_neighbor_pos = i;
						break;
					}
				}

				for (int i = 0; i < STGAdjacentList.get(
						curState[first_neighbor_pos]).size(); i++) {

					newState = new Integer[VertexNum];
					Arrays.fill(newState, -1);
					newState[cur_pos] = STGAdjacentList.get(
							curState[first_neighbor_pos]).get(i);

					if (!this.localQueryList.get(cur_pos).getSourceList()
							.contains(newState[cur_pos])) {
						continue;
					}

					for (int k = 0; k < cur_pos; k++) {
						newState[k] = curState[k];
					}

					tag = 0;
					for (int j = first_neighbor_pos + 1; j < cur_pos; j++) {
						if (JGAdjacentMatrix[cur_pos][j] == 1) {
							if (!STGAdjacentList.get(newState[cur_pos])
									.contains(newState[j])) {
								tag = 1;
								break;
							}
						}
					}
					if (tag == 0) {
						Queue.add(newState);
					}
				}
			}
		}

		for (int j = 0; j < newSourceList.size(); j++) {
			this.localQueryList.get(j).setSourceList(newSourceList.get(j));
		}
	}

	private int FindUnmatchingPos(Integer[] curState) {
		for (int i = 0; i < curState.length; i++) {
			if (curState[i] == -1)
				return i;
		}
		return -1;
	}

	public void reduceSourcesQTree(
			HashMap<Integer, ArrayList<Integer>> STGAdjacentList) {

		int[][] JGAdjacentMatrix = new int[this.localQueryList.size()][this.localQueryList
				.size()];
		ArrayList<ArrayList<Integer>> newSourceList = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> LocalQueriesRelevantSourceList = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < JGAdjacentMatrix.length; i++) {
			Arrays.fill(JGAdjacentMatrix[i], 0);
		}

		for (int i = 0; i < JGAdjacentMatrix.length; i++) {

			newSourceList.add(new ArrayList<Integer>());
			ArrayList<Integer> aLocalQuerySourceList = this.localQueryList.get(
					i).getSourceList();
			LocalQueriesRelevantSourceList.add(new ArrayList<Integer>());
			for (int j = 0; j < aLocalQuerySourceList.size(); j++) {
				LocalQueriesRelevantSourceList.get(
						LocalQueriesRelevantSourceList.size() - 1).add(
						aLocalQuerySourceList.get(j));
				LocalQueriesRelevantSourceList.get(
						LocalQueriesRelevantSourceList.size() - 1).addAll(
						STGAdjacentList.get(aLocalQuerySourceList.get(j)));
			}

			for (int j = i + 1; j < JGAdjacentMatrix[i].length; j++) {

				for (int k = 0; k < this.localQueryList.get(i).getLocalBGP().IDVertexmap
						.size(); k++) {
					String curVar = this.localQueryList.get(i).getLocalBGP().IDVertexmap
							.get(k);
					// if (!curVar.startsWith("?"))
					// continue;
					if (this.localQueryList.get(j).getLocalBGP().VertexIDmap
							.containsKey(curVar)) {

						JGAdjacentMatrix[i][j] = 1;
						JGAdjacentMatrix[j][i] = 1;
						break;
					}
				}
			}
		}

		for (int i = 0; i < JGAdjacentMatrix.length; i++) {
			ArrayList<Integer> firstSourceList = this.localQueryList.get(i)
					.getSourceList();
			for (int k = 0; k < firstSourceList.size(); k++) {
				int firstSourceID = firstSourceList.get(k);
				int tag = 0;
				for (int j = 0; j < JGAdjacentMatrix[i].length; j++) {
					if (i == j)
						continue;
					if (JGAdjacentMatrix[i][j] == 1) {

						if (LocalQueriesRelevantSourceList.get(j).contains(
								firstSourceID)) {
							tag = 1;
							break;
						}
					}
				}
				if (tag == 1) {
					newSourceList.get(i).add(firstSourceID);
				}
			}
		}

		for (int j = 0; j < newSourceList.size(); j++) {
			this.localQueryList.get(j).setSourceList(newSourceList.get(j));
		}
	}
}