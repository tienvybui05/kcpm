Feature("Register Form - Bằng lái xe");

const fillValidData = (I) => {
  const random = Date.now() + Math.floor(Math.random() * 100000);

  const email = `test${random}@gmail.com`;

  const phone = "0" + Math.floor(100000000 + Math.random() * 900000000);

  const license = "A1-" + Math.floor(100000 + Math.random() * 900000);

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", email);
  I.fillField("Sdt", phone);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");

  return license;
};

Scenario("TC_REGISTER - Nhập bằng lái xe hợp lệ (A1-123456)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField(
    "Bang_Lai_Xe",
    "A1-" + Math.floor(100000 + Math.random() * 900000),
  );

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario(
  "TC_REGISTER - Nhập bằng lái xe sai định dạng (123456A1)",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Bang_Lai_Xe", "123456A1");

    I.click("Đăng ký tài xế");

    I.see("Bằng lái xe không hợp lệ");
  },
);

Scenario("TC_REGISTER - Để trống bằng lái xe", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "");

  I.click("Đăng ký tài xế");

  I.see("Bằng lái xe không hợp lệ");
});

Scenario("TC_REGISTER - Boundary Min (7 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "A1-1234");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Min+ (8 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "B2-12345");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Nominal (10 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "A1-1234567");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Max- (15 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "A1-123456789012");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Boundary Max (20 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "B2-12345678901234567");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});
