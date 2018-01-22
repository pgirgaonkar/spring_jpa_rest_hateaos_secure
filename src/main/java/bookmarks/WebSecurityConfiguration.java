package bookmarks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

//GlobalAuthenticationConfigurerAdapter
@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService());
    }

    private UserDetailsService userDetailsService() {
        return (username -> accountRepository.findByUsername(username).map(
                account -> new User(account.getUsername(),account.getPassword(),true, true,true,true,
                        AuthorityUtils.createAuthorityList("USER", "write")))
                .orElseThrow(()->new UserNotFoundException("count not find user '" + username +"'"))
        );
    }

}
