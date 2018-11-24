package nl.topicus.puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {
	private static final Shape3D L1 = new Shape3D(new boolean[][][] { { { true, false }, { true, true } } });

	private static final Shape3D L2 = new Shape3D(
			new boolean[][][] { { { true, false, false }, { true, true, true } } });

	private static final Shape3D Z = new Shape3D(
			new boolean[][][] { { { true, true, false }, { false, true, true } } });

	private static final Shape3D T = new Shape3D(
			new boolean[][][] { { { false, true, false }, { true, true, true } } });

	private int[][] blocks = new int[4][];

	private int[] avail = { 1, 4, 1, 1 };

	private Shape3D target;

	private int steps = 0;

	public Solver(Shape3D target) {
		this.target = target;
		target.calcBlockNumbering();
		blocks[0] = toBlocks(L1, target);
		blocks[1] = toBlocks(L2, target);
		blocks[2] = toBlocks(Z, target);
		blocks[3] = toBlocks(T, target);
	}

	private int[] toBlocks(Shape3D block, Shape3D target) {
		return block.getRotations().stream().flatMap(s -> generateTranslations(target, s).stream())
				.filter(s -> target.fits(s.getShape(), s.getXd(), s.getYd(), s.getZd()))
				.mapToInt(s -> s.toState(target)).toArray();
	}

	private List<TranslatedShape3D> generateTranslations(Shape3D target, Shape3D block) {
		List<TranslatedShape3D> ret = new ArrayList<>();
		for (int z = 0; z <= target.getDimZ() - block.getDimZ(); z++) {
			for (int y = 0; y <= target.getDimY() - block.getDimY(); y++) {
				for (int x = 0; x <= target.getDimX() - block.getDimX(); x++) {
					ret.add(new TranslatedShape3D(block, x, y, z));
				}
			}
		}
		return ret;
	}

	public List<int[]> solve() {
		long start = System.currentTimeMillis();
		List<int[]> solutions = new ArrayList<>();
		solve(new int[] { 0, 0, 0, 0 }, 0, 0, new int[7], 0, solutions);
		long end = System.currentTimeMillis();
		System.out.println(
				"Found " + solutions.size() + " solution(s) in " + (end - start) + "ms using " + steps + " steps");
		if (solutions.size() > 0) {
			System.out.println(target.toString(solutions.get(0)));
		}
		return solutions;
	}

	private void solve(int[] used, int state, int depth, int[] placed, int placeCount, List<int[]> solutions) {
		steps++;
		if (depth == 27) {
			solutions.add(Arrays.copyOf(placed, placed.length));
			return;
		}

		if ((state & 1 << depth) > 0) {
			solve(used, state, depth + 1, placed, placeCount, solutions);
		}

		for (int blockType = 0; blockType < blocks.length; blockType++) {
			if (used[blockType] < avail[blockType]) {
				for (int block : blocks[blockType]) {
					if ((block & 1 << depth) > 0 && (block & state) == 0) {
						int newState = state | block;
						used[blockType]++;
						placed[placeCount] = block;
						solve(used, newState, depth + 1, placed, placeCount + 1, solutions);
						used[blockType]--;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Shape3D cube = new Shape3D(
				new boolean[][][] { { { true, true, true }, { true, true, true }, { true, true, true } },
						{ { true, true, true }, { true, true, true }, { true, true, true } },
						{ { true, true, true }, { true, true, true }, { true, true, true } } });
		new Solver(cube).solve();

		Shape3D shape1 = new Shape3D(new boolean[][][] {
				{ { true, true, true, false, false }, { true, true, true, true, false },
						{ true, true, true, true, true }, { false, true, true, true, true },
						{ false, false, true, true, true } },
				{ { false, false, false, false, false }, { false, true, true, true, false },
						{ false, true, false, true, false }, { false, true, true, true, false },
						{ false, false, false, false, false } } });
		new Solver(shape1).solve();

		Shape3D shape2 = new Shape3D(new boolean[][][] {
				{ { true, true, true, false, false }, { true, true, true, true, false },
						{ true, true, false, true, true }, { false, true, true, true, true },
						{ false, false, true, true, true } },
				{ { false, false, false, false, false }, { false, true, true, true, false },
						{ false, true, true, true, false }, { false, true, true, true, false },
						{ false, false, false, false, false } } });
		new Solver(shape2).solve();

		Shape3D shapeT = new Shape3D(
				new boolean[][][] { { { true, true, true }, { true, true, true }, { true, true, true } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } } });
		new Solver(shapeT).solve();

		Shape3D shapeI = new Shape3D(
				new boolean[][][] { { { true, true, true }, { true, true, true }, { true, true, true } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { false, false, false }, { true, true, true }, { false, false, false } },
						{ { true, true, true }, { true, true, true }, { true, true, true } } });
		new Solver(shapeI).solve();

		Shape3D stairs = new Shape3D(new boolean[][][] {
				{ { true, false, false }, { true, true, false }, { true, true, true }, { false, true, true },
						{ false, false, true } },
				{ { true, false, false }, { true, true, false }, { true, true, true }, { false, true, true },
						{ false, false, true } },
				{ { true, false, false }, { true, true, false }, { true, true, true }, { false, true, true },
						{ false, false, true } } });
		new Solver(stairs).solve();
	}
}
