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
package greyfox.rxnetwork.internal.strategy;

import greyfox.rxnetwork.internal.strategy.network.NetworkObservingStrategy;

/**
 * @author Radek Kozak
 */

public interface ObservingStrategyProvider<T extends ObservingStrategy> {

    /**
     * Implement this method to determine under what condition your provider can {@link #provide()}
     * concrete {@link NetworkObservingStrategy}.
     *
     * @return {@code true} if concrete {@link NetworkObservingStrategy} can be provided
     * for given criteria, {@code false} if not
     */
    boolean canProvide();

    /**
     * Implement this to return concrete {@link NetworkObservingStrategy}.
     *
     * @return {@link NetworkObservingStrategy}
     */
    T provide();
}