package Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class BGPGraph {
	protected ArrayList<TriplePattern> triplePatternList;
	protected TreeMap<String, Integer> VertexIDmap;
	protected TreeMap<Integer, String> IDVertexmap;
	protected TreeMap<Integer, TreeMap<Integer, String>> AdjacencyMatrix;

	public BGPGraph() {
		super();
		triplePatternList = new ArrayList<TriplePattern>();
		VertexIDmap = new TreeMap<String, Integer>();
		IDVertexmap = new TreeMap<Integer, String>();
		AdjacencyMatrix = new TreeMap<Integer, TreeMap<Integer, String>>();
	}

	@Override
	public String toString() {
		return "BGPGraph [triplePatternList=" + triplePatternList
				+ ", VertexIDmap=" + VertexIDmap + ", IDVertexmap="
				+ IDVertexmap + ", AdjacencyMatrix=" + AdjacencyMatrix + "]";
	}

	public void addTriplePattern(TriplePattern myPattern) {
		this.triplePatternList.add(myPattern);
		String subjectStr = myPattern.getSubjectStr(), objectStr = myPattern
				.getObjectStr(), predicateStr = myPattern.getPredicateStr();
		int cur_id = 0;

		if (!this.VertexIDmap.containsKey(subjectStr)) {
			cur_id = this.VertexIDmap.size();
			this.VertexIDmap.put(subjectStr, cur_id);
			this.IDVertexmap.put(cur_id, subjectStr);
		}
		if (!this.VertexIDmap.containsKey(objectStr)) {
			cur_id = this.VertexIDmap.size();
			this.VertexIDmap.put(objectStr, cur_id);
			this.IDVertexmap.put(cur_id, objectStr);
		}

		int subjectID = this.VertexIDmap.get(subjectStr), objectID = this.VertexIDmap
				.get(objectStr);
		if (!this.AdjacencyMatrix.containsKey(subjectID)) {
			this.AdjacencyMatrix.put(subjectID, new TreeMap<Integer, String>());
		}
		this.AdjacencyMatrix.get(subjectID).put(objectID, predicateStr);
	}

	public TreeMap<String, Integer> getVertexIDmap() {
		return VertexIDmap;
	}

	public void setVertexIDmap(TreeMap<String, Integer> vertexIDmap) {
		VertexIDmap = vertexIDmap;
	}

	public TreeMap<Integer, String> getIDVertexmap() {
		return IDVertexmap;
	}

	public void setIDVertexmap(TreeMap<Integer, String> iDVertexmap) {
		IDVertexmap = iDVertexmap;
	}

	public TreeMap<Integer, TreeMap<Integer, String>> getAdjacencyMatrix() {
		return AdjacencyMatrix;
	}

	public void setAdjacencyMatrix(
			TreeMap<Integer, TreeMap<Integer, String>> adjacencyMatrix) {
		AdjacencyMatrix = adjacencyMatrix;
	}

	public TriplePattern getTriplePattern(int idx) {
		return triplePatternList.get(idx);
	}

	public void sort() {
		Collections.sort(this.triplePatternList);
	}

	public ArrayList<TriplePattern> getTriplePatternList() {
		return triplePatternList;
	}

	public void setTriplePatternList(ArrayList<TriplePattern> triplePatternList) {
		this.triplePatternList = triplePatternList;
	}

	private int FindUnmatchingPos(Integer[] curState) {
		for (int i = 0; i < curState.length; i++) {
			if (curState[i] == -1)
				return i;
		}
		return -1;
	}

	public Integer[] checkIsomorphic(BGPGraph o) {
		if (o.VertexIDmap.size() != this.VertexIDmap.size())
			return null;

		int VertexNum = o.VertexIDmap.size();
		LinkedList<Integer[]> Queue = new LinkedList<Integer[]>();
		Integer[] curState = new Integer[VertexNum];
		Integer[] newState = new Integer[VertexNum];
		int cur_pos = 0, tag = 0;

		for (int i = 0; i < VertexNum; i++) {
			curState = new Integer[VertexNum];
			Arrays.fill(curState, -1);
			curState[0] = i;
			Queue.add(curState);
		}

		while (Queue.size() != 0) {
			curState = Queue.pollLast();

			cur_pos = FindUnmatchingPos(curState);
			if (-1 == cur_pos) {
				return curState;
			} else {
				for (int i = 0; i < VertexNum; i++) {
					newState = new Integer[VertexNum];
					for (int j = 0; j < VertexNum; j++) {
						newState[j] = curState[j];
					}
					newState[cur_pos] = i;
					tag = 0;

					for (int j = 0; j < cur_pos; j++) {
						if (newState[j] == newState[cur_pos]) {
							tag = 1;
							break;
						}

						if (o.AdjacencyMatrix.containsKey(j)
								&& o.AdjacencyMatrix.get(j)
										.containsKey(cur_pos)) {
							if (this.AdjacencyMatrix.containsKey(newState[j])
									&& this.AdjacencyMatrix.get(newState[j])
											.containsKey(newState[cur_pos])) {
								if (!o.AdjacencyMatrix
										.get(j)
										.get(cur_pos)
										.equals(this.AdjacencyMatrix.get(
												newState[j]).get(
												newState[cur_pos]))) {
									tag = 1;
									break;
								}
							} else {
								tag = 1;
								break;
							}
						}

						if (o.AdjacencyMatrix.containsKey(cur_pos)
								&& o.AdjacencyMatrix.get(cur_pos)
										.containsKey(j)) {
							if (this.AdjacencyMatrix
									.containsKey(newState[cur_pos])
									&& this.AdjacencyMatrix.get(
											newState[cur_pos]).containsKey(
											newState[j])) {
								if (!o.AdjacencyMatrix
										.get(cur_pos)
										.get(j)
										.equals(this.AdjacencyMatrix.get(
												newState[cur_pos]).get(
												newState[j]))) {
									tag = 1;
									break;
								}
							} else {
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

		return null;
	}

	public Integer[] checkSubgraph(BGPGraph sub) {
		int VertexNum = sub.VertexIDmap.size();
		int superVertexNum = this.VertexIDmap.size();
		LinkedList<Integer[]> Queue = new LinkedList<Integer[]>();
		Integer[] curState = new Integer[VertexNum];
		Integer[] newState = new Integer[VertexNum];
		int cur_pos = 0, tag = 0;

		for (int i = 0; i < superVertexNum; i++) {
			curState = new Integer[VertexNum];
			Arrays.fill(curState, -1);
			curState[0] = i;
			Queue.add(curState);
		}

		while (Queue.size() != 0) {
			curState = Queue.pollLast();

			cur_pos = FindUnmatchingPos(curState);
			if (-1 == cur_pos) {
				return curState;
			} else {
				for (int i = 0; i < superVertexNum; i++) {
					newState = new Integer[VertexNum];
					for (int j = 0; j < VertexNum; j++) {
						newState[j] = curState[j];
					}
					newState[cur_pos] = i;
					tag = 0;

					for (int j = 0; j < cur_pos; j++) {
						if (newState[j] == newState[cur_pos]) {
							tag = 1;
							break;
						}

						if (sub.AdjacencyMatrix.containsKey(j)
								&& sub.AdjacencyMatrix.get(j).containsKey(
										cur_pos)) {
							if (this.AdjacencyMatrix.containsKey(newState[j])
									&& this.AdjacencyMatrix.get(newState[j])
											.containsKey(newState[cur_pos])) {

								if (!sub.AdjacencyMatrix
										.get(j)
										.get(cur_pos)
										.equals(this.AdjacencyMatrix.get(
												newState[j]).get(
												newState[cur_pos]))) {
									tag = 1;
									break;
								}
							} else {
								tag = 1;
								break;
							}
						}

						if (sub.AdjacencyMatrix.containsKey(cur_pos)
								&& sub.AdjacencyMatrix.get(cur_pos)
										.containsKey(j)) {
							if (this.AdjacencyMatrix
									.containsKey(newState[cur_pos])
									&& this.AdjacencyMatrix.get(
											newState[cur_pos]).containsKey(
											newState[j])) {
								if (!sub.AdjacencyMatrix
										.get(cur_pos)
										.get(j)
										.equals(this.AdjacencyMatrix.get(
												newState[cur_pos]).get(
												newState[j]))) {
									tag = 1;
									break;
								}
							} else {
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

		return null;
	}

}
