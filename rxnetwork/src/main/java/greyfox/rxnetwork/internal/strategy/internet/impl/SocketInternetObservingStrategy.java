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

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static android.support.annotation.VisibleForTesting.PRIVATE;

import static java.util.logging.Logger.getLogger;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

/**
 * @author Radek Kozak
 */

public final class SocketInternetObservingStrategy extends EndpointInternetObservingStrategy {

    /** Either default 80 or a user-specified port. In range [1..65535]. */
    private final int port;

    @VisibleForTesting(otherwise = PRIVATE)
    SocketInternetObservingStrategy() {
        throw new AssertionError("Use static factory methods or Builder to create strategy");
    }

    @VisibleForTesting(otherwise = PRIVATE)
    @RestrictTo(LIBRARY_GROUP)
    SocketInternetObservingStrategy(@NonNull Builder builder) {
        super(builder);
        port = builder.port;
    }

    @NonNull
    public static SocketInternetObservingStrategy create() { return builder().build(); }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    Logger logger() {
        return getLogger(SocketInternetObservingStrategy.class.getSimpleName());
    }

    @Override
    protected boolean checkConnection() {
        boolean isConnected;
        Socket socket = null;
        try {
            socket = connectSocketTo(new InetSocketAddress(endpoint, port), timeout);
            isConnected = isSocketConnected(socket);
        } catch (IOException ioe) {
            onError("Problem occurred while checking endpoint", ioe);
            isConnected = Boolean.FALSE;
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ioe) {
                onError("Could not close the socket", ioe);
            }
        }

        return isConnected;
    }

    Socket connectSocketTo(SocketAddress socketAddress, int timeout) throws IOException {
        final Socket socket = new Socket();
        socket.connect(socketAddress, timeout);
        return socket;
    }

    private boolean isSocketConnected(Socket socket) {
        return socket.isConnected();
    }

    /**
     * {@link SocketInternetObservingStrategy} builder static inner class.
     */
    public static final class Builder extends EndpointInternetObservingStrategy.Builder
            <SocketInternetObservingStrategy, SocketInternetObservingStrategy.Builder> {

        /**
         * Canonical hostname.
         * <p>
         * Endpoint effectively acting as a host part of {@link InetSocketAddress}
         */
        private static final String DEFAULT_ENDPOINT = "g.cn";
        private static final int DEFAULT_PORT = 80;

        private int port = DEFAULT_PORT;

        public Builder() {
            super.endpoint(DEFAULT_ENDPOINT);
        }

        @NonNull
        public Builder port(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Invalid port: " + port);
            }

            this.port = port;
            return self();
        }

        @NonNull
        @Override
        public SocketInternetObservingStrategy build() {
            return new SocketInternetObservingStrategy(this);
        }
    }
}
