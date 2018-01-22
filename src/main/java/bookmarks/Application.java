package bookmarks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


//
// curl -X POST -vu android-bookmarks:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_secret=123456&client_id=android-bookmarks"
// curl -v POST http://127.0.0.1:8080/bookmarks -H "Authorization: Bearer <oauth_token>""

@SpringBootApplication
public class Application {

    private Random rnd = new Random();

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository){
        return (clr) -> {
            Arrays.asList("Prafulla,Ameya,Shirish,Shreyas,Avadhut,Sachin,Sameer".split(","))
                    .forEach( user -> {
                        Account a = accountRepository.save(new Account(user, user+"_paswd"));

                        bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_1.mylogin.ner/"+rnd.nextInt(50)+1, " this is a sample first"));
                        bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_2.mylogin.ner/"+rnd.nextInt(50)+1, " this is a sample Second"));

                    });
        };
    }

    //CORS
    @Bean
    FilterRegistrationBean corsFilter(@Value("${tagit.origin:http://localhost:9000}")String origin){
        return new FilterRegistrationBean(new Filter(){
            public void doFilter(ServletRequest req, ServletResponse res , FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest)req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();

                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
                response.setHeader("Access-Control-max-Age",Long.toString(60*60));
                response.setHeader("Acess-Control-Allow-Credentials","true");
                response.setHeader("Access-Control-Allow-Headers","Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
                if("OPTIONS".equals((method)))
                    response.setStatus(HttpStatus.OK.value());
                else
                    chain.doFilter(req,res);


            }

            public void init(FilterConfig filterConfig){
            }

            public void destroy(){}
        });
    }
}
