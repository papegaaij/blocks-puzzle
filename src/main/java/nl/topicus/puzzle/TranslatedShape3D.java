package nl.topicus.puzzle;

public class TranslatedShape3D {
	private Shape3D shape;
	private int xd;
	private int yd;
	private int zd;

	public TranslatedShape3D(Shape3D shape, int xd, int yd, int zd) {
		this.shape = shape;
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
	}

	public Shape3D getShape() {
		return shape;
	}

	public int getXd() {
		return xd;
	}

	public int getYd() {
		return yd;
	}

	public int getZd() {
		return zd;
	}

	public int toState(Shape3D target) {
		int ret = 0;
		for (int z = 0; z < shape.getDimZ(); z++) {
			for (int y = 0; y < shape.getDimY(); y++) {
				for (int x = 0; x < shape.getDimX(); x++) {
					if (shape.get(x, y, z)) {
						int nr = target.nr(x + xd, y + yd, z + zd);
						if (nr < 0)
							throw new IllegalStateException();
						ret |= (1 << nr);
					}
				}
			}
		}
		return ret;
	}
}
