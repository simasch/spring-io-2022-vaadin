package ch.martinelli.vaadin.demo.security;

import ch.martinelli.vaadin.demo.db.tables.records.SecurityUserRecord;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static ch.martinelli.vaadin.demo.db.tables.SecurityUser.SECURITY_USER;

@Component
public class SecurityService {

    private final DSLContext ctx;

    @Autowired
    public SecurityService(DSLContext ctx) {
        this.ctx = ctx;
    }

    private Optional<Authentication> getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }

    public Optional<SecurityUserRecord> getUser() {
        return getAuthentication().map(authentication -> ctx.selectFrom(SECURITY_USER).where(SECURITY_USER.USERNAME.eq(authentication.getName())).fetchOne());
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
