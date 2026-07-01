const { spawnSync } = require("child_process");

const args = process.argv.slice(2);

let mode = "dry";
let file = null;
let workers = null; // Thêm biến để nhận số luồng

args.forEach((arg) => {
  if (arg.startsWith("--mode=")) {
    mode = arg.split("=")[1];
  }

  if (arg.startsWith("--file=")) {
    file = arg.split("=")[1];
  }

  // Thêm logic bắt cờ --workers
  if (arg.startsWith("--workers=")) {
    workers = arg.split("=")[1];
  }
});

process.env.JIRA_MODE = mode;

if (file) {
  process.env.TEST_FILE = `./tests/${file}`;
} else {
  process.env.TEST_FILE = "./tests/**/*_test.js";
}

console.log(
  `🚀 Running mode=${mode}, file=${file || "ALL"}${workers ? `, workers=${workers}` : ""}`,
);

// Chuyển đổi lệnh chạy dựa vào việc có truyền workers hay không
let commandArgs = ["codeceptjs", "run"];
if (workers) {
  commandArgs = ["codeceptjs", "run-workers", workers];
}

const result = spawnSync("npx", commandArgs, {
  stdio: "inherit",
  shell: true,
  env: process.env,
});

console.log("🏁 CodeceptJS finished.");

process.exit(result.status != null ? result.status : 1);