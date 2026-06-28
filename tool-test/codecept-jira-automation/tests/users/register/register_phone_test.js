Feature("Register Form - Số điện thoại");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Mat_Khau", "Abc@123");
  I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", "A1");
};

// soDienThoai - min-=9
Scenario(
  "[soDienThoai - min-=9] Đăng ký với số điện thoại 9 chữ số",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "098765432");

    I.click("Đăng ký tài xế");

    I.see("Số điện thoại phải đúng 10 chữ số");
  },
);

// soDienThoai - min=10
Scenario(
  "[soDienThoai - min=10] Đăng ký với số điện thoại 10 chữ số",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "0987654321");

    I.click("Đăng ký tài xế");

    I.waitForURL(/\/login$/, 30);
  },
);

// soDienThoai - min+=11
Scenario(
  "[soDienThoai - min+=11] Đăng ký với số điện thoại 11 chữ số",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "09876543210");

    I.click("Đăng ký tài xế");

    I.see("Số điện thoại phải đúng 10 chữ số");
  },
);

// soDienThoai - max+=12
Scenario(
  "[soDienThoai - max+=12] Đăng ký với số điện thoại 12 chữ số",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "098765432101");

    I.click("Đăng ký tài xế");

    I.see("Số điện thoại phải đúng 10 chữ số");
  },
);

// soDienThoai - invalid
Scenario(
  "[soDienThoai - invalid] Đăng ký với số điện thoại bắt đầu bằng 1",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "1987654321");

    I.click("Đăng ký tài xế");

    I.see("Số điện thoại phải bắt đầu bằng 0");
  },
);

// soDienThoai - invalid
Scenario(
  "[soDienThoai - invalid] Đăng ký với số điện thoại chứa chữ cái",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "09876543ab");

    I.click("Đăng ký tài xế");

    I.see("Số điện thoại chỉ được chứa chữ số");
  },
);

// soDienThoai - invalid
Scenario(
  "[soDienThoai - invalid] Đăng ký với số điện thoại rỗng",
  async ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);
    I.fillField("Sdt", "");

    I.click("Đăng ký tài xế");

    I.see("người dùng chưa cung cấp số điện thoại");
  },
);
