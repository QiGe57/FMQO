package Common;

public class Pair<T1, T2> implements Comparable<Pair<T1, T2>> {
	public T1 first;
	public T2 second;

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public int compareTo(Pair<T1, T2> o) {
		if (this.first.toString().compareTo(o.toString()) != 0) {
			return this.first.toString().compareTo(o.second.toString());
		}
		return this.second.toString().compareTo(o.second.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
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
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

}
