package resenje;

public class MemoryPoolInfo {
    private String name;
    private long used;
    private long free;

    public MemoryPoolInfo(String name, long used, long free) {
        this.name = name;
        this.used = used;
        this.free = free;
    }

    public String getName() {
        return name;
    }

    public long getUsed() {
        return used;
    }

    public long getFree() {
        return free;
    }

    public long getTotal() {
        return used + free;
    }

    public double getUsagePercentage() {
        long total = getTotal();
        return total > 0 ? (used * 100.0) / total : 0.0;
    }

}
