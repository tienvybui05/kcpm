const fs = require("fs");
const crypto = require("crypto");
const FormData = require("form-data");
const client = require("./client");

const {
  PROJECT_KEY,
  JIRA_AUTOMATION_ISSUE_TYPE,
  JIRA_BUG_ISSUE_TYPE,
} = require("./config");

function normalizeLabel(value) {
  return (
    String(value || "unknown")
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "")
      .slice(0, 60) || "unknown"
  );
}

function escapeJqlText(value) {
  return String(value || "")
    .replace(/\\/g, "\\\\")
    .replace(/"/g, '\\"');
}

function toTitle(value) {
  return String(value || "unknown")
    .replace(/[_-]+/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

function parseTestMeta(test) {
  const rawFile = test?.file || "";
  const normalized = rawFile.replace(/\\/g, "/");

  let relativePath = normalized;

  if (normalized.includes("/tests/")) {
    relativePath = "tests/" + normalized.split("/tests/")[1];
  }

  const parts = relativePath.replace(/^tests\//, "").split("/").filter(Boolean);

  const fileName = parts[parts.length - 1] || "unknown_test.js";
  const group = parts.length > 1 ? parts[0] : "general";
  const featureRaw = fileName.replace(/_test\.js$/i, "").replace(/\.js$/i, "");

  return {
    group,
    groupName: toTitle(group),
    feature: featureRaw,
    featureName: toTitle(featureRaw),
    fileName,
    filePath: relativePath,
  };
}

function createFingerprint(meta, test) {
  const raw = [meta.group, meta.feature, test?.title || "unknown", meta.fileName].join("|");
  const hash = crypto.createHash("sha1").update(raw).digest("hex").slice(0, 12);

  return {
    raw,
    label: `fp-${hash}`,
  };
}

function adfParagraph(text) {
  return {
    type: "paragraph",
    content: [
      {
        type: "text",
        text: String(text || ""),
      },
    ],
  };
}

function getErrorMessage(err, test) {
  const candidates = [
    err?.message,
    err?.inspect?.(),
    err?.stack,
    test?.err?.message,
    test?.err?.inspect?.(),
    test?.err?.stack,
  ];

  const message = candidates.find((item) => item && String(item).trim());

  if (!message) {
    return "Không lấy được nội dung lỗi từ CodeceptJS";
  }

  return String(message).slice(0, 3000);
}

function cleanErrorMessage(errorMessage) {
  return String(errorMessage || "")
    .replace(/\x1b\[[0-9;]*m/g, "")
    .slice(0, 3000);
}

function extractExpectedActual(errorMessage) {
  const text = String(errorMessage || "");

  let expected = "Không tách được dữ liệu kì vọng từ lỗi.";
  let actual = "Không tách được dữ liệu thực tế từ lỗi.";

  const urlExpectedMatch = text.match(/expected url to include\s+"([^"]+)"/i);
  if (urlExpectedMatch) {
    expected = `URL phải chứa: ${urlExpectedMatch[1]}`;
  }

  const seeExpectedMatch = text.match(/expected web application to include\s+"([^"]+)"/i);
  if (seeExpectedMatch) {
    expected = `Giao diện phải hiển thị: ${seeExpectedMatch[1]}`;
  }

  const diffMatch = text.match(/\+ expected - actual\s+([\s\S]*)/i);

  if (diffMatch) {
    const diffText = diffMatch[1];

    const actualMatch = diffText.match(/^\s*-([^\n\r]+)/m);
    const expectedLineMatch = diffText.match(/^\s*\+([^\n\r]+)/m);

    if (actualMatch) {
      actual = actualMatch[1].trim();
    }

    if (expectedLineMatch && expected === "Không tách được dữ liệu kì vọng từ lỗi.") {
      expected = expectedLineMatch[1].trim();
    }
  }

  return {
    expected,
    actual,
  };
}

function formatVietnamTime(date = new Date()) {
  return new Intl.DateTimeFormat("vi-VN", {
    timeZone: "Asia/Ho_Chi_Minh",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  }).format(date);
}

function buildDescription(err, test, meta, fingerprint, screenshotPath) {
  const rawErrorMessage = getErrorMessage(err, test);
  const errorMessage = cleanErrorMessage(rawErrorMessage);
  const { expected, actual } = extractExpectedActual(errorMessage);

  return {
    type: "doc",
    version: 1,
    content: [
      adfParagraph("Loại kiểm thử: UI Automation"),
      adfParagraph(`Nhóm chức năng: ${meta.groupName}`),
      adfParagraph(`Tính năng: ${meta.featureName}`),
      adfParagraph(`Kịch bản kiểm thử: ${test?.title || "N/A"}`),
      adfParagraph(`File kiểm thử: ${meta.filePath}`),

      adfParagraph(`Kì vọng: ${expected}`),
      adfParagraph(`Kết quả thực tế: ${actual}`),

      adfParagraph(`Error Message: ${errorMessage}`),
      adfParagraph(`Fingerprint: ${fingerprint.raw}`),
      adfParagraph(`Fingerprint Label: ${fingerprint.label}`),
      adfParagraph(`Thời gian ghi nhận: ${formatVietnamTime()} GMT+7`),
      adfParagraph(
        screenshotPath
          ? "Screenshot: đã tự động chụp và attach vào issue."
          : "Screenshot: không chụp được hoặc không tìm thấy file ảnh."
      ),
    ],
  };
}

async function searchIssues(jql, maxResults = 10) {
  console.log(`🔍 JQL: ${jql}`);

  const res = await client.get("/search/jql", {
    params: {
      jql,
      maxResults,
      fields: "key,summary,status,labels,parent",
    },
  });

  return res.data.issues || [];
}

async function findTaskBySummary(summary, labels = []) {
  const labelJql = labels
    .map((label) => `labels = "${escapeJqlText(label)}"`)
    .join(" AND ");

  const jql = [
    `project = "${escapeJqlText(PROJECT_KEY)}"`,
    `summary ~ "${escapeJqlText(summary)}"`,
    `issuetype = "${escapeJqlText(JIRA_AUTOMATION_ISSUE_TYPE || "Task")}"`,
    `statusCategory != Done`,
    labelJql,
  ]
    .filter(Boolean)
    .join(" AND ");

  const issues = await searchIssues(jql, 1);
  return issues[0]?.key || null;
}

async function findSubTaskByFingerprint(parentKey, fingerprintLabel) {
  const jql = [
    `project = "${escapeJqlText(PROJECT_KEY)}"`,
    `parent = "${escapeJqlText(parentKey)}"`,
    `labels = "${escapeJqlText(fingerprintLabel)}"`,
    `statusCategory != Done`,
  ].join(" AND ");

  const issues = await searchIssues(jql, 1);
  return issues[0]?.key || null;
}

async function createTask(summary, meta) {
  const labels = ["ui-automation", normalizeLabel(meta.group), normalizeLabel(meta.feature)];

  console.log(`🔎 Finding Task: ${summary}`);

  const existedKey = await findTaskBySummary(summary, labels);

  if (existedKey) {
    console.log(`📌 TASK FOUND: ${existedKey}`);
    return existedKey;
  }

  console.log("➕ Creating new Jira Task...");

  const res = await client.post("/issue", {
    fields: {
      project: {
        key: PROJECT_KEY,
      },
      summary,
      issuetype: {
        name: JIRA_AUTOMATION_ISSUE_TYPE || "Task",
      },
      labels,
      description: {
        type: "doc",
        version: 1,
        content: [
          adfParagraph(`UI Automation Feature: ${meta.groupName} - ${meta.featureName}`),
          adfParagraph(`Folder: tests/${meta.group}`),
          adfParagraph(`Thời gian tạo: ${formatVietnamTime()} GMT+7`),
        ],
      },
    },
  });

  console.log(`📌 TASK CREATED: ${res.data.key}`);
  return res.data.key;
}

async function createSubTask(parentKey, summary, err, test, meta, fingerprint, screenshotPath) {
  console.log(`🔎 Finding Bug/Subtask by fingerprint: ${fingerprint.label}`);

  const existedKey = await findSubTaskByFingerprint(parentKey, fingerprint.label);

  const rawErrorMessage = getErrorMessage(err, test);
  const errorMessage = cleanErrorMessage(rawErrorMessage);
  const { expected, actual } = extractExpectedActual(errorMessage);

  if (existedKey) {
    console.log(`🧩 BUG FOUND: ${existedKey}`);

    await addComment(
      existedKey,
      [
        `Retest: vẫn lỗi tại ${formatVietnamTime()} GMT+7`,
        `Kịch bản: ${test?.title || "N/A"}`,
        `File kiểm thử: ${meta.filePath}`,
        `Kì vọng: ${expected}`,
        `Kết quả thực tế: ${actual}`,
        `Error: ${errorMessage}`,
        screenshotPath
          ? "Screenshot: đã tự động chụp và attach thêm vào issue."
          : "Screenshot: không chụp được hoặc không tìm thấy file ảnh.",
      ].join("\n")
    );

    if (screenshotPath) {
      await attachFile(existedKey, screenshotPath);
      console.log(`📎 SCREENSHOT ATTACHED TO EXISTING BUG: ${existedKey}`);
    }

    return existedKey;
  }

  console.log("➕ Creating new Jira Bug/Subtask...");

  const res = await client.post("/issue", {
    fields: {
      project: {
        key: PROJECT_KEY,
      },
      issuetype: {
        name: JIRA_BUG_ISSUE_TYPE || "Subbug",
      },
      parent: {
        key: parentKey,
      },
      summary,
      labels: [
        "ui-automation",
        normalizeLabel(meta.group),
        normalizeLabel(meta.feature),
        fingerprint.label,
      ],
      description: buildDescription(err, test, meta, fingerprint, screenshotPath),
    },
  });

  const issueKey = res.data.key;
  console.log(`🧩 BUG CREATED: ${issueKey}`);

  if (screenshotPath) {
    await attachFile(issueKey, screenshotPath);
    console.log(`📎 SCREENSHOT ATTACHED: ${issueKey}`);
  }

  return issueKey;
}

async function addComment(issueKey, text) {
  console.log(`💬 Adding retest comment to ${issueKey}`);

  return client.post(`/issue/${issueKey}/comment`, {
    body: {
      type: "doc",
      version: 1,
      content: [adfParagraph(text)],
    },
  });
}

async function attachFile(issueKey, filePath) {
  if (!filePath || !fs.existsSync(filePath)) {
    console.log(`⚠️ Screenshot file not found: ${filePath}`);
    return false;
  }

  const form = new FormData();
  form.append("file", fs.createReadStream(filePath));

  await client.post(`/issue/${issueKey}/attachments`, form, {
    headers: {
      ...form.getHeaders(),
      "X-Atlassian-Token": "no-check",
    },
    maxBodyLength: Infinity,
    maxContentLength: Infinity,
  });

  return true;
}

module.exports = {
  parseTestMeta,
  createFingerprint,
  createTask,
  createSubTask,
  addComment,
  attachFile,
};