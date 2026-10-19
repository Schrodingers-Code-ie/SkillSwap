package ie.schrodingerscode.skillswap.auth.web;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.application.LoginUser;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.web.dto.LoginRequest;
import ie.schrodingerscode.skillswap.auth.web.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Logs a user in and keeps them logged in with a session cookie.
 * Logout is handled by spring security, see SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUser loginUser;
    //saves the logged-in user into the HTTP session, so the next request still knows who it is
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public LoginController(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = loginUser.login(request.email(), request.password());

        //new session ID after login, so an old (maybe stolen) session ID stops working
        if (httpRequest.getSession(false) != null) {
            httpRequest.changeSessionId();
        }

        //the user ID is the principal, SessionCurrentUser reads it back. no roles yet, so empty list
        var auth = new UsernamePasswordAuthenticationToken(user.getId(), null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        //without this the login only lasts for this one request
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return UserResponse.from(user);
    }
}
