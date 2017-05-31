/*
 * Copyright (C) 2017 Greyfox, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greyfox.rxnetwork.internal.strategy.internet.impl;

import static greyfox.rxnetwork.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import greyfox.rxnetwork.internal.strategy.internet.InternetObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Radek Kozak
 */
public abstract class BaseEndpointInternetObservingStrategy implements InternetObservingStrategy {

    abstract long delay();

    abstract long interval();

    abstract Logger logger();

    private Function<Long, Boolean> toConnectionState() {
        return new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long tick) throws Exception {
                return checkConnection();
            }
        };
    }

    @Override
    public Observable<Boolean> observe() {
        return Observable.interval(delay(), interval(), TimeUnit.MILLISECONDS)
                .map(toConnectionState()).distinctUntilChanged();
    }

    protected abstract boolean checkConnection();

    void onError(String message, Exception exception) {
        logger().log(Level.WARNING, message + ": " + exception.getMessage()
                + ((exception.getCause() != null) ? ": " + exception.getCause().getMessage() : ""));
    }

    /**
     * {@code BuiltInInternetObservingStrategy} builder static inner class.
     */
    @SuppressWarnings("unchecked")
    public abstract static class Builder<T extends Builder> {

        static final int DEFAULT_DELAY_MS = 0;
        static final int DEFAULT_INTERVAL_MS = 3000;
        static final String DEFAULT_ENDPOINT = "www.google.cn";
        static final int DEFAULT_PORT = 80;

        private long delay = DEFAULT_DELAY_MS;
        private long interval = DEFAULT_INTERVAL_MS;
        private String endpoint = DEFAULT_ENDPOINT;
        private int port = DEFAULT_PORT;

        public long getDelay() {
            return delay;
        }

        public long getInterval() {
            return interval;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public int getPort() {
            return port;
        }

        /**
         * Sets the {@code delay} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param delay the {@code delay} to set
         *
         * @return a reference to this Builder
         */
        @NonNull
        public T delay(long delay) {
            this.delay = delay;
            return self();
        }

        /**
         * Sets the {@code interval} and returns a reference to this Builder so that the methods can
         * be chained together.
         *
         * @param interval the {@code interval} to set
         *
         * @return a reference to this Builder
         */
        @NonNull
        public T interval(long interval) {
            this.interval = interval;
            return self();
        }

        /**
         * Sets the {@code endpoint} and returns a reference to this Builder so that the methods can
         * be chained together.
         *
         * @param endpoint the {@code endpoint} to set
         *
         * @return a reference to this Builder
         */
        @NonNull
        public T endpoint(@NonNull String endpoint) {
            this.endpoint = checkNotNull(endpoint, "endpoint");
            return self();
        }

        /**
         * Sets the {@code port} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param port the {@code port} to set
         *
         * @return a reference to this Builder
         */
        @NonNull
        public T port(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Invalid port: " + port);
            }

            this.port = port;
            return self();
        }

        /**
         * Returns a {@code InternetObservingStrategy} built from the parameters previously
         * set.
         *
         * @return a {@code InternetObservingStrategy} built with parameters of
         * this {@code Builder}
         */
        @NonNull
        public abstract InternetObservingStrategy build();

        protected final T self() {
            return (T) this;
        }
    }
}