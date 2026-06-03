// 直接require server.js 中的数据，输出为JSON
const fs = require('fs');
const path = require('path');

// 读取server.js内容
const serverContent = fs.readFileSync(path.join(__dirname, '../simple-backend/server.js'), 'utf8');

// 提取数据定义
function extractArray(name, content) {
  const regex = new RegExp(`const ${name} = (\\[.*?\\]);`, 's');
  const match = content.match(regex);
  if (match) {
    try {
      return eval(match[1]);
    } catch(e) {
      console.error(`解析 ${name} 失败:`, e.message);
      return [];
    }
  }
  return [];
}

const departments = extractArray('mockDepartments', serverContent);
const owners = extractArray('mockOwners', serverContent);
const classificationStandards = extractArray('mockClassificationStandards', serverContent);
const classifications = extractArray('mockClassifications', serverContent);
const gradingStandards = extractArray('mockGradingStandards', serverContent);
const gradings = extractArray('mockGradings', serverContent);
const assets = extractArray('mockAssets', serverContent);
const fields = extractArray('mockFields', serverContent);

const data = {
  departments,
  owners,
  classificationStandards,
  classifications,
  gradingStandards,
  gradings,
  assets,
  fields
};

console.log(JSON.stringify(data, null, 2));
