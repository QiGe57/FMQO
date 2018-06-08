package mqo;

import java.io.File;
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
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

public class LocalQueryTest {

	public static void main(String[] args) {
		try {
			File dataDir = new File(
					"E:/software/FedX/sesameDB/repositories/myRepo19");
			Repository repo = new SailRepository(new NativeStore(dataDir));
			repo.initialize();

			RepositoryConnection con = repo.getConnection();

			String queryString = "select ?p ?x where{ "
					+ " <http://dbtune.org/jamendo/track/179>	?p	?x. " + " } ";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			long startTime = System.currentTimeMillis();
			TupleQueryResult result = tupleQuery.evaluate();
			long secondTime = System.currentTimeMillis();

			List<String> bindingNames = result.getBindingNames();
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value val_1 = bindingSet.getValue(bindingNames.get(0));
				Value val_2 = bindingSet.getValue(bindingNames.get(1));
				System.out.println(val_1);
				System.out.println(val_2);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("evaluation time = " + (secondTime - startTime));
			System.out.println("fetch time = " + (endTime - secondTime));
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
