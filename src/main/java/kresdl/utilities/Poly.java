package kresdl.utilities;

import java.awt.Polygon;

import kresdl.geometry.Vec2;

/**
 * Convenience subclass of Polygon.
 */
@SuppressWarnings("serial")
public class Poly extends Polygon {

    /**
     * Constructs polygon with allocation for n points.
     *
     * @param nPoints number of points
     */
    public Poly(int nPoints) {
        super(new int[nPoints], new int[nPoints], nPoints);
    }

    /**
     * Initialize with three points.
     *
     * @param a point a
     * @param b point b
     * @param c point c
     * @param backFacing invert face cull
     * @return true if face is visible
     */
    public boolean init(Vec2 a, Vec2 b, Vec2 c, boolean backFacing) {
        if (!isVisible(a, b, c) ^ backFacing) {
            return false;
        }
        xpoints[0] = (int) a.x;
        xpoints[1] = (int) b.x;
        xpoints[2] = (int) c.x;
        ypoints[0] = (int) a.y;
        ypoints[1] = (int) b.y;
        ypoints[2] = (int) c.y;
        return true;
    }

    /**
     * Initialize with four points.
     *
     * @param a point a
     * @param b point b
     * @param c point c
     * @param d point d
     * @param backFacing invert face cull
     * @return true if face is visible
     */
    public boolean init(Vec2 a, Vec2 b, Vec2 c, Vec2 d, boolean backFacing) {
        if (!isVisible(a, b, c) ^ backFacing) {
            return false;
        }
        xpoints[0] = (int) a.x;
        xpoints[1] = (int) b.x;
        xpoints[2] = (int) c.x;
        xpoints[3] = (int) d.x;
        ypoints[0] = (int) a.y;
        ypoints[1] = (int) b.y;
        ypoints[2] = (int) c.y;
        ypoints[3] = (int) d.y;
        return true;
    }

    /**
     * Initialize with points using indexing.
     *
     * @param points array of points
     * @param indices array of indices
     * @param backFacing invert face cull
     * @return true if face is visible
     */
    public boolean init(Vec2[] points, int[] indices, boolean backFacing) {
        if (!isVisible(points[indices[0]], points[indices[1]], points[indices[2]]) ^ backFacing) {
            return false;
        }
        for (int q = 0; q < npoints; q++) {
            Vec2 t = points[indices[q]];
            xpoints[q] = (int) t.x;
            ypoints[q] = (int) t.y;
        }
        return true;
    }
    
    private static boolean isVisible(Vec2 a, Vec2 b, Vec2 c) {
        return (Vec2.cross(Vec2.sub(b, a), Vec2.sub(c, a)) < 0.0d);
    }
}
