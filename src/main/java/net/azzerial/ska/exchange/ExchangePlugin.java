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

import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
import io.javalin.plugin.Plugin;
import net.azzerial.skhc.entities.Market;
import net.azzerial.skhc.entities.Offer;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;

import static net.azzerial.ska.Main.exchange;

public final class ExchangePlugin extends Plugin<Void> {

    @Override
    public void onInitialize(@NotNull JavalinConfig config) {
        // register routes to serve the exchange market object
        config.router.mount(router -> {
            router.get("exchange/market", this::handleMarket);
            router.get("exchange/market.png", this::handleMarketImage);
            router.get("exchange/market/lastPrice", this::handleMarketLastPrice);
            router.get("exchange/market/buyOffers", this::handleMarketBuyOffers);
            router.get("exchange/market/sellOffers", this::handleMarketSellOffers);
        });
    }

    /* Routes */

    @OpenApi(
        path = "/exchange/market",
        methods = HttpMethod.GET,
        description = "Get the current exchange market object.",
        operationId = "exchange_market",
        tags = { "Exchange Market" },
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The exchange market object.",
                content = {
                    @OpenApiContent(
                        from = Market.class,
                        mimeType = "application/json"
                    )
                }
            )
        }
    )
    private void handleMarket(Context ctx) {
        ctx.json(exchange.market);
    }

    @OpenApi(
        path = "/exchange/market.png",
        methods = HttpMethod.GET,
        description = "Get a PNG image representation (fake screenshot) of the current exchange market object.",
        operationId = "exchange_market.png",
        tags = { "Exchange Market" },
        queryParams = {
            @OpenApiParam(
                name = "timezone",
                description = "The timezone used for the image timestamp. See https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html for the format.",
                example = "UTC+2"
            )
        },
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "An image of the exchange market object.",
                content = {
                    @OpenApiContent(
                        mimeType = "image/png"
                    )
                }
            ),
            @OpenApiResponse(
                status = "400",
                description = "Invalid timezone query parameter.",
                content = {
                    @OpenApiContent(
                        from = String.class,
                        mimeType = "text/plain",
                        example = "Invalid 'timezone' format, see https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html"
                    )
                }
            )
        }
    )
    private void handleMarketImage(Context ctx) {
        final String timezone = ctx.queryParam("timezone");
        ZoneId zone = ZoneId.of("UTC+00");

        if (timezone != null) {
            try {
                zone = ZoneId.of(timezone);
            } catch (Exception ignored) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.result("Invalid 'timezone' format, see https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html");
                return;
            }
        }

        final MarketImage image = new MarketImage(exchange.market, zone);

        image.draw();
        ctx.contentType(ContentType.IMAGE_PNG);
        ctx.result(image.writeToStream());
    }

    @OpenApi(
        path = "exchange/market/lastPrice",
        methods = HttpMethod.GET,
        description = "Get the last price of the current exchange market object.",
        operationId = "exchange_market_lastPrice",
        tags = { "Exchange Market" },
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The last price.",
                content = {
                    @OpenApiContent(
                        from = Integer.class,
                        mimeType = "text/plain",
                        example = "0"
                    )
                }
            )
        }
    )
    private void handleMarketLastPrice(Context ctx) {
        ctx.result(Integer.toString(exchange.market.lastPrice));
    }

    @OpenApi(
        path = "exchange/market/buyOffers",
        methods = HttpMethod.GET,
        description = "Get the 5 best buy offers of the current exchange market object.",
        operationId = "exchange_market_buyOffers",
        tags = { "Exchange Market" },
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The 5 best buy offers.",
                content = {
                    @OpenApiContent(
                        from = Offer[].class,
                        mimeType = "application/json"
                    )
                }
            )
        }
    )
    private void handleMarketBuyOffers(Context ctx) {
        ctx.json(exchange.market.buyOffers);
    }

    @OpenApi(
        path = "exchange/market/sellOffers",
        methods = HttpMethod.GET,
        description = "Get the 5 best sell offers of the current exchange market object.",
        operationId = "exchange_market_sellOffers",
        tags = { "Exchange Market" },
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The 5 best sell offers.",
                content = {
                    @OpenApiContent(
                        from = Offer[].class,
                        mimeType = "application/json"
                    )
                }
            )
        }
    )
    private void handleMarketSellOffers(Context ctx) {
        ctx.json(exchange.market.sellOffers);
    }
}
