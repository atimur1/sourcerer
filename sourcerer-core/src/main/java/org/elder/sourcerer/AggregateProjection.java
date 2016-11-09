package org.elder.sourcerer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A projection is a function that given an aggregate and an event, returns a new aggregate
 * representing the state of the aggregate with the event applied.
 *
 * @param <TState> The type of aggregate (state) that the projection operates on
 * @param <TEvent> The type of event that the projection is capable of applying to aggregates.
 */
public interface AggregateProjection<TState, TEvent> {
    /**
     * Applies an event to an aggregate, creating a new aggregate representing a snapshot-in-time
     * state of the aggregate with the event applied. This must be a pure function, returning a
     * semantically equivalent new aggregate state given the same (or semantically equivalent)
     * aggregate and event.
     * <p>
     * All types are expected to be immutable, the function must return a new aggregate instance and
     * must not attempt to mutate either the given aggregate or event. The implementation should
     * have no externally visible side effects or depend on external state.
     * <p>
     * Unlike commands, there is no mechanism by which events can be rejected as they represent past
     * tense, already happened events. The projection must be able to deal with any event type and
     * values of event types that may have been created in the past, substituting sane defaults for
     * missing values that may be required in later versions of the event.
     * <p>
     * The state provided may be null, representing a previously non-existent aggregate. For this
     * case, the projection must be able to instantiate a new aggregate with defaults set (if
     * applicable) and apply the given event to it.
     *
     * @param id    The id of the aggregate being projected.
     * @param state A snapshot in time state of an aggregate.
     * @param event The event to apply to the aggregate.
     * @return A new aggregate instance of the same type as the provided, representing a
     * snapshot-in-time state of the given aggregate with the provided event applied.
     */
    @NotNull TState apply(@NotNull String id, @Nullable TState state, @NotNull TEvent event);

    /**
     * Applies a sequence of events to an aggregate, creating a new aggregate representing a
     * snapshot-in-time state of the aggregate with the events applied.
     * <p>
     * The implementation of this function must be semantically equivalent to a left fold of the
     * single event apply function over the state and sequence of events.
     * <p>
     * This must be a pure function, returning a semantically equivalent new aggregate state given
     * the same (or semantically equivalent) aggregate and event.
     * <p>
     * All types are expected to be immutable, the function must return a new aggregate instance and
     * must not attempt to mutate either the given aggregate or event. The implementation should
     * have no externally visible side effects or depend on external state.
     * <p>
     * Unlike commands, there is no mechanism by which events can be rejected as they represent past
     * tense, already happened events. The projection must be able to deal with any event type and
     * values of event types that may have been created in the past, substituting sane defaults for
     * missing values that may be required in later versions of the event.
     * <p>
     * The state provided may be null, representing a previously non-existent aggregate. For this
     * case, the projection must be able to instantiate a new aggregate with defaults set (if
     * applicable) and apply the given event to it.
     *
     * @param id     The id of the aggregate being projected.
     * @param state  A snapshot in time state of an aggregate.
     * @param events The events to apply to the aggregate.
     * @return A new aggregate instance of the same type as the provided, representing a
     * snapshot-in-time state of the given aggregate with the provided event applied.
     */
    default @NotNull TState apply(
            @NotNull final String id,
            @Nullable TState state,
            @NotNull final Iterable<TEvent> events) {
        for (TEvent e : events) {
            state = apply(id, state, e);
        }

        return state;
    }
}