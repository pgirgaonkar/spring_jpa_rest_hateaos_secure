package bookmarks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class BookmarkRestController {

    private final AccountRepository accountRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkRestController(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
        this.accountRepository = accountRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @RequestMapping(method= RequestMethod.GET)
    public Collection<BookmarkResource> readBookmarks(@PathVariable String userId){
        this.validateUser(userId);
        Collection<BookmarkResource> x = this.bookmarkRepository.findByAccountUsername(userId).stream().map(BookmarkResource::new).collect(Collectors.toList());
        //return new Collection<BookmarkResource>(x);
        return x;
    }

    //TODO - STUDY
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
        this.validateUser(userId);

        return this.accountRepository
                .findByUsername(userId)
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
    BookmarkResource readBookmark(@PathVariable String userId , @PathVariable Long bookmarkId) {
        this.validateUser(userId);
        return new BookmarkResource(bookmarkRepository.findOne(bookmarkId));
    }


    private void validateUser(String userId){
        this.accountRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
