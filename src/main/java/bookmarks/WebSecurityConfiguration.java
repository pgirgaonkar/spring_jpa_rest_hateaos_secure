package bookmarks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {
        Arrays.asList("Prafulla,Ameya,Shirish,Shreyas,Avadhut,Sachin,Sameer".split(","))
                .forEach( user -> {
                    Account a;
                    a = accountRepository.save(new Account(user, user+"_paswd"));

                    bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_1.mylogin.in/"+51, " this is a sample first"));
                    bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_2.mylogin.in/"+41, " this is a sample Second"));

                });
        builder.userDetailsService(userDetailsService());
    }

    private UserDetailsService userDetailsService() {

        System.out.println(accountRepository
                .findByUsername("Prafulla").toString());

     return (username) -> accountRepository
                .findByUsername(username)
                .map(
                account -> new User(account.getUsername(),account.getPassword(),true, true,true,true,
                        AuthorityUtils.createAuthorityList("USER", "write")))
                .orElseThrow(()->new UserNotFoundException("count not find user '" + username +"'"));
    }

}
