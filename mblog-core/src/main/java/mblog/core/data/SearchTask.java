package mblog.core.data;

import java.io.Serializable;

public class SearchTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String jobKey;

    private String excludeKey;

    private String degree;

    private String sex;

    private Long workLimit;

    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExcludeKey() {
        return excludeKey;
    }

    public void setExcludeKey(String excludeKey) {
        this.excludeKey = excludeKey;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Long getWorkLimit() {
        return workLimit;
    }

    public void setWorkLimit(Long workLimit) {
        this.workLimit = workLimit;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
