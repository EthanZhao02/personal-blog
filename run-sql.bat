ALTER TABLE friend_link ADD COLUMN category VARCHAR(50) DEFAULT 'tech';
UPDATE friend_link SET category = 'tech' WHERE category IS NULL;