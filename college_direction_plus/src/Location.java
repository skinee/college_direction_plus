public class Location {
    private int id;
    private String name;
    private String description;

    public Location(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "名称: " + name + ", 描述: " + description;
    }
}