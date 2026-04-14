const fs = require('fs');
let c = fs.readFileSync('C:/Users/byy90/.qclaw/workspace/personal-blog/client/src/views/ArticleDetail.vue', 'utf8');
const idx = c.lastIndexOf('<style');
const stylePart = c.substring(idx);

const startMarker = '.comments-section {';
const endMarker = '.sub-comments {';
const si = stylePart.indexOf(startMarker);
const ei = stylePart.indexOf(endMarker);
if (si < 0 || ei < 0) { console.log('NOT FOUND', si, ei); process.exit(1); }

const before = stylePart.substring(0, si);
const after = stylePart.substring(ei + endMarker.length);

const newCSS = `
/* 评论区 */
.comments-section {
  margin-top: 40px;
}
.comments-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 24px;
}
.comments-count {
  font-size: 14px;
  color: var(--text-lighter);
  font-weight: 400;
}

/* 评论表单 - 与留言板一致 */
.comment-form {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 32px;
}
.form-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}
.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-input:focus {
  outline: none;
  border-color: var(--accent);
}
.form-input::placeholder {
  color: var(--text-lighter);
}
.form-input.full-row {
  width: 100%;
  margin-bottom: 12px;
}
.comment-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.comment-textarea:focus {
  outline: none;
  border-color: var(--accent);
}
.reply-notice {
  margin-top: 8px;
  padding: 6px 12px;
  background: rgba(200,169,126,0.08);
  border-radius: 6px;
  font-size: 13px;
  color: var(--accent-dark);
}
.cancel-reply {
  margin-left: 8px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 12px;
}
.cancel-reply:hover { color: #f56c6c; }
.form-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
.btn-submit {
  padding: 8px 20px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}
.btn-submit:hover:not(:disabled) {
  background: var(--accent-dark);
  transform: translateY(-1px);
}
.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 评论列表 - 与留言板一致 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.comment-item {
  display: flex;
  gap: 14px;
  animation: fadeUp 0.35s both;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
}
.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-letter {
  font-size: 14px;
  font-weight: 700;
  color: var(--accent);
  font-family: 'Noto Serif SC', serif;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}
.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}
.comment-author.linked {
  color: var(--accent-dark);
  text-decoration: none;
}
.comment-author.linked:hover {
  text-decoration: underline;
}
.comment-badge {
  font-size: 11px;
  padding: 1px 8px;
  background: rgba(200,169,126,0.15);
  color: var(--accent-dark);
  border-radius: 10px;
  font-weight: 600;
}
.comment-time {
  font-size: 12px;
  color: var(--text-lighter);
}
.comment-email {
  font-size: 11px;
  color: var(--text-lighter);
}
.comment-text {
  font-size: 14px;
  color: var(--text);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.comment-actions {
  margin-top: 6px;
}
.action-btn {
  font-size: 12px;
  color: #c0c0c0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-right: 10px;
  transition: color 0.2s;
}
.action-btn:hover { color: var(--accent); }
.action-btn.del-btn:hover { color: #f56c6c; }

.sub-comments {
`;

const newStyle = before + newCSS + after;
c = c.substring(0, idx) + newStyle;
fs.writeFileSync('C:/Users/byy90/.qclaw/workspace/personal-blog/client/src/views/ArticleDetail.vue', c, 'utf8');
console.log('OK - wrote ' + c.length + ' chars');
