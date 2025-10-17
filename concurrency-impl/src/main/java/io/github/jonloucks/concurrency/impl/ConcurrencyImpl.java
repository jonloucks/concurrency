package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConcurrencyImpl implements Concurrency {
    
    @Override
    public AutoClose open() {
        return idempotent.transitionToOpened(this::realOpen);
    }
    
    @Override
    public <T> Waitable<T> createWaitable(T initialValue) {
        return new WaitableImpl<>(initialValue);
    }

    ConcurrencyImpl(Config config, Repository repository, boolean autoOpen) {
        final Config validConfig = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = autoOpen ? validRepository.open() : AutoClose.NONE;
        this.idempotent = validConfig.contracts().claim(Idempotent.FACTORY).get();
    }
    
    private AutoClose realOpen() {
        return this::close;
    }

    private void close() {
        idempotent.transitionToClosed(this::realClose);
    }
    
    private void realClose() {
        closeRepository.close();
    }
    
    private final AutoClose closeRepository;
    private final Idempotent idempotent;
}
