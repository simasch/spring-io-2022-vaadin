package ch.martinelli.vaadin.demo.security;

import org.jooq.DSLContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static ch.martinelli.vaadin.demo.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.martinelli.vaadin.demo.db.tables.SecurityUser.SECURITY_USER;
import static ch.martinelli.vaadin.demo.db.tables.UserGroup.USER_GROUP;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DSLContext dsl;

    public UserDetailsServiceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var securityUserRecord = dsl
                .selectFrom(SECURITY_USER)
                .where(SECURITY_USER.USERNAME.eq(username))
                .fetchOne();

        if (securityUserRecord != null) {
            var groups = dsl
                    .select(USER_GROUP.securityGroup().NAME)
                    .from(USER_GROUP)
                    .fetch();

            return new User(securityUserRecord.getUsername(), securityUserRecord.getSecret(),
                    groups.stream()
                            .map(group -> new SimpleGrantedAuthority("ROLE_" + group.getValue(SECURITY_GROUP.NAME)))
                            .toList());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
