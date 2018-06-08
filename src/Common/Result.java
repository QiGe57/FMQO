package Common;

import java.util.Arrays;

public class Result implements Comparable<Result> {
	public String[] itemArr;

	public Result(String[] itemArr) {
		super();
		this.itemArr = itemArr;
	}

	@Override
	public String toString() {
		return "Result [itemArr=" + Arrays.toString(itemArr) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(itemArr);
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
		Result other = (Result) obj;
		if (!Arrays.equals(itemArr, other.itemArr))
			return false;
		return true;
	}

	@Override
	public int compareTo(Result o) {
		return Arrays.toString(itemArr).compareTo(Arrays.toString(o.itemArr));
	}
}
