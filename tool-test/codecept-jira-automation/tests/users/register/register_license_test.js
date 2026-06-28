Feature("Register Form - Bằng lái xe");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "Abc@123");
  I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
};

// bangLaiXe - min-
Scenario("[bangLaiXe - min-] Không truyền bằng lái xe", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "");

  I.click("Đăng ký tài xế");

  I.see("Bằng lái xe không được để trống");
});

// bangLaiXe - min
Scenario('[bangLaiXe - min] Đăng ký với bangLaiXe = "B"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "B");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// bangLaiXe - min+
Scenario('[bangLaiXe - min+] Đăng ký với bangLaiXe = "B1"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "B1");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// bangLaiXe - valid
Scenario('[bangLaiXe - valid] Đăng ký với bangLaiXe = "B2"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "B2");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// bangLaiXe - valid
Scenario('[bangLaiXe - valid] Đăng ký với bangLaiXe = "A1"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "A1");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// bangLaiXe - valid
Scenario('[bangLaiXe - valid] Đăng ký với bangLaiXe = "C"', async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  I.fillField("Bang_Lai_Xe", "C");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// bangLaiXe - invalid
Scenario(
  '[bangLaiXe - invalid] Đăng ký với bangLaiXe = "B@1"',
  async ({ I }) => {
    I.amOnPage("/register");

    ```
fillValidData(I);
I.fillField("Bang_Lai_Xe", "B@1");

I.click("Đăng ký tài xế");

I.see("Bằng lái xe không hợp lệ");
```;
  },
);
