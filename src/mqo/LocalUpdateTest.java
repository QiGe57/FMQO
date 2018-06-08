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
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

public class LocalUpdateTest {

	public static void main(String[] args) {

		try {

			File dataDir = new File(
					"E:/software/FedX/sesameDB/repositories/myRepo05");
			Repository repo = new SailRepository(new NativeStore(dataDir));
			repo.initialize();

			RepositoryConnection conn = repo.getConnection();

			String updateQuery = "insert DATA { <http://dbpedia.org/resource/Aristotle>	<http://dbpedia.org/ontology/birthYear>	\"-0384\"^^<http://www.w3.org/2001/XMLSchema#gYear>. ";

			Update update = conn.prepareUpdate(QueryLanguage.SPARQL,
					updateQuery);
			update.execute();

			conn.commit();
			conn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (UpdateExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
