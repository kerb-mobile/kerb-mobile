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
import { statSync } from 'fs';

const SRC = 'assets/icon-only.png';
const OUT = 'assets/play-store-icon-512.png';
const SIZE = 512;
const MAX_BYTES = 1024 * 1024; // Play's own limit for the listing icon

const src = await sharp(SRC).metadata();
if (!src.width || !src.height) throw new Error(`${SRC} is not a readable image`);
if (src.width !== src.height) {
  throw new Error(
    `${SRC} is ${src.width}x${src.height}; a square source is required, or ` +
      'fitting to 512x512 crops the mark',
  );
}
if (src.width < SIZE) {
  throw new Error(`${SRC} is ${src.width}px wide; upscaling to ${SIZE} softens the icon`);
}

await sharp(SRC)
  .resize(SIZE, SIZE, { fit: 'cover' })
  .ensureAlpha()
  .png({ compressionLevel: 9 })
  .toFile(OUT);

const out = await sharp(OUT).metadata();
const bytes = statSync(OUT).size;
console.log(`${OUT} ${out.width}x${out.height} ${out.channels}ch alpha=${out.hasAlpha} ${bytes} bytes`);

// Fail loudly rather than ship an icon Play Console will reject.
const problems = [];
if (out.width !== SIZE || out.height !== SIZE) problems.push(`not ${SIZE}x${SIZE}`);
if (!out.hasAlpha || out.channels !== 4) {
  problems.push('no alpha channel — Play requires a 32-bit PNG with alpha');
}
if (bytes > MAX_BYTES) problems.push(`${bytes} bytes is over Play's ${MAX_BYTES}`);
if (problems.length) {
  console.error(`FAILED: ${problems.join('; ')}`);
  process.exit(1);
}
