// Builds the Google Play feature graphic (1024x500) from the app's own icon.
// Run: .toolchain/node/bin/node .toolchain/feature-graphic.mjs
import sharp from 'sharp';
import { mkdirSync } from 'fs';

mkdirSync('store/graphics', { recursive: true });

const W = 1024;
const H = 500;
const iconSize = 236;

// The source icon is a square tile, so it is rounded here to read as an app
// icon against the gradient instead of a pasted black box.
const rounded = Buffer.from(`
<svg width="${iconSize}" height="${iconSize}" xmlns="http://www.w3.org/2000/svg">
  <rect width="${iconSize}" height="${iconSize}" rx="54" ry="54" fill="#ffffff"/>
</svg>`);

const icon = await sharp('assets/icon-only.png')
  .resize(iconSize, iconSize, { fit: 'cover' })
  .composite([{ input: rounded, blend: 'dest-in' }])
  .png()
  .toBuffer();

const backdrop = Buffer.from(`
<svg width="${W}" height="${H}" viewBox="0 0 ${W} ${H}" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#141416"/>
      <stop offset="55%" stop-color="#0B0B0C"/>
      <stop offset="100%" stop-color="#1B0D06"/>
    </linearGradient>
    <linearGradient id="glow" x1="0" y1="1" x2="1" y2="0">
      <stop offset="0%" stop-color="#FD5204" stop-opacity="0.30"/>
      <stop offset="70%" stop-color="#FD5204" stop-opacity="0"/>
    </linearGradient>
  </defs>
  <rect width="${W}" height="${H}" fill="url(#bg)"/>
  <circle cx="${W - 40}" cy="${H + 40}" r="320" fill="url(#glow)"/>
  <rect x="0" y="0" width="6" height="${H}" fill="#FD5204"/>
</svg>`);

const wordmark = Buffer.from(`
<svg width="${W}" height="${H}" viewBox="0 0 ${W} ${H}" xmlns="http://www.w3.org/2000/svg">
  <text x="380" y="228" font-family="Helvetica Neue, Helvetica, Arial, sans-serif"
        font-size="104" font-weight="700" fill="#FFFFFF" letter-spacing="-2">Kerb</text>
  <text x="383" y="278" font-family="Helvetica Neue, Helvetica, Arial, sans-serif"
        font-size="30" font-weight="500" fill="#FD5204" letter-spacing="3">RIDE · DELIVER · CONNECT</text>
  <text x="383" y="344" font-family="Helvetica Neue, Helvetica, Arial, sans-serif"
        font-size="30" font-weight="400" fill="#C9C9CE">Local rides with upfront fares,</text>
  <text x="383" y="384" font-family="Helvetica Neue, Helvetica, Arial, sans-serif"
        font-size="30" font-weight="400" fill="#C9C9CE">from drivers who live where you live.</text>
</svg>`);

await sharp(backdrop)
  .composite([
    { input: wordmark, top: 0, left: 0 },
    { input: icon, top: Math.round((H - iconSize) / 2), left: 84 },
  ])
  // Play wants 24-bit PNGs with no alpha channel.
  .flatten({ background: '#0B0B0C' })
  .removeAlpha()
  .png({ compressionLevel: 9 })
  .toFile('store/graphics/feature-graphic-1024x500.png');

const meta = await sharp('store/graphics/feature-graphic-1024x500.png').metadata();
console.log(`feature graphic ${meta.width}x${meta.height} ${meta.channels}ch alpha=${meta.hasAlpha}`);
