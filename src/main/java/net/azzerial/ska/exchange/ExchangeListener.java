/*
 * Copyright 2025 Robin Mercier (azzerial)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.azzerial.ska.exchange;

import net.azzerial.skhc.entities.Market;
import net.azzerial.skhc.events.ListenerAdapter;
import net.azzerial.skhc.events.exchange.ExchangeEvent;
import net.azzerial.skhc.events.exchange.ExchangeUpdateEvent;
import org.jetbrains.annotations.NotNull;

public final class ExchangeListener extends ListenerAdapter {

    public Market market;

    /* Methods */

    @Override
    public void onExchange(@NotNull ExchangeEvent event) {
        this.market = event.getMarket();
    }

    @Override
    public void onExchangeUpdate(@NotNull ExchangeUpdateEvent event) {
        this.market = event.getMarket();
    }
}
