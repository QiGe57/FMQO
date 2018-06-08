package mqo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import Common.FullQuery;

public class VerifyMultipleOPTIONAlCost {

	public static void main(String[] args) {

		try {

			ArrayList<String> allQueryList = new ArrayList<String>();

			InputStream in1 = new FileInputStream(new File(
					"E:/PengPeng/Data/WSDM/MQO/"
							+ "optional_rewritten_queries.txt"));
			Reader inr1 = new InputStreamReader(in1);
			BufferedReader br1 = new BufferedReader(inr1);

			String str = br1.readLine();
			String queryStr = "";
			int count = 0, OPTIONAL_num = 6;
			while (str != null) {
				if (str.startsWith("***********")) {
					allQueryList.add(queryStr);

					queryStr = "";
					count++;
				} else {
					queryStr += str + " ";
				}
				str = br1.readLine();
			}

			br1.close();

			PrintStream out1 = new PrintStream(new File(
					"E:/PengPeng/Data/WSDM/MQO/optional_cost_sesame.txt"));

			for (int i = 0; i < allQueryList.size(); i++) {

				String qiry = allQueryList.get(i);
				if (qiry.contains("ASK") || qiry.contains("ask")) {
					continue;
				}
				String[] TermArr = qiry.split("OPTIONAL");
				if (TermArr.length <= OPTIONAL_num) {
					continue;
				}
				qiry = TermArr[0] + "   ";
				for (int queryIdx = 0; queryIdx < OPTIONAL_num; queryIdx++) {
					qiry = qiry.trim();
					qiry = qiry.substring(0, qiry.length() - 2);
					if (queryIdx == 0) {
						qiry = qiry + "  }";
					} else if (queryIdx != TermArr.length - 1) {
						qiry = qiry + " OPTIONAL " + TermArr[queryIdx] + "  }";
					} else {
						qiry = qiry + " OPTIONAL " + TermArr[queryIdx];
					}

					String sesameServer = "http://172.31.19.14:8080/sesame/";
					String repositoryID = "watdiv10M_8_7_db";

					Repository repo = new HTTPRepository(sesameServer,
							repositoryID);
					repo.initialize();

					RepositoryConnection con = repo.getConnection();
					System.out.println(qiry);
					long startTime = System.currentTimeMillis();
					TupleQuery tupleQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, qiry);
					System.out.println(i + " " + queryIdx
							+ "==========================");
					System.out.println(tupleQuery);
					TupleQueryResult result = tupleQuery.evaluate();

					List<String> bindingNames = result.getBindingNames();
					int res_count = 0;
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						// if (res_count % 10000 == 0) {
						// for (int i = 0; i < bindingNames.size(); i++) {
						// Value val_1 =
						// bindingSet.getValue(bindingNames.get(i));
						// System.out.print(bindingNames.get(i) + "\t" + val_1 +
						// "\t");
						// }
						// System.out.println();
						// System.out.println(res_count);
						// }
						res_count++;
					}
					long endTime = System.currentTimeMillis();
					System.out.println("evaluation time = "
							+ (endTime - startTime));
					System.out.println("result number = " + res_count);
					con.close();

					out1.print(i + ", ");
					out1.print(res_count + ", ");
					out1.print((endTime - startTime) + ", ");
					out1.println(queryIdx);
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
