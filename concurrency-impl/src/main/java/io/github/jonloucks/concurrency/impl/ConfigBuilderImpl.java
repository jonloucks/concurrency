package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;

import java.time.Duration;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConfigBuilderImpl implements Concurrency.Config.Builder {
    @Override
    public Builder useReflection(boolean useReflection) {
        this.useReflection = useReflection;
        return this;
    }
    
    @Override
    public Builder useServiceLoader(boolean useServiceLoader) {
        this.useServiceLoader = useServiceLoader;
        return this;
    }
    
    @Override
    public Builder contracts(Contracts contracts) {
        this.contracts = contractsCheck(contracts);
        return this;
    }

    @Override
    public Builder shutdownTimeout(Duration shutdownTimeout) {
        this.shutdownTimeout = nullCheck(shutdownTimeout, "Shut down timeout must be present.");
        return this;
    }
 
    @Override
    public Builder reflectionClassName(String reflectionClassName) {
        this.reflectionClassName = nullCheck(reflectionClassName, "Reflection class name must be present.");
        return this;
    }
    
    @Override
    public Builder serviceLoaderClass(Class<? extends ConcurrencyFactory> serviceLoaderClass) {
        this.serviceLoaderClass = nullCheck(serviceLoaderClass, "Service loader class must be present.");
        return this;
    }
    
    @Override
    public boolean useReflection() {
        return useReflection;
    }
    
    @Override
    public String reflectionClassName() {
        return reflectionClassName;
    }
    
    @Override
    public boolean useServiceLoader() {
        return useServiceLoader;
    }
    
    @Override
    public Class<? extends ConcurrencyFactory> serviceLoaderClass() {
        return serviceLoaderClass;
    }
    
    @Override
    public Contracts contracts() {
        return contracts;
    }
    
    @Override
    public Duration shutdownTimeout() {
        return shutdownTimeout;
    }
 
    ConfigBuilderImpl() {
    
    }
    
    private boolean useReflection = DEFAULT.useReflection();
    private boolean useServiceLoader = DEFAULT.useServiceLoader();
    private Contracts contracts = DEFAULT.contracts();
    private Duration shutdownTimeout = DEFAULT.shutdownTimeout();
    private String reflectionClassName = DEFAULT.reflectionClassName();
    private Class<? extends ConcurrencyFactory> serviceLoaderClass = DEFAULT.serviceLoaderClass();
}
