package kresdl.utilities;

import kresdl.geometry.Matrix;
import kresdl.geometry.Vec;
import kresdl.geometry.Vec2;

/**
 * Camera
 */
public class Camera {

    protected Vec pos, target, dir;
    protected double aspectRatio, zNear, zFar, fov;
    protected boolean glSpace;

    protected Matrix view, proj, matrix;

    /**
     * Constructs camera with given parameters.
     *
     * @param pos position
     * @param target look-at position
     * @param aspectRatio viewport's width/height
     * @param zNear near z plane
     * @param zFar far z plane
     * @param fov angle of field-of-view in radians
     * @param glSpace adapt camera to OpenGL-space
     */
    public Camera(Vec pos, Vec target, double aspectRatio, double zNear, double zFar, double fov, boolean glSpace) {
        this.pos = pos;
        this.target = target;
        this.dir = Vec.nrm(Vec.sub(target, pos));
        this.aspectRatio = aspectRatio;
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
        this.glSpace = glSpace;

        if (glSpace) {
            view = Matrix.glView(pos, target);
            proj = Matrix.glProj(zNear, zFar, fov, aspectRatio);
        } else {
            view = Matrix.view(pos, target);
            proj = Matrix.proj(zNear, zFar, fov, aspectRatio);
        }
        matrix = Matrix.mul(view, proj);
    }

    protected void buildTransform() {
        if (glSpace) {
            view = Matrix.glView(pos, target);
        } else {
            view = Matrix.view(pos, target);
        }
        matrix = Matrix.mul(view, proj);
    }

    /**
     * Move camera.
     *
     * @param movement new position relative to current position
     */
    public void move(Vec movement) {
        pos = Vec.add(pos, movement);
        target = Vec.add(pos, dir);
        buildTransform();
    }

    /**
     * Rotate camera.
     *
     * @param movement mouse movement in screen pixels
     * @param sensitivity sensitivity
     */
    public void rotate(Vec2 movement, double sensitivity) {
        dir = Vec.nrm(Vec.tn(dir, Matrix.mul(Matrix.rx(-sensitivity * movement.y), Matrix.ry(sensitivity * movement.x))));
        target = Vec.add(pos, dir);
        buildTransform();
    }

    /**
     * Returns position.
     *
     * @return camera's position
     */
    public Vec getPos() {
        return pos;
    }

    /**
     * Returns view matrix.
     *
     * @return camera's view matrix
     */
    public Matrix getView() {
        return view;
    }

    /**
     * Returns projection matrix.
     *
     * @return camera's projection matrix
     */
    public Matrix getProj() {
        return proj;
    }

    /**
     * Returns matrix.
     *
     * @return camera's view-projection matrix
     */
    public Matrix getMatrix() {
        return matrix;
    }
}
