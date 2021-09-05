package br.blog.makoto.quarkus.service;

import br.blog.makoto.quarkus.model.Channel;
import br.blog.makoto.quarkus.model.ChannelCreateRequest;
import br.blog.makoto.quarkus.model.ChannelUpdateRequest;
import br.blog.makoto.quarkus.repository.ChannelRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@ApplicationScoped
public class ChannelService {
  private final ChannelRepository channelRepository;

  @Inject
  public ChannelService(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
  }

  public Multi<Channel> listAll() {
    return channelRepository.listAll();
  }

  public Uni<Optional<Channel>> findById(Long id) {
    return channelRepository.findById(id);
  }

  public Uni<Channel> create(ChannelCreateRequest channelRequest) {
    return channelRepository.create(channelRequest);
  }

  public Uni<Void> update(Long id, ChannelUpdateRequest channel) {
    return channelRepository
        .update(id, channel)
        .chain(
            updated ->
                updated
                    ? Uni.createFrom().nullItem()
                    : Uni.createFrom().failure(new NotFoundException("Channel not found")));
  }

  public Uni<Void> delete(Long id) {
    return channelRepository
        .delete(id)
        .chain(
            updated ->
                updated
                    ? Uni.createFrom().nullItem()
                    : Uni.createFrom().failure(new NotFoundException("Channel not found")));
  }
}
