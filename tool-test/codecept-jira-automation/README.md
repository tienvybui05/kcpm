# Framework Automation CodeceptJS tích hợp Jira

## Tổng quan

Dự án này là framework kiểm thử tự động UI được xây dựng bằng CodeceptJS kết hợp Playwright, có tích hợp Jira API để tự động tạo bug khi test thất bại.

Khi test fail, hệ thống có thể tự động tạo Jira Task và Sub-task để theo dõi lỗi.

Framework hỗ trợ 2 chế độ chạy:

- Chế độ dry-run: chỉ chạy test, không đẩy dữ liệu lên Jira
- Chế độ jira-run: chạy test và tự động đẩy bug lên Jira khi fail

Ngoài ra hỗ trợ chạy toàn bộ test hoặc theo từng file.

---

## Tính năng chính

- Kiểm thử UI bằng CodeceptJS + Playwright
- Tự động tạo Jira Task khi test fail
- Tự động tạo Jira Sub-task cho từng lỗi cụ thể
- Chế độ dry-run (không gửi Jira)
- Chế độ jira-run (có gửi Jira)
- Chạy toàn bộ test hoặc theo file riêng
- CLI runner giống Postman collection runner
- Tự động chụp màn hình khi test fail

---

## Cấu trúc dự án

codecept-ev/

├── jira/

│ ├── client.js # Client gọi Jira API (Axios)

│ ├── config.js # Cấu hình môi trường

│ ├── service.js # Logic tạo Task/Sub-task Jira

│ ├── reporter.js # Lắng nghe event fail của CodeceptJS

│

├── tests/ # Chứa test case

├── output/ # Chứa screenshot khi fail

├── run.js # CLI runner (dry/jira/file mode)

├── codecept.conf.js

├── steps_file.js

├── .env

├── package.json

---

## Tạo file test

Cài dependencies:

```bash
npx codeceptjs gt
```

## Cài đặt

Cài dependencies:

```bash
npm install
```

## Cấu hình môi trường

Tạo file .env ở thư mục gốc:

```
JIRA_BASE=https://your-domain.atlassian.net
EMAIL=your-email@example.com
TOKEN=your-jira-api-token
PROJECT_KEY=SCRUM

JIRA_MODE=dry
```

Lưu ý:

- Không commit file .env lên GitHub
- Không chia sẻ token Jira ra ngoài

## Chạy test cơ bản

Chạy tất cả test:

```bash
npx codeceptjs run
```

## Chạy bằng CLI runner

### 1. Chế độ dry-run (không đẩy Jira)

```bash
node run.js --mode=dry
```

Mô tả:

- Chạy test bình thường
- Không tạo Jira issue

### 2. Chế độ Jira-run (có tạo bug)

```bash
node run.js --mode=jira
```

Mô tả:

- Chạy test
- Khi fail sẽ tự động tạo Jira Task và Sub-task

### 3. Chạy theo file test

```bash
node run.js --mode=jira --file=register_test.js
```

Mô tả:

- Chỉ chạy file test được chỉ định
- Vẫn tạo Jira nếu có lỗi

### 4. Chạy toàn bộ test với Jira

```bash
node run.js --mode=jira
```

## Luồng tạo Jira

Khi test thất bại:

- Nhận sự kiện fail từ CodeceptJS
- Xác định feature và scenario
- Tạo Jira Task nếu chưa tồn tại trong session
- Tạo Jira Sub-task cho lỗi cụ thể
- Ghi thông tin lỗi vào description

## Artifact khi test fail

Khi test fail:

- Screenshot sẽ được lưu trong thư mục `output/`
- Có thể dùng để debug hoặc đính kèm Jira

## Ví dụ luồng test

- Truy cập trang web
- Nhập dữ liệu form
- Submit form
- Kiểm tra kết quả

Nếu fail:

- Test bị đánh fail
- Nếu bật jira mode → tạo issue tự động

## Cấu hình quan trọng

- Mode Jira được điều khiển bằng biến môi trường hoặc CLI
- Group bug theo file test hoặc suite
- Tránh tạo trùng bug bằng cache trong runtime

## Yêu cầu hệ thống

- Node.js từ 16 trở lên
- Jira Cloud + API Token
- CodeceptJS 4.x
- Playwright

## Vấn đề hiện tại

- Một số test có thể bị timeout khi navigation
- Feature name có thể hiển thị "Unknown" nếu thiếu metadata
- Có thể cần retry strategy cho test flaky

## Hướng phát triển

- Đính kèm screenshot trực tiếp lên Jira
- Retry tự động cho test fail không ổn định
- Phân loại bug theo severity
- Tích hợp Slack/Teams notify
- Dashboard báo cáo HTML
- Chạy song song (parallel execution)
