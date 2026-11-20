// Builds the Google Play listing icon (512x512) from the app's own icon.
// Run: node tools/play-icon.mjs   (or: npm run icon:play)
//
// Two requirements pull in opposite directions, which is why this script exists:
// Play wants a 32-bit PNG *with* an alpha channel, while the same source tile
// feeds the iOS AppIcon, and App Store Connect rejects an icon that has alpha.
// So the source stays opaque and the alpha channel is added here, at generation
// time. Regenerating with a plain `sharp(...).resize()` writes 24-bit again and
// Play Console refuses it — always go through this script.
import sharp from 'sharp';

const SRC = 'assets/icon-only.png';
const OUT = 'assets/play-store-icon-512.png';

await sharp(SRC)
  .resize(512, 512, { fit: 'cover' })
  .ensureAlpha()
  .png({ compressionLevel: 9 })
  .toFile(OUT);

const { width, height, channels, hasAlpha } = await sharp(OUT).metadata();
console.log(`${OUT} ${width}x${height} ${channels}ch alpha=${hasAlpha}`);

if (width !== 512 || height !== 512 || !hasAlpha) {
  console.error('Play needs 512x512 with alpha — regeneration failed its own check.');
  process.exit(1);
}
