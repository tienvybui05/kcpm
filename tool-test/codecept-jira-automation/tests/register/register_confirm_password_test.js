Feature("Register Form - Xác nhận mật khẩu");

const fillValidData = (I) => {
  const random = Date.now() + Math.floor(Math.random() * 100000);

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${random}@gmail.com`);
  I.fillField(
    "Sdt",
    "0" + Math.floor(100000000 + Math.random() * 900000000)
  );
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField(
    "Bang_Lai_Xe",
    `A1-${Math.floor(100000 + Math.random() * 900000)}`
  );
};

Scenario(
  "TC_REGISTER - Nhập xác nhận mật khẩu trùng với mật khẩu gốc",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);

    I.fillField("Mat_Khau", "abc123");
    I.fillField("Xac_Nhan_Mat_Khau", "abc123");

    I.click("Đăng ký tài xế");

    I.waitForURL(/\/login$/, 30);
  }
);

Scenario(
  "TC_REGISTER - Nhập xác nhận mật khẩu khác mật khẩu gốc",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);

    I.fillField("Mat_Khau", "abc123");
    I.fillField("Xac_Nhan_Mat_Khau", "abc124");

    I.click("Đăng ký tài xế");

    I.see("Mật khẩu và xác nhận mật khẩu không khớp!");
  }
);

// Boundary Min
Scenario("TC_REGISTER - Boundary Min (6 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Mat_Khau", "abcdef");
  I.fillField("Xac_Nhan_Mat_Khau", "abcdef");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Min+
Scenario("TC_REGISTER - Boundary Min+ (7 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Mat_Khau", "abcdefg");
  I.fillField("Xac_Nhan_Mat_Khau", "abcdefg");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Nominal
Scenario("TC_REGISTER - Boundary Nominal (10 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Mat_Khau", "abcdefghij");
  I.fillField("Xac_Nhan_Mat_Khau", "abcdefghij");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max-
Scenario("TC_REGISTER - Boundary Max- (15 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Mat_Khau", "a".repeat(15));
  I.fillField("Xac_Nhan_Mat_Khau", "a".repeat(15));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// Boundary Max
Scenario("TC_REGISTER - Boundary Max (20 ký tự)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Mat_Khau", "a".repeat(20));
  I.fillField("Xac_Nhan_Mat_Khau", "a".repeat(20));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});