package mqo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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

import Common.LocalQuery;
import Common.Tool;
import Common.TriplePattern;

public class MultiSourceQuery {

	public static void main(String[] args) {

		String str = "";
		int count = 0;
		String[] TermArr;

		try {
			ArrayList<RepositoryConnection> repoConnList = new ArrayList<RepositoryConnection>();

			InputStream in7 = new FileInputStream(new File("config.txt"));
			Reader inr7 = new InputStreamReader(in7);
			BufferedReader br7 = new BufferedReader(inr7);

			str = br7.readLine();
			while (str != null) {
				str = str.trim();
				TermArr = str.split("\t");

				Repository repo = new HTTPRepository(TermArr[0], TermArr[1]);
				repo.initialize();

				RepositoryConnection con = repo.getConnection();

				repoConnList.add(con);

				if (count % 10000 == 6)
					System.out.println(count);
				count++;
				str = br7.readLine();
			}
			br7.close();

			long startTime = System.currentTimeMillis();

			String queryString = "SELECT ?drugDesc ?cpd ?equation WHERE { "
					+ " ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/cathartics> . "
					+ " ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd . "
					+ " ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?drugDesc . "
					+ " ?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd . "
					+ " ?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme> . "
					+ " ?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme . "
					+ " ?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation . "
					+ " }";

			SPARQLParser parser = new SPARQLParser();
			ParsedQuery query = parser.parseQuery(queryString, null);
			System.out.println(query.toString());

			StatementPatternCollector collector = new StatementPatternCollector();
			query.getTupleExpr().visit(collector);

			List<StatementPattern> patterns = collector.getStatementPatterns();
			ArrayList<LocalQuery> localQueryList = new ArrayList<LocalQuery>();
			TreeMap<String, Integer> VarIDMap = new TreeMap<String, Integer>();
			TreeMap<Integer, String> IDVarMap = new TreeMap<Integer, String>();
			int var_id = 0;
			String varStr = "";

			String curTriplePatternStr = "";
			for (int i = 0; i < patterns.size(); i++) {
				StatementPattern curPattern = patterns.get(i);
				TriplePattern myPattern = new TriplePattern();

				curTriplePatternStr = "";
				if (!curPattern.getSubjectVar().isConstant()) {
					varStr = "?" + curPattern.getSubjectVar().getName();
					curTriplePatternStr += varStr + "\t";

					if (!VarIDMap.containsKey(varStr)) {
						VarIDMap.put(varStr, var_id);
						IDVarMap.put(var_id, varStr);
						var_id++;
					}
				} else {
					curTriplePatternStr += "<"
							+ curPattern.getSubjectVar().getValue().toString()
							+ ">\t";
				}

				if (!curPattern.getPredicateVar().isConstant()) {
					varStr = "?" + curPattern.getPredicateVar().getName();
					curTriplePatternStr += varStr + "\t";

					if (!VarIDMap.containsKey(varStr)) {
						VarIDMap.put(varStr, var_id);
						IDVarMap.put(var_id, varStr);
						var_id++;
					}
				} else {
					curTriplePatternStr += "<"
							+ curPattern.getPredicateVar().getValue()
									.toString() + ">\t";
				}

				if (!curPattern.getObjectVar().isConstant()) {
					varStr = "?" + curPattern.getObjectVar().getName();
					curTriplePatternStr += varStr + "\t";

					if (!VarIDMap.containsKey(varStr)) {
						VarIDMap.put(varStr, var_id);
						IDVarMap.put(var_id, varStr);
						var_id++;
					}
				} else {
					curTriplePatternStr += "<"
							+ curPattern.getObjectVar().getValue().toString()
							+ ">\t";
				}
				curTriplePatternStr = curTriplePatternStr.trim();
				curTriplePatternStr += ".";

				ArrayList<Integer> curSourceList = new ArrayList<Integer>();

				for (int k = 0; k < repoConnList.size(); k++) {
					String askQueryStr = "ask { " + curTriplePatternStr + "}";
					RepositoryConnection con = repoConnList.get(k);
					BooleanQuery booleanQuery = con.prepareBooleanQuery(
							QueryLanguage.SPARQL, askQueryStr);

					boolean ask_res = booleanQuery.evaluate();
					if (ask_res) {
						curSourceList.add(k);
					}
					System.out.println("results of asking triple pattern in "
							+ k + " ------" + curTriplePatternStr + " "
							+ ask_res);
				}

				if (curSourceList.size() == 1) {
					int tag = 0;
					for (int k = 0; k < localQueryList.size(); k++) {
						if (localQueryList.get(k).getSourceList()
								.equals(curSourceList)) {
							localQueryList.get(k).getTriplePatternList()
									.add(myPattern);
							tag = 1;
							break;
						}
					}

					if (0 == tag) {
						LocalQuery curLocalQuery = new LocalQuery();
						curLocalQuery.getSourceList().addAll(curSourceList);
						curLocalQuery.getTriplePatternList().add(myPattern);
						localQueryList.add(curLocalQuery);
					}
				} else {
					LocalQuery curLocalQuery = new LocalQuery();
					curLocalQuery.getSourceList().addAll(curSourceList);
					curLocalQuery.getTriplePatternList().add(myPattern);
					localQueryList.add(curLocalQuery);
				}
			}

			ArrayList<ArrayList<String[]>> allResList = new ArrayList<ArrayList<String[]>>();

			for (int j = 0; j < localQueryList.size(); j++) {
				LocalQuery curLocalQuery = localQueryList.get(j);
				ArrayList<Integer> curSourceList = curLocalQuery
						.getSourceList();
				String curLocalQueryStr = curLocalQuery.toSPARQLString();
				System.out.println(curSourceList);
				System.out.println(curLocalQueryStr);
				ArrayList<String[]> curResList = new ArrayList<String[]>();

				for (int k = 0; k < curSourceList.size(); k++) {
					RepositoryConnection con = repoConnList.get(curSourceList
							.get(k));
					TupleQuery tupleQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, curLocalQueryStr);

					TupleQueryResult result = tupleQuery.evaluate();
					List<String> bindingNames = result.getBindingNames();

					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						String[] curRes = new String[VarIDMap.size()];
						Arrays.fill(curRes, "");
						for (int i = 0; i < bindingNames.size(); i++) {
							varStr = "?" + bindingNames.get(i);
							var_id = VarIDMap.get(varStr);
							Value val_1 = bindingSet.getValue(bindingNames
									.get(i));
							curRes[var_id] = val_1.toString();
						}
						curResList.add(curRes);
					}
				}
				allResList.add(curResList);
			}

