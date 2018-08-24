package mblog.base.lang;

public enum EnumTaskStatus {
    STOPED("STOPED", "未运行"),
    RUNNING("RUNNING", "运行中");

    EnumTaskStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;

    private String value;

//    public String getValueByName(String name){
//        if(EnumTaskStatus.STOPED.name.equals(name)){
//            return EnumTaskStatus.STOPED.value;
//        }
//        if(EnumTaskStatus.RUNNING.name.equals(name)){
//            return EnumTaskStatus.RUNNING.value;
//        }
//        return null;
//    }

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
