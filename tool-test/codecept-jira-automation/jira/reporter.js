const fs = require("fs");
const path = require("path");
const { event, container } = require("codeceptjs");

const { validateConfig } = require("./config");
const {
  parseTestMeta,
  createFingerprint,
  createTask,
  createSubTask,
} = require("./service");

const JIRA_MODE = process.env.JIRA_MODE || "dry";

// Cache task trong 1 lần chạy để không tạo lại task cha nhiều lần
const taskCache = {};

function safeName(value) {
  return (
    String(value || "unknown")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/[^a-zA-Z0-9-_]+/g, "-")
      .replace(/^-+|-+$/g, "")
      .slice(0, 120) || "unknown"
  );
}

async function captureScreenshot(test, meta) {
  try {
    const helper = container.helpers("Playwright");

    if (!helper || typeof helper.saveScreenshot !== "function") {
      console.log("⚠️ Playwright helper does not support saveScreenshot");
      return null;
    }

    const fileName = `${safeName(meta.group)}-${safeName(meta.feature)}-${safeName(
      test?.title
    )}-${Date.now()}.png`;

    await helper.saveScreenshot(fileName, true);

    const screenshotPath = path.join(process.cwd(), "output", fileName);

    if (fs.existsSync(screenshotPath)) {
      console.log(`📸 Screenshot saved: ${screenshotPath}`);
      return screenshotPath;
    }

    console.log("⚠️ Screenshot command ran, but file was not found in output folder");
    return null;
  } catch (e) {
    console.log("⚠️ Screenshot capture failed:", e.message);
    return null;
  }
}

async function sendFailedTestToJira(test, err, meta, fingerprint, screenshotPath) {
  const taskSummary = `[UI Automation] ${meta.groupName} - ${meta.featureName}`;
  const bugSummary = `[UI Bug] ${meta.groupName} - ${meta.featureName}: ${test.title}`;

  console.log("--------------------------------------------------");
  console.log(`📤 Sending failed UI test to Jira`);
  console.log(`📌 Task summary: ${taskSummary}`);
  console.log(`🐞 Bug summary: ${bugSummary}`);
  console.log(`🔖 Fingerprint: ${fingerprint?.label || fingerprint}`);
  console.log("--------------------------------------------------");

  try {
    if (!taskCache[taskSummary]) {
      console.log("🔎 Finding or creating Jira Task...");
      taskCache[taskSummary] = await createTask(taskSummary, meta);
      console.log(`📌 TASK READY: ${taskCache[taskSummary]}`);
    } else {
      console.log(`📌 TASK FROM CACHE: ${taskCache[taskSummary]}`);
    }

    console.log("🔎 Finding or creating Jira Bug/Subtask...");

    const subtaskKey = await createSubTask(
      taskCache[taskSummary],
      bugSummary,
      err,
      test,
      meta,
      fingerprint,
      screenshotPath
    );

    console.log(`🧩 BUG READY: ${subtaskKey}`);

    if (screenshotPath) {
      console.log(`📎 Screenshot path sent to service: ${screenshotPath}`);
    }

    console.log("✅ Jira sync completed for this failed test");
  } catch (e) {
    console.log("❌ Jira error while creating/updating issue:");

    if (e.response?.data) {
      console.log(JSON.stringify(e.response.data, null, 2));
    } else {
      console.log(e.message);
    }
  }
}

function initJiraReporter() {
  if (JIRA_MODE !== "jira") {
    console.log("🟡 Jira reporter disabled (dry-run mode)");
    return;
  }

  validateConfig();
  console.log("🟢 Jira reporter enabled");

  event.dispatcher.on(event.test.failed, async (test, err) => {
    const meta = parseTestMeta(test);
    const fingerprint = createFingerprint(meta, test);

    console.log(`🔥 FAIL DETECTED: ${meta.groupName} - ${meta.featureName} -> ${test.title}`);

    const screenshotPath = await captureScreenshot(test, meta);

    await sendFailedTestToJira(
      test,
      err,
      meta,
      fingerprint,
      screenshotPath
    );
  });

  event.dispatcher.on(event.all.after, () => {
    console.log("🏁 UI test run finished.");
  });
}

module.exports = { initJiraReporter };