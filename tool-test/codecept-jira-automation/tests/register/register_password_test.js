Feature("Register Form - Mật khẩu");

const fillValidData = (I) => {
  const random = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${random}@gmail.com`);
  I.fillField("Sdt", `0${String(random).slice(-9)}`);
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1-${String(random).slice(-6)}`);
};

Scenario("TC_REGISTER - Nhập mật khẩu hợp lệ (6 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

Scenario("TC_REGISTER - Nhập mật khẩu không hợp lệ (4 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abc1");
  I.fillField("Xac_Nhan_Mat_Khau", "abc1");

  I.click("Đăng ký tài xế");

  I.see("Mật khẩu phải có ít nhất 6 ký tự!");
});

// Boundary Min- (5 ký tự)
Scenario("TC_REGISTER - Boundary Min- (5 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abc12");
  I.fillField("Xac_Nhan_Mat_Khau", "abc12");

  I.click("Đăng ký tài xế");

  I.see("Mật khẩu phải có ít nhất 6 ký tự!");
});

// Boundary Min (6 ký tự)
Scenario("TC_REGISTER - Boundary Min (6 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Min+ (7 ký tự)
Scenario("TC_REGISTER - Boundary Min+ (7 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abc1234");
  I.fillField("Xac_Nhan_Mat_Khau", "abc1234");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Nominal value
Scenario("TC_REGISTER - Nominal (10 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "abcdefghij");
  I.fillField("Xac_Nhan_Mat_Khau", "abcdefghij");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Upper range
Scenario("TC_REGISTER - Upper Range (20 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Mat_Khau", "a".repeat(20));
  I.fillField("Xac_Nhan_Mat_Khau", "a".repeat(20));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});