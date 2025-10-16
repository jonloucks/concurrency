package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;

import static java.util.Optional.ofNullable;

final class ConcurrencyImpl implements Concurrency {
    
    @Override
    public AutoClose open() {
        return idempotent.transitionToOpened(this::realOpen);
    }
    
    ConcurrencyImpl(Config config, Repository repository) {
//        this.config = configCheck(config);
//        this.repository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = repository.open();
        this.idempotent = config.contracts().claim(Idempotent.FACTORY).get();
    }
    
    private AutoClose realOpen() {
        return this::close;
    }

    private void close() {
        idempotent.transitionToClosed(this::realClose);
    }
    
    private void realClose() {
        ofNullable(closeRepository).ifPresent(close -> {
//            repository = null;
            closeRepository = null;
            close.close();
        });
    }

    @SuppressWarnings("FieldCanBeLocal")
//    private final Config config;
    private final Idempotent idempotent;
    @SuppressWarnings("unused")
//    private Repository repository;
    private AutoClose closeRepository;
}
