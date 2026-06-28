Feature("Register Form - Ngày sinh");

const fillValidData = (I) => {
const timestamp = Date.now();

I.fillField("Ho_Ten", "Nguyen Van A");
I.fillField("Email", `test${timestamp}@gmail.com`);
I.fillField("Sdt", `09${String(timestamp).slice(-8)}`);
I.fillField("Mat_Khau", "Abc@123");
I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");
I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
I.fillField("Bang_Lai_Xe", "A1");
};

const setBirthDate = (I, date) => {
I.usePlaywrightTo("set birth date", async ({ page }) => {
await page.locator('input[name="Ngay_sinh"]').fill(date);
});
};

// ngaySinh - invalid
Scenario(
"[ngaySinh - invalid] Đăng ký với ngày sinh dưới 18 tuổi",
async ({ I }) => {
I.amOnPage("/register");


fillValidData(I);
setBirthDate(I, "2010-01-01");

I.click("Đăng ký tài xế");

I.see("Tài xế phải từ 18 tuổi trở lên");


}
);

// ngaySinh - min
Scenario(
"[ngaySinh - min] Đăng ký với ngày sinh đúng 18 tuổi",
async ({ I }) => {
I.amOnPage("/register");


fillValidData(I);
setBirthDate(I, "2008-01-01");

I.click("Đăng ký tài xế");

I.waitForURL(/\/login$/, 30);


}
);

// ngaySinh - invalid
Scenario(
"[ngaySinh - invalid] Đăng ký với ngày sinh trong tương lai",
async ({ I }) => {
I.amOnPage("/register");


fillValidData(I);
setBirthDate(I, "2026-08-17");

I.click("Đăng ký tài xế");

I.see("Ngày sinh không được là ngày trong tương lai");


}
);
