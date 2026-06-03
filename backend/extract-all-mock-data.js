const fs = require('fs');
const path = require('path');
const serverContent = fs.readFileSync(path.join(__dirname, '../simple-backend/server.js'), 'utf8');

function extractArray(name, content) {
  const regex = new RegExp(`const ${name} = (\\[.*?\\]);`, 's');
  const match = content.match(regex);
  if (match) {
    try { return eval(match[1]); } catch(e) { return []; }
  }
  return [];
}

const data = {
  users: extractArray('mockUsers', serverContent),
  roles: extractArray('mockRoles', serverContent),
  permissions: extractArray('mockPermissions', serverContent),
  userRoles: extractArray('mockUserRoles', serverContent),
  departments: extractArray('mockDepartments', serverContent),
  owners: extractArray('mockOwners', serverContent),
  classificationStandards: extractArray('mockClassificationStandards', serverContent),
  classifications: extractArray('mockClassifications', serverContent),
  gradingStandards: extractArray('mockGradingStandards', serverContent),
  gradings: extractArray('mockGradings', serverContent),
  assets: extractArray('mockAssets', serverContent),
  fields: extractArray('mockFields', serverContent),
};

console.log(JSON.stringify(data, null, 2));
