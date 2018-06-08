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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

public class HTTPQueryMultiTest {

	static String DataSetStr = "DBPedia";

	public static void main(String[] args) {
		try {
			String qiry = "";

			ArrayList<String> allQueryList = new ArrayList<String>();

			InputStream in1 = new FileInputStream(new File(""));
			Reader inr1 = new InputStreamReader(in1);
			BufferedReader br1 = new BufferedReader(inr1);

			String str = br1.readLine();
			String queryStr = "";
			int count = 0;
			while (str != null) {
				if (str.startsWith("myownlinebreak")) {
					if (count != 15) {
						allQueryList.add("select * where { " + queryStr + " }");
						System.out.println(queryStr);
					}

					// allQueryList.add(queryStr);
					if (allQueryList.size() == 2000) {
						break;
					}

					queryStr = "";
					count++;
				} else {
					queryStr += str + " ";
				}
				str = br1.readLine();
			}

			br1.close();

			int res_count = 0;
			ArrayList<Long> timeList = new ArrayList<Long>();
			ArrayList<Integer> costList = new ArrayList<Integer>();
			ArrayList<Integer> resCountList = new ArrayList<Integer>();
			long startTime = System.currentTimeMillis();

			for (int i = 0; i < allQueryList.size(); i++) {

				qiry = allQueryList.get(i);
				if (qiry.contains("ASK") || qiry.contains("ask")) {
					continue;
				}
				String sesameServer = "http://172.31.19.14:8080/sesame/";
				String repositoryID = "dbpedia_8_7_db";
				// String repositoryID = "dbpedia_db";

				Repository repo = new HTTPRepository(sesameServer, repositoryID);
				repo.initialize();

				RepositoryConnection con = repo.getConnection();
				System.out.println("======" + i + "=======");
				System.out.println(qiry);

				long startTime1 = System.currentTimeMillis();
				TupleQuery tupleQuery = con.prepareTupleQuery(
						QueryLanguage.SPARQL, qiry);
				TupleQueryResult result = tupleQuery.evaluate();

				List<String> bindingNames = result.getBindingNames();
				res_count = 0;
				while (result.hasNext()) {
					// BindingSet bindingSet = result.next();
					// for (int var_id = 0; var_id < bindingNames.size();
					// var_id++) {
					// Value val_1 = bindingSet.getValue(bindingNames
					// .get(var_id));
					// System.out.print(bindingNames.get(var_id) + "\t"
					// + val_1 + "\t");
					// }
					// System.out.println();
					res_count++;
				}
				long endTime1 = System.currentTimeMillis();

				timeList.add(endTime1 - startTime1);
				System.out.println("evaluation time = "
						+ (endTime1 - startTime1));
				resCountList.add(res_count);
				int tmp_sel_val = 0;
				costList.add(tmp_sel_val);
				con.close();
			}
			long endTime = System.currentTimeMillis();
			System.out.println("evaluation time = " + (endTime - startTime));

			System.out.println("End to query!");
			System.out.println(res_count);

			PrintStream out1 = new PrintStream(new File(
					"E:/PengPeng/Data/DBPedia/res_info.txt"));
			// PrintStream out1 = new PrintStream(new File("E:/PengPeng/Data/"
			// + DataSetStr + "/MQO/sesame_res_info.txt"));

			for (int i = 0; i < resCountList.size(); i++) {
				if (resCountList.get(i) != 0) {
					out1.print(i + ", ");
					out1.print(resCountList.get(i) + ", ");
					out1.print(timeList.get(i) + ", ");
					out1.println(costList.get(i));
				}
			}
			out1.flush();
			out1.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ArrayList<String> findAllTPs(String queryStr)
			throws MalformedQueryException {
		ArrayList<String> tpList = new ArrayList<String>();

		SPARQLParser parser = new SPARQLParser();
		ParsedQuery query = parser.parseQuery(queryStr, null);

		StatementPatternCollector collector = new StatementPatternCollector();
		query.getTupleExpr().visit(collector);

		List<StatementPattern> patterns = collector.getStatementPatterns();
		for (int i = 0; i < patterns.size(); i++) {
			StatementPattern curPattern = patterns.get(i);

			String curTriplePatternStr = "", varStr = "";
			if (!curPattern.getSubjectVar().isConstant()) {
				varStr = "?" + curPattern.getSubjectVar().getName();
				curTriplePatternStr += varStr + "\t";
			} else {
				curTriplePatternStr += "<"
						+ curPattern.getSubjectVar().getValue().toString()
						+ ">\t";
			}

			if (!curPattern.getPredicateVar().isConstant()) {
				varStr = "?" + curPattern.getPredicateVar().getName();
				curTriplePatternStr += varStr + "\t";
			} else {
				curTriplePatternStr += "<"
						+ curPattern.getPredicateVar().getValue().toString()
						+ ">\t";
			}

			if (!curPattern.getObjectVar().isConstant()) {
				varStr = "?" + curPattern.getObjectVar().getName();
				curTriplePatternStr += varStr + "\t";
			} else {
				curTriplePatternStr += "<"
						+ curPattern.getObjectVar().getValue().toString()
						+ ">\t";
			}
			curTriplePatternStr = curTriplePatternStr.trim();
			curTriplePatternStr += ".";

			tpList.add(curTriplePatternStr);
		}

		return tpList;
	}

}
