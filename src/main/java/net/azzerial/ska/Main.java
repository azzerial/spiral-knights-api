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

package net.azzerial.ska;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import net.azzerial.ska.exchange.ExchangeListener;
import net.azzerial.ska.exchange.ExchangePlugin;
import net.azzerial.skhc.SKClient;
import net.azzerial.skhc.SKClientBuilder;
import net.azzerial.skhc.enums.Language;
import net.azzerial.skhc.enums.Region;
import net.azzerial.skhc.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public final class Main {

    private static final Logger log = LoggerFactory.getLogger("net.azzerial.ska");

    private static final SKClient client;

    // this listener gives access to the current market object
    public static final ExchangeListener exchange = new ExchangeListener();

    static {
        final String username = System.getenv("SPIRAL_KNIGHTS_USERNAME");
        final String password = System.getenv("SPIRAL_KNIGHTS_PASSWORD");

        try {
            // create the SKClient
            client = SKClientBuilder.create(username, password)
                .setRegion(Region.EU_WEST)
                .setLanguage(Language.ENGLISH)
                .enableServices(Service.EXCHANGE)
                .build();

            // register the event listeners
            client.addEventListeners(exchange);

            // bring the client online
            if (!client.connect()) {
                throw new ExceptionInInitializerError("This client is already connected!");
            }
        } catch (LoginException e) {
            log.error("Client connection failed: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    /* Constructors */

    private Main() {}

    /* Methods */

    public static void main(String[] args) {
        // create and start the Javalin webserver
        Javalin
            .create(config -> {
                config.showJavalinBanner = false;

                // register the routing plugins
                config.registerPlugin(new ExchangePlugin());

                // register the documentation plugins
                final String docsPath = "/openapi.json";

                config.registerPlugin(new OpenApiPlugin(openapi ->
                    openapi
                        .withDocumentationPath(docsPath)
                        .withDefinitionConfiguration((version, definition) -> definition
                            .withInfo(info -> info
                                .title("Spiral Knights Api")
                                .version("1.0.0")
                                .description("Spiral Knights unofficial public REST API")
                                .contact("azzerial", "https://github.com/azzerial", "robin@azzerial.net")
                                .license("Apache License 2.0", "https://www.apache.org/licenses/LICENSE-2.0.txt", "Apache-2.0")
                            )
                        )
                        .withPrettyOutput()
                ));
                config.registerPlugin(new SwaggerPlugin(swagger -> {
                    swagger.setDocumentationPath(docsPath);
                    swagger.setTitle("Spiral Knights Api - Documentation");
                }));
            })
            .start(8080);
    }
}
