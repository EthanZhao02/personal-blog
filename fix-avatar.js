const fs = require('fs');
const file = 'D:\\personal-blog\\client\\src\\views\\ArticleDetail.vue';
let s = fs.readFileSync(file, 'utf8');
const oldBlock = `.about-avatar .avatar-glow-ring {\r\n  position: absolute;\r\n  inset: -5px;\r\n  border-radius: 20px;\r\n  background: conic-gradient(from 0deg, transparent 0%, rgba(56, 248, 255, 0.25) 30%, rgba(155, 92, 255, 0.25) 70%, transparent 100%);\r\n  animation: ringRotate 10s linear infinite;\r\n  opacity: 0.5;\r\n}\r\n.about-avatar .avatar-glow-ring::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 2px;\r\n  background: rgba(8, 14, 27, 0.95);\r\n  border-radius: 18px;\r\n}`;

const newBlock = `.about-avatar .avatar-glow-ring {\r\n  position: absolute;\r\n  inset: -7px;\r\n  border-radius: 22px;\r\n  background: conic-gradient(from 0deg, transparent 0%, rgba(56, 248, 255, 0.7) 30%, rgba(155, 92, 255, 0.7) 70%, transparent 100%);\r\n  animation: ringRotate 4s linear infinite;\r\n  opacity: 0.85;\r\n  filter: drop-shadow(0 0 6px rgba(56, 248, 255, 0.45));\r\n}\r\n.about-avatar .avatar-glow-ring::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 2px;\r\n  background: rgba(8, 14, 27, 0.92);\r\n  border-radius: 20px;\r\n}`;

if (!s.includes(oldBlock)) {
  console.log('old block not found');
  process.exit(1);
}
s = s.replace(oldBlock, newBlock);
fs.writeFileSync(file, s, 'utf8');
console.log('done');
