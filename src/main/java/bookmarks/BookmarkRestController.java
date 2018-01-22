package bookmarks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/{userId}/bookmarks")
@RequestMapping("/bookmarks")
public class BookmarkRestController {

    private final AccountRepository accountRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkRestController(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
        this.accountRepository = accountRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @RequestMapping(method= RequestMethod.GET)
    public Collection<BookmarkResource> readBookmarks(Principal principal){
        this.validateUser(principal);
        Collection<BookmarkResource> x = this.bookmarkRepository.findByAccountUsername(principal.getName()).stream().map(BookmarkResource::new).collect(Collectors.toList());
        //return new Collection<BookmarkResource>(x);
        return x;
    }

    //TODO - STUDY
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
        this.validateUser(principal);

        return this.accountRepository
                .findByUsername(principal.getName())
                .map(account -> {
                    Bookmark result = bookmarkRepository.save(new Bookmark(account,
                            input.getUri(), input.getDescription()));

//                    URI location = ServletUriComponentsBuilder
//                            .fromCurrentRequest().path("/{id}")
//                            .buildAndExpand(result.getId()).toUri();

                    Link forOneBookmark = new BookmarkResource(result).getLink("self");
                    return ResponseEntity.created(URI.create(forOneBookmark.getHref())).build();
                })
                .orElse(ResponseEntity.noContent().build());

    }

    @RequestMapping(method = RequestMethod.GET, value="/{bookmarkId}")
    BookmarkResource readBookmark(Principal principal , @PathVariable Long bookmarkId) {
        this.validateUser(principal);
        return new BookmarkResource(bookmarkRepository.findOne(bookmarkId));
    }


    private void validateUser(Principal principal){
        String userId = principal.getName();
        this.accountRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
