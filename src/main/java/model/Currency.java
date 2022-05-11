package model;

public enum Currency {
    RUB(1.0), USD(60.0), EUR(80.0);

    private final double coef;

    Currency(double coef) {
        this.coef = coef;
    }

    public double convertFromRub(double price) {
        return price / coef;
    }
}
