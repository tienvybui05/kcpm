const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Suc Khoe Pin");

Before(async ({ I }) => {
    await loginOnce(I);
});

Data([
    { value: "0" },
    { value: "1" },
    { value: "50" },
    { value: "99" },
    { value: "100" },
]).Scenario(
    "Standard BVA suc khoe pin",
    async ({ I, current }) => {
        await openAddVehicleModal(I);

        fillNominalVehicleForm(I);

        I.fillField("#sucKhoePin", current.value);

        // Trường hợp này nút được mở, tiến hành click tạo xe
        I.click("Tạo Xe & Pin");

        // KÌ VỌNG: Hệ thống xử lý tạo thành công và thông báo/đóng modal
        I.wait(2); // Đợi 2 giây để hệ thống xử lý API
        I.dontSee("Thêm Xe Mới & Tạo Pin"); // Kiểm tra modal đã đóng (tức là tạo thành công)
    }
);