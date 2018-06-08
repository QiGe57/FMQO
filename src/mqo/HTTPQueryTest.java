package mqo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class HTTPQueryTest {

	public static void main(String[] args) {

		// File dataDir = new File(
		// "E:/software/FedX/sesameDB/repositories/myRepo19");
		// Repository repo = new SailRepository(new NativeStore(dataDir));
		// repo.initialize();

		String sesameServer = "http://172.31.19.14:8080/sesame/";
		String repositoryID = "watdiv10M_8_7_db";

		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		try {
			repo.initialize();

			RepositoryConnection con = repo.getConnection();

			String queryString = loadQuery("E:/PengPeng/Data/DBPedia/MQO/test_query.txt");

			queryString = "select * where { <http://db.uwaterloo.ca/~galuc/wsdbm/User2>	<http://xmlns.com/foaf/age>	?rv_16_0 . } ";
			System.out.println(queryString);
			long startTime = System.currentTimeMillis();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			System.out.println("==========================");
			System.out.println(tupleQuery);
			TupleQueryResult result = tupleQuery.evaluate();

			List<String> bindingNames = result.getBindingNames();
			int res_count = 0;
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				// if (res_count % 10000 == 0) {
				// for (int i = 0; i < bindingNames.size(); i++) {
				// Value val_1 = bindingSet.getValue(bindingNames.get(i));
				// System.out.print(bindingNames.get(i) + "\t" + val_1 + "\t");
				// }
				// System.out.println();
				// System.out.println(res_count);
				// }
				res_count++;
			}
			long endTime = System.currentTimeMillis();
			System.out.println("evaluation time = " + (endTime - startTime));
			System.out.println("result number = " + res_count);
			con.close();
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
