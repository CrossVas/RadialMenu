package crossvas.mods.radialmenu.utils.internal;

/**
 * Immutable RGBA color class intended as a lightweight, platform-safe replacement
 * for {@code java.awt.Color}, especially useful on macOS and other environments
 * where {@code java.awt} is unavailable or problematic.
 * <p>
 * Supports construction from float/int components, 24-bit hex strings, and packed ints.
 * Provides convenience methods to create colors and modify alpha channel.
 */
public class RColor {

    private final int red, green, blue, alpha;

    public RColor(float r, float g, float b, float a) {
        this(clamp255(r), clamp255(g), clamp255(b), clamp255(a));
    }

    public RColor(int r, int g, int b, int a) {
        this.red = clampInt(r);
        this.green = clampInt(g);
        this.blue = clampInt(b);
        this.alpha = clampInt(a);
    }

    /**
     * Creates a color from separate RGBA ints.
     */
    public static RColor ofRGBA(int r, int g, int b, int a) {
        return new RColor(r, g, b, a);
    }

    /**
     * Creates a color from a packed RGBA int.
     * <p>
     * Format: 0xRRGGBBAA (red highest byte, alpha the lowest byte).
     */
    public static RColor ofRGBA(int rgba) {
        int r = (rgba >> 24) & 0xFF;
        int g = (rgba >> 16) & 0xFF;
        int b = (rgba >> 8) & 0xFF;
        int a = rgba & 0xFF;
        return new RColor(r, g, b, a);
    }

    /**
     * Creates an opaque color from a 24-bit RGB int.
     * <p>
     * Format: 0xRRGGBB
     */
    public static RColor ofHex(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new RColor(r, g, b, 255);
    }

    /**
     * Creates an opaque color from a 6-digit hex string "RRGGBB".
     * <p>
     * Example: "7FFF8E"
     * @throws IllegalArgumentException if string is not 6 characters.
     */
    public static RColor ofHex(String hex) {
        if (hex.length() != 6)
            throw new IllegalArgumentException("Hex string must be 6 characters (RRGGBB)");
        int rgb = Integer.parseInt(hex, 16);
        return ofHex(rgb);
    }

    /**
     * Returns a new color with the same RGB components but modified alpha.
     * @param alpha alpha value [0..255]
     */
    public RColor withAlpha(int alpha) {
        return new RColor(red, green, blue, alpha);
    }

    // getters
    public int getRed()   { return red; }
    public int getGreen() { return green; }
    public int getBlue()  { return blue; }
    public int getAlpha() { return alpha; }

    // helpers
    private static int clampInt(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static int clamp255(float value) {
        return clampInt((int)(value * 255.0f + 0.5f));
    }
}
