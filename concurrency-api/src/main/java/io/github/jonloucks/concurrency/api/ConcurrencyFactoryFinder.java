package io.github.jonloucks.concurrency.api;

import java.util.Optional;
import java.util.ServiceLoader;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Optional.ofNullable;

/**
 * Responsible for locating and creating the ConcurrencyFactory for a deployment.
 */
final class ConcurrencyFactoryFinder {
    ConcurrencyFactoryFinder(Concurrency.Config config) {
        this.config = configCheck(config);
    }
    
    Optional<ConcurrencyFactory> find() {
        final Optional<ConcurrencyFactory> byReflection = createByReflection();
        return byReflection.isPresent() ? byReflection : createByServiceLoader();
    }
    
    private Optional<ConcurrencyFactory> createByServiceLoader() {
        if (config.useServiceLoader()) {
            try {
                for (ConcurrencyFactory factory : ServiceLoader.load(getServiceFactoryClass())) {
                    return Optional.of(factory);
                }
            } catch (Throwable ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    private Class<? extends ConcurrencyFactory> getServiceFactoryClass() {
        return nullCheck(config.serviceLoaderClass(), "Concurrency Service Loader class must be present.");
    }
    
    private Optional<ConcurrencyFactory> createByReflection() {
        if (config.useReflection()) {
            return getReflectionClassName().map(this::createNewInstance);
        }
        return Optional.empty();
    }
    
    private ConcurrencyFactory createNewInstance(String className) {
        try {
            return (ConcurrencyFactory)Class.forName(className).getConstructor().newInstance();
        } catch (Throwable thrown) {
            return null;
        }
    }

    private Optional<String> getReflectionClassName() {
        return ofNullable(config.reflectionClassName()).filter(x -> !x.isEmpty());
    }
    
    private final Concurrency.Config config;
}
