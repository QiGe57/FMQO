package Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String[]> l_relation = null, r_relation = null;
		Join(l_relation, r_relation, 0);

	}

	public static void Join(ArrayList<String[]> l_relation,
			ArrayList<String[]> r_relation, int joining_pos) {
		if (joining_pos == 0) {
			Collections.sort(l_relation, new Comparator<String[]>() {
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
			Collections.sort(l_relation, new Comparator<String[]>() {
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
			Collections.sort(l_relation, new Comparator<String[]>() {
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
			Collections.sort(l_relation, new Comparator<String[]>() {
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
			Collections.sort(l_relation, new Comparator<String[]>() {
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

		int l_iter = 0, r_iter = 0, len = l_relation.get(0).length;
		ArrayList<String[]> n_relation = new ArrayList<String[]>();
		while (l_iter < l_relation.size() && r_iter < r_relation.size()) {
			if (l_iter == l_relation.size()) {
				r_iter++;
			} else if (r_iter == r_relation.size()) {
				l_iter++;
			} else if (l_relation.get(l_iter)[joining_pos].compareTo(r_relation
					.get(r_iter)[joining_pos]) < 0) {
				l_iter++;
			} else if (l_relation.get(l_iter)[joining_pos].compareTo(r_relation
					.get(r_iter)[joining_pos]) > 0) {
				r_iter++;
			} else {
				int l_iter_end = l_iter + 1, r_iter_end = r_iter + 1;

				while (l_relation.get(l_iter_end)[joining_pos]
						.compareTo(l_relation.get(l_iter)[joining_pos]) == 0) {
					l_iter_end++;
					if (l_iter_end == l_relation.size()) {
						break;
					}
				}
				while (r_relation.get(r_iter_end)[joining_pos]
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
							if (!l_relation.get(i)[k].equals("")
									&& !r_relation.get(j)[k].equals("")) {
								if (l_relation.get(i)[k].equals(r_relation
										.get(j)[k])) {
									newRes[k] = l_relation.get(i)[k];
								} else {
									tag = 1;
									break;
								}
							} else if (l_relation.get(i)[k].equals("")
									&& !r_relation.get(j)[k].equals("")) {
								newRes[k] = r_relation.get(j)[k];
							} else if (!l_relation.get(i)[k].equals("")
									&& r_relation.get(j)[k].equals("")) {
								newRes[k] = l_relation.get(i)[k];
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

		l_relation.clear();
		l_relation.addAll(n_relation);
	}

}
