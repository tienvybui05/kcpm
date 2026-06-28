Feature("Register Form - Email");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "Abc@123");
  I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1`);
};

// Nominal
Scenario("[email - nominal] Đăng ký với email hợp lệ", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", `test${Date.now()}@gmail.com`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// email - min-
Scenario("[email - min-] Đăng ký với email rỗng", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "");

  I.click("Đăng ký tài xế");

  I.see("Đăng ký tài xế thất bại: Người dùng chưa cung cấp email");
});

// email - min = 5
Scenario('[email - min=5] Đăng ký với email = "a@b.c"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "a@b.c");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// email - min+ = 6
Scenario('[email - min+=6] Đăng ký với email = "ab@c.d"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Email", "ab@c.d");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// email - max- = 253
Scenario("[email - max-=253] Đăng ký với email 253 ký tự", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const email = `${"a".repeat(247)}@b.com`;

  I.fillField("Email", email);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// email - max = 254
Scenario("[email - max=254] Đăng ký với email 254 ký tự", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const email = `${"a".repeat(248)}@b.com`;

  I.fillField("Email", email);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// email - max+ = 255
Scenario("[email - max+=255] Đăng ký với email 255 ký tự", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const email = `${"a".repeat(249)}@b.com`;

  I.fillField("Email", email);

  I.click("Đăng ký tài xế");

  I.see("Email vượt quá độ dài cho phép (tối đa 254 ký tự)");
});

// email - invalid
Scenario("[email - invalid] Đăng ký với email sai định dạng", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Email", "invalid-email");

  I.click("Đăng ký tài xế");

  I.see("Email không đúng định dạng");
});
