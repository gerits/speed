package be.rubengerits.speedview;

public enum SpeedUnit {
    metric(3.6f),
    imperial(2.236f);

    private final float factor;

    SpeedUnit(float factor) {
        this.factor = factor;
    }

    public float getFactor() {
        return factor;
    }

    public float convertFromMetersPerSecond(float metersPerSecond, int roundLevel) {
        float result = metersPerSecond * factor;

        return Math.round(result / Math.pow(10f, roundLevel) * Math.pow(10f, roundLevel));
    }

}
