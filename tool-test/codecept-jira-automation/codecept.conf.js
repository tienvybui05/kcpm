require("dotenv").config();

const { setHeadlessWhen } = require("@codeceptjs/configure");
const { initJiraReporter } = require("./jira/reporter");

setHeadlessWhen(process.env.HEADLESS);

// Chỉ upload Jira khi bật chế độ jira
if (process.env.JIRA_MODE === "jira") {
  initJiraReporter();
}

exports.config = {
  tests: process.env.TEST_FILE || "./tests/**/*_test.js",

  output: "./output",

  helpers: {
    Playwright: {
      browser: "chromium",
      url: process.env.WEB_URL || "http://localhost:3000",

      show: true,
      waitForTimeout: 10000,
      windowSize: "1366x768",

      // tự chụp ảnh khi fail
      screenshotOnFail: true,
    },

    // BẮT BUỘC để dùng I.sendPostRequest()
    REST: {
      endpoint: process.env.API_URL || "http://localhost:8080",

      defaultHeaders: {
        "Content-Type": "application/json",
      },
    },
  },

  include: {
    I: "./steps_file.js",
  },

  plugins: {
    screenshotOnFail: {
      enabled: true,
    },
  },

  noGlobals: true,

  name: "codecept-jira-automation",
};