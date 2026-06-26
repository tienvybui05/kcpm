Feature("Register Form - Họ tên");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1-${timestamp.toString().slice(-6)}`);
};

// Nominal
Scenario("TC_REGISTER - Nhập họ tên hợp lệ (10 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "Nguyen Van A");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Invalid
Scenario("TC_REGISTER - Nhập họ tên 1 ký tự", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "A");

  I.click("Đăng ký tài xế");

  I.see("Họ tên phải ≥2 ký tự");
});

Scenario("TC_REGISTER - Nhập họ tên 65 ký tự", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "A".repeat(65));

  I.click("Đăng ký tài xế");

  I.see("Họ tên ≤64 ký tự");
});

Scenario("TC_REGISTER - Nhập họ tên có ký tự đặc biệt", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "Nguyen@Van");

  I.click("Đăng ký tài xế");

  I.see("Ký tự không hợp lệ");
});

// Boundary Min
Scenario("TC_REGISTER - Boundary Min (2 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "AB");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Min+
Scenario("TC_REGISTER - Boundary Min+ (3 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "ABC");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Nominal
Scenario("TC_REGISTER - Boundary Nominal (33 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "A".repeat(33));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max-
Scenario("TC_REGISTER - Boundary Max- (63 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "A".repeat(63));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max
Scenario("TC_REGISTER - Boundary Max (64 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Ho_Ten", "A".repeat(64));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});
