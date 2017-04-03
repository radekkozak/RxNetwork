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
package greyfox.rxnetwork2.internal.net;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.annotation.VisibleForTesting.PRIVATE;

import static greyfox.rxnetwork2.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;

/**
 * Helper class for getting {@link RxNetworkInfo network information} from given resources.
 *
 * @author Radek Kozak
 */
public final class RxNetworkInfoHelper {

    @VisibleForTesting(otherwise = PRIVATE)
    RxNetworkInfoHelper() {
        throw new AssertionError("No instances.");
    }

    /**
     * Gets {@link RxNetworkInfo network information} from provided {@link Context context}.
     *
     * @param context {@link Context}
     *
     * @return {@link RxNetworkInfo} instance
     */
    public static RxNetworkInfo getNetworkInfoFrom(@NonNull final Context context) {
        checkNotNull(context, "context == null");

        final ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo == null ? RxNetworkInfo.create() : RxNetworkInfo.createFrom(networkInfo);
    }

    /**
     * Gets {@link RxNetworkInfo network information} from given {@link Network} instance
     * along with {@link NetworkCapabilities} provided at the time of registering network callback
     * in {@linkplain ConnectivityManager#registerNetworkCallback} (if available).
     *
     * @param network {@link Network}
     * @param manager {@link ConnectivityManager}
     *
     * @return {@link RxNetworkInfo} instance
     */
    @RequiresApi(LOLLIPOP)
    public static RxNetworkInfo getNetworkInfoFrom(@NonNull Network network,
            @NonNull ConnectivityManager manager) {

        checkNotNull(network, "network == null");
        checkNotNull(manager, "manager == null");

        final NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
        final NetworkInfo networkInfo = manager.getNetworkInfo(network);
        final RxNetworkInfo rxNetworkInfo;

        if (networkInfo != null) {
            RxNetworkInfo.Builder builder = RxNetworkInfo.builderFrom(networkInfo);
            if (networkCapabilities != null) builder.networkCapabilities(networkCapabilities);
            rxNetworkInfo = builder.build();
        } else {
            rxNetworkInfo = RxNetworkInfo.create();
        }

        return rxNetworkInfo;
    }
}
