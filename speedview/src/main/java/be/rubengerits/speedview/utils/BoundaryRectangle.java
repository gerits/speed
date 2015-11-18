package be.rubengerits.speedview.utils;

public class BoundaryRectangle {

    private int xMin, yMin, xMax, yMax;

    public int getXMin() {
        return xMin;
    }

    public void validateXMin(int xMin) {
        this.xMin = this.xMin > 0 ? Math.min(this.xMin, xMin) : Math.max(0, xMin);
    }

    public int getYMin() {
        return yMin;
    }

    public void validateYMin(int yMin) {
        this.yMin = this.yMin > 0 ? Math.min(this.yMin, yMin) : Math.max(0, yMin);
    }

    public int getXMax() {
        return xMax;
    }

    public void validateXMax(int xMax) {
        this.xMax = Math.max(this.xMax, xMax);
    }

    public int getYMax() {
        return yMax;
    }

    public void validateYMax(int yMax) {
        this.yMax = Math.max(this.yMax, yMax);
    }

    public void clear() {
        xMin = 0;
        yMin = 0;
        xMax = 0;
        yMax = 0;
    }
}