			System.out.println("result number = " + allResList.get(0).size());

			for (int i = 1; i < allResList.size(); i++) {
				System.out.println("result number before " + i + "th join = "
						+ allResList.get(i).size());
				if (allResList.get(0).size() != 0
						&& allResList.get(i).size() != 0) {
					int joining_pos = 0;
					for (int k = 0; k < VarIDMap.size(); k++) {
						if (!allResList.get(0).get(0)[k].equals("")
								&& !allResList.get(i).get(0)[k].equals("")) {
							joining_pos = k;
						}
					}
					Tool.Join(allResList.get(0), allResList.get(i), joining_pos);
				}
				System.out.println("result number after " + i + "th join = "
						+ allResList.get(0).size());
			}
			long endTime = System.currentTimeMillis();

			for (int k = 0; k < repoConnList.size(); k++) {
				repoConnList.get(k).close();
			}

			System.out.println("evaluation time = " + (endTime - startTime));

			PrintStream out = new PrintStream(new File("res.txt"));
			for (int k = 0; k < allResList.get(0).size(); k++) {
				for (int i = 0; i < allResList.get(0).get(k).length; i++) {
					out.print(IDVarMap.get(i) + "\t"
							+ allResList.get(0).get(k)[i] + "\t");
				}
				out.println();
			}
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
}
