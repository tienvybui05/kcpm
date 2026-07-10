const API_CONFIG = {
  baseURL:
    process.env.NODE_ENV === "production"
      ? "https://35.240.156.187:8080"
      : "http://localhost:8080",

  // Cấu hình fetch mặc định
  fetchConfig: {
    mode: "cors",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  },
};

export default API_CONFIG;