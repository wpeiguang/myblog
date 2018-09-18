package mblog.core.persist.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mto_resume")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResumePO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String mobile;
  @Column(name = "resume_id")
  private String resumeId;
  @Column(name = "lock_date")
  private Date lockDate;
  private String source;
  private String job;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getResumeId() {
    return resumeId;
  }

  public void setResumeId(String resumeId) {
    this.resumeId = resumeId;
  }

  public Date getLockDate() {
    return lockDate;
  }

  public void setLockDate(Date lockDate) {
    this.lockDate = lockDate;
  }

  public String getJob() {
    return job;
  }

  public void setJob(String job) {
    this.job = job;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
