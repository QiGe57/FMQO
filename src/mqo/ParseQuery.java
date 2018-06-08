package mqo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.algebra.Add;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.evaluation.impl.CompareOptimizer;
import org.openrdf.query.algebra.helpers.QueryModelTreePrinter;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.query.parser.sparql.ast.ASTQueryContainer;
import org.openrdf.query.parser.sparql.ast.SyntaxTreeBuilder;

import Common.ParserTreeNode;
import Common.ParserTreeNode.NodeType;

public class ParseQuery {
	static int totalQueryNum = 50;

	public static void main(String[] args) {

		try {

			PrintStream out = new PrintStream(new File(
					"E:/PengPeng/Data/DBPedia/MQO/test_log.txt"));
			PrintStream out1 = new PrintStream(new File(
					"E:/PengPeng/Data/DBPedia/MQO/workload/query_all.txt"));

			String allQueryStr = loadQuery("E:/PengPeng/Data/DBPedia/MQO/query_workload.txt");

			String[] queryArr = allQueryStr.split("\n");
			for (int queryIdx = 0; queryIdx < queryArr.length; queryIdx++) {
				if (queryIdx == 60)
					System.out.println(queryArr[queryIdx]);
				if (queryIdx >= 183 && queryIdx < 215) {
					continue;
				}
				if (queryIdx >= 183 && queryIdx < 215) {
					continue;
				}
				if (queryIdx >= 241 && queryIdx < 272) {
					continue;
				}
				System.out.println(queryIdx);
				String queryString = queryArr[queryIdx];
				SPARQLParser parser = new SPARQLParser();
				ParsedQuery query = parser.parseQuery(queryString, null);
				out.println(query.toString());

				StatementPatternCollector collector = new StatementPatternCollector();
				query.getTupleExpr().visit(collector);

				List<StatementPattern> patterns = collector
						.getStatementPatterns();
				// each ArrayList<StatementPattern> maps to a BGP
				// initially, each statement maps to a BGP
				ArrayList<ArrayList<String>> BGPList = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<QueryModelNode>> invertedList = new ArrayList<ArrayList<QueryModelNode>>();
				for (int i = 0; i < patterns.size(); i++) {

					StatementPattern curPattern = patterns.get(i);
					invertedList.add(new ArrayList<QueryModelNode>());
					BGPList.add(new ArrayList<String>());
					BGPList.get(BGPList.size() - 1).add(TP2String(curPattern));

					// out.println("==================" + i +
					// "==================");
					// out.println(curPattern);

					QueryModelNode curQueryNode = curPattern.getParentNode();
					invertedList.get(invertedList.size() - 1).add(curQueryNode);
					while (curQueryNode != null) {
						out.println(curQueryNode);
						out.println("-------------------------"
								+ "---------------------------");

						curQueryNode = curQueryNode.getParentNode();
						invertedList.get(invertedList.size() - 1).add(
								curQueryNode);
					}
				}

				while (BGPList.size() > 1) {
					for (int i = invertedList.size() - 1; i > 0; i--) {
						if (invertedList.get(i - 1).get(0)
								.equals(invertedList.get(i).get(0))) {
							mergePatterns(BGPList.get(i - 1), BGPList.get(i),
									invertedList.get(i - 1).get(0));
							invertedList.get(i - 1).remove(0);
							invertedList.remove(i);
							BGPList.remove(i);
							i--;
						} else if (invertedList.get(i).get(0).getSignature()
								.equals("Filter")) {
							String filterExpStr = invertedList.get(i).get(0)
									.toString();

							String newFilterExpStr = handleFilterExp(filterExpStr);
							for (int k = 0; k < BGPList.get(i).size(); k++) {
								String bgpStr = BGPList.get(i).get(k) + "\n";
								bgpStr += newFilterExpStr;
								BGPList.get(i).set(k, bgpStr);
							}
							invertedList.get(i).remove(0);
						}
					}
				}

				out.println("*****************************"
						+ "*****************************");

				out.println(BGPList.size());
				out.println(BGPList.get(0).size());
				for (int i = 0; i < BGPList.get(0).size(); i++) {
					out.println(BGPList.get(0).get(i));
					out.println("-------------------------"
							+ "-------------------------");

					String newBGPStr = "";
					int tag = 0;
					String bgpStr = BGPList.get(0).get(i);
					bgpStr = bgpStr.trim();
					String[] TermArr = bgpStr.split("\n");
					newBGPStr = "select * where { ";
					for (int k = 0; k < TermArr.length; k++) {
						TermArr[k] = TermArr[k].trim();
						String[] TermArr1 = TermArr[k].split("\t");
						if (TermArr1[0].startsWith("?")
								&& TermArr1[1]
										.startsWith("<http://www.w3.org/2000/01/rdf-schema#label>")
								&& TermArr1[2].startsWith("?")) {
							tag = 1;
						}
						newBGPStr += TermArr[k] + "  ";
					}
					newBGPStr += " } ";

					if (tag == 0)
						out1.println(newBGPStr);
				}
			}

			out.flush();
			out.close();

			out1.flush();
			out1.close();

		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String handleFilterExp(String filterExpStr) {
		String newFilterStr = "", varStr = "", valueStr = "", opStr = "";
		String[] TermArr = filterExpStr.split("\n");
		int tag = 0, tag2 = 0;
		for (int i = 1; i < TermArr.length; i++) {
			TermArr[i] = TermArr[i].trim();
			if (TermArr[i].startsWith("Union") || TermArr[i].startsWith("Join")
					|| TermArr[i].startsWith("Filter")
					|| TermArr[i].startsWith("LeftJoin")) {
				break;
			} else if (TermArr[i].startsWith("Compare")) {
				opStr = TermArr[i].replace("Compare (", "");
				opStr = opStr.substring(0, opStr.length() - 1);
				newFilterStr = varStr;
				tag = 1;
			} else if (TermArr[i].startsWith("Regex")) {
				newFilterStr = varStr;
				tag = 2;
			} else if (TermArr[i].startsWith("Lang")) {
				tag2 = 3;
			} else if (TermArr[i].startsWith("ValueConstant")) {
				TermArr[i] = TermArr[i].substring(0, TermArr[i].length() - 1);
				valueStr = TermArr[i].replace("ValueConstant (value=", "");
				if (tag == 1) {
					newFilterStr += " " + opStr + " " + valueStr;
				} else if (tag == 2) {
					newFilterStr += ", " + valueStr;
					newFilterStr += ", " + valueStr;
				}
			} else if (TermArr[i].startsWith("Var")) {
				TermArr[i] = TermArr[i].substring(0, TermArr[i].length() - 1);
				varStr = "?" + TermArr[i].replace("Var (name=", "");
				if (tag2 == 3) {
					varStr = " lang(" + varStr + ") ";
				}
			}
		}

		newFilterStr = "FILTER ( " + varStr + " " + newFilterStr + " )";
		return newFilterStr;
	}

	private static String TP2String(StatementPattern curPattern) {
		String curTriplePatternStr = "";

		if (!curPattern.getSubjectVar().isConstant()) {
			curTriplePatternStr += "?" + curPattern.getSubjectVar().getName()
					+ "\t";
		} else {
			curTriplePatternStr += "<"
					+ curPattern.getSubjectVar().getValue().toString() + ">\t";
		}

		if (!curPattern.getPredicateVar().isConstant()) {
			curTriplePatternStr += "?" + curPattern.getPredicateVar().getName()
					+ "\t";
		} else {
			curTriplePatternStr += "<"
					+ curPattern.getPredicateVar().getValue().toString()
					+ ">\t";
		}

		if (!curPattern.getObjectVar().isConstant()) {
			curTriplePatternStr += "?" + curPattern.getObjectVar().getName()
					+ "\t";
		} else {
			System.out.println(curPattern.getObjectVar().getValue().toString());
			if (!curPattern.getObjectVar().getValue().toString()
					.startsWith("\"")) {
				curTriplePatternStr += "<"
						+ curPattern.getObjectVar().getValue().toString()
						+ ">\t";
			} else {
				curTriplePatternStr += curPattern.getObjectVar().getValue()
						.toString()
						+ "\t";
			}
		}
		curTriplePatternStr += ".";

		return curTriplePatternStr;
	}

	private static void mergePatterns(ArrayList<String> BGPList1,
			ArrayList<String> BGPList2, QueryModelNode mergeMode) {
		String curModeStr = mergeMode.getSignature();
		if (curModeStr.startsWith("Union")) {
			BGPList1.addAll(BGPList2);
		} else if (curModeStr.startsWith("Join")) {
			ArrayList<String> newBGPList = new ArrayList<String>();
			for (int i = 0; i < BGPList1.size(); i++) {
				for (int j = 0; j < BGPList2.size(); j++) {
					String tmpBGP = BGPList1.get(i) + "\n";
					tmpBGP += BGPList2.get(j);
					newBGPList.add(tmpBGP);
				}
			}
			BGPList1.clear();
			BGPList1.addAll(newBGPList);
		} else if (curModeStr.startsWith("LeftJoin")) {
			ArrayList<String> newBGPList = new ArrayList<String>();
			for (int i = 0; i < BGPList1.size(); i++) {
				for (int j = 0; j < BGPList2.size(); j++) {
					String tmpBGP = BGPList1.get(i) + "\n";
					tmpBGP += BGPList2.get(j);
					newBGPList.add(tmpBGP);
				}
			}
			BGPList1.addAll(newBGPList);
		}
	}

	private static String loadQuery(String fileStr) throws IOException {
		File tmpFile = new File(fileStr);
		InputStream in7 = new FileInputStream(tmpFile);
		Long filelength = tmpFile.length();
		byte[] filecontent = new byte[filelength.intValue()];
		in7.read(filecontent);

		in7.close();
		return new String(filecontent);
	}
}
