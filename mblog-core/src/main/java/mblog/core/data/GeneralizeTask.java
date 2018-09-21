package mblog.core.data;

import java.io.Serializable;

public class GeneralizeTask implements Serializable {
    private long id;

    private String project;

    private String code;

    private int inteval;

    private int amount;

    private int successCount;

    private int remainCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(int remainCount) {
        this.remainCount = remainCount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getInteval() {
        return inteval;
    }

    public void setInteval(int inteval) {
        this.inteval = inteval;
    }
}
