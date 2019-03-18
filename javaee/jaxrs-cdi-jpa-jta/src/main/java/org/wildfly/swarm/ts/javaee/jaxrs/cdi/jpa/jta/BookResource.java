package org.wildfly.swarm.ts.javaee.jaxrs.cdi.jpa.jta;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/")
public class BookResource {
    @Inject
    private BookService books;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> list() {
        return books.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book get(@PathParam("id") int id) {
        return books.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Book book) throws URISyntaxException {
        Book created = books.create(book.getTitle(), book.getAuthor());
        return Response.created(new URI("/" + created.getId())).build();
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") int id) {
        books.delete(id);
    }
}
