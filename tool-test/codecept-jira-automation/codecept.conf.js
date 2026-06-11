require("dotenv").config();

const { setHeadlessWhen } = require("@codeceptjs/configure");
const { initJiraReporter } = require("./jira/reporter");

setHeadlessWhen(process.env.HEADLESS);

if (process.env.JIRA_MODE === "jira") {
  initJiraReporter();
}

exports.config = {
  tests: process.env.TEST_FILE || "./tests/**/*_test.js",
  output: "./output",

  helpers: {
    Playwright: {
      browser: "chromium",
      url: process.env.APP_URL || "http://localhost:3000",
      show: true,
      // CodeceptJS/Playwright vẫn có thể tự sinh screenshot lỗi trong output.
      // Reporter bên dưới sẽ chủ động chụp thêm screenshot và upload lên Jira.
    },
  },

  include: {
    I: "./steps_file.js",
  },

  noGlobals: true,
};
