package nl.topicus.puzzle;

import static java.lang.Math.abs;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;

public class Shape3D {
	// z y x
	private boolean[][][] space;

	private int[][][] blockNumbering;

	// http://www.euclideanspace.com/maths/algebra/matrix/transforms/examples/index.htm
	private static final List<FieldMatrix<Int>> ROTATIONS = List.of(m(1, 0, 0, 0, 1, 0, 0, 0, 1),
			m(1, 0, 0, 0, 0, -1, 0, 1, 0), m(1, 0, 0, 0, -1, 0, 0, 0, -1), m(1, 0, 0, 0, 0, 1, 0, -1, 0),
			m(0, -1, 0, 1, 0, 0, 0, 0, 1), m(0, 0, 1, 1, 0, 0, 0, 1, 0), m(0, 1, 0, 1, 0, 0, 0, 0, -1),
			m(0, 0, -1, 1, 0, 0, 0, -1, 0), m(-1, 0, 0, 0, -1, 0, 0, 0, 1), m(-1, 0, 0, 0, 0, -1, 0, -1, 0),
			m(-1, 0, 0, 0, 1, 0, 0, 0, -1), m(-1, 0, 0, 0, 0, 1, 0, 1, 0), m(0, 1, 0, -1, 0, 0, 0, 0, 1),
			m(0, 0, 1, -1, 0, 0, 0, -1, 0), m(0, -1, 0, -1, 0, 0, 0, 0, -1), m(0, 0, -1, -1, 0, 0, 0, 1, 0),
			m(0, 0, -1, 0, 1, 0, 1, 0, 0), m(0, 1, 0, 0, 0, 1, 1, 0, 0), m(0, 0, 1, 0, -1, 0, 1, 0, 0),
			m(0, -1, 0, 0, 0, -1, 1, 0, 0), m(0, 0, -1, 0, -1, 0, -1, 0, 0), m(0, -1, 0, 0, 0, 1, -1, 0, 0),
			m(0, 0, 1, 0, 1, 0, -1, 0, 0), m(0, 1, 0, 0, 0, -1, -1, 0, 0));

	public Shape3D(boolean[][][] space) {
		this.space = space;
	}

	public void calcBlockNumbering() {
		int number = 0;
		blockNumbering = new int[getDimZ()][getDimY()][getDimX()];
		for (int z = 0; z < getDimZ(); z++) {
			for (int y = 0; y < getDimY(); y++) {
				for (int x = 0; x < getDimX(); x++) {
					if (space[z][y][x]) {
						blockNumbering[z][y][x] = number;
						number++;
					} else {
						blockNumbering[z][y][x] = -1;
					}
				}
			}
		}
	}

	public Set<Shape3D> getRotations() {
		return ROTATIONS.stream().map(this::rotate).collect(Collectors.toSet());
	}

	public Shape3D rotate(FieldMatrix<Int> matrix) {
		FieldVector<Int> newDim = matrix.preMultiply(v(getDimX(), getDimY(), getDimZ()));
		int zs = abs(z(newDim));
		int ys = abs(y(newDim));
		int xs = abs(x(newDim));
		int zd = z(newDim) < 0 ? zs - 1 : 0;
		int yd = y(newDim) < 0 ? ys - 1 : 0;
		int xd = x(newDim) < 0 ? xs - 1 : 0;
		boolean[][][] newSpace = new boolean[zs][ys][xs];
		for (int z = 0; z < getDimZ(); z++) {
			for (int y = 0; y < getDimY(); y++) {
				for (int x = 0; x < getDimX(); x++) {
					if (space[z][y][x]) {
						FieldVector<Int> newCoor = matrix.preMultiply(v(x, y, z));
						newSpace[z(newCoor) + zd][y(newCoor) + yd][x(newCoor) + xd] = true;
					}
				}
			}
		}
		Shape3D ret = new Shape3D(newSpace);
//		System.out.println(this);
//		System.out.println(matrix);
//		System.out.println(ret);
//		System.out.println("=====================");
		return ret;
	}

	private int z(FieldVector<Int> vec) {
		return vec.getEntry(2).v();
	}

	private int y(FieldVector<Int> vec) {
		return vec.getEntry(1).v();
	}

	private int x(FieldVector<Int> vec) {
		return vec.getEntry(0).v();
	}

	public int getDimZ() {
		return space.length;
	}

	public int getDimY() {
		return space[0].length;
	}

	public int getDimX() {
		return space[0][0].length;
	}

	public boolean get(int x, int y, int z) {
		return space[z][y][x];
	}

	public int nr(int x, int y, int z) {
		return blockNumbering[z][y][x];
	}

	public boolean fits(Shape3D other, int xd, int yd, int zd) {
		if (getDimX() < other.getDimX() + xd || getDimY() < other.getDimY() + yd || getDimZ() < other.getDimZ() + zd)
			return false;

		for (int z = 0; z < other.getDimZ(); z++) {
			for (int y = 0; y < other.getDimY(); y++) {
				for (int x = 0; x < other.getDimX(); x++) {
					if (other.get(x, y, z) && !space[z + zd][y + yd][x + xd])
						return false;
				}
			}
		}
		return true;
	}

	private static FieldMatrix<Int> m(int... v) {
		return new Array2DRowFieldMatrix<>(new Int[][] { { new Int(v[0]), new Int(v[1]), new Int(v[2]) },
				{ new Int(v[3]), new Int(v[4]), new Int(v[5]) }, { new Int(v[6]), new Int(v[7]), new Int(v[8]) } });
	}

	private static FieldVector<Int> v(int... v) {
		return new ArrayFieldVector<>(new Int[] { new Int(v[0]), new Int(v[1]), new Int(v[2]) });
	}

	public String toString(int[] blocks) {
		int[][][] layout = new int[getDimZ()][getDimY()][getDimX()];

		for (int z = 0; z < getDimZ(); z++) {
			for (int y = 0; y < getDimY(); y++) {
				for (int x = 0; x < getDimX(); x++) {
					int nr = blockNumbering[z][y][x];
					int block = -1;
					if (nr != -1) {
						for (int curBlock = 0; curBlock < blocks.length; curBlock++) {
							if ((blocks[curBlock] & 1 << nr) > 0) {
								block = curBlock;
								break;
							}
						}
					}
					layout[z][y][x] = block;
				}
			}
		}

		StringBuilder ret = new StringBuilder();
		for (int z = 0; z < getDimZ(); z++) {
			ret.append(z).append("\n");
			for (int y = 0; y < getDimY(); y++) {
				for (int x = 0; x < getDimX(); x++) {
					int nr = layout[z][y][x];
					ret.append(nr == -1 ? " " : nr);
				}
				ret.append("\n");
			}
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int z = 0; z < getDimZ(); z++) {
			ret.append(z).append("\n");
			for (int y = 0; y < getDimY(); y++) {
				for (int x = 0; x < getDimX(); x++) {
					ret.append(space[z][y][x] ? "#" : " ");
				}
				ret.append("\n");
			}
		}
		return ret.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Shape3D && Arrays.deepEquals(space, ((Shape3D) obj).space);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(space);
	}
}
