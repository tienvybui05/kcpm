const axios = require("axios");
const { JIRA_BASE, EMAIL, TOKEN } = require("./config");

const auth = Buffer.from(`${EMAIL}:${TOKEN}`).toString("base64");

const client = axios.create({
  baseURL: `${JIRA_BASE}/rest/api/3`,
  headers: {
    Authorization: `Basic ${auth}`,
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});

module.exports = client;
