package mqo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

public class LocalLoadTest {

	public static void main(String[] args) {

		try {
			File dataDir = new File(
					"E:/PengPeng/Data/Yago2/yago2s/yagoGeonamesClassesSDB");
			Repository nativeRep = new SailRepository(new NativeStore(dataDir));
			nativeRep.initialize();

			String fileName = "E:/PengPeng/Data/Yago2/yago2s/yagoGeonamesClasses.ttl";
			File dataFile = new File(fileName);
			RepositoryConnection conn = nativeRep.getConnection();

			try {
				String baseURI = "http://example.org/example/local";
				conn.add(dataFile, baseURI, RDFFormat.TURTLE);
				System.out.println(fileName);
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
