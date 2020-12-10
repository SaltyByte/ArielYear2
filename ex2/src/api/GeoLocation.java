package api;

public class GeoLocation implements geo_location {

    private double x,y,z;

    public GeoLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public double distance(geo_location g) {
        return Math.sqrt(Math.pow(g.x() - x, 2) + Math.pow(g.y() - y, 2) + Math.pow(g.z() - z, 2));
    }
}
