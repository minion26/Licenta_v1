package com.example.licentav1.config;

import com.example.licentav1.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authenticationProvider(authentificationProvider)
//                .addFilter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .cors();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionMangConfig -> sessionMangConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers("/api/v1/auth/**").permitAll(); //white list

                    authConfig.requestMatchers("api/v1/users/update/**").hasAnyAuthority("ADMIN", "TEACHER", "STUDENT");
                    authConfig.requestMatchers("api/v1/users/changePassword").hasAnyAuthority("ADMIN", "TEACHER", "STUDENT");

                    authConfig.requestMatchers("/api/v1/homework-announcements/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/lectures/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/materials/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/homework/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/student-answers/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/exam/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/question-exam/**").hasAnyAuthority("TEACHER", "STUDENT");
                    authConfig.requestMatchers("/api/v1/student-exam/**").hasAnyAuthority("TEACHER", "STUDENT");


                    authConfig.requestMatchers("api/v1/question/**").hasAnyAuthority("TEACHER");


                    authConfig.requestMatchers("api/v1/students/create").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/teachers/create").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/students/upload").hasAnyAuthority("ADMIN");
//                    authConfig.requestMatchers("api/v1/students").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/teachers/create").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/teachers/upload").hasAnyAuthority("ADMIN");
//                    authConfig.requestMatchers("api/v1/teachers").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/users/get-the-superuser").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/users/create").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/users/admins").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/courses/create").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/courses/upload").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/didactic/upload").hasAnyAuthority("ADMIN");
                    authConfig.requestMatchers("api/v1/students-follow-courses/upload").hasAnyAuthority("ADMIN");

                    authConfig.anyRequest().authenticated();
                        }
                );


        return http.build();
    }
}
