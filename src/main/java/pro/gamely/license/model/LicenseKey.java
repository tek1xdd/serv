package pro.gamely.license.model;
import jakarta.persistence.*; import org.hibernate.annotations.CreationTimestamp; import java.time.Instant;
@Entity @Table(name="license_keys", uniqueConstraints=@UniqueConstraint(columnNames={"licenseKey"}))
public class LicenseKey {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, length=128) private String licenseKey;
  @Column(length=64) private String boundMac;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private LicenseStatus status = LicenseStatus.ACTIVE;
  @CreationTimestamp private Instant createdAt;
  private Instant activatedAt;
  @Column(length=255) private String note; // Telegram user
  public LicenseKey() {}
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getLicenseKey(){return licenseKey;} public void setLicenseKey(String v){this.licenseKey=v;}
  public String getBoundMac(){return boundMac;} public void setBoundMac(String v){this.boundMac=v;}
  public LicenseStatus getStatus(){return status;} public void setStatus(LicenseStatus v){this.status=v;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){this.createdAt=v;}
  public Instant getActivatedAt(){return activatedAt;} public void setActivatedAt(Instant v){this.activatedAt=v;}
  public String getNote(){return note;} public void setNote(String v){this.note=v;}
}
