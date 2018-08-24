package mblog.core.persist.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

@Entity
@Table(name = "mto_search_task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SearchTaskPO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "job_key")
  private String jobKey;
  @Column(name = "exclude_key")
  private String excludeKey;
  @Column(name = "degree")
  private String degree;
  @Column(name = "sex")
  private String sex;
  @Column(name = "work_limit")
  private Long workLimit;
  @Column(name = "status")
  private String status;

  public SearchTaskPO(){

  }
  public SearchTaskPO(long id){
    this.id = id;
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getExcludeKey() {
    return excludeKey;
  }

  public void setExcludeKey(String excludeKey) {
    this.excludeKey = excludeKey;
  }

  public Long getWorkLimit() {
    return workLimit;
  }

  public void setWorkLimit(Long workLimit) {
    this.workLimit = workLimit;
  }
}
