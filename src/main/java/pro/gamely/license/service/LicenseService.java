package pro.gamely.license.service;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import pro.gamely.license.model.*; import pro.gamely.license.repo.LicenseKeyRepository; import pro.gamely.license.util.MacUtils;
import java.time.Instant; import java.util.*;
@Service public class LicenseService {
  private final LicenseKeyRepository repo; public LicenseService(LicenseKeyRepository repo){this.repo=repo;}

  @Transactional public Map<String,Object> checkAndBind(String licenseKey, String macHeader){
    Map<String,Object> resp=new HashMap<>();
    if (MacUtils.isInvalidMacHeader(macHeader)) {
      resp.put("statusCode", false);
      resp.put("message", "Некорректный MAC (пусто/None)");
      return resp;
    }
    String normalizedMac=MacUtils.normalize(macHeader);
    var opt=repo.findByLicenseKey(licenseKey);
    if(opt.isEmpty()){resp.put("statusCode",false); resp.put("message","Ключ не найден"); return resp;}
    var key=opt.get();
    if(key.getStatus()==LicenseStatus.REVOKED){resp.put("statusCode",false); resp.put("message","Ключ отозван"); return resp;}
    if(key.getBoundMac()==null || key.getBoundMac().isBlank()){
      key.setBoundMac(normalizedMac); key.setActivatedAt(Instant.now()); repo.save(key);
      resp.put("statusCode",true); resp.put("message","Ключ привязан и валиден"); resp.put("bound",true); return resp;
    }
    if(key.getBoundMac().equals(normalizedMac)){
      resp.put("statusCode",true); resp.put("message","Ключ валиден"); resp.put("bound",true); return resp;
    }
    resp.put("statusCode",false); resp.put("message","Ключ уже привязан к другому устройству"); return resp;
  }

  @Transactional public Map<String,Object> resetByClient(String licenseKey, String macHeader){
    Map<String,Object> m=new HashMap<>();
    if (MacUtils.isInvalidMacHeader(macHeader)) { m.put("ok", false); m.put("message", "Некорректный MAC (пусто/None)"); return m; }
    String mac=MacUtils.normalize(macHeader);
    var opt=repo.findByLicenseKey(licenseKey); if(opt.isEmpty()){m.put("ok",false); m.put("message","Ключ не найден"); return m;}
    var k=opt.get();
    if(k.getBoundMac()!=null && !k.getBoundMac().equals(mac)){ m.put("ok",false); m.put("message","MAC не соответствует привязанному"); return m; }
    k.setBoundMac(null); k.setActivatedAt(null); k.setStatus(LicenseStatus.ACTIVE); repo.save(k);
    m.put("ok",true); m.put("message","MAC сброшен"); return m;
  }

  public Map<String,Object> guard(String licenseKey, String macHeader){
    Map<String,Object> m=new HashMap<>();
    if (MacUtils.isInvalidMacHeader(macHeader)) { m.put("alive",false); m.put("reason","bad-mac"); return m; }
    String mac=MacUtils.normalize(macHeader);
    var opt=repo.findByLicenseKey(licenseKey); if(opt.isEmpty()){ m.put("alive",false); m.put("reason","deleted"); return m; }
    var k=opt.get();
    if(k.getStatus()==LicenseStatus.REVOKED){ m.put("alive",false); m.put("reason","revoked"); return m; }
    if(k.getBoundMac()!=null && !k.getBoundMac().equals(mac)){ m.put("alive",false); m.put("reason","other-mac"); return m; }
    m.put("alive",true); return m;
  }
}
