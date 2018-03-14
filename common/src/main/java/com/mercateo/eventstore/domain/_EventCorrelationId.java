package com.mercateo.eventstore.domain;

import java.util.UUID;

import org.immutables.value.Value;

import com.mercateo.immutables.Wrapped;
import com.mercateo.immutables.Wrapper;

@Value.Immutable
@Wrapped
public abstract class _EventCorrelationId extends Wrapper<UUID> {
}
