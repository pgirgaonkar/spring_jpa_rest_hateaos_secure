package bookmarks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Random;

@SpringBootApplication
public class Application {

    private Random rnd = new Random();

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository){
        return (clr) -> {
            Arrays.asList("Prafulla, Ameya,Shirish, Shreyas, Avadhut, Sachin, Sameer".split(","))
                    .forEach( user -> {
                        Account a = accountRepository.save(new Account(user, user+"_paswd"));

                        bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_1.mylogin.ner/"+rnd.nextInt(50)+1, " this is a sample first"));
                        bookmarkRepository.save(new Bookmark(a, "HttPs://"+user+"_2.mylogin.ner/"+rnd.nextInt(50)+1, " this is a sample Second"));

                    });
        };
    }
}
