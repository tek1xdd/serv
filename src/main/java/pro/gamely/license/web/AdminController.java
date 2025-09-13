package pro.gamely.license.web;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
import pro.gamely.license.model.*; import pro.gamely.license.repo.LicenseKeyRepository;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Controller @RequestMapping("/admin") public class AdminController {
  private final LicenseKeyRepository repo; private final SecureRandom rnd=new SecureRandom();
  public AdminController(LicenseKeyRepository repo){this.repo=repo;}

  @GetMapping public String dashboard(Model model){
    long total=repo.count(); long active=repo.findAll().stream().filter(k->k.getStatus()==LicenseStatus.ACTIVE).count(); long revoked=repo.findAll().stream().filter(k->k.getStatus()==LicenseStatus.REVOKED).count();
    model.addAttribute("total",total); model.addAttribute("active",active); model.addAttribute("revoked",revoked);
    return "dashboard";
  }
  @GetMapping("/keys") public String list(Model model){ model.addAttribute("items", repo.findAll()); return "keys/list"; }
  @GetMapping("/keys/new") public String createForm(){ return "keys/form"; }
  @PostMapping("/keys") public String createSingle(@RequestParam("licenseKey") String licenseKey, @RequestParam(name="note", required=false) String tg){ var e=new LicenseKey(); e.setLicenseKey(licenseKey.trim()); e.setNote(tg); e.setStatus(LicenseStatus.ACTIVE); repo.save(e); return "redirect:/admin/keys"; }
  @PostMapping("/keys/generate") public String generate(@RequestParam("count") int count, @RequestParam(name="note", required=false) String tg){ if(count<1) count=1; if(count>500) count=500; for(int i=0;i<count;i++){ var e=new LicenseKey(); e.setLicenseKey(gen()); e.setNote(tg); e.setStatus(LicenseStatus.ACTIVE); repo.save(e);} return "redirect:/admin/keys"; }
  @PostMapping("/keys/{id}/reset") public String reset(@PathVariable("id") Long id){ var e=repo.findById(id).orElse(null); if(e!=null){ e.setBoundMac(null); e.setActivatedAt(null); e.setStatus(LicenseStatus.ACTIVE); repo.save(e);} return "redirect:/admin/keys"; }
  @PostMapping("/keys/{id}/delete") public String delete(@PathVariable("id") Long id){ if(repo.existsById(id)) repo.deleteById(id); return "redirect:/admin/keys"; }
  @PostMapping("/keys/{id}/kick") public String kick(@PathVariable("id") Long id){ repo.findById(id).ifPresent(repo::delete); return "redirect:/admin/keys"; }

  @PostMapping("/keys/bulk")
  public ResponseEntity<?> bulk(@RequestParam(name="ids", required=false) List<Long> ids,
                                @RequestParam("action") String action) {
    if(ids==null || ids.isEmpty()){
      // nothing selected -> just redirect back
      return ResponseEntity.status(302).header("Location","/admin/keys").build();
    }
    switch(action){
      case "reset" -> {
        repo.findByIdIn(ids).forEach(k->{ k.setBoundMac(null); k.setActivatedAt(null); k.setStatus(LicenseStatus.ACTIVE); });
        repo.saveAll(repo.findByIdIn(ids));
        return ResponseEntity.status(302).header("Location","/admin/keys").build();
      }
      case "delete", "kick" -> {
        repo.deleteAllById(ids);
        return ResponseEntity.status(302).header("Location","/admin/keys").build();
      }
      case "export" -> {
        var keys = repo.findByIdIn(ids).stream().map(LicenseKey::getLicenseKey).collect(Collectors.toList());
        String body = String.join("\n", keys) + "\n";
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=keys.txt")
          .contentType(new MediaType("text","plain", StandardCharsets.UTF_8))
          .body(body);
      }
      default -> {
        return ResponseEntity.status(302).header("Location","/admin/keys").build();
      }
    }
  }

  private String gen(){ String alphabet="ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; StringBuilder sb=new StringBuilder(); for(int g=0; g<4; g++){ if(g>0) sb.append('-'); for(int i=0;i<3;i++){ sb.append(alphabet.charAt(rnd.nextInt(alphabet.length()))); } } return sb.toString(); }
}
