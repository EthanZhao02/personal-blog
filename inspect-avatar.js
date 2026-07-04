const fs = require('fs');
const s = fs.readFileSync('D:\\personal-blog\\client\\src\\views\\ArticleDetail.vue', 'utf8');
const idx = s.indexOf('.about-avatar .avatar-glow-ring');
if (idx === -1) {
  console.log('not found');
} else {
  console.log(JSON.stringify(s.substring(idx, idx + 700)));
}
