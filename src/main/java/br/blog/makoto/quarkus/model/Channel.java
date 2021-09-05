package br.blog.makoto.quarkus.model;

import java.util.Objects;

public class Channel {
  private final Long id;
  private final String accountId;
  private final String name;
  private final String description;

  public Channel(Long id, String accountId, String name, String description) {
    this.id = id;
    this.accountId = accountId;
    this.name = name;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Channel channel = (Channel) o;
    return Objects.equals(id, channel.id)
        && Objects.equals(accountId, channel.accountId)
        && Objects.equals(name, channel.name)
        && Objects.equals(description, channel.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, name, description);
  }
}
