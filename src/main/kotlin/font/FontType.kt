package font

import graphics.Image
import loader.loadBufferedReader
import resources.Resource

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad polygons for any text using this font.
 *
 * @author Karl
 */
class FontType
/**
 * Creates a new font and loads up the data about each character from the
 * font file.
 *
 * @param textureAtlas
 * - the ID of the font atlas texture.
 * @param fontFile
 * - the font file containing information about each character in
 * the texture atlas.
 */
(val textureAtlas: Image, fontFile: Resource) {
    val metaFile: MetaFile = MetaFile(fontFile)
}
