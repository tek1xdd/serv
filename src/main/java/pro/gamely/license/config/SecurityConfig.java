package pro.gamely.license.config;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration; import org.springframework.security.config.Customizer; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.core.userdetails.*; import org.springframework.security.provisioning.InMemoryUserDetailsManager; import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
  @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable())
       .authorizeHttpRequests(auth->auth.requestMatchers("/api/**","/h2-console/**","/css/**").permitAll().anyRequest().authenticated())
       .headers(h->h.frameOptions(f->f.sameOrigin()))
       .formLogin(login->login.defaultSuccessUrl("/admin/keys", true).permitAll())
       .httpBasic(Customizer.withDefaults())
       .logout(Customizer.withDefaults());
    return http.build();
  }
  @Bean public UserDetailsService users(){ UserDetails admin=User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build(); return new InMemoryUserDetailsManager(admin); }
}
