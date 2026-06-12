Feature("Register Form - Địa chỉ");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Bang_Lai_Xe", `A1-${timestamp.toString().slice(-6)}`);
};

// Nominal
Scenario("TC_REGISTER - Nhập địa chỉ hợp lệ", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Invalid
Scenario("TC_REGISTER - Để trống địa chỉ", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "");

  I.click("Đăng ký tài xế");

  I.see("Địa chỉ không hợp lệ");
});

// Boundary Min
Scenario("TC_REGISTER - Boundary Min (1 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "A");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Min+
Scenario("TC_REGISTER - Boundary Min+ (2 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "AB");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Nominal
Scenario("TC_REGISTER - Boundary Nominal (50 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "A".repeat(50));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max-
Scenario("TC_REGISTER - Boundary Max- (254 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "A".repeat(254));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max
Scenario("TC_REGISTER - Boundary Max (255 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Dia_Chi", "A".repeat(255));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});
