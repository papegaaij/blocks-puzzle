package nl.topicus.puzzle;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;

public class Int implements FieldElement<Int> {
	public static final Int ZERO = new Int(0);
	public static final Int ONE = new Int(1);
	public static final Int MIN_ONE = new Int(-1);

	private int value;

	public Int(int value) {
		this.value = value;
	}
	
	public int v() {
		return value;
	}

	@Override
	public Int add(Int a) throws NullArgumentException {
		return new Int(value + a.value);
	}

	@Override
	public Int subtract(Int a) throws NullArgumentException {
		return new Int(value - a.value);
	}

	@Override
	public Int negate() {
		return new Int(-value);
	}

	@Override
	public Int multiply(int n) {
		return new Int(value * n);
	}

	@Override
	public Int multiply(Int a) throws NullArgumentException {
		return new Int(value * a.value);
	}

	@Override
	public Int divide(Int a) throws NullArgumentException, MathArithmeticException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Int reciprocal() throws MathArithmeticException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Int && ((Int) obj).value == value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	private static final Field<Int> INSTANCE = new Field<Int>() {
		@Override
		public Int getZero() {
			return ZERO;
		}

		@Override
		public Int getOne() {
			return ONE;
		}

		@Override
		public Class<? extends FieldElement<Int>> getRuntimeClass() {
			return Int.class;
		}
	};

	@Override
	public Field<Int> getField() {
		return INSTANCE;
	}
}