Feature("Register Form - Xác nhận mật khẩu");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", "A1");
};

// confirm password - valid
Scenario(
  "[xacNhanMatKhau - valid] Xác nhận mật khẩu trùng mật khẩu gốc",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);

    I.fillField("Mat_Khau", "Abc@123");
    I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");

    I.click("Đăng ký tài xế");

    I.waitForURL(/\/login$/, 30);
  },
);

// confirm password - invalid
Scenario(
  "[xacNhanMatKhau - invalid] Xác nhận mật khẩu không khớp",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);

    I.fillField("Mat_Khau", "Abc@123");
    I.fillField("Xac_Nhan_Mat_Khau", "Abc@124");

    I.click("Đăng ký tài xế");

    I.see("Mật khẩu và xác nhận mật khẩu không khớp!");
  },
);
