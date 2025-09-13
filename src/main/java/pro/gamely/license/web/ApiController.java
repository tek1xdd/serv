package pro.gamely.license.web;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import pro.gamely.license.service.LicenseService; import java.util.*;
@RestController @RequestMapping("/api") public class ApiController {
  private final LicenseService service; public ApiController(LicenseService service){this.service=service;}
  @GetMapping("/key/{key}") public ResponseEntity<Map<String,Object>> check(@RequestHeader(name="mac", required=false) String mac, @PathVariable("key") String key){ return ResponseEntity.ok(service.checkAndBind(key, mac)); }
  @PostMapping("/key/{key}/reset") public Map<String,Object> resetByClient(@RequestHeader(name="mac", required=false) String mac, @PathVariable("key") String key){ return service.resetByClient(key, mac); }
  @GetMapping("/guard/{key}") public Map<String,Object> guard(@RequestHeader(name="mac", required=false) String mac, @PathVariable("key") String key){ return service.guard(key, mac); }
  @GetMapping("/key/ping") public Map<String,Object> ping(){ Map<String,Object> m=new HashMap<>(); m.put("ok",true); m.put("ts",System.currentTimeMillis()); return m; }
}
