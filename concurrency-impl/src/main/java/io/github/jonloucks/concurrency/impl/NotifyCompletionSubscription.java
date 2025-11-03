package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.contracts.api.AutoClose;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.jonloucks.concurrency.impl.Internal.onCompletionCheck;
import static io.github.jonloucks.concurrency.impl.Internal.removeExact;

final class NotifyCompletionSubscription<T> implements OnCompletion<T> {

    NotifyCompletionSubscription(OnCompletion<T> referent, List<NotifyCompletionSubscription<T>> ownerList) {
        this.referent = onCompletionCheck(referent);
        this.ownerList = ownerList;
    }
    
    AutoClose open() {
        ownerList.add(this);
        return this::close;
    }
    
    void close() {
        if (isClosed.compareAndSet(false, true)) {
            removeExact(ownerList, this);
        }
    }
    
    @Override
    public void onCompletion(Completion<T> completion) {
        if (isActive()) {
            referent.onCompletion(completion);
        }
    }
    
    private boolean isActive() {
        return !isClosed.get();
    }
    
    private final OnCompletion<T> referent;
    private final List<NotifyCompletionSubscription<T>> ownerList;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
}
