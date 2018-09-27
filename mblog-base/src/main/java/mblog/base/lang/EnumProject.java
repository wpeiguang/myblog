package mblog.base.lang;

public enum EnumProject {

    BITFISH("BITFISH", "比特鱼"),
    BAOSHI_XINGQIU("BAOSHI_XINGQIU", "宝石星球");

    EnumProject(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
