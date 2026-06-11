const { spawnSync } = require("child_process");

const args = process.argv.slice(2);

let mode = "dry";
let file = null;

args.forEach((arg) => {
  if (arg.startsWith("--mode=")) {
    mode = arg.split("=")[1];
  }

  if (arg.startsWith("--file=")) {
    file = arg.split("=")[1];
  }
});

process.env.JIRA_MODE = mode;

if (file) {
  process.env.TEST_FILE = `./tests/${file}`;
} else {
  process.env.TEST_FILE = "./tests/**/*_test.js";
}

console.log(`🚀 Running mode=${mode}, file=${file || "ALL"}`);

const result = spawnSync("npx", ["codeceptjs", "run"], {
  stdio: "inherit",
  shell: true,
  env: process.env,
});

console.log("🏁 CodeceptJS finished.");

process.exit(result.status || 0);