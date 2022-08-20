package com.khwish.backend.services.impl;

import com.khwish.backend.services.ConcurrentExecutorService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

@Service
public class ConcurrentExecutorServiceImpl implements ConcurrentExecutorService {
    @Override
    public CompletableFuture<Void> execute(Runnable runnable) {
        return CompletableFuture.runAsync(runnable);
    }

    @Override
    public <T> CompletableFuture<T> execute(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier);
    }
}
