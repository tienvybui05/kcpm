require("dotenv").config();

const required = ["JIRA_BASE", "EMAIL", "TOKEN", "PROJECT_KEY"];

function validateConfig() {
  const missing = required.filter((key) => !process.env[key]);

  if (missing.length > 0) {
    throw new Error(`Missing Jira config in .env: ${missing.join(", ")}`);
  }
}

module.exports = {
  JIRA_BASE: process.env.JIRA_BASE,
  EMAIL: process.env.EMAIL,
  TOKEN: process.env.TOKEN,
  PROJECT_KEY: process.env.PROJECT_KEY,

  JIRA_SERVICE_ISSUE_TYPE: process.env.JIRA_SERVICE_ISSUE_TYPE || "Epic",
  JIRA_AUTOMATION_ISSUE_TYPE: process.env.JIRA_AUTOMATION_ISSUE_TYPE || "Task",
  JIRA_BUG_ISSUE_TYPE: process.env.JIRA_BUG_ISSUE_TYPE || "Subbug",

  validateConfig,
};