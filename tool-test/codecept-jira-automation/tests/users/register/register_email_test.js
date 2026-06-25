Feature("Register Form - Email");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1-${timestamp.toString().slice(-6)}`);
};

// Nominal
Scenario("TC_REGISTER - Nhập email hợp lệ", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", `test${Date.now()}@gmail.com`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Invalid
Scenario("TC_REGISTER - Email thiếu @", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "abcgmail.com");

  I.click("Đăng ký tài xế");

  I.see("Email không hợp lệ");
});

Scenario("TC_REGISTER - Email thiếu domain", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "abc@");

  I.click("Đăng ký tài xế");

  I.see("Email không hợp lệ");
});

Scenario(
  "TC_REGISTER - Email có ký tự đặc biệt không hợp lệ",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Email", "abc@#mail.com");

    I.click("Đăng ký tài xế");

    I.see("Email không hợp lệ");
  },
);

// Boundary Min
Scenario("TC_REGISTER - Boundary Min (Email 5 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "a@b.c");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Min+
Scenario("TC_REGISTER - Boundary Min+ (Email 6 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "a@bc.de");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Nominal
Scenario("TC_REGISTER - Boundary Nominal (Email 32 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "user123456789012345678901234@ab.com");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max-
Scenario("TC_REGISTER - Boundary Max- (Email 63 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "a".repeat(50) + "@gmail.com");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max
Scenario("TC_REGISTER - Boundary Max (Email 64 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "a".repeat(51) + "@gmail.com");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});
