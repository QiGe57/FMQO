package mqo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import Common.FullQuery;
import Common.HittingSet;
import Common.IntermediateResults;
import Common.LocalQuery;
import Common.Pair;
import Common.RewrittenQuery;
import Common.SeverInfo;
import Common.TriplePattern;

public class MultiQuery_Hybrid {

	static int[] templateNumArr = { 10 };
	static int[] queryNumArr = { 150 };
	static String[] configFileArr = {
			"E:/PengPeng/Data/WSDM/config_watdiv_10M.txt",
			"E:/PengPeng/Data/FedBench2.0/data/LifeScienceDomain/config_lfd.txt",
			"E:/PengPeng/Data/FedBench2.0/data/LifeScienceDomain/config_lfd.txt",
			"E:/PengPeng/Data/FedBench2.0/data/LifeScienceDomain/config_lfd.txt",
			"E:/PengPeng/Data/FedBench2.0/data/LifeScienceDomain/config_lfd.txt" };
	static int windowSize = 50;

	public static void main(String[] args) {

		try {
			PrintStream out1 = new PrintStream(new File(
					"E:/PengPeng/Data/WSDM/MQO/" + "log_lifeifei.txt"));

			for (int i = 0; i < templateNumArr.length; i++) {
				System.out.println("#########" + templateNumArr[i]
						+ "#########");
				System.out.println("#########" + queryNumArr[i] + "#########");
				System.out.println(configFileArr[i]);
				run(configFileArr[i],
						"E:/PengPeng/Data/WSDM/MQO/workload/query_"
								+ templateNumArr[i] + "_" + queryNumArr[i]
								+ ".txt",
						"E:/PengPeng/Data/WSDM/MQO/res_lifeifei.txt", out1,
						queryNumArr[i]);
				out1.println("==========" + templateNumArr[i] + "=========="
						+ queryNumArr[i] + "==========" + configFileArr[i]
						+ "==========");

				System.out.println("#########" + templateNumArr[i]
						+ "#########");
				System.out.println("#########" + queryNumArr[i] + "#########");
				System.out.println(configFileArr[i]);
				run(configFileArr[i],
						"E:/PengPeng/Data/WSDM/MQO/workload/query_"
								+ templateNumArr[i] + "_" + queryNumArr[i]
								+ ".txt",
						"E:/PengPeng/Data/WSDM/MQO/res_lifeifei.txt", out1,
						queryNumArr[i]);
				out1.println("==========" + templateNumArr[i] + "=========="
						+ queryNumArr[i] + "==========" + configFileArr[i]
						+ "==========");
			}

			out1.flush();
			out1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void run(String configFileStr, String workloadFileStr,
			String resFileStr, PrintStream out1, int queryNum) {

		String str = "";
		int count = 0;
		String[] TermArr;

		try {
			ArrayList<SeverInfo> severList = new ArrayList<SeverInfo>();

			InputStream in16 = new FileInputStream(new File(configFileStr));
			Reader inr16 = new InputStreamReader(in16);
			BufferedReader br16 = new BufferedReader(inr16);

			str = br16.readLine();
			while (str != null) {
				str = str.trim();
				TermArr = str.split("\t");

				severList.add(new SeverInfo(TermArr[0], TermArr[1]));

				if (count % 10000 == 6)
					System.out.println(count);
				count++;
				str = br16.readLine();
			}
			br16.close();

			ArrayList<FullQuery> allQueryList = new ArrayList<FullQuery>();

			InputStream in6 = new FileInputStream(new File(workloadFileStr));
			Reader inr6 = new InputStreamReader(in6);
			BufferedReader br6 = new BufferedReader(inr6);

			str = br6.readLine();
			while (str != null) {
				str = str.trim();

				allQueryList.add(new FullQuery(str));

				if (count % 10000 == 6)
					System.out.println(count);
				count++;
				str = br6.readLine();
			}
			br6.close();

			int call_num = 0;
			long startTime = System.currentTimeMillis();

			TreeMap<String, ArrayList<Integer>> tpSourceMap = new TreeMap<String, ArrayList<Integer>>();
			for (int windowIdx = 0; windowIdx < queryNum / windowSize; windowIdx++) {

				for (int queryIdx = windowIdx * windowSize; queryIdx < windowSize
						* (windowIdx + 1); queryIdx++) {

					FullQuery curFullQuery = allQueryList.get(queryIdx);

					SPARQLParser parser = new SPARQLParser();
					ParsedQuery query = parser.parseQuery(
							curFullQuery.getSPARQLStr(), null);
					System.out.println(curFullQuery.getSPARQLStr());
					System.out.println("myownlinebreak " + queryNum + " "
							+ windowIdx + " " + queryIdx);

					StatementPatternCollector collector = new StatementPatternCollector();
					query.getTupleExpr().visit(collector);

					List<StatementPattern> patterns = collector
							.getStatementPatterns();
					ArrayList<LocalQuery> curLocalQueryList = new ArrayList<LocalQuery>();

					int var_id = 0;
					String varStr = "";
					TreeMap<String, Integer> tmpVarIDMap = new TreeMap<String, Integer>();

					String curTriplePatternStr = "";
					for (int i = 0; i < patterns.size(); i++) {
						StatementPattern curPattern = patterns.get(i);
						TriplePattern myPattern = new TriplePattern();

						curTriplePatternStr = "";
						if (!curPattern.getSubjectVar().isConstant()) {
							myPattern.setSubjectVarTag(true);
							varStr = "?" + curPattern.getSubjectVar().getName();

							if (!tmpVarIDMap.containsKey(varStr)) {
								tmpVarIDMap.put(varStr, var_id);
								curFullQuery.getVarIDMap().put(varStr, var_id);
								curFullQuery.getIDVarMap().put(var_id, varStr);
								var_id++;
							}
							myPattern.setSubjectStr(varStr);
							curTriplePatternStr += varStr + "\t";
						} else {
							myPattern.setSubjectVarTag(false);
							curTriplePatternStr += "<"
									+ curPattern.getSubjectVar().getValue()
											.toString() + ">\t";
							myPattern.setSubjectStr("<"
									+ curPattern.getSubjectVar().getValue()
											.toString() + ">");
						}

						if (!curPattern.getPredicateVar().isConstant()) {
							myPattern.setPredicateVarTag(true);
							varStr = "?"
									+ curPattern.getPredicateVar().getName();
							if (!tmpVarIDMap.containsKey(varStr)) {
								tmpVarIDMap.put(varStr, var_id);
								curFullQuery.getVarIDMap().put(varStr, var_id);
								curFullQuery.getIDVarMap().put(var_id, varStr);
								var_id++;
							}
							curTriplePatternStr += varStr + "\t";
							myPattern.setPredicateStr(varStr);
						} else {
							myPattern.setPredicateVarTag(false);
							curTriplePatternStr += "<"
									+ curPattern.getPredicateVar().getValue()
											.toString() + ">\t";
							myPattern.setPredicateStr("<"
									+ curPattern.getPredicateVar().getValue()
											.toString() + ">");
						}

						if (!curPattern.getObjectVar().isConstant()) {
							myPattern.setObjectVarTag(true);
							varStr = "?" + curPattern.getObjectVar().getName();

							if (!tmpVarIDMap.containsKey(varStr)) {
								tmpVarIDMap.put(varStr, var_id);
								curFullQuery.getVarIDMap().put(varStr, var_id);
								curFullQuery.getIDVarMap().put(var_id, varStr);
								var_id++;
							}
							curTriplePatternStr += varStr + "\t";
							myPattern.setObjectStr(varStr);
						} else {
							myPattern.setObjectVarTag(false);
							String tmpConstantStr = curPattern.getObjectVar()
									.getValue().toString();
							if (!tmpConstantStr.startsWith("\"")) {
								curTriplePatternStr += "<" + tmpConstantStr
										+ ">\t";
								myPattern.setObjectStr("<" + tmpConstantStr
										+ ">");
							} else {
								tmpConstantStr = tmpConstantStr
										.replace(
												"^^<http://www.w3.org/2001/XMLSchema#string>",
												"");
								curTriplePatternStr += tmpConstantStr + "\t";
								myPattern.setObjectStr(tmpConstantStr);
							}

						}
						curTriplePatternStr = curTriplePatternStr.trim();
						curTriplePatternStr += ".";
						curFullQuery.addTriplePattern(myPattern);

						ArrayList<Integer> curSourceList = new ArrayList<Integer>();
						if (tpSourceMap.containsKey(myPattern.getSignature())) {
							curSourceList = tpSourceMap.get(myPattern
									.getSignature());
						} else {
							for (int k = 0; k < severList.size(); k++) {
								SeverInfo tmpSeverInfo = severList.get(k);
								Repository repo = new HTTPRepository(
										tmpSeverInfo.getSesameServer(),
										tmpSeverInfo.getRepositoryID());
								repo.initialize();

								RepositoryConnection con = repo.getConnection();

								String askQueryStr = "ask { "
										+ curTriplePatternStr + "}";
								// System.out.println(askQueryStr +
								// "---------");

								BooleanQuery booleanQuery = con
										.prepareBooleanQuery(
												QueryLanguage.SPARQL,
												askQueryStr);

								boolean ask_res = booleanQuery.evaluate();
								if (ask_res) {
									curSourceList.add(k);
								}

								con.close();
							}
							tpSourceMap.put(myPattern.getSignature(),
									curSourceList);
						}

						if (curSourceList.size() == 1) {
							int tag = 0;
							for (int k = 0; k < curLocalQueryList.size(); k++) {
								if (curLocalQueryList.get(k).getSourceList()
										.equals(curSourceList)) {
									curLocalQueryList.get(k).addTriplePattern(
											myPattern);
									tag = 1;
									break;
								}
							}

							if (0 == tag) {
								LocalQuery curLocalQuery = new LocalQuery();
								curLocalQuery.getSourceList().addAll(
										curSourceList);
								curLocalQuery.addTriplePattern(myPattern);
								curLocalQueryList.add(curLocalQuery);
							}
						} else {
							LocalQuery curLocalQuery = new LocalQuery();
							curLocalQuery.getSourceList().addAll(curSourceList);
							curLocalQuery.addTriplePattern(myPattern);
							curLocalQueryList.add(curLocalQuery);
						}

					}
					curFullQuery.addLocalQueries(curLocalQueryList);
				}
				// System.out.println(allQueryList);

				ArrayList<HittingSet> oneEdgeHittingList = new ArrayList<HittingSet>();

				for (int queryIdx = windowIdx * windowSize; queryIdx < windowSize
						* (windowIdx + 1); queryIdx++) {
					FullQuery curFullQuery = allQueryList.get(queryIdx);

					for (int j = 0; j < curFullQuery.getLocalQueryList().size(); j++) {
						LocalQuery curLocalQuery = curFullQuery
								.getLocalQuery(j);
						curLocalQuery.sort();
						// System.out.println(curLocalQuery.toSPARQLString());

						for (int k = 0; k < curLocalQuery
								.getTriplePatternList().size(); k++) {
							HittingSet tmpHittingSet = new HittingSet();
							tmpHittingSet.initializeTriplePattern(curLocalQuery
									.getTriplePatternList().get(k));

							int tmpIdx = oneEdgeHittingList
									.indexOf(tmpHittingSet);
							if (-1 == tmpIdx) {
								tmpHittingSet.addHittingPair(queryIdx, j);
								oneEdgeHittingList.add(tmpHittingSet);
							} else {
								if (!oneEdgeHittingList.get(tmpIdx).hasBeenHit(
										queryIdx)) {
									oneEdgeHittingList.get(tmpIdx)
											.addHittingPair(queryIdx, j);
								} else {
									tmpHittingSet.addHittingPair(queryIdx, j);
									oneEdgeHittingList.add(tmpHittingSet);
								}
							}
						}
					}
				}
				// System.out.println(oneEdgeHittingList);

				ArrayList<RewrittenQuery> rewrittenQueryList = new ArrayList<RewrittenQuery>();
				sortByHittingQueryNum(oneEdgeHittingList, 0,
						oneEdgeHittingList.size());
				// group all local queries into different sets
				while (oneEdgeHittingList.size() > 0) {
					HittingSet tmpHittingSet = oneEdgeHittingList.get(0);
					if (tmpHittingSet.getHittingQuerySet().size() == 0)
						break;

					// rewrite local queries
					rewrittenQueryList.addAll(RewrittenQuery.rewriteQueries(
							allQueryList, tmpHittingSet,
							rewrittenQueryList.size()));

					for (int j = 1; j < oneEdgeHittingList.size(); j++) {
						oneEdgeHittingList.get(j).removeQueries(tmpHittingSet);
					}

					sortByHittingQueryNum(oneEdgeHittingList, 1,
							oneEdgeHittingList.size());
				}

				for (int queryIdx = 0; queryIdx < rewrittenQueryList.size(); queryIdx++) {
					System.out.println("============"
							+ " begin to find results of rewritten query "
							+ queryIdx + " in " + windowIdx + "=============");
					RewrittenQuery curRewrittenQuery = rewrittenQueryList
							.get(queryIdx);
					HashSet<String> tmpMainVertexIDSet = new HashSet<String>(
							curRewrittenQuery.getMainPatternGraph()
									.getVertexIDmap().keySet());

					String curRewrittenQueryStr = curRewrittenQuery
							.toSPARQLString();
					ArrayList<Integer> curSourceList = curRewrittenQuery
							.getSourceList();

					System.out.println(curRewrittenQuery);
					System.out.println(curSourceList);
					System.out.println("************");
					System.out.println(curRewrittenQueryStr);
					System.out.println("************");

					if (curSourceList.size() == 0) {
						ArrayList<String> bindingNames = new ArrayList<String>();
						String tmpVarStr = "";
						for (int tpIdx = 0; tpIdx < curRewrittenQuery
								.getMainPatternGraph().getTriplePatternList()
								.size(); tpIdx++) {
							TriplePattern curTriplePattern = curRewrittenQuery
									.getMainPatternGraph().getTriplePattern(
											tpIdx);
							if (curTriplePattern.isSubjectVar()
									&& !bindingNames.contains(curTriplePattern
											.getSubjectStr())) {
								tmpVarStr = curTriplePattern.getSubjectStr();
								tmpVarStr = tmpVarStr.substring(1);
								bindingNames.add(tmpVarStr);
							}

							if (curTriplePattern.isPredicateVar()
									&& !bindingNames.contains(curTriplePattern
											.getPredicateStr())) {
								tmpVarStr = curTriplePattern.getPredicateStr();
								tmpVarStr = tmpVarStr.substring(1);
								bindingNames.add(tmpVarStr);
							}

							if (curTriplePattern.isObjectVar()
									&& !bindingNames.contains(curTriplePattern
											.getObjectStr())) {
								tmpVarStr = curTriplePattern.getObjectStr();
								tmpVarStr = tmpVarStr.substring(1);
								bindingNames.add(tmpVarStr);
							}
						}
						for (int bgpIdx = 0; bgpIdx < curRewrittenQuery
								.getOptionalPatternList().size(); bgpIdx++) {
							for (int tpIdx = 0; tpIdx < curRewrittenQuery
									.getOptionalPatternList().get(bgpIdx)
									.getTriplePatternList().size(); tpIdx++) {

								TriplePattern curTriplePattern = curRewrittenQuery
										.getOptionalPatternList().get(bgpIdx)
										.getTriplePattern(tpIdx);
								if (curTriplePattern.isSubjectVar()
										&& !bindingNames
												.contains(curTriplePattern
														.getSubjectStr())) {
									tmpVarStr = curTriplePattern
											.getSubjectStr();
									tmpVarStr = tmpVarStr.substring(1);
									bindingNames.add(tmpVarStr);
								}

								if (curTriplePattern.isPredicateVar()
										&& !bindingNames
												.contains(curTriplePattern
														.getPredicateStr())) {
									tmpVarStr = curTriplePattern
											.getPredicateStr();
									tmpVarStr = tmpVarStr.substring(1);
									bindingNames.add(tmpVarStr);
								}

								if (curTriplePattern.isObjectVar()
										&& !bindingNames
												.contains(curTriplePattern
														.getObjectStr())) {
									tmpVarStr = curTriplePattern.getObjectStr();
									tmpVarStr = tmpVarStr.substring(1);
									bindingNames.add(tmpVarStr);
								}
							}
						}
						curRewrittenQuery.addAllBindingNames(bindingNames);
						continue;
					}

					for (int k = 0; k < curSourceList.size(); k++) {
						call_num++;
						SeverInfo tmpSeverInfo = severList.get(curSourceList
								.get(k));
						Repository repo = new HTTPRepository(
								tmpSeverInfo.getSesameServer(),
								tmpSeverInfo.getRepositoryID());
						repo.initialize();

						RepositoryConnection con = repo.getConnection();

						TupleQuery tupleQuery = con.prepareTupleQuery(
								QueryLanguage.SPARQL, curRewrittenQueryStr);

						TupleQueryResult result = tupleQuery.evaluate();
						List<String> bindingNames = result.getBindingNames();
						if (k == 0) {
							curRewrittenQuery.addAllBindingNames(bindingNames);
						}

						int res_count = 0;
						while (result.hasNext()) {
							if (res_count++ == 100000)
								break;
							BindingSet bindingSet = result.next();
							String[] curRes = new String[bindingNames.size()];
							String[] curResOfMainPattern = new String[bindingNames
									.size()];
							int res_item_count = 0;
							for (int i = 0; i < bindingNames.size(); i++) {
								Value val_1 = bindingSet.getValue(bindingNames
										.get(i));
								if (val_1 != null) {
									curRes[i] = val_1.toString();
									if (tmpMainVertexIDSet.contains("?"
											+ bindingNames.get(i))) {
										curResOfMainPattern[i] = curRes[i];
									}
									res_item_count++;
								} else {
									curRes[i] = "";
								}
							}

							if (curRewrittenQuery.getBindingNames().size() == tmpMainVertexIDSet
									.size()
									|| res_item_count != tmpMainVertexIDSet
											.size()) {
								curRewrittenQuery.addResult(curRes);
							}

						}
						con.close();
					}

					// curRewrittenQuery
					// .distributeResultsInHittingQuery(allQueryList);
				}

				// firstRewrittenQuery
				// .printResultsInLog("E:/PengPeng/Data/WSDM/MQO/tmp_res_first.txt");
				LinkedList<IntermediateResults> IntermediateResultsQueue = new LinkedList<IntermediateResults>();
				ArrayList<IntermediateResults> finalIntermediateResultsList = new ArrayList<IntermediateResults>();

				IntermediateResultsQueue.add(new IntermediateResults(
						rewrittenQueryList.get(0)));
				rewrittenQueryList.remove(0);

				int queryIdx = 1;
				while (rewrittenQueryList.size() != 0) {

					System.out.println("============" + "begin to " + queryIdx
							+ "th join" + "=============" + queryNum);
					queryIdx++;

					LinkedList<IntermediateResults> newIntermediateResultsList = new LinkedList<IntermediateResults>();
					int curIntermediateResultsID = findNextJoinableRewrittenQuery(
							IntermediateResultsQueue, rewrittenQueryList);
					IntermediateResults curIntermediateResults = null;
					if (curIntermediateResultsID != -1) {
						curIntermediateResults = new IntermediateResults(
								rewrittenQueryList
										.get(curIntermediateResultsID));
						rewrittenQueryList.remove(curIntermediateResultsID);
					} else {
						finalIntermediateResultsList
								.addAll(IntermediateResultsQueue);
						IntermediateResultsQueue.clear();

						curIntermediateResults = new IntermediateResults(
								rewrittenQueryList.get(0));
						IntermediateResultsQueue.add(curIntermediateResults);
						rewrittenQueryList.remove(0);

						continue;
					}

					while (IntermediateResultsQueue.size() != 0) {

						IntermediateResults preIntermediateResults = IntermediateResultsQueue
								.pollLast();
						newIntermediateResultsList.addAll(IntermediateResults
								.Join(preIntermediateResults,
										curIntermediateResults));
					}
					if (curIntermediateResults.getHittingQuerySetGroup().size() != 0) {
						newIntermediateResultsList.add(curIntermediateResults);
					}
					IntermediateResultsQueue.addAll(newIntermediateResultsList);
				}
				finalIntermediateResultsList.addAll(IntermediateResultsQueue);

				for (int schemeIdx = 0; schemeIdx < finalIntermediateResultsList
						.size(); schemeIdx++) {
					System.out.println("+++++++++" + " begin to " + schemeIdx
							+ "th distribution " + "+++++++++");

					finalIntermediateResultsList.get(schemeIdx)
							.distributeResultsInFullQuery(allQueryList);

				}
			}

			long endTime = System.currentTimeMillis();

			PrintStream out = new PrintStream(new File(resFileStr));
			for (int queryIdx = 0; queryIdx < queryNum; queryIdx++) {
				FullQuery curFullQuery = allQueryList.get(queryIdx);

				for (int i = 0; i < curFullQuery.getResultList().size(); i++) {
					for (int j = 0; j < curFullQuery.getResultList().get(i).length; j++) {
						out.print(curFullQuery.getIDVarMap().get(j) + "\t"
								+ curFullQuery.getResultList().get(i)[j] + "\t");
					}
					out.println();
				}
				out.println("============there are "
						+ curFullQuery.getResultList().size()
						+ " results for query " + queryIdx + " =============");
			}

			out1.println("evaluation time = " + (endTime - startTime));
			out1.println("number of remote call = " + call_num);
			System.out.println("evaluation time = " + (endTime - startTime));
			System.out.println("number of remote call = " + call_num);
			System.out.println("end!!!");

			out.flush();
			out.close();

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int findNextJoinableRewrittenQuery(
			LinkedList<IntermediateResults> intermediateResultsQueue,
			ArrayList<RewrittenQuery> rewrittenQueryList) {

		for (int i = 0; i < rewrittenQueryList.size(); i++) {
			RewrittenQuery curRewrittenQuery = rewrittenQueryList.get(i);
			for (int j = 0; j < intermediateResultsQueue.size(); j++) {
				if (IntermediateResults.canJoin(
						intermediateResultsQueue.get(j), curRewrittenQuery)) {
					return i;
				}
			}
		}

		return -1;
	}

	private static void sortByHittingQueryNum(
			ArrayList<HittingSet> oneEdgeHittingList, int min, int max) {
		TreeMap<Integer, ArrayList<HittingSet>> countMap = new TreeMap<Integer, ArrayList<HittingSet>>();
		int cur_count = 0;

		for (int i = min; i < max; i++) {
			HittingSet curHittingSet = oneEdgeHittingList.get(i);
			cur_count = curHittingSet.getHittingQuerySet().size();
			if (!countMap.containsKey(cur_count)) {
				countMap.put(cur_count, new ArrayList<HittingSet>());
			}
			countMap.get(cur_count).add(curHittingSet);
		}

		Iterator<Entry<Integer, ArrayList<HittingSet>>> iter = countMap
				.entrySet().iterator();
		oneEdgeHittingList.clear();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<HittingSet>> e = iter.next();
			ArrayList<HittingSet> curList = e.getValue();
			oneEdgeHittingList.addAll(0, curList);
		}
	}

}
