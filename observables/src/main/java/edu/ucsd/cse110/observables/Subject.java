package edu.ucsd.cse110.observables;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.List;
/**
 * A subject that can be observed by observers. Analogous to LiveData.
 *
 * @param <T> The type of the value that the subject holds.
 * @see <a href="https://developer.android.com/reference/androidx/lifecycle/LiveData">Android LiveData</a>
 */
public interface Subject<T> {


    /**
     * @return The current value, or null if {@link #isInitialized()} is false.
     */
    @Nullable
    T getValue();

    /**
     * @return True if this subject has observers.
     */
    boolean hasObservers();

    /**
     * @return True if this subject has been initialized with an explicit value.
     */
    boolean isInitialized();


    /**
     * Register an observer to be notified when the value changes. This is
     * equivalent to Android's LiveData.observeForever (no owner parameter).
     *
     * @param observer The observer to registerObserver.
     * @return The observer that was registered, so that it can be unregistered later.
     */
    @MainThread
    Observer<? super T> observe(@NonNull Observer<? super T> observer);

    /**
     * Unregister an observer so that it will no longer be notified when the value changes.
     *
     * @param observer The observer to unregister.
     */
    @MainThread
    void removeObserver(@NonNull Observer<? super T> observer);


    /**
     * Unregister all observers so that they will no longer be notified when the value changes.
     */
    @MainThread
    void removeObservers();

    /**
     * Get the list of observers. This method is for testing purposes only.
     * Do NOT use this method in production code. Android Studio will warn!
     *
     * @return The list of observers.
     */
    @VisibleForTesting
    List<Observer<? super T>> getObservers();
}