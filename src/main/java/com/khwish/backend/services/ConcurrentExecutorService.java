package com.khwish.backend.services;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ConcurrentExecutorService {
    CompletableFuture<Void> execute(Runnable runnable);

    <T> CompletableFuture<T> execute(Supplier<T> supplier);
}
