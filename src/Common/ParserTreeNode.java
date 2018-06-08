package Common;

import org.openrdf.query.algebra.StatementPattern;

public class ParserTreeNode {
	public enum NodeType {
		Leaf, InterNode, Root;
	}

	private StatementPattern tp;
	private NodeType tag;
	private ParserTreeNode left;
	private ParserTreeNode right;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tp == null) ? 0 : tp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParserTreeNode other = (ParserTreeNode) obj;
		if (tp == null) {
			if (other.tp != null)
				return false;
		} else if (!tp.equals(other.tp))
			return false;
		return true;
	}

	public ParserTreeNode(StatementPattern tp, NodeType tag) {
		super();
		this.tp = tp;
		this.tag = tag;
	}

	public StatementPattern getTp() {
		return tp;
	}

	public void setTp(StatementPattern tp) {
		this.tp = tp;
	}

	public NodeType getTag() {
		return tag;
	}

	public void setTag(NodeType tag) {
		this.tag = tag;
	}

}
