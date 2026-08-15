const fs = require('fs');
const path = require('path');

const file1 = path.join(__dirname, 'backend/src/main/resources/topics_seed.json');
const file2 = path.join(__dirname, 'backend/src/main/resources/topics_seed_2.json');

const data1 = JSON.parse(fs.readFileSync(file1, 'utf8'));
const data2 = JSON.parse(fs.readFileSync(file2, 'utf8'));

const merged = [...data1, ...data2];

fs.writeFileSync(file1, JSON.stringify(merged, null, 2), 'utf8');
console.log('Merged successfully. Total topics:', merged.length);
