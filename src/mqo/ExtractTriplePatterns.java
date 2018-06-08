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

public class ExtractTriplePatterns {
	static int totalQueryNum = 50;

	public static void main(String[] args) {

		try {

			String queryString = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ " PREFIX  geo:  <http://www.w3.org/2003/01/geo/wgs84_pos#> "
					+ " PREFIX  foaf: <http://xmlns.com/foaf/0.1/> "
					+ " SELECT  *  WHERE "
					+ "    { {?city rdfs:label \"Nathavas\"@en .} UNION {?alias <http://dbpedia.org/property/disambiguates> ?city . "
					+ "      ?alias rdfs:label ?n " + " } }";
			SPARQLParser parser = new SPARQLParser();
			ParsedQuery query = parser.parseQuery(queryString, null);

			System.out.println(query);

			StatementPatternCollector collector = new StatementPatternCollector();
			query.getTupleExpr().visit(collector);
			System.out.println(query);

			List<StatementPattern> patterns = collector.getStatementPatterns();

			for (int i = 0; i < patterns.size(); i++) {

				StatementPattern curPattern = patterns.get(i);

				System.out.println(TP2String(curPattern));

				QueryModelNode curQueryNode = curPattern.getParentNode();
				System.out.println(curQueryNode);
				// while (curQueryNode != null) {
				// System.out.println(curQueryNode.getSignature());
				// curQueryNode = curQueryNode.getParentNode();
				// }
			}

		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

}
