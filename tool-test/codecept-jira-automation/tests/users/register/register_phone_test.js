Feature("Register Form - Số điện thoại");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1-${String(timestamp).slice(-6)}`);
};

Scenario(
  "TC_REGISTER - Nhập số điện thoại hợp lệ (10 số, bắt đầu bằng 0)",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "0987654321");

    I.click("Đăng ký tài xế");

    I.waitForURL(/\/login$/, 30);
  },
);

Scenario("TC_REGISTER - Số điện thoại không bắt đầu bằng 0", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "1234567890");

  I.click("Đăng ký tài xế");

  I.see("Số điện thoại không hợp lệ");
});

Scenario("TC_REGISTER - Số điện thoại chứa ký tự chữ", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "09abc54321");

  I.click("Đăng ký tài xế");

  I.see("Số điện thoại không hợp lệ");
});

// Boundary Value Analysis

Scenario("TC_REGISTER - Boundary Min- (9 chữ số)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "123456789");

  I.click("Đăng ký tài xế");

  I.see("Số điện thoại không hợp lệ");
});

Scenario("TC_REGISTER - Boundary Min (10 chữ số)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "0123456789");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Nom (10 chữ số)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "0981234567");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Max (10 chữ số)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "0999999999");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Max+ (11 chữ số)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Sdt", "01234567890");

  I.click("Đăng ký tài xế");

  I.see("Số điện thoại không hợp lệ");
});
