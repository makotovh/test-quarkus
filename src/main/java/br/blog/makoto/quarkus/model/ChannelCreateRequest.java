package br.blog.makoto.quarkus.model;

import java.util.Objects;

public class ChannelCreateRequest {
  private final String accountId;
  private final String name;
  private final String description;

  public ChannelCreateRequest(String accountId, String name, String description) {
    this.accountId = accountId;
    this.name = name;
    this.description = description;
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
    ChannelCreateRequest that = (ChannelCreateRequest) o;
    return Objects.equals(accountId, that.accountId)
        && Objects.equals(name, that.name)
        && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, name, description);
  }
}
