package br.blog.makoto.quarkus.repository;

import br.blog.makoto.quarkus.model.Channel;
import br.blog.makoto.quarkus.model.ChannelCreateRequest;
import br.blog.makoto.quarkus.model.ChannelUpdateRequest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ChannelRepository {

  private final PgPool client;
  private final boolean schemaCreate;

  public ChannelRepository(
      PgPool client,
      @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") boolean schemaCreate) {
    this.client = client;
    this.schemaCreate = schemaCreate;
  }

  @PostConstruct
  void config() {
    if (schemaCreate) {
      client
          .query("DROP TABLE IF EXISTS channels")
          .execute()
          .flatMap(
              r ->
                  client
                      .query(
                          "CREATE TABLE channels (id SERIAL PRIMARY KEY, account_id TEXT NOT NULL, name TEXT NOT NULL, description TEXT)")
                      .execute())
          .flatMap(
              r ->
                  client
                      .query(
                          "INSERT INTO channels (account_id, name, description) VALUES ('dev', 'email', 'Email channel')")
                      .execute())
          .flatMap(
              r ->
                  client
                      .query(
                          "INSERT INTO channels (account_id, name, description) VALUES ('dev', 'sms', 'SMS channel')")
                      .execute())
          .flatMap(
              r ->
                  client
                      .query(
                          "INSERT INTO channels (account_id, name, description) VALUES ('one-api', 'web', 'Web channel')")
                      .execute())
          .await()
          .indefinitely();
    }
  }

  public Multi<Channel> listAll() {
    return client
        .query("Select * from channels order by name")
        .execute()
        .toMulti()
        .flatMap(rows -> Multi.createFrom().iterable(rows))
        .map(this::transformToChannel);
  }

  public Uni<Optional<Channel>> findById(Long id) {
    return client
        .preparedQuery("Select * from channels where id = $1")
        .execute(Tuple.of(id))
        .map(RowSet::iterator)
        .map(iterator -> iterator.hasNext() ? transformToChannel(iterator.next()) : null)
        .map(Optional::ofNullable);
  }

  public Uni<Channel> create(ChannelCreateRequest channel) {
    return client
        .preparedQuery(
            "INSERT INTO channels (account_id, name, description) VALUES ($1, $2, $3) RETURNING id")
        .execute(Tuple.of(channel.getAccountId(), channel.getName(), channel.getDescription()))
        .map(RowSet::iterator)
        .map(RowIterator::next)
        .map(
            row ->
                new Channel(
                    row.getLong("id"),
                    channel.getAccountId(),
                    channel.getName(),
                    channel.getDescription()));
  }

  public Uni<Boolean> update(Long id, ChannelUpdateRequest channel) {
    return client
        .preparedQuery("Update channels set name=$1, description=$2 where id = $3")
        .execute(Tuple.of(channel.getName(), channel.getDescription(), id))
        .map(rows -> rows.rowCount() == 1);
  }

  public Uni<Boolean> delete(Long id) {
    return client
        .preparedQuery("Delete from channels where id = $1")
        .execute(Tuple.of(id))
        .map(rows -> rows.rowCount() == 1);
  }

  private Channel transformToChannel(Row row) {
    return new Channel(
        row.getLong("id"),
        row.getString("account_id"),
        row.getString("name"),
        row.getString("description"));
  }
}
