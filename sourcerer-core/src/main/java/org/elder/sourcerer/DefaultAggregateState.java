package org.elder.sourcerer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Default sourcerer implementation of AggregateState.
 */
public class DefaultAggregateState<TState, TEvent> implements AggregateState<TState, TEvent> {
    private final AggregateProjection<TState, TEvent> projection;
    private final String id;
    private final TState originalState;
    private final TState state;
    private final ImmutableList<TEvent> appliedEvents;

    public DefaultAggregateState(
            @NotNull final AggregateProjection<TState, TEvent> projection,
            @NotNull final String id,
            @Nullable final TState originalState) {
        this(projection, id, originalState, null, ImmutableList.of());
    }

    public DefaultAggregateState(
            @NotNull final AggregateProjection<TState, TEvent> projection,
            @NotNull final String id,
            @Nullable final TState originalState,
            @Nullable final TState state,
            @NotNull final List<TEvent> events) {
        Preconditions.checkNotNull(projection);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(events);
        this.projection = projection;
        this.id = id;
        this.originalState = originalState;
        this.state = state;
        this.appliedEvents = ImmutableList.copyOf(events);
    }

    @NotNull
    @Override
    public AggregateState<TState, TEvent> apply(@NotNull final TEvent event) {
        Preconditions.checkNotNull(event);
        @NotNull TState newState = projection.apply(id, state, event);
        return new DefaultAggregateState<>(
                projection,
                id,
                originalState,
                newState,
                ImmutableList.<TEvent>builder().addAll(appliedEvents).add(event).build());
    }

    @NotNull
    @Override
    public AggregateState<TState, TEvent> apply(@NotNull final Iterable<? extends TEvent> events) {
        Preconditions.checkNotNull(events);
        @NotNull TState newState = projection.apply(id, state, events);
        return new DefaultAggregateState<>(
                projection,
                id,
                originalState,
                newState,
                ImmutableList.<TEvent>builder().addAll(appliedEvents).addAll(events).build());
    }

    @Override
    public String id() {
        return id;
    }

    @Nullable
    @Override
    public TState originalState() {
        return originalState;
    }

    @NotNull
    @Override
    public TState state() {
        return state;
    }

    @NotNull
    @Override
    public List<TEvent> events() {
        return appliedEvents;
    }

    @Override
    public MutableAggregateState<TState, TEvent> toMutable() {
        return new DefaultMutableAggregateState<>(
                projection,
                id,
                originalState,
                state,
                appliedEvents);
    }
}
