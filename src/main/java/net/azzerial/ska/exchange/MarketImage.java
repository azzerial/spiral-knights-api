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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class MarketImage {

    // sizes
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int RADIUS = 24;
    // formats
    private static final DateTimeFormatter CREATION_TIME_FORMAT = DateTimeFormatter.ofPattern("'Market offers on' EEEE, d MMMM yyyy 'at' HH:mm:ss ('UTC'x).", Locale.ENGLISH);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.ENGLISH);
    // colors
    private static final Color BACKGROUND_COLOR = new Color(35, 44, 51);
    private static final Color BOX_COLOR = new Color(33, 52, 62);
    private static final Color TITLE_BOX_COLOR = new Color(103, 81, 41);
    private static final Color CELL_BOX_COLOR = new Color(31, 60, 73);
    private static final Color TEXT_COLOR = new Color(208, 220, 242);
    private static final Color PRICE_TEXT_COLOR = new Color(248, 211, 156);
    private static final Color ENERGY_TEXT_COLOR = new Color(41, 214, 255);
    // fonts
    private static final Font REGULAR_FONT;
    private static final Font LARGE_FONT;
    private static final Font LARGE_ITALIC_FONT;
    // images
    private static final BufferedImage CROWN_21_IMAGE;
    private static final BufferedImage CROWN_32_IMAGE;
    private static final BufferedImage ENERGY_24_IMAGE;

    static {
        final ClassLoader classLoader = MarketImage.class.getClassLoader();

        try {
            final Font arialBlack = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(classLoader.getResourceAsStream("arial_black.ttf")));

            REGULAR_FONT = arialBlack.deriveFont(Font.PLAIN, 22);
            LARGE_FONT = arialBlack.deriveFont(Font.PLAIN, 32);
            LARGE_ITALIC_FONT = arialBlack.deriveFont(Font.ITALIC, 32);
            CROWN_21_IMAGE = ImageIO.read(Objects.requireNonNull(classLoader.getResourceAsStream("crown_21.png")));
            CROWN_32_IMAGE = ImageIO.read(Objects.requireNonNull(classLoader.getResourceAsStream("crown_32.png")));
            ENERGY_24_IMAGE = ImageIO.read(Objects.requireNonNull(classLoader.getResourceAsStream("energy_24.png")));
        } catch (IOException | FontFormatException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final BufferedImage TEMPLATE;

    static {
        TEMPLATE = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        final Graphics2D g = TEMPLATE.createGraphics();

        // init
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // fill background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // draw title box
        g.setColor(TITLE_BOX_COLOR);
        g.fillRoundRect(32, 32, 1216, 64, RADIUS, RADIUS);

        // draw offers box
        g.setColor(BOX_COLOR);
        g.fillRoundRect(32, 128, 592, 464, RADIUS, RADIUS);
        g.fillRoundRect(656, 128, 592, 464, RADIUS, RADIUS);

        // draw buy & sell offers cells
        for (int i = 0; i != 5; i++) {
            // draw buy & sell offers cell
            g.setColor(CELL_BOX_COLOR);
            g.fillRoundRect(56, 192 + (i * 78), 544, 64, RADIUS, RADIUS);
            g.fillRoundRect(680, 192 + (i * 78), 544, 64, RADIUS, RADIUS);

            // draw buy & sell offers inner cell
            g.setColor(BOX_COLOR);
            g.fillRoundRect(68, 204 + (i * 78), 266, 40, RADIUS, RADIUS);
            g.fillRoundRect(692, 204 + (i * 78), 266, 40, RADIUS, RADIUS);
        }

        // draw footer box
        g.setColor(BOX_COLOR);
        g.fillRoundRect(32, 624, 1216, 64, RADIUS, RADIUS);

        // clean
        g.dispose();
    }

    private final Market market;
    private final OffsetDateTime dateTime;
    private final BufferedImage bufferedImage;

    /* Constructors */

    public MarketImage(Market market, ZoneId zoneId) {
        this.market = market;
        this.dateTime = OffsetDateTime.now(zoneId);
        this.bufferedImage = new BufferedImage(
            TEMPLATE.getColorModel(),
            TEMPLATE.copyData(TEMPLATE.getRaster().createCompatibleWritableRaster()),
            TEMPLATE.isAlphaPremultiplied(),
            null
        );
    }

    /* Methods */

    public synchronized void draw() {
        final Graphics2D g = bufferedImage.createGraphics();

        // init
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // draw components
        drawTitle(g);
        drawOffers(g);
        drawFooter(g);

        // clean
        g.dispose();
    }

    public InputStream writeToStream() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        InputStream in = null;
        try {
            ImageIO.write(bufferedImage, "png", out);
            in = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    /* Internal */

    private void drawTitle(Graphics2D g) {
        final String lastPrice = NUMBER_FORMAT.format(market.lastPrice);
        final int titleStart = center(
            350 + (int) LARGE_FONT.getStringBounds(lastPrice, g.getFontRenderContext()).getWidth(),
            WIDTH
        );

        // draw text
        g.setColor(PRICE_TEXT_COLOR);
        g.setFont(LARGE_ITALIC_FONT);
        g.drawString("Last trade price:", titleStart, 74);
        g.drawImage(CROWN_32_IMAGE, null, titleStart + 314, 47);
        g.setFont(LARGE_FONT);
        g.drawString(lastPrice, titleStart + 350, 74);
    }

    private void drawOffers(Graphics2D g) {
        // draw titles
        g.setColor(TEXT_COLOR);
        g.setFont(REGULAR_FONT);
        g.drawString("Top Offers to Buy", 56, 168);
        g.drawString("Top Offers to Sell", 680, 168);
        g.drawImage(ENERGY_24_IMAGE, null, 274, 148);
        g.drawImage(ENERGY_24_IMAGE, null, 898, 148);
        g.setColor(ENERGY_TEXT_COLOR);
        g.drawString("100", 300, 168);
        g.drawString("100", 924, 168);

        // draw cells
        for (int i = 0; i != 5; i++) {
            final String buyPrice = NUMBER_FORMAT.format(market.buyOffers[i].price);
            final String sellPrice = NUMBER_FORMAT.format(market.sellOffers[i].price);
            final String buyVolume = NUMBER_FORMAT.format(market.buyOffers[i].volume);
            final String sellVolume = NUMBER_FORMAT.format(market.sellOffers[i].volume);
            final int buyPriceStart = padRight(
                (int) REGULAR_FONT.getStringBounds(buyPrice, g.getFontRenderContext()).getWidth(),
                214
            );
            final int sellPriceStart = padRight(
                (int) REGULAR_FONT.getStringBounds(sellPrice, g.getFontRenderContext()).getWidth(),
                214
            );

            // draw price texts
            g.drawImage(CROWN_21_IMAGE, null, 80, 214 + (i * 78));
            g.drawImage(CROWN_21_IMAGE, null, 704, 214 + (i * 78));
            g.setColor(PRICE_TEXT_COLOR);
            g.drawString(buyPrice, 108 + buyPriceStart, 232 + (i * 78));
            g.drawString(sellPrice, 732 + sellPriceStart, 232 + (i * 78));

            // draw volume texts
            g.setColor(TEXT_COLOR);
            g.drawString("x " + buyVolume, 352, 232 + (i * 78));
            g.drawString("x " + sellVolume, 976, 232 + (i * 78));
        }
    }

    private void drawFooter(Graphics2D g) {
        // draw text
        g.setColor(TEXT_COLOR);
        g.setFont(REGULAR_FONT);
        g.drawString(dateTime.format(CREATION_TIME_FORMAT), 56, 664);
    }

    // util

    private int center(int elementSize, int parentSize) {
        return Math.floorDiv(parentSize - elementSize, 2);
    }

    private int padRight(int elementSize, int parentSize) {
        return parentSize - elementSize;
    }
}
