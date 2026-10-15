package net.sachikonya.brewmaster.item;

public enum FlaskTier {
    BASE(1000),
    IRON(1500),
    GOLD(2000),
    DIAMOND(2500),
    NETHERITE(5000);

    private final int capacity;

    FlaskTier(int capacity) { this.capacity = capacity; }

    public int getCapacity() { return capacity; }

    public static FlaskTier byIndex(int index) {
        FlaskTier[] values = values();
        return values[Math.clamp(index, 0, values.length - 1)];
    }
}
