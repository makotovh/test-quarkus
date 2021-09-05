package br.blog.makoto.quarkus.resource;

import br.blog.makoto.quarkus.model.Channel;
import br.blog.makoto.quarkus.model.ChannelCreateRequest;
import br.blog.makoto.quarkus.model.ChannelUpdateRequest;
import br.blog.makoto.quarkus.service.ChannelService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;

@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {

  private final ChannelService channelService;

  public ChannelResource(ChannelService channelService) {
    this.channelService = channelService;
  }


  @GET
  public Multi<Channel> getAll() {
    return channelService.listAll();
  }

  @GET
  @Path("/{id}")
  public Uni<Response> get(@PathParam("id") Long id) {
    return channelService
        .findById(id)
        .map(channel -> channel.isPresent() ? Response.ok(channel) : Response.status(NOT_FOUND))
        .map(Response.ResponseBuilder::build);
  }

  @POST
  public Uni<Response> create(ChannelCreateRequest channel) {
    return channelService
        .create(channel)
        .map(createdChannel -> Response.status(CREATED).entity(createdChannel).build());
  }

  @PUT
  @Path("/{id}")
  public Uni<Channel> update(ChannelUpdateRequest channel, @PathParam("id") Long id) {
    return channelService
        .update(id, channel)
        .replaceWith(channelService.findById(id).map(Optional::get));
  }

  @DELETE
  @Path("/{id}")
  public Uni<Response> delete(@PathParam("id") Long id) {
    return channelService.delete(id)
            .map(a -> Response.status(NO_CONTENT).build());
  }
}
