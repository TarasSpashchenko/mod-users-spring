package org.folio.modusers.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class OffsetRequest implements Pageable {

  private final int offset;
  private final int limit;

  private Sort sort = Sort.by(Direction.DESC, "id");

  public OffsetRequest(int offset, int limit) {
    this.limit = limit;
    this.offset = offset;
  }

  public OffsetRequest(int offset, int limit, String sort) {
    this.limit = limit;
    this.offset = offset;
    this.sort = Sort.by(Direction.fromString("desc"), "id");
  }

  @Override
  public int getPageNumber() {
    return offset / limit;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Pageable next() {
    return new OffsetRequest((int) (getOffset() + getPageSize()), getPageSize());
  }

  public Pageable previous() {
    return hasPrevious() ?
        new OffsetRequest((int) (getOffset() - getPageSize()), getPageSize()) : this;
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Pageable first() {
    return new OffsetRequest(0, getPageSize());
  }

  @Override
  public boolean hasPrevious() {
    return offset > limit;
  }
}
